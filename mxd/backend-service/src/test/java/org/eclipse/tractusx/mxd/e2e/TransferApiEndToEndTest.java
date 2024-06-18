/********************************************************************************
 *  Copyright (c) 2024 SAP SE
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       SAP SE - initial API and implementation
 *
 ********************************************************************************/

package org.eclipse.tractusx.mxd.e2e;

import io.restassured.response.ValidatableResponse;
import jakarta.json.JsonObject;
import org.eclipse.edc.junit.annotations.PostgresqlIntegrationTest;
import org.eclipse.edc.junit.extensions.EdcRuntimeExtension;
import org.eclipse.tractusx.mxd.testfixtures.PostgresRuntime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.http.ContentType.JSON;
import static jakarta.json.Json.createObjectBuilder;
import static org.eclipse.tractusx.mxd.testfixtures.PostgresqlEndToEndInstance.createContainer;
import static org.eclipse.tractusx.mxd.testfixtures.PostgresqlEndToEndInstance.destroyContainer;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class TransferApiEndToEndTest {

    @BeforeAll
    static void setUp() {
        createContainer();
    }

    @AfterAll
    static void tearDown() {
        destroyContainer();
    }

    @Nested
    @PostgresqlIntegrationTest
    class Postgres extends Tests implements PostgresRuntime {
        Postgres() {
            super(RUNTIME);
        }
    }

    abstract static class Tests extends BackendServiceApiEndToEndTestBase {

        private final static String TRANSFER_API_ENDPOINT = "/v1/transfers/";
        private final static String CONTENT_API_ENDPOINT = "/v1/contents/";

        Tests(EdcRuntimeExtension runtime) {
            super(runtime);
        }

        @Test
        void createTransfer_shouldBeStored() {
            String contentId = createContent(createContentJson());
            String contentUrl = getBaseUri().toString() + CONTENT_API_ENDPOINT + contentId;

            String transferId = UUID.randomUUID().toString();
            JsonObject transferJson = getTransferJson(transferId, contentUrl);

            var responseBody = createTransfer(transferJson);

            responseBody
            .body("id", is(transferId))
            .body("authCode", is(transferJson.getString("authCode")))
            .body("authKey", is(transferJson.getString("authKey")))
            .body("endpoint", is(transferJson.getString("endpoint")));
        }

        @Test
        void getTransferWithId() {
            String contentId = createContent(createContentJson());
            String contentUrl = getBaseUri().toString() + CONTENT_API_ENDPOINT + contentId;

            String transferId = UUID.randomUUID().toString();
            JsonObject transferJson = getTransferJson(transferId, contentUrl);

            createTransfer(transferJson);

            baseRequest()
                    .get(TRANSFER_API_ENDPOINT + transferId)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("id", is(transferId))
                    .body("authCode", is(transferJson.getString("authCode")))
                    .body("authKey", is(transferJson.getString("authKey")))
                    .body("endpoint", is(transferJson.getString("endpoint")));
        }

        @Test
        void getAllTransfer() {
            String contentId = createContent(createContentJson());
            String contentUrl = getBaseUri().toString() + CONTENT_API_ENDPOINT + contentId;

            String transferId = UUID.randomUUID().toString();
            JsonObject transferJson = getTransferJson(transferId, contentUrl);

            createTransfer(transferJson);

            baseRequest()
                    .get(TRANSFER_API_ENDPOINT + transferId)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("size()", is(greaterThan(0)));
        }

        @Test
        void getAssetContent() {
            JsonObject contentJson = createContentJson();
            String contentId = createContent(contentJson);
            String contentUrl = getBaseUri().toString() + CONTENT_API_ENDPOINT + contentId;

            String transferId = UUID.randomUUID().toString();
            JsonObject transferJson = getTransferJson(transferId, contentUrl);

            createTransfer(transferJson);

            baseRequest()
                    .get(TRANSFER_API_ENDPOINT + transferId + "/contents")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("userId", is(contentJson.getString("userId")))
                    .body("title", is(contentJson.getString("title")))
                    .body("text", is(contentJson.getString("text")))
            ;

        }

        public JsonObject getTransferJson(String id, String contentUrl) {
            return createObjectBuilder()
                    .add("id", id)
                    .add("endpoint", contentUrl)
                    .add("authKey", "Authorization")
                    .add("authCode", "100000")
                    .build();
        }

        ValidatableResponse createTransfer(JsonObject transferJson) {
            return baseRequest()
                    .contentType(JSON)
                    .body(transferJson)
                    .post(TRANSFER_API_ENDPOINT)
                    .then()
                    .statusCode(200);
        }
        String createContent(JsonObject contentJson) {
            var responseBody = baseRequest()
                    .contentType(JSON)
                    .body(contentJson)
                    .post(CONTENT_API_ENDPOINT)
                    .then()
                    .log().ifError()
                    .statusCode(200);

            return responseBody.extract().jsonPath()
                    .getString("id");
        }

        JsonObject createContentJson() {
            return createObjectBuilder()
                    .add("userId", UUID.randomUUID().toString())
                    .add("title", "Test")
                    .add("text", "Test")
                    .build();
        }
    }
}
