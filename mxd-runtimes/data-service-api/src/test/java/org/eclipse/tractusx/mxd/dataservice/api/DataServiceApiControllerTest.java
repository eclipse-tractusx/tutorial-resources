/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.tractusx.mxd.dataservice.api;

import io.restassured.specification.RequestSpecification;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.web.jersey.testfixtures.RestControllerTestBase;
import org.eclipse.tractusx.mxd.dataservice.model.DataRecord;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
class DataServiceApiControllerTest extends RestControllerTestBase {

    private final ConcurrentHashMap<String, DataRecord> database = new ConcurrentHashMap<>();

    @Test
    void getAll_empty() {
        var records = baseRequest()
                .get()
                .then()
                .statusCode(200)
                .extract().body().as(DataRecord[].class);

        assertThat(records).isEmpty();
    }

    @Test
    void getAll() {
        database.put("15", new DataRecord("15", "name", "desc"));

        var records = baseRequest()
                .get()
                .then()
                .statusCode(200)
                .extract().body().as(DataRecord[].class);

        assertThat(records).allMatch(dr -> dr.id().equals("15"));
    }

    @Test
    void delete() {
        database.put("15", new DataRecord("15", "name", "desc"));

        baseRequest()
                .delete("/15")
                .then()
                .statusCode(204);

        assertThat(database).isEmpty();
    }

    @Test
    void delete_notExists() {
        baseRequest()
                .delete("/15")
                .then()
                .statusCode(404);
    }

    @Test
    void create() {
        var dr = new DataRecord("id", "name", "desc");
        baseRequest()
                .body(dr)
                .post()
                .then()
                .statusCode(204);
        assertThat(database).containsOnlyKeys(dr.id());
    }

    @Test
    void create_alreadyExists() {
        var dr = new DataRecord("id", "name", "desc");
        database.put("id", dr);
        baseRequest()
                .body(dr)
                .post()
                .then()
                .statusCode(409);
        assertThat(database).containsOnlyKeys(dr.id());
    }

    @Test
    void update() {
        var dr = new DataRecord("id", "name", "desc");
        database.put("id", dr);

        var newDr = new DataRecord("id", "new-name", "new-desc");

        baseRequest()
                .body(newDr)
                .put()
                .then()
                .statusCode(204);
        assertThat(database).allSatisfy((s, dataRecord) -> {
            assertThat(s).isEqualTo("id");
            assertThat(dataRecord).usingRecursiveComparison().isEqualTo(newDr);
        });
    }

    @Test
    void update_notExists() {

        var newDr = new DataRecord("id", "new-name", "new-desc");

        baseRequest()
                .body(newDr)
                .put()
                .then()
                .statusCode(404);
        assertThat(database).isEmpty();
    }

    @Test
    void findById() {
        var dr = new DataRecord("id", "name", "desc");
        database.put("id", dr);

        var found = baseRequest()
                .get("/id")
                .then()
                .statusCode(200)
                .extract().body().as(DataRecord.class);
        assertThat(found).usingRecursiveComparison().isEqualTo(dr);
    }

    @Test
    void findById_notExists() {
        baseRequest()
                .get("/id")
                .then()
                .statusCode(404);
    }

    @Override
    protected Object controller() {
        return new DataServiceApiController(database);
    }

    private RequestSpecification baseRequest() {
        return given()
                .when()
                .contentType("application/json")
                .baseUri("http://localhost:" + port + "/v1/data");
    }
}