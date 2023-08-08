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

import { NgFor, NgIf } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import {
  AtomicConstraint,
  Constraint,
  ConstraintTemplate,
  LogicalConstraint,
} from 'src/app/models/policy';
import { PolicyService } from 'src/app/services/policy.service';
import { ConstraintListComponent } from '../../../constraint/constraint.list.component';
import { AtomicConstraintComponent } from '../../../constraint/atomic.constraint.component';

@Component({
  selector: 'logical-dialog',
  templateUrl: './logical-dialog.component.html',
  standalone: true,
  imports: [
    MatDialogModule,
    MatFormFieldModule,
    FormsModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    NgFor,
    MatIconModule,
    NgIf,
    MatCardModule,
    ConstraintListComponent,
    AtomicConstraintComponent,
  ],
})
export class LogicalConstraintDialogComponent {
  logicalOperators: string[];
  operators: string[];
  constraint: LogicalConstraint;
  currentConstraint: AtomicConstraint = new AtomicConstraint();
  constraints: ConstraintTemplate[];

  constructor(
    policyService: PolicyService,
    public dialogRef: MatDialogRef<LogicalConstraintDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      constraint: LogicalConstraint;
      constraints: ConstraintTemplate[];
    },
  ) {
    this.constraint = data.constraint.clone();
    this.logicalOperators = policyService.logicalOperators();
    this.operators = policyService.operators();
    this.constraints = data.constraints;

    let constraints = this.constraint.constraints
      .filter((c) => c instanceof AtomicConstraint)
      .map((c) => c as AtomicConstraint);

    if (constraints.length > 0) {
      this.currentConstraint = constraints[0];
    }
  }

  onConstraintAdd(constraint: Constraint) {
    if (constraint instanceof AtomicConstraint) {
      this.currentConstraint = constraint;
    }
  }

  onConstraintRemove(constraint: Constraint) {
    if (constraint instanceof AtomicConstraint) {
      if (this.constraint.constraints.length == 0) {
        this.currentConstraint = new AtomicConstraint();
      }
    }
  }

  onConstraintEdit(constraint: Constraint) {
    this.editConstraint(constraint);
  }

  isEditorDisabled() {
    return this.constraint.constraints.length == 0;
  }

  editConstraint(constraint: Constraint) {
    if (constraint instanceof AtomicConstraint) {
      this.currentConstraint = constraint;
    }
  }
  cancel() {
    this.dialogRef.close();
  }
}
