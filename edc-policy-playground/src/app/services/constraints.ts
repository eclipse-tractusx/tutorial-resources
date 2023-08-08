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

import {
  AtomicConstraint,
  LogicalConstraint,
  Operator,
  Value,
  ValueKind,
} from '../models/policy';

const IN_FORCE = 'edc:inForceDate';
const DATE_EXPRESSION = 'edc:dateExpression';

const XSD_DATETIME = 'xsd:datetime';

export const bpnConstraint = () => {
  return new AtomicConstraint(
    'BusinessPartnerNumber',
    Operator.Eq,
    '<bpnNumber>',
  );
};

export const inForceFixedConstraint = () => {
  let constraint = new LogicalConstraint();
  constraint.constraints.push(
    new AtomicConstraint(
      IN_FORCE,
      Operator.Gte,
      new Value('2023-01-01T00:00:01Z', XSD_DATETIME),
      ValueKind.Value,
    ),
  );
  constraint.constraints.push(
    new AtomicConstraint(
      IN_FORCE,
      Operator.Lte,
      new Value('2024-01-01T00:00:01Z', XSD_DATETIME),
      ValueKind.Value,
    ),
  );
  return constraint;
};

export const inForceDurationConstraint = () => {
  let constraint = new LogicalConstraint();
  constraint.constraints.push(
    new AtomicConstraint(
      IN_FORCE,
      Operator.Gte,
      new Value('contractAgreement', DATE_EXPRESSION),
      ValueKind.Value,
    ),
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
  return credentials.map((c) => new AtomicConstraint(c, Operator.Eq, 'active'));
};
