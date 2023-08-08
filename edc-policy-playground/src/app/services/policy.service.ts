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
  Action,
  AtomicConstraint,
  ConstraintTemplate,
  LogicalConstraint,
  LogicalOperator,
  Operator,
  Value,
  ValueKind,
} from '../models/policy';
import {
  bpnConstraint,
  credentialsConstraints,
  inForceDurationConstraint,
  inForceFixedConstraint,
} from './constraints';

@Injectable({ providedIn: 'root' })
export class PolicyService {
  constructor() {}

  logicalOperators(): string[] {
    return this.values(LogicalOperator);
  }

  operators(): string[] {
    return this.values(Operator);
  }

  valueKinds(): string[] {
    return this.values(ValueKind);
  }

  actions(): string[] {
    return this.values(Action);
  }

  private values(val: object): string[] {
    return Object.values(val).filter(
      (value) => typeof value === 'string',
    ) as string[];
  }

  constraintTemplates(): ConstraintTemplate[] {
    let credentialConstraints = credentialsConstraints();

    let credentialConstraintsTemplate = credentialConstraints.map((c) => {
      return {
        name: c.leftOperand + ' credential',
        multiple: false,
        factory: () => c,
      };
    });
    return [
      {
        name: 'Atomic Constraint',
        multiple: false,
        factory: () => new AtomicConstraint('<field>', Operator.Eq, '<value>'),
      },
      {
        name: 'Logical Constraint',
        multiple: true,
        factory: () => new LogicalConstraint(),
      },
      {
        name: 'BPN Constraint',
        multiple: false,
        factory: bpnConstraint,
      },
      {
        name: 'In Force Constraint (Duration)',
        multiple: true,
        factory: inForceDurationConstraint,
      },
      {
        name: 'In Force Constraint (Fixed)',
        multiple: true,
        factory: inForceFixedConstraint,
      },
      ...credentialConstraintsTemplate
    ];
  }
}
