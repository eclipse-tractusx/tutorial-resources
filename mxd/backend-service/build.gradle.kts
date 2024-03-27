/*******************************************************************************
 *
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 ******************************************************************************/

import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin

plugins {
    id("java")
    `java-library`
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
   testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.restAssured)
    implementation(libs.restAssured)
    testImplementation(libs.assertj)
    testImplementation(libs.edc.junit)
    implementation(libs.edc.configuration.filesystem)
    implementation(libs.edc.boot)
    implementation(libs.edc.json.ld)
    implementation(libs.edc.web.spi)
    implementation(libs.edc.api.core)
    implementation(libs.edc.core)
    implementation(libs.edc.http)
    implementation(libs.edc.http.spi)
    implementation(libs.edc.jersey.core)
    implementation(libs.swagger.core)
    implementation(libs.edc.transaction.spi)
    implementation(libs.edc.connector.core)
    implementation(libs.edc.sql.core)
    implementation(libs.apache.commons)
    implementation(libs.postgresql)
    implementation(libs.edc.transform)
    implementation(libs.edc.transaction)


}
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("backend-service.jar")
}
// this task copies some legal docs into the build folder, so we can easily copy them into the docker images
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
dockerTask.dependsOn(tasks.named("build"),tasks.named("copyDockerFile"),tasks.named("copyJar"))
copyJar.dependsOn(tasks.named("build"))