/*
 *  Copyright (c) 2024 Metaform Systems, Inc.
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Metaform Systems, Inc. - initial API and implementation
 *
 */

package org.eclipse.edc.demo.tests.transfer;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import org.eclipse.edc.catalog.transform.JsonObjectToCatalogTransformer;
import org.eclipse.edc.catalog.transform.JsonObjectToDataServiceTransformer;
import org.eclipse.edc.catalog.transform.JsonObjectToDatasetTransformer;
import org.eclipse.edc.catalog.transform.JsonObjectToDistributionTransformer;
import org.eclipse.edc.connector.controlplane.catalog.spi.Catalog;
import org.eclipse.edc.connector.controlplane.catalog.spi.Dataset;
import org.eclipse.edc.connector.controlplane.transform.odrl.OdrlTransformersFactory;
import org.eclipse.edc.json.JacksonTypeManager;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.jsonld.util.JacksonJsonLd;
import org.eclipse.edc.junit.annotations.EndToEndTest;
import org.eclipse.edc.junit.testfixtures.TestUtils;
import org.eclipse.edc.participant.spi.ParticipantIdMapper;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.transform.TypeTransformerRegistryImpl;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.transform.transformer.edc.to.JsonValueToGenericTypeTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.spi.constants.CoreConstants.JSON_LD;

/**
 * This test is designed to run against an MVD deployed in a Kubernetes cluster, with an active ingress controller.
 * The cluster MUST be deployed and seeded according to the README before running this test!
 */
@EndToEndTest
public class TransferEndToEndTest {
    // Management API base URL of the consumer connector, goes through Ingress controller
    private static final String CONSUMER_MANAGEMENT_URL = "http://localhost/bob/management";
    // Catalog Query API URL of the consumer connector, goes through ingress controller
    private static final String CONSUMER_CATALOG_URL = "http://localhost/bob/fc";
    // DSP service URL of the provider, not reachable outside the cluster
    private static final String PROVIDER_DSP_URL = "http://alice-controlplane:8084";
    // DID of the provider company
    private static final String PROVIDER_ID = "BPNL000000000001";
    // public API endpoint of the provider-qna connector, goes through the ingress controller
    private static final String PROVIDER_PUBLIC_URL = "http://localhost/alice/api/public";


    private static final Duration TEST_TIMEOUT_DURATION = Duration.ofSeconds(120);
    private static final Duration TEST_POLL_DELAY = Duration.ofSeconds(2);

    private final TypeTransformerRegistry transformerRegistry = new TypeTransformerRegistryImpl();
    private final JsonLd jsonLd = new TitaniumJsonLd(new ConsoleMonitor());

    @BeforeEach
    void setup() {
        transformerRegistry.register(new JsonObjectToCatalogTransformer());
        transformerRegistry.register(new JsonObjectToDatasetTransformer());
        transformerRegistry.register(new JsonObjectToDataServiceTransformer());
        transformerRegistry.register(new JsonObjectToDistributionTransformer());
        transformerRegistry.register(new JsonValueToGenericTypeTransformer(new JacksonTypeManager(), JSON_LD));
        OdrlTransformersFactory.jsonObjectToOdrlTransformers(new ParticipantIdMapper() {
            @Override
            public String toIri(String s) {
                return s;
            }

            @Override
            public String fromIri(String s) {
                return s;
            }
        }).forEach(transformerRegistry::register);
    }

