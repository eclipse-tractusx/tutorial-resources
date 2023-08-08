/*
 *  Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */


import { Injectable } from '@angular/core';
import { Constraint, Permission, PolicyConfiguration } from '../models/policy';
import {
  bpnConstraint,
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

function bpnPolicy(): PolicyConfiguration {
  return createPolicy('Bpn Policy', bpnConstraint(), 'Bpn permission');
}

function inForceFixedPolicy(): PolicyConfiguration {
  return createPolicy('InForce Policy (Fixed)', inForceFixedConstraint());
}

function inForceDurationPolicy(): PolicyConfiguration {
  return createPolicy('InForce Policy (Duration)', inForceDurationConstraint());
}

function credentialsPolicies(): PolicyConfiguration[] {
  return credentialsConstraints().map((c) => {
    let splitted = c.leftOperand?.split('.');

    if (!splitted || splitted.length == 1) {
        return createPolicy(`${c.leftOperand}  Policy`, c);
    } else {
        return createPolicy(`${splitted[0]} Policy (${splitted[1]})`, c);
    }
  });
}

function createPolicy(
  name: string,
  constraint: Constraint,
  policyName: String = 'Permission',
): PolicyConfiguration {
  let config = new PolicyConfiguration(name);
  let permission = new Permission();
  permission.name = policyName;
  permission.constraints.push(constraint);
  config.policy.permissions.push(permission);
  return config;
}
