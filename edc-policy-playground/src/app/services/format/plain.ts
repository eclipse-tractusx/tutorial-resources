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

export const policyRequestTemplate = {
  '@context': {
    edc: 'https://w3id.org/edc/v0.0.1/ns/',
  },
  '@type': 'PolicyDefinitionRequest',
  '@id': '{{POLICY_ID}}',
  policy: {},
};

const policyHeader = {
  '@type': 'Set',
  '@context': 'http://www.w3.org/ns/odrl.jsonld',
};

export const emptyPolicy = Object.assign(policyRequestTemplate, {
  policy: {
    ...policyHeader,
    permission: [],
  },
});

export class PlainFormatter implements JsonLdFormatter {
  toJsonLd(policyConfig: PolicyConfiguration): object {
    const permission = policyConfig.policy.permissions.map(this.mapPermission.bind(this));

    return Object.assign(emptyPolicy, {
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
      return {
        leftOperand: {
          '@value': constraint.leftOperand,
        },
        operator: constraint.operator.toString(),
        rightOperand: this.mapRightOperand(constraint),
      };
    } else if (constraint instanceof LogicalConstraint) {
      const obj: any = {
        '@type': 'LogicalConstraint',
      };
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