    @DisplayName("Tests a successful End-to-End contract negotiation and data transfer")
    @Test
    void transferData_hasPermission_shouldTransferData() {
        var emptyQueryBody = Json.createObjectBuilder()
                .add("@context", Json.createObjectBuilder().add("edc", "https://w3id.org/edc/v0.0.1/ns/"))
                .add("@type", "QuerySpec")
                .build();
        var offerId = new AtomicReference<String>();
        // get catalog, extract offer ID
        await().atMost(TEST_TIMEOUT_DURATION)
                .pollDelay(TEST_POLL_DELAY)
                .untilAsserted(() -> {
                    var jo = baseRequest()
                            .baseUri(CONSUMER_CATALOG_URL)
                            .body(emptyQueryBody)
                            .post("/catalog/v1alpha/catalog/query")
                            .then()
                            .log().ifError()
                            .statusCode(200)
                            .extract().body().as(JsonArray.class);

                    var offerIdsFiltered = jo.stream().map(jv -> {

                                var expanded = jsonLd.expand(jv.asJsonObject()).orElseThrow(f -> new AssertionError(f.getFailureDetail()));
                                var cat = transformerRegistry.transform(expanded, Catalog.class).orElseThrow(f -> new AssertionError(f.getFailureDetail()));
                                return cat.getDatasets().stream().filter(ds -> ds instanceof Catalog) // filter for CatalogAssets
                                        .map(ds -> (Catalog) ds)
                                        .filter(sc -> sc.getDataServices().stream().anyMatch(dataService -> dataService.getEndpointUrl().contains("alice"))) // filter for assets from Alice
                                        .flatMap(c -> c.getDatasets().stream())
                                        .filter(dataset -> dataset.getId().equals("asset-1")) // filter for the asset we're allowed to negotiate
                                        .map(Dataset::getOffers)
                                        .map(offers -> offers.keySet().iterator().next())
                                        .findFirst()
                                        .orElse(null);
                            })
                            .filter(Objects::nonNull)
                            .toList();
                    assertThat(offerIdsFiltered).hasSize(1).doesNotContainNull();
                    var oid = offerIdsFiltered.get(0);
                    assertThat(oid).isNotNull();
                    offerId.set(oid);
                });

        System.out.println("Initiate contract negotiation");

        // initiate negotiation
        var negotiationRequest = TestUtils.getResourceFileContentAsString("negotiation-request.json")
                .replace("{{PROVIDER_ID}}", PROVIDER_ID)
                .replace("{{PROVIDER_DSP_URL}}", PROVIDER_DSP_URL)
                .replace("{{OFFER_ID}}", offerId.get());
        var negotiationId = baseRequest()
                .body(negotiationRequest)
                .post(CONSUMER_MANAGEMENT_URL + "/v3/contractnegotiations")
                .then()
                .log().ifError()
                .statusCode(200)
                .extract().body().jsonPath().getString("@id");
        assertThat(negotiationId).isNotNull();

        System.out.println("Wait until negotiation is FINALIZED");
        //wait until negotiation is FINALIZED
        var agreementId = new AtomicReference<String>();
        await().atMost(TEST_TIMEOUT_DURATION)
                .pollDelay(TEST_POLL_DELAY)
                .untilAsserted(() -> {
                    var jp = baseRequest()
                            .get(CONSUMER_MANAGEMENT_URL + "/v3/contractnegotiations/" + negotiationId)
                            .then()
                            .statusCode(200)
                            .extract().body().jsonPath();
                    var state = jp.getString("state");
                    assertThat(state).isEqualTo("FINALIZED");
                    agreementId.set(jp.getString("contractAgreementId"));
                });

        System.out.println("Start transfer process");
        //start transfer process
        var tpRequest = TestUtils.getResourceFileContentAsString("transfer-request.json")
                .replace("{{PROVIDER_ID}}", PROVIDER_ID)
                .replace("{{PROVIDER_DSP_URL}}", PROVIDER_DSP_URL)
                .replace("{{CONTRACT_ID}}", agreementId.get());

        var transferProcessId = baseRequest()
                .body(tpRequest)
                .post(CONSUMER_MANAGEMENT_URL + "/v3/transferprocesses")
                .then()
                .log().ifError()
                .statusCode(200)
                .extract().body().jsonPath().getString("@id");

        // wait until transfer process is in STARTED state
        System.out.println("Wait until transfer process is STARTED");
        await().atMost(TEST_TIMEOUT_DURATION)
                .pollDelay(TEST_POLL_DELAY)
                .untilAsserted(() -> {
                    var jp = baseRequest()
                            .body(emptyQueryBody)
                            .post(CONSUMER_MANAGEMENT_URL + "/v3/transferprocesses/request")
                            .then()
                            .statusCode(200)
                            .extract().body().jsonPath();

                    assertThat(jp.getString("state")).contains("STARTED");
                });

        System.out.printf("Fetch EDR with ID %s%n", transferProcessId);
        // fetch EDR for transfer processs
        var endpoint = new AtomicReference<String>();
        var token = new AtomicReference<String>();
        await().atMost(TEST_TIMEOUT_DURATION)
                .pollDelay(TEST_POLL_DELAY)
                .untilAsserted(() -> {
                    var jp = baseRequest()
                            .get(CONSUMER_MANAGEMENT_URL + "/v3/edrs/%s/dataaddress".formatted(transferProcessId))
                            .then()
                            .log().ifValidationFails()
                            .statusCode(200)
                            .onFailMessage("Expected to find an EDR with transfer ID %s but did not!".formatted(transferProcessId))
                            .extract().body().jsonPath();

                    endpoint.set(jp.getString("endpoint"));
                    token.set(jp.getString("authorization"));

                    assertThat(endpoint.get()).isNotNull().endsWith("/api/public");
                    assertThat(token.get()).isNotNull();
                });

        //download exemplary JSON data from public endpoint
        var response = given()
                .header("Authorization", token.get())
                .get(PROVIDER_PUBLIC_URL)
                .then()
                .log().ifError()
                .statusCode(200)
                .extract().body().asString();

        assertThat(response).isNotEmpty();
    }

