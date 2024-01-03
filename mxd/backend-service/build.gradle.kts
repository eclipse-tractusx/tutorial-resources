/*******************************************************************************
 *
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(libs.edc.configuration.filesystem)
    implementation("org.eclipse.edc:boot:0.4.1")
    implementation("org.eclipse.edc:connector-core:0.4.1")
    implementation("org.eclipse.edc:http:0.4.1")
    implementation("org.eclipse.edc:json-ld:0.4.0")
    implementation("org.eclipse.edc:web-spi:0.4.1")
    implementation("org.eclipse.edc:api-core:0.4.1")
    implementation("org.eclipse.edc:core-spi:0.4.1")
    implementation("org.eclipse.edc:http:0.4.1")
    implementation("org.eclipse.edc:http-spi:0.4.1")
    implementation("org.eclipse.edc:jersey-core:0.4.1")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.19")
    implementation("org.eclipse.edc:transaction-spi:0.4.1")
    implementation("org.eclipse.edc:connector-core:0.4.1")
    implementation("org.eclipse.edc:sql-core:0.4.1")
    implementation("org.apache.commons:commons-dbcp2:2.11.0")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("org.eclipse.edc:transform-core:0.4.1")


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
    buildArgs.put("JAR", "${project.buildDir}/libs/${project.name}.jar")
    println("build 1 : ${buildArgs.get()}")
    images.add("${project.name}:${project.version}")
    images.add("${project.name}:latest")
}
dockerTask.dependsOn(tasks.named("build"),tasks.named("copyDockerFile"),tasks.named("copyJar"))
copyJar.dependsOn(tasks.named("build"))