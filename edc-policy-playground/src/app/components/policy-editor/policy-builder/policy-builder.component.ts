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
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSelectModule } from '@angular/material/select';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { Permission, PolicyConfiguration } from 'src/app/models/policy';
import { MatCardModule } from '@angular/material/card';
import { NgFor, NgIf } from '@angular/common';
import { PermissionComponent } from './permission/permission.component';
import { FormsModule } from '@angular/forms';
import { PolicyService } from 'src/app/services/policy.service';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'policy-builder',
  templateUrl: 'policy-builder.component.html',
  styleUrls: ['policy-builder.component.css'],
  standalone: true,
  imports: [
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatExpansionModule,
    MatSelectModule,
    MatListModule,
    MatDividerModule,
    NgFor,
    NgIf,
    MatCardModule,
    PermissionComponent,
    FormsModule,
    MatTooltipModule,
  ],
})
export class PolicyBuilderComponent {
  private _policyConfig!: PolicyConfiguration;
  @Input()
  get policyConfig() {
    return this._policyConfig;
  }

  set policyConfig(cfg: PolicyConfiguration) {
    this._policyConfig = cfg;
    if (cfg.policy.permissions.length > 0) {
      this.currentPermission = cfg.policy.permissions[0];
    } else {
      this.currentPermission = undefined;
    }
  }

  @Output() policyChange = new EventEmitter<PolicyConfiguration>();

  currentPermission?: Permission;

  selectedPermissions: Permission[] = [];

  panelOpenState = true;

  constructor(policyService: PolicyService) {}

  addPermission() {
    this.currentPermission = new Permission('New Permission');
    this.policyConfig.policy.permissions.push(this.currentPermission);
    this.policyChange.emit(this.policyConfig);
  }

  onPermissionChange() {
    this.policyChange.emit(this.policyConfig);
  }

  onPermissionSelectionChange(selection: Permission[]) {
    if (selection != null && selection.length > 0) {
      this.currentPermission = selection[0];
    }
  }
  removePermission() {
    if (this.currentPermission) {
      this.policyConfig.policy.permissions =
        this.policyConfig.policy.permissions.filter(
          (item) => item != this.currentPermission,
        );

      this.currentPermission = this.policyConfig.policy.permissions[0];

      this.policyChange.emit(this.policyConfig);
    }
  }
}
