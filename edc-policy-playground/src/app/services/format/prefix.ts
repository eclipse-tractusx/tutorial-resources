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
    odrl: 'http://www.w3.org/ns/odrl/2/',
  },
  '@type': 'PolicyDefinitionRequest',
  '@id': '{{POLICY_ID}}',
  policy: {},
};

const policyHeader = {
  '@type': 'odrl:Set',
};

export const emptyPolicy = Object.assign(policyRequestTemplate, {
  policy: {
    ...policyHeader,
    'odrl:permission': [],
  },
});

export class PrefixFormatter implements JsonLdFormatter {
  toJsonLd(policyConfig: PolicyConfiguration): object {
    const permission = policyConfig.policy.permissions.map(this.mapPermission.bind(this));

    return Object.assign(emptyPolicy, {
      policy: { ...policyHeader, 'odrl:permission': permission },
    });
  }

  mapPermission(permission: Permission): object {
    return {
      'odrl:action': permission.action.toString(),
      'odrl:constraint': permission.constraints.map(this.mapConstraint.bind(this)),
    };
  }

  mapConstraint(constraint: Constraint): object {
    if (constraint instanceof AtomicConstraint) {
      return {
        'odrl:leftOperand': constraint.leftOperand,
        'odrl:operator': {
          '@id': 'odrl:' + constraint.operator.toString(),
        },
        'odlr:rightOperand': this.mapRightOperand(constraint),
      };
    } else if (constraint instanceof LogicalConstraint) {
      const obj: any = {
        '@type': 'odrl:LogicalConstraint',
      };
      obj['odlr:' + constraint.operator.toString().toLowerCase()] = constraint.constraints.map(
        this.mapConstraint.bind(this),
      );
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
