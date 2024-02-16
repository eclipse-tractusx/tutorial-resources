#
#  Copyright (c) 2023 Contributors to the Eclipse Foundation
#
#  See the NOTICE file(s) distributed with this work for additional
#  information regarding copyright ownership.
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#  License for the specific language governing permissions and limitations
#  under the License.
#
#  SPDX-License-Identifier: Apache-2.0
#

variable "humanReadableName" {
  type        = string
  description = "Human readable name of the connector, NOT the BPN!!. Required."
}

variable "minio-api-port" {
  default = 9000
}

variable "minio-console-port" {
  default = 9001
}

variable "minio-username" {
  type        = string
  description = "minio-username"
}

variable "minio-password" {
  type        = string
  description = "minio-password"
}
