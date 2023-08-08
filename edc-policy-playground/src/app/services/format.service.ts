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
import {
  AtomicConstraint,
  Constraint,
  LogicalConstraint,
  Permission,
  PolicyConfiguration,
  Value,
} from '../models/policy';

export const policyRequestTemplate = {
  '@context': {
    edc: 'https://w3id.org/edc/v0.0.1/ns/',
  },
  '@type': 'PolicyDefinitionRequest',
  '@id': '{{POLICY_ID}}',
  policy: {},
};

export const emptyPolicy = Object.assign(policyRequestTemplate, {
  policy: {
    '@type': 'Set',
    '@context': 'http://www.w3.org/ns/odrl.jsonld',
    permission: [],
  },
});

@Injectable({ providedIn: 'root' })
export class FormatService {
  constructor() {}

  toJsonLd(policyConfig: PolicyConfiguration): any {
    let permissions = policyConfig.policy.permissions.map(this.mapPermission.bind(this));

    return Object.assign(emptyPolicy, { policy: { permissions } });
  }

  formatPolicy(policy: any) {
    return JSON.stringify(policy, null, 2);
  }

  mapPermission(permission: Permission): object {
    return {
      action: permission.action.toString(),
      constraint: permission.constraints.map(this.mapConstraint.bind(this)),
    };
  }

  mapConstraint(constraint: Constraint): object {
    if (constraint instanceof AtomicConstraint) {
      return {
        leftOperand: constraint.leftOperand,
        operator: constraint.operator.toString(),
        rightOperand: this.mapRightOperand(constraint),
      };
    } else if (constraint instanceof LogicalConstraint) {
      let obj: any = {
        '@type': 'LogicalConstraint',
      };
      obj[constraint.operator.toString().toLowerCase()] =
        constraint.constraints.map(this.mapConstraint.bind(this));
      return obj;
    }

    return {};
  }

  mapRightOperand(
    constraint: AtomicConstraint,
  ): string | number | object | undefined {
    if (constraint.rightOperand instanceof Value) {
      return {
        '@value': constraint.rightOperand.value,
        '@type': constraint.rightOperand.ty,
      };
    } else {
      return constraint.rightOperand;
    }
  }
}
