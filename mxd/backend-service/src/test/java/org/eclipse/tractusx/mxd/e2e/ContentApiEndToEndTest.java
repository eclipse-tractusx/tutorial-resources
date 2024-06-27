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
import org.eclipse.tractusx.mxd.testfixtures.PostgresRuntime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
        void getContentWithId() {
            JsonObject contentJson = getContentJson();
            String contentId = createContent(contentJson);

            baseRequest()
                    .when()
                    .get(ENDPOINT + contentId)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("size()", equalTo(3));
        }

        @Test
        void getAllContents() {
            JsonObject contentJson = getContentJson();
            createContent(contentJson);

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
            String contentId = createContent(contentJson);

            baseRequest()
                    .when()
                    .get(ENDPOINT + contentId)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON)
                    .body("userId", is(contentJson.getInt("userId")))
                    .body("title", is(contentJson.getString("title")))
                    .body("text", is(contentJson.getString("text")));
        }

        @Test
        void getRandomContent() {
            JsonObject content = getContentJson();

            assertThat(content.getString("title")).isNotNull();
            assertThat(content.getString("text")).isNotNull();
            assertThat(content.getInt("userId")).isGreaterThan(-1);
        }

        String createContent(JsonObject contentJson) {
            var responseBody = baseRequest()
                    .contentType(ContentType.JSON)
                    .body(contentJson)
                    .post(ENDPOINT)
                    .then()
                    .log().ifError()
                    .statusCode(200);

            return responseBody.extract().jsonPath()
                    .getString("id");
        }

        public JsonObject getContentJson() {
            var responseBody = baseRequest()
                    .when()
                    .get(ENDPOINT + "random")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(JSON);

            return createObjectBuilder()
                    .add("userId", responseBody.extract().jsonPath().getInt("userId"))
                    .add("title", responseBody.extract().jsonPath().getString("title"))
                    .add("text", responseBody.extract().jsonPath().getString("text"))
                    .build();

        }
    }
}