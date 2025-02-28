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
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

dependencies {

    // used for the runtime
    runtimeOnly(libs.bom.issuer)

    // DID and Participant Context API - they should eventually be provided by the BOM
    runtimeOnly(libs.edc.ih.api.did)
    runtimeOnly(libs.edc.ih.api.participants)

    // used for custom extensions
    implementation(libs.edc.ih.spi)
    implementation(libs.edc.spi.issuance)

    testImplementation(libs.edc.lib.crypto.ih)
    testImplementation(libs.edc.lib.keys.ih)
    testImplementation(libs.edc.junit)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("${project.name}.jar")
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}
