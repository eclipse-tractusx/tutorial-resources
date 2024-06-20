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

/*eslint-disable @typescript-eslint/no-explicit-any*/

import {
  AtomicConstraint,
  Constraint,
  LogicalConstraint,
  Permission,
  PolicyConfiguration,
  Value,
} from 'src/app/models/policy';
import { JsonLdFormatter } from '../format.service';
import { PolicyService } from '../policy.service';

const CONTEXTS = ['http://www.w3.org/ns/odrl.jsonld'];
const NESTED_CONTEXT = { '@vocab': 'https://w3id.org/edc/v0.0.1/ns/' };

export const policyRequestTemplate = {
  '@context': {},
  '@type': 'PolicyDefinition',
  '@id': '{{POLICY_ID}}',
  policy: {},
};

const policyHeader = {
  '@type': 'Set',
};

export const emptyPolicy = Object.assign(policyRequestTemplate, {
  policy: {
    ...policyHeader,
    permission: [],
  },
});

export class PlainFormatter implements JsonLdFormatter {
  policyService: PolicyService;
  constructor(policyService: PolicyService) {
    this.policyService = policyService;
  }

  toJsonLd(policyConfig: PolicyConfiguration): object {
    const permission = policyConfig.policy.permissions.map(this.mapPermission.bind(this));
    const additionalNamespaces = this.policyService.namespacesFor(policyConfig);
    const additionalContexts = this.policyService.contextsFor(policyConfig);

    const context = [...CONTEXTS, ...additionalContexts, Object.assign(NESTED_CONTEXT, additionalNamespaces)];

    return Object.assign(emptyPolicy, {
      '@context': context,
      policy: { ...policyHeader, permission },
    });
  }

  mapPermission(permission: Permission): object {
    return {
      action: permission.action.toString(),
      constraint: permission.constraints.map(this.mapConstraint.bind(this)),
    };
  }

  mapConstraint(constraint: Constraint): object {
    if (constraint instanceof AtomicConstraint) {
      const leftOperand = constraint.leftOperand.toString();
      return {
        leftOperand,
        operator: constraint.operator.toString(),
        rightOperand: this.mapRightOperand(constraint),
      };
    } else if (constraint instanceof LogicalConstraint) {
      const obj: any = {};
      obj[constraint.operator.toString().toLowerCase()] = constraint.constraints.map(this.mapConstraint.bind(this));
      return obj;
    }

    return {};
  }

  mapRightOperand(constraint: AtomicConstraint): string | number | object | undefined {
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
