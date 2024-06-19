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
import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin
import java.time.Duration

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.bmuschko.docker-remote-api") version "9.4.0"
}

val edcVersion = libs.versions.edc

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath(libs.edc.build.plugin)
    }
}

allprojects {
    apply(plugin = "org.eclipse.edc.edc-build")

    repositories {
        mavenCentral()
    }

    // configure which version of the annotation processor to use. defaults to the same version as the plugin
    configure<org.eclipse.edc.plugins.autodoc.AutodocExtension> {
        processorVersion.set(edcVersion)
        outputDirectory.set(project.layout.buildDirectory.asFile.get())
    }
}

// the "dockerize" task is added to all projects that use the `shadowJar` plugin
subprojects {
    afterEvaluate {
        if (project.plugins.hasPlugin("com.github.johnrengelman.shadow") &&
                file("${project.projectDir}/src/main/docker/Dockerfile").exists()
        ) {
            val buildDir = project.layout.buildDirectory.get().asFile

            val agentFile = buildDir.resolve("opentelemetry-javaagent.jar")
            // create task to download the opentelemetry agent
            val openTelemetryAgentUrl = "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.32.0/opentelemetry-javaagent.jar"
            val downloadOtel = tasks.create("downloadOtel") {
                // only execute task if the opentelemetry agent does not exist. invoke the "clean" task to force
                onlyIf {
                    !agentFile.exists()
                }
                // this task could be the first in the graph, so "build/" may not yet exist. Let's be defensive
                doFirst {
                    buildDir.mkdirs()
                }
                // download the jar file
                doLast {
                    val download = { url: String, destFile: File ->
                        ant.invokeMethod(
                                "get",
                                mapOf("src" to url, "dest" to destFile)
                        )
                    }
                    logger.lifecycle("Downloading OpenTelemetry Agent")
                    download(openTelemetryAgentUrl, agentFile)
                }
            }

            //actually apply the plugin to the (sub-)project
            apply(plugin = "com.bmuschko.docker-remote-api")
            // configure the "dockerize" task
            val dockerTask: DockerBuildImage = tasks.create("dockerize", DockerBuildImage::class) {
                val dockerContextDir = project.projectDir
                dockerFile.set(file("$dockerContextDir/src/main/docker/Dockerfile"))
                images.add("${project.name}:${project.version}")
                images.add("${project.name}:latest")
                // specify platform with the -Dplatform flag:
                if (System.getProperty("platform") != null)
                    platform.set(System.getProperty("platform"))
                buildArgs.put("JAR", "build/libs/${project.name}.jar")
                buildArgs.put("OTEL_JAR", agentFile.relativeTo(dockerContextDir).path.replace("\\", "/"))
                inputDir.set(file(dockerContextDir))
            }
            dockerTask
                    .dependsOn(tasks.named(ShadowJavaPlugin.SHADOW_JAR_TASK_NAME))
                    .dependsOn(downloadOtel)
        }
    }
}
