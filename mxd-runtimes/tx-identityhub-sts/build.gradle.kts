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
    runtimeOnly(libs.bom.ih.withsts)
    runtimeOnly(libs.bom.ih.sql)
    runtimeOnly(libs.bom.ih.sql.sts)
    runtimeOnly(libs.edc.vault.hashicorp)

    // used for custom extensions
    implementation(libs.edc.core.connector)
    implementation(libs.edc.ih.spi)

    testImplementation(libs.edc.lib.crypto)
    testImplementation(libs.edc.lib.keys)
    testImplementation(libs.edc.junit)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("${project.name}.jar")
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}
