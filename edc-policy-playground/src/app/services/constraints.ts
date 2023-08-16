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

import { AtomicConstraint, LogicalConstraint, Operator, Value, ValueKind } from '../models/policy';

const IN_FORCE = 'edc:inForceDate';
const DATE_EXPRESSION = 'edc:dateExpression';

const XSD_DATETIME = 'xsd:datetime';

export const bpnConstraint = () => {
  return new AtomicConstraint('BusinessPartnerNumber', Operator.Eq, '<bpnNumber>');
};

export const bpnGroupConstraint = () => {
  return new AtomicConstraint('BusinessPartnerGroup', Operator.In, '<group>');
};

export const inForceFixedConstraint = () => {
  const constraint = new LogicalConstraint();
  constraint.constraints.push(
    new AtomicConstraint(IN_FORCE, Operator.Gte, new Value('2023-01-01T00:00:01Z', XSD_DATETIME), ValueKind.Value),
  );
  constraint.constraints.push(
    new AtomicConstraint(IN_FORCE, Operator.Lte, new Value('2024-01-01T00:00:01Z', XSD_DATETIME), ValueKind.Value),
  );
  return constraint;
};

export const inForceDurationConstraint = () => {
  const constraint = new LogicalConstraint();
  constraint.constraints.push(
    new AtomicConstraint(IN_FORCE, Operator.Gte, new Value('contractAgreement', DATE_EXPRESSION), ValueKind.Value),
  );
  constraint.constraints.push(
    new AtomicConstraint(
      IN_FORCE,
      Operator.Lte,
      new Value('contractAgreement + 100d', DATE_EXPRESSION),
      ValueKind.Value,
    ),
  );
  return constraint;
};

const credentials = [
  'Membership',
  'Dismantler',
  'FrameworkAgreement.pcf',
  'FrameworkAgreement.sustainability',
  'FrameworkAgreement.quality',
  'FrameworkAgreement.traceability',
  'FrameworkAgreement.behavioraltwin',
  'BPN',
];

export const credentialsConstraints = () => {
  return credentials.map(c => new AtomicConstraint(c, Operator.Eq, 'active'));
};
