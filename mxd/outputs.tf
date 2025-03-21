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

output "bob-database-credentials" {
  value = local.bob-postgres
}

output "alice-database-credentials" {
  value = local.alice-postgres
}

output "bob-urls" {
  value = module.bob-connector.urls
}

output "alice-urls" {
  value = module.alice-connector.urls
}

# output "bob-node-ip" {
#   value = module.bob-connector.node-ip
# }

output "alice-node-ip" {
  value = module.alice-connector.node-ip
}
