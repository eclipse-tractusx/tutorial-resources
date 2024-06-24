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

import { Component } from '@angular/core';
import { EditorComponent } from '../editor/editor.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { PolicyBuilderComponent } from './policy-builder/policy-builder.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { OutputKind, PolicyConfiguration } from 'src/app/models/policy';
import { FormsModule } from '@angular/forms';
import { NgFor } from '@angular/common';
import { FormatService } from 'src/app/services/format.service';
import { PolicyConfigurationStore } from 'src/app/stores/policy.store';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PolicyService } from 'src/app/services/policy.service';

@Component({
  selector: 'app-policy-editor',
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

  outputFormats: string[];
  policyConfig: PolicyConfiguration;

  currentFormat: OutputKind;

  configurations: PolicyConfiguration[] = [];

  constructor(
    public formatService: FormatService,
    public store: PolicyConfigurationStore,
    public policyService: PolicyService,
  ) {
    this.configurations = store.loadConfigurations();
    this.currentFormat = OutputKind.Plain;

    if (this.configurations.length == 0) {
      store.store(new PolicyConfiguration('Policy Template'));
    }
    this.policyConfig = this.configurations[0];
    this.outputFormats = policyService.supportedOutput();

    this.updateJsonText(this.policyConfig, this.currentFormat);
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
    this.updateJsonText(cfg, this.currentFormat);
  }
  onConfigChange(cfg: PolicyConfiguration) {
    this.updateJsonText(cfg, this.currentFormat);
  }

  updateJsonText(cfg: PolicyConfiguration, format: OutputKind) {
    const ld = this.formatService.toJsonLd(cfg, format);
    this.text = this.formatService.formatPolicy(ld);
  }
}
