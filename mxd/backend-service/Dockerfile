#
#  Copyright (c) 2024 SAP SE
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#       SAP SE - initial API and implementation
#
#

FROM eclipse-temurin:21.0.2_13-jre-alpine

WORKDIR /app

COPY backend-service.jar /app/backend-service.jar

EXPOSE 8080

CMD java -jar backend-service.jar
