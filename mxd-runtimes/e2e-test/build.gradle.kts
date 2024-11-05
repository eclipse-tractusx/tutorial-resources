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

plugins {
    `java-library`
}

dependencies {
    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.spi.catalog)
    testImplementation(libs.edc.lib.transform)
    testImplementation(catalogLibs.core.fc)
    testImplementation(libs.edc.lib.jsonld)
    testImplementation(libs.edc.controlplane.transform)


    testImplementation(libs.jakarta.json.api)
    testImplementation(libs.jackson.datatype.jakarta.jsonp)
    testImplementation(libs.parsson)
    testImplementation(libs.restAssured)
    testImplementation(libs.awaitility)
}
