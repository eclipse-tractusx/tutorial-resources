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

import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    id("java")
    `java-library`
    `java-test-fixtures`
    id("application")
    alias(libs.plugins.shadow)
    id("com.bmuschko.docker-remote-api") version "9.3.6"
}

group = "org.eclipse.tractusx.mxd.backendservice"
version = "1.0.0"

repositories {
    mavenCentral()
}
application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

dependencies {
    implementation(libs.restAssured)
    implementation(libs.edc.configuration.filesystem)
    implementation(libs.edc.json.ld)
    implementation(libs.edc.http)
    implementation(libs.edc.http.lib)
    implementation(libs.edc.connector.core)
    implementation(libs.edc.sql.core)
    implementation(libs.apache.commons)
    implementation(libs.postgresql)
    implementation(libs.edc.boot)
    implementation(libs.edc.spi.transfer)

    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.assertj)
    testImplementation(libs.edc.junit)
    testImplementation(libs.postgres.containers)
    testImplementation(testFixtures(libs.edc.sql.core))

}
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("backend-service.jar")
}
tasks {
    "test"(Test::class) {
        useJUnitPlatform()

        testLogging {
            showStandardStreams = true
        }
    }
}

tasks.register("printClasspath") {
    doLast {
        println("${sourceSets["main"].runtimeClasspath.asPath}")
    }
}

val copyDockerFile = tasks.create("copyDockerFile", Copy::class) {
    from(project.projectDir)
    into("${project.buildDir}/docker")

    include("Dockerfile")
}
val copyJar = tasks.create("copyJar", Copy::class) {
    from("${project.buildDir}/libs")
    into("${project.buildDir}/docker")
    include("${project.name}.jar")
}

val dockerTask: DockerBuildImage = tasks.create("dockerize", DockerBuildImage::class) {
    images.add("${project.name}:${project.version}")
    images.add("${project.name}:latest")
}
dockerTask.dependsOn(tasks.named("build"), tasks.named("copyDockerFile"), tasks.named("copyJar"))
copyJar.dependsOn(tasks.named("build"))