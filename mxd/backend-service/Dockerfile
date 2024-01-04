#*******************************************************************************
# * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
# * Copyright (c) 2023 Contributors to the Eclipse Foundation
# *
# * See the NOTICE file(s) distributed with this work for additional
# * information regarding copyright ownership.
# *
# * This program and the accompanying materials are made available under the
# * terms of the Apache License, Version 2.0 which is available at
# * https://www.apache.org/licenses/LICENSE-2.0.
# *
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# * License for the specific language governing permissions and limitations
# * under the License.
# *
# * SPDX-License-Identifier: Apache-2.0
# *    Contributors:Ravinder Kumar
# *    Backend-API and implementation
# * 
# ******************************************************************************/

# Use the eclipse-temurin:17.0.8.1_1-jre-alpine as the base image
FROM eclipse-temurin:17.0.8.1_1-jre-alpine

# Set the working directory
WORKDIR /app

# Copy your JAR file from the local build context to the image
COPY backend-service.jar /app/backend-service.jar

# Expose the port your application is running on
EXPOSE 8080

# Start your Java application
CMD java -jar backend-service.jar
