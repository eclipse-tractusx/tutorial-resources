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



plugins {
    id("java")
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
    id(libs.plugins.swagger.get().pluginId)
}

group = "org.eclipse.tractusx.mxd.dataservice"
version = "1.0.0"

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

dependencies {

    implementation(libs.edc.http)
    implementation(libs.edc.http.lib)
    implementation(libs.edc.boot)

    runtimeOnly(libs.edc.core.connector)
    runtimeOnly(libs.edc.api.observability)
    runtimeOnly(libs.edc.sql.transactionlocal)

    testImplementation(testFixtures(libs.edc.core.jersey))
    testImplementation(libs.restAssured)
    testImplementation(libs.assertj)
    testImplementation(libs.edc.junit)

}
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("${project.name}.jar")
}