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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  AtomicConstraint,
  Constraint,
  ConstraintTemplate,
  LogicalConstraint,
  Permission,
} from 'src/app/models/policy';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatListModule } from '@angular/material/list';
import { NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConstraintDialogComponent } from './dialog/constraint-dialog/constraint-dialog.component';
import { MatMenuModule } from '@angular/material/menu';
import { LogicalConstraintDialogComponent } from './dialog/logical-dialog/logical-dialog.component';
import { PolicyService } from 'src/app/services/policy.service';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ConstraintListComponent } from '../constraint/constraint.list.component';

@Component({
  selector: 'permission',
  templateUrl: './permission.component.html',
  standalone: true,
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    NgFor,
    FormsModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    MatTooltipModule,
    ConstraintListComponent,
  ],
})
export class PermissionComponent {
  @Input() permission!: Permission;
  actions: string[];

  @Output()
  permissionChange = new EventEmitter<void>();

  @Input() constraints: ConstraintTemplate[] = [];

  constructor(
    public dialog: MatDialog,
    policyService: PolicyService,
  ) {
    this.actions = policyService.actions();
    this.constraints = policyService.constraintTemplates();
  }

  onConstraintChange(_constraint: Constraint) {
    this.permissionChange.emit();
  }

  onConstraintEdit(constraint: Constraint) {
    this.editConstraint(constraint);
  }

  editConstraint(constraint: Constraint) {
    const onResult = (result: Constraint) => {
      const idx = this.permission.constraints.indexOf(constraint);
      if (result != null && idx != -1) {
        this.permission.constraints[idx] = result;
        this.permissionChange.emit();
      }
    };
    if (constraint instanceof AtomicConstraint) {
      const dialogRef = this.dialog.open(ConstraintDialogComponent, {
        data: constraint,
        minWidth: '400px',
      });

      dialogRef.afterClosed().subscribe(onResult);
    } else if (constraint instanceof LogicalConstraint) {
      const dialogRef = this.dialog.open(LogicalConstraintDialogComponent, {
        data: {
          constraint: constraint,
          constraints: this.constraints.filter((c) => !c.multiple),
        },
        minWidth: '600px',
      });
      dialogRef.afterClosed().subscribe(onResult);
    }
  }
}
