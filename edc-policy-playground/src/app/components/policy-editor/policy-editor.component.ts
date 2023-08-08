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

import { Component } from '@angular/core';
import { EditorComponent } from '../editor/editor.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { PolicyBuilderComponent } from './policy-builder/policy-builder.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { PolicyConfiguration } from 'src/app/models/policy';
import { FormsModule } from '@angular/forms';
import { NgFor } from '@angular/common';
import { FormatService } from 'src/app/services/format.service';
import { PolicyConfigurationStore } from 'src/app/stores/policy.store';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'policy-editor',
  templateUrl: './policy-editor.component.html',
  styleUrls: ['./policy-editor.component.css'],
  standalone: true,
  imports: [
    EditorComponent,
    MatGridListModule,
    MatCardModule,
    PolicyBuilderComponent,
    MatFormFieldModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule,
    FormsModule,
    NgFor,
    MatTooltipModule,
  ],
})
export class PolicyEditorComponent {
  text!: string;

  policyConfig: PolicyConfiguration;

  configurations: PolicyConfiguration[] = [];

  constructor(
    public formatService: FormatService,
    public store: PolicyConfigurationStore,
  ) {
    this.configurations = store.loadConfigurations();

    if (this.configurations.length == 0) {
      store.store(new PolicyConfiguration('Policy Template'));
    }
    this.policyConfig = this.configurations[0];

    this.updateJsonText(this.policyConfig);
  }

  addPolicy(): void {
    this.policyConfig = new PolicyConfiguration('New Policy');
    this.store.store(this.policyConfig);
  }

  copyPolicy() {
    this.policyConfig = this.policyConfig.clone();
    this.store.store(this.policyConfig);
  }

  onConfigSelectionChange(cfg: PolicyConfiguration) {
    this.policyConfig = cfg;
    this.updateJsonText(cfg);
  }
  onConfigChange(cfg: PolicyConfiguration) {
    this.updateJsonText(cfg);
  }

  updateJsonText(cfg: PolicyConfiguration) {
    let ld = this.formatService.toJsonLd(cfg);
    this.text = this.formatService.formatPolicy(ld);
  }
}
