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

import { NgFor, NgSwitch, NgSwitchCase } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { AtomicConstraint, Value, ValueKind } from 'src/app/models/policy';
import { PolicyService } from 'src/app/services/policy.service';
import { ValueExpressionComponent } from './value.expression.component';

@Component({
  selector: 'app-atomic-constraint',
  templateUrl: './atomic.constraint.component.html',
  styleUrls: [],
  standalone: true,
  imports: [
    MatListModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    FormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    NgFor,
    NgSwitch,
    NgSwitchCase,
    ValueExpressionComponent,
  ],
})
export class AtomicConstraintComponent {
  operators: string[];
  types: string[];
  @Input() constraint!: AtomicConstraint;
  @Input() disabled = false;

  constructor(policyService: PolicyService) {
    this.operators = policyService.operators();
    this.types = policyService.valueKinds();
  }

  onKindChange(kind: ValueKind) {
    switch (kind) {
      case ValueKind.Number: {
        this.constraint.rightOperand = 0;
        break;
      }
      case ValueKind.String: {
        this.constraint.rightOperand = '';
        break;
      }
      case ValueKind.Value: {
        this.constraint.rightOperand = new Value('', '');
      }
    }
  }
}
