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
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import {
  Constraint,
  ConstraintContainer,
  ConstraintTemplate,
} from 'src/app/models/policy';
import { PolicyService } from 'src/app/services/policy.service';

@Component({
  selector: 'constraint-list',
  templateUrl: './constraint.list.component.html',
  standalone: true,
  imports: [
    MatListModule,
    MatMenuModule,
    MatIconModule,
    NgFor,
    MatButtonModule,
  ],
})
export class ConstraintListComponent {
  @Input() container!: ConstraintContainer;

  @Input() constraints: ConstraintTemplate[] = [];

  @Output()
  onConstraintRemove = new EventEmitter<Constraint>();

  @Output()
  onConstraintAdd = new EventEmitter<Constraint>();

  @Output()
  onConstraintEdit = new EventEmitter<Constraint>();

  constructor(policyService: PolicyService) {
  }

  addConstraint(constraint: Constraint) {
    this.container.constraints.push(constraint);
    this.onConstraintAdd.emit(constraint);
    this.editConstraint(constraint);
  }

  removeConstraint(constraint: Constraint) {
    this.container.constraints = this.container.constraints.filter(
      (item) => item != constraint,
    );
    this.onConstraintRemove.emit(constraint);
  }

  editConstraint(constraint: Constraint) {
    this.onConstraintEdit.emit(constraint);
  }

}
