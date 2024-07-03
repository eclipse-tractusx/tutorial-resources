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

rootProject.name = "custom-tractusx-edc"

include(":custom-edc-controlplane-postgresql-hashicorp-vault")
include(":custom-edc-dataplane-hashicorp-vault")


dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}
