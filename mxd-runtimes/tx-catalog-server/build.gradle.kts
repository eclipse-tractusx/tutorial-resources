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
    // management API - is not included in the FederatedCatalog BOM
    runtimeOnly(catalogLibs.api.management)
    runtimeOnly(catalogLibs.api.management.config)

    runtimeOnly(libs.edc.vault.hashicorp)
    runtimeOnly(catalogLibs.sts.remote.client)

    // official FC BOMs
    runtimeOnly(catalogLibs.bom.catalog)
    runtimeOnly(catalogLibs.bom.catalog.sql){
        exclude("org.eclipse.edc", "target-node-directory-sql") //not needed, nodes are populated from a file
    }

    // libs from tx
    runtimeOnly(catalogLibs.tx.bdrs.client) // audience mapper
    runtimeOnly(catalogLibs.tx.core.jsonld) // locally cached context files
    runtimeOnly(catalogLibs.tx.dcp) // the default scope mapper
    runtimeOnly(catalogLibs.tx.fc) // file-based node directory

    runtimeOnly(libs.edc.did.core) // DidResolverRegistry, DidPublicKeyResolver
    runtimeOnly(libs.edc.did.web) // for registering the WebDidResolver
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xml")
    mergeServiceFiles()
    archiveFileName.set("${project.name}.jar")
}

edcBuild {
    publish.set(false)
}