    @DisplayName("Asserts that attempting to negotiate a contract for a non-permitted Assets fails")
    @Test
    void transferData_requestsForbiddenAsset_shouldFail() {
        System.out.println("Provider dataplane is online, fetching catalog");

        var emptyQueryBody = Json.createObjectBuilder()
                .add("@context", Json.createObjectBuilder().add("edc", "https://w3id.org/edc/v0.0.1/ns/"))
                .add("@type", "QuerySpec")
                .build();
        var offerId = new AtomicReference<String>();
        // get catalog, extract offer ID
        await().atMost(TEST_TIMEOUT_DURATION)
                .pollDelay(TEST_POLL_DELAY)
                .untilAsserted(() -> {
                    var jo = baseRequest()
                            .baseUri(CONSUMER_CATALOG_URL)
                            .body(emptyQueryBody)
                            .post("/catalog/v1alpha/catalog/query")
                            .then()
                            .log().ifError()
                            .statusCode(200)
                            .extract().body().as(JsonArray.class);

                    var offerIdsFiltered = jo.stream().map(jv -> {

                                var expanded = jsonLd.expand(jv.asJsonObject()).orElseThrow(f -> new AssertionError(f.getFailureDetail()));
                                var cat = transformerRegistry.transform(expanded, Catalog.class).orElseThrow(f -> new AssertionError(f.getFailureDetail()));
                                return cat.getDatasets().stream().filter(ds -> ds instanceof Catalog) // filter for CatalogAssets
                                        .map(ds -> (Catalog) ds)
                                        .filter(sc -> sc.getDataServices().stream().anyMatch(dataService -> dataService.getEndpointUrl().contains("alice"))) // filter for assets from Alice
                                        .flatMap(c -> c.getDatasets().stream())
                                        .filter(dataset -> dataset.getId().equals("asset-3")) // filter for the asset we're allowed to negotiate
                                        .map(Dataset::getOffers)
                                        .map(offers -> offers.keySet().iterator().next())
                                        .findFirst()
                                        .orElse(null);
                            })
                            .filter(Objects::nonNull)
                            .toList();
                    assertThat(offerIdsFiltered).hasSize(1).doesNotContainNull();
                    var oid = offerIdsFiltered.get(0);
                    assertThat(oid).isNotNull();
                    offerId.set(oid);
                });

        System.out.println("Initiate contract negotiation");

        // initiate negotiation
        var negotiationRequest = TestUtils.getResourceFileContentAsString("negotiation-request.json")
                .replace("{{PROVIDER_ID}}", PROVIDER_ID)
                .replace("{{PROVIDER_DSP_URL}}", PROVIDER_DSP_URL)
                .replace("{{OFFER_ID}}", offerId.get())
                .replaceFirst("\"@id\": \"cx-policy:Membership\"", "\"@id\": \"cx-policy:Dismantler\"");
        var negotiationId = baseRequest()
                .body(negotiationRequest)
                .post(CONSUMER_MANAGEMENT_URL + "/v3/contractnegotiations")
                .then()
                .log().ifError()
                .statusCode(200)
                .extract().body().jsonPath().getString("@id");
        assertThat(negotiationId).isNotNull();

        //wait until negotiation is TERMINATED
        await().atMost(TEST_TIMEOUT_DURATION)
                .pollDelay(TEST_POLL_DELAY)
                .untilAsserted(() -> {
                    var jp = baseRequest()
                            .get(CONSUMER_MANAGEMENT_URL + "/v3/contractnegotiations/" + negotiationId)
                            .then()
                            .statusCode(200)
                            .extract().body().jsonPath();
                    var state = jp.getString("state");
                    assertThat(state).isEqualTo("TERMINATED");
                });
    }

    private static RequestSpecification baseRequest() {
        RestAssured.port = 80;
        return given()
                .header("x-api-key", "password")
                .contentType("application/json")
                .when();
    }

}
