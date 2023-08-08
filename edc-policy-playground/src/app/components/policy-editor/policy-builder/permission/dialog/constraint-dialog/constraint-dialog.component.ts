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

import { NgFor } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { AtomicConstraint, Operator } from 'src/app/models/policy';
import { AtomicConstraintComponent } from '../../../constraint/atomic.constraint.component';

@Component({
  selector: 'constraint-dialog',
  templateUrl: './constraint-dialog.component.html',
  standalone: true,
  imports: [
    MatDialogModule,
    MatFormFieldModule,
    FormsModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    NgFor,
    AtomicConstraintComponent
  ],
})
export class ConstraintDialogComponent {
  operators: string[];
  constraint: AtomicConstraint;
  constructor(
    public dialogRef: MatDialogRef<ConstraintDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AtomicConstraint,
  ) {
    this.constraint = data.clone();

    this.operators = Object.values(Operator).filter(
      (value) => typeof value === 'string',
    ) as string[];
  }

  cancel() {
    this.dialogRef.close();
  }
}
