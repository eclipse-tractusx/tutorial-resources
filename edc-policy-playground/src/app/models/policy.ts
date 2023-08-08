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

export class Permission {
  name?: String;
  action: Action = Action.Use;
  constraints: Constraint[] = [];

  constructor(name?: string) {
    this.name = name;
  }

  clone(): Permission {
    let perm = new Permission();
    perm.action = this.action;
    perm.name = this.name;
    perm.constraints = this.constraints.map((c) => c.clone());
    return perm;
  }

  toString(): string {
    return `(${this.action.toString()}) constraints: [ ${this.constraints
      .map((c) => c.toString())
      .join(',')} ]`;
  }
}

export class LogicalConstraint implements Constraint {
  operator: LogicalOperator;
  constraints: Constraint[] = [];

  constructor(operator: LogicalOperator = LogicalOperator.And) {
    this.operator = operator;
  }

  clone(): LogicalConstraint {
    let cloned = new LogicalConstraint();
    cloned.operator = this.operator;
    cloned.constraints = this.constraints.map((c) => c.clone());
    return cloned;
  }
  toString() {
    return `${this.operator} constraint: [ ${this.constraints
      .map((c) => c.toString())
      .join(',')} ]`;
  }
}

export class AtomicConstraint implements Constraint {
  leftOperand?: string;
  operator: Operator;
  rightOperand?: string | number | Value;
  kind: ValueKind;

  constructor();
  constructor(leftOperand: string);
  constructor(
    leftOperand: string,
    operator: Operator,
    rightOperator: RightOperand,
  );
  constructor(
    leftOperand: string,
    operator: Operator,
    rightOperator: RightOperand,
    kind: ValueKind,
  );
  constructor(
    leftOperand?: string,
    operator: Operator = Operator.Eq,
    rightOperand?: string | number | Value,
    kind: ValueKind = ValueKind.String,
  ) {
    this.kind = kind;
    this.operator = operator;
    this.leftOperand = leftOperand;
    this.rightOperand = rightOperand;
  }
  clone(): AtomicConstraint {
    let cloned = new AtomicConstraint();
    cloned.kind = this.kind;
    cloned.leftOperand = this.leftOperand;
    cloned.operator = this.operator;
    cloned.rightOperand = this.rightOperand;
    return cloned;
  }

  toString() {
    return `Constraint ${this.leftOperand} ${this.operator.toString()} ${
      this.rightOperand != null ? this.rightOperand.toString() : ''
    }`;
  }
}

export enum ValueKind {
  String = "String",
  Number = "Number",
  Value = "Value",
}

export class Value {
  value?: string | number;
  ty?: string;

  constructor(value?: string | number, ty?: string) {
    this.value = value;
    this.ty = ty;
  }

  toString() {
    return `${this.value}`;
  }
}

export enum LogicalOperator {
  And = 'And',
  Or = 'Or',
}

export interface Constraint {
  clone(): Constraint;
}

export enum Action {
  Use = 'use',
}

export enum Operator {
  Eq = 'eq',
  Gte = 'gte',
  Lte = 'lte',
}

export interface ConstraintContainer {
  constraints: Constraint[];
}

export class Policy {
  permissions: Permission[] = [];

  clone(): Policy {
    let policy = new Policy();
    policy.permissions = this.permissions.map((perm) => perm.clone());
    return policy;
  }
}

export class PolicyConfiguration {
  name: string;
  description?: string;
  policy: Policy;

  constructor(name: string) {
    this.name = name;
    this.policy = new Policy();
  }

  clone(): PolicyConfiguration {
    let config = new PolicyConfiguration(this.name + ' (Copy)');
    config.policy = this.policy.clone();

    return config;
  }
}

export interface ConstraintTemplate {
  name: string;
  multiple: boolean;
  factory: () => Constraint;
}

export type RightOperand = string | number | Value;
