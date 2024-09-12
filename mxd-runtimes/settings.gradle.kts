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

rootProject.name = "mxd-runtimes"

pluginManagement {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        mavenCentral()
    }
    // this version catalog is separate because it is supposed to be temporary. Once T-X provides a dedicated CatalogServer
    // runtime, it can be replaced with a dependency on that
    versionCatalogs {
        create("catalogLibs") {
            from(files("./gradle/catalogserver.versions.toml"))
        }
    }
}

include(":tx-identityhub")
include(":tx-identityhub-sts")
include(":tx-catalog-server")
include(":tx-sts")
include(":data-service-api")
include(":jwt-signer")
include(":e2e-test")
