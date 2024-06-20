/*******************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

import { Injectable } from '@angular/core';
import { Constraint, Permission, PolicyConfiguration } from '../models/policy';
import {
  bpnConstraint,
  bpnGroupConstraint,
  credentialsConstraints,
  inForceDurationConstraint,
  inForceFixedConstraint,
} from '../services/constraints';

@Injectable()
export class PolicyConfigurationStore {
  configurations: PolicyConfiguration[] = [];

  constructor() {
    this.configurations = [
      bpnPolicy(),
      bpnGroupPolicy(),
      inForceFixedPolicy(),
      inForceDurationPolicy(),
      ...credentialsPolicies(),
    ];
  }

  loadConfigurations(): PolicyConfiguration[] {
    return this.configurations;
  }

  store(config: PolicyConfiguration) {
    this.configurations.push(config);
  }
}

export function bpnPolicy(): PolicyConfiguration {
  return createPolicy('Business Partner Number Policy', bpnConstraint(), 'Bpn permission');
}

export function bpnGroupPolicy(): PolicyConfiguration {
  return createPolicy('Business Partner Group Policy', bpnGroupConstraint(), 'Business Partner Group permission');
}

function inForceFixedPolicy(): PolicyConfiguration {
  return createPolicy('InForce Policy (Fixed)', inForceFixedConstraint());
}

function inForceDurationPolicy(): PolicyConfiguration {
  return createPolicy('InForce Policy (Duration)', inForceDurationConstraint());
}

function credentialsPolicies(): PolicyConfiguration[] {
  return credentialsConstraints().map(c => {
    return createPolicy(`${c.get_label()}  Policy`, c);
  });
}

function createPolicy(name: string, constraint: Constraint, policyName = 'Permission'): PolicyConfiguration {
  const config = new PolicyConfiguration(name);
  const permission = new Permission();
  permission.name = policyName;
  permission.constraints.push(constraint);
  config.policy.permissions.push(permission);
  return config;
}
