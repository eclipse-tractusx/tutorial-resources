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
import org.eclipse.tractusx.mxd.backendservice.store.ContentStoreService;
import org.eclipse.tractusx.mxd.testfixtures.PostgresRuntime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.UUID;

import static io.restassured.http.ContentType.JSON;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.mxd.testfixtures.PostgresqlEndToEndInstance.createContainer;
import static org.eclipse.tractusx.mxd.testfixtures.PostgresqlEndToEndInstance.destroyContainer;
import static org.hamcrest.Matchers.*;

public class ContentApiEndToEndTest {

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

        private final static String ENDPOINT = "/v1/contents/";

        Tests(EdcRuntimeExtension runtime) {
            super(runtime);
        }

        @Test
        void getContentById() {
            LinkedHashMap content = getContent();
            String contentId = getContentIndex().save(content);

            baseRequest()
                    .when()
                    .get(ENDPOINT + contentId)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .body("userId", is(content.get("userId")))
                    .body("title", is(content.get("title")))
                    .body("text", is(content.get("text")));
        }

        @Test
        void getAllContents() {
            LinkedHashMap content = getContent();
            getContentIndex().save(content);

            baseRequest()
                    .when()
                    .get(ENDPOINT)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("size()", is(greaterThan(0)));
        }

        @Test
        void createAsset_shouldBeStored() {
            JsonObject contentJson = getContentJson();

            var responseBody = baseRequest()
                    .contentType(ContentType.JSON)
                    .body(contentJson)
                    .post(ENDPOINT)
                    .then()
                    .log().ifError()
                    .statusCode(200);

            String contentId = responseBody.extract().jsonPath()
                    .getString("id");

            assertThat(getContentIndex().findById(contentId)).isNotNull();

            baseRequest()
                    .when()
                    .get(ENDPOINT + contentId)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("userId", is(contentJson.getString("userId")))
                    .body("title", is(contentJson.getString("title")))
                    .body("text", is(contentJson.getString("text")));
        }

        @Test
        void getRandomContent() {
            baseRequest()
                    .when()
                    .get(ENDPOINT + "random")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("userId", is(greaterThan(-1)))
                    .body("title",  not(emptyString()))
                    .body("text",  not(emptyString()));

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

        public JsonObject getContentJson() {
            String id = UUID.randomUUID().toString();
            return createObjectBuilder()
                    .add("userId", id)
                    .add("title", "Test")
                    .add("text", "Test")
                    .build();

        }
    }
}