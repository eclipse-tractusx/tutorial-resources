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

export class Permission {
  name?: string;
  action: Action = Action.Use;
  constraints: Constraint[] = [];

  constructor(name?: string) {
    this.name = name;
  }

  clone(): Permission {
    const perm = new Permission();
    perm.action = this.action;
    perm.name = this.name;
    perm.constraints = this.constraints.map(c => c.clone());
    return perm;
  }

  toString(): string {
    return `(${this.action.toString()}) constraints: [ ${this.constraints.map(c => c.toString()).join(',')} ]`;
  }
}

export class LogicalConstraint implements Constraint {
  operator: LogicalOperator;
  constraints: Constraint[] = [];

  constructor(operator: LogicalOperator = LogicalOperator.And) {
    this.operator = operator;
  }

  clone(): LogicalConstraint {
    const cloned = new LogicalConstraint();
    cloned.operator = this.operator;
    cloned.constraints = this.constraints.map(c => c.clone());
    return cloned;
  }

  get_prefixes(): string[] {
    return this.constraints.flatMap(c => c.get_prefixes());
  }

  get_contexts(): string[] {
    return this.constraints.flatMap(c => c.get_contexts());
  }

  toString() {
    return `${this.operator} constraint: [ ${this.constraints.map(c => c.toString()).join(',')} ]`;
  }
}

export class LeftOperand {
  value: string;

  constructor(value: string) {
    this.value = value;
  }

  toString() {
    return `${this.value}`;
  }
}

export class AtomicConstraint implements Constraint {
  leftOperand!: LeftOperand;
  operator: Operator;
  rightOperand?: string | number | Value;
  kind: ValueKind;
  contexts: string[] = [];
  prefixes: string[] = [];
  label?: string;
  constructor();
  constructor(leftOperand: LeftOperand);
  constructor(leftOperand: LeftOperand, operator: Operator, rightOperator: RightOperand);
  constructor(leftOperand: LeftOperand, operator: Operator, rightOperator: RightOperand, kind: ValueKind);
  constructor(
    leftOperand?: LeftOperand,
    operator: Operator = Operator.Eq,
    rightOperand?: string | number | Value,
    kind: ValueKind = ValueKind.String,
  ) {
    this.kind = kind;
    this.operator = operator;
    this.leftOperand = leftOperand != null ? leftOperand : new LeftOperand('');
    this.rightOperand = rightOperand;
  }

  clone(): AtomicConstraint {
    const cloned = new AtomicConstraint();
    cloned.kind = this.kind;
    cloned.leftOperand = this.leftOperand;
    cloned.operator = this.operator;
    cloned.rightOperand = this.rightOperand;
    cloned.contexts = this.contexts;
    cloned.label = this.label;
    cloned.prefixes = this.prefixes;
    return cloned;
  }

  get_prefixes(): string[] {
    return this.prefixes;
  }

  get_contexts(): string[] {
    return this.contexts;
  }

  get_label(): string {
    return this.label != null ? this.label : this.leftOperand.value;
  }

  with_context(ctx: string): AtomicConstraint {
    this.contexts.push(ctx);
    return this;
  }

  with_prefix(prefix: string): AtomicConstraint {
    this.prefixes.push(prefix);
    return this;
  }

  with_label(label: string): AtomicConstraint {
    this.label = label;
    return this;
  }

  toString() {
    return `Constraint ${this.leftOperand} ${this.operator.toString()} ${
      this.rightOperand != null ? this.rightOperand.toString() : ''
    }`;
  }
}

export enum ValueKind {
  String = 'String',
  Number = 'Number',
  Value = 'Value',
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

  get_prefixes(): string[];
  get_contexts(): string[];
}

export enum Action {
  Use = 'use',
}

export enum Operator {
  Eq = 'eq',
  Neq = 'neq',
  Gte = 'gteq',
  Lte = 'lteq',
  In = 'isPartOf',
  AnyOf = 'isAnyOf',
  AllOf = 'isAllOf',
  NoneOf = 'isNoneOf',
}

export interface ConstraintContainer {
  constraints: Constraint[];
}

export class Policy {
  permissions: Permission[] = [];

  clone(): Policy {
    const policy = new Policy();
    policy.permissions = this.permissions.map(perm => perm.clone());
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
    const config = new PolicyConfiguration(this.name + ' (Copy)');
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

export enum OutputKind {
  Prefixed = 'Prefixed',
  Plain = 'Plain',
}
