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

import { NgFor } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { Constraint, ConstraintContainer, ConstraintTemplate } from 'src/app/models/policy';

@Component({
  selector: 'app-constraint-list',
  templateUrl: './constraint.list.component.html',
  standalone: true,
  imports: [MatListModule, MatMenuModule, MatIconModule, NgFor, MatButtonModule],
})
export class ConstraintListComponent {
  @Input() container!: ConstraintContainer;

  @Input() constraints: ConstraintTemplate[] = [];

  @Output()
  constraintRemove = new EventEmitter<Constraint>();

  @Output()
  constraintAdd = new EventEmitter<Constraint>();

  @Output()
  constraintEdit = new EventEmitter<Constraint>();

  addConstraint(constraint: Constraint) {
    this.container.constraints.push(constraint);
    this.constraintAdd.emit(constraint);
    this.editConstraint(constraint);
  }

  removeConstraint(constraint: Constraint) {
    this.container.constraints = this.container.constraints.filter(item => item != constraint);
    this.constraintRemove.emit(constraint);
  }

  editConstraint(constraint: Constraint) {
    this.constraintEdit.emit(constraint);
  }
}
