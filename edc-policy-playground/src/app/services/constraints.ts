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

import { AtomicConstraint, LeftOperand, LogicalConstraint, Operator, Value, ValueKind } from '../models/policy';

const IN_FORCE = 'inForceDate';
const DATE_EXPRESSION = 'dateExpression';

const TX_BASE_CONTEXT = 'https://w3id.org/tractusx/edc/v0.0.1';
const TX_POLICY_CONTEXT = 'https://w3id.org/tractusx/policy/v1.0.0';

const XSD_PREFIX = 'xsd';
const XSD_DATETIME = XSD_PREFIX + ':datetime';

export const bpnConstraint = () => {
  return new AtomicConstraint(
    new LeftOperand('BusinessPartnerNumber'),
    Operator.Eq,
    '<bpnNumber>',
    ValueKind.String,
  ).with_context(TX_BASE_CONTEXT);
};

export const bpnGroupConstraint = () => {
  return new AtomicConstraint(new LeftOperand('BusinessPartnerGroup'), Operator.In, '<group>').with_context(
    TX_BASE_CONTEXT,
  );
};

export const inForceFixedConstraint = () => {
  const constraint = new LogicalConstraint();
  constraint.constraints.push(
    new AtomicConstraint(
      new LeftOperand(IN_FORCE),
      Operator.Gte,
      new Value('2023-01-01T00:00:01Z', XSD_DATETIME),
      ValueKind.Value,
    ).with_prefix(XSD_PREFIX),
  );
  constraint.constraints.push(
    new AtomicConstraint(
      new LeftOperand(IN_FORCE),
      Operator.Lte,
      new Value('2024-01-01T00:00:01Z', XSD_DATETIME),
      ValueKind.Value,
    ).with_prefix(XSD_PREFIX),
  );
  return constraint;
};

export const inForceDurationConstraint = () => {
  const constraint = new LogicalConstraint();
  constraint.constraints.push(
    new AtomicConstraint(
      new LeftOperand(IN_FORCE),
      Operator.Gte,
      new Value('contractAgreement+0s', DATE_EXPRESSION),
      ValueKind.Value,
    ),
  );
  constraint.constraints.push(
    new AtomicConstraint(
      new LeftOperand(IN_FORCE),
      Operator.Lte,
      new Value('contractAgreement+100d', DATE_EXPRESSION),
      ValueKind.Value,
    ),
  );
  return constraint;
};

const fremeworkAgreements = [
  'Pcf',
  'Traceability',
  'Quality',
  'CircularEconomy',
  'DemandCapacity',
  'Puris',
  'BusinessPartner',
  'BehavioralTwin',
];

const frameworkCredentials = fremeworkAgreements.map(frame => {
  return { name: 'FrameworkAgreement', value: `${frame}:<version>`, label: frame };
});

const baseCredentials = [
  { name: 'Membership', value: 'active', label: 'Membership' },
  { name: 'Dismantler', value: 'active', label: 'Dismantler' },
];

const credentials = [...baseCredentials, ...frameworkCredentials];

export const credentialsConstraints = () => {
  return credentials.map(c =>
    new AtomicConstraint(new LeftOperand(c.name), Operator.Eq, c.value)
      .with_context(TX_POLICY_CONTEXT)
      .with_label(c.label),
  );
};
