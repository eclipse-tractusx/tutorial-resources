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

import io.restassured.http.ContentType;
import jakarta.json.JsonObject;
import org.eclipse.edc.junit.annotations.PostgresqlIntegrationTest;
import org.eclipse.edc.junit.extensions.EdcRuntimeExtension;
import org.eclipse.tractusx.mxd.backendservice.entity.Transfer;
import org.eclipse.tractusx.mxd.backendservice.store.ContentStoreService;
import org.eclipse.tractusx.mxd.backendservice.store.TransferStoreService;
import org.eclipse.tractusx.mxd.testfixtures.PostgresRuntime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.http.ContentType.JSON;
import static jakarta.json.Json.createObjectBuilder;
import static org.hamcrest.Matchers.is;

public class TransferApiEndToEndTest {

    @Nested
    @PostgresqlIntegrationTest
    class Postgres extends Tests implements PostgresRuntime {
        Postgres() {
            super(RUNTIME);
        }
    }

    abstract static class Tests extends BackendServiceApiEndToEndTestBase {

        private final static String ENDPOINT = "/v1/transfers/";

        Tests(EdcRuntimeExtension runtime) {
            super(runtime);
        }

        @Test
        void createTransfer_shouldBeStored() {
            LinkedHashMap content = getContent();
            String contentId = getContentIndex().save(content);
            String contentUrl = getBaseUri().toString() + "/v1/contents/" + contentId;

            String id = UUID.randomUUID().toString();
            JsonObject transferJson = getTransferJson(id, contentUrl);

            baseRequest()
                    .contentType(ContentType.JSON)
                    .body(transferJson)
                    .post(ENDPOINT)
                    .then()
                    .statusCode(200)
                    .body("id", is(id))
                    .body("authCode", is(transferJson.getString("authCode")))
                    .body("authKey", is(transferJson.getString("authKey")))
                    .body("endpoint", is(transferJson.getString("endpoint")));

            Map headers = Map.of(
                    "authCode", transferJson.getString("authCode"),
                    "authKey", transferJson.getString("authKey")
            );

            baseRequest()
                    .headers(headers)
                    .get(ENDPOINT + id + "/contents")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("userId", is(content.get("userId")))
                    .body("title", is(content.get("title")))
                    .body("text", is(content.get("text")));
        }

        @Test
        void getTransferWithId() {
            LinkedHashMap content = getContent();
            String contentId = getContentIndex().save(content);
            String contentUrl = getBaseUri().toString() + "/v1/contents/" + contentId;

            String id = UUID.randomUUID().toString();
            Transfer transfer = getTransfer(id, contentUrl);

            getTransferIndex().save(transfer, content.toString());

            baseRequest()
                    .get(ENDPOINT + id)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("id", is(id))
                    .body("authCode", is(transfer.getAuthCode()))
                    .body("authKey", is(transfer.getAuthKey()))
                    .body("endpoint", is(transfer.getEndpoint()));
        }

        @Test
        void getAssetContent() {
            String userId = UUID.randomUUID().toString();
            JsonObject content = getContentJson(userId);
            String contentId = getContentIndex().save(content);
            String contentUrl = getBaseUri().toString() + "/v1/contents/" + contentId;

            String id = UUID.randomUUID().toString();
            Transfer transfer = getTransfer(id, contentUrl);

            getTransferIndex().save(transfer, content.toString());

            baseRequest()
                    .get(ENDPOINT + id + "/contents")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("userId", is(userId))
                    .body("id", is(content.getString("id")))
                    .body("title", is(content.getString("title")));

        }

        public JsonObject getTransferJson(String id, String contentUrl) {
            return createObjectBuilder()
                    .add("id", id)
                    .add("endpoint", contentUrl)
                    .add("authKey", "Authorization")
                    .add("authCode", "100000")
                    .build();
        }

        public JsonObject getContentJson(String id) {
            return createObjectBuilder()
                    .add("userId", id)
                    .add("id", "0")
                    .add("title", "test")
                    .build();

        }

        private Transfer getTransfer(String id, String endPoint) {
            return Transfer.Builder
                    .newInstance()
                    .id(id)
                    .authKey("Authorization")
                    .authCode("100000")
                    .endpoint(endPoint)
                    .build();
        }

        private TransferStoreService getTransferIndex() {
            return runtime.getContext().getService(TransferStoreService.class);
        }

        private ContentStoreService getContentIndex() {
            return runtime.getContext().getService(ContentStoreService.class);
        }

        private LinkedHashMap getContent() {
            String id = UUID.randomUUID().toString();
            LinkedHashMap<String, String> content
                    = new LinkedHashMap<>();
            content.put("userId", id);
            content.put("title", "Test");
            content.put("text", "Test");

            return  content;
        }
    }
}
