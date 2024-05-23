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

import { Injectable } from '@angular/core';
import { OutputKind, PolicyConfiguration } from '../models/policy';
import { PlainFormatter } from './format/plain';
import { PolicyService } from './policy.service';

@Injectable({ providedIn: 'root' })
export class FormatService {
  formatters: Map<OutputKind, JsonLdFormatter> = new Map();
  constructor(public policyService: PolicyService) {
    this.formatters.set(OutputKind.Plain, new PlainFormatter(policyService));
  }

  toJsonLd(policyConfig: PolicyConfiguration, format: OutputKind = OutputKind.Prefixed): object {
    const formatter = this.formatters.get(format);
    if (formatter != null) {
      return formatter.toJsonLd(policyConfig);
    } else {
      throw new Error('Formatter not found');
    }
  }

  formatPolicy(policy: object) {
    return JSON.stringify(policy, null, 2);
  }
}

export interface JsonLdFormatter {
  toJsonLd(policyConfig: PolicyConfiguration): object;
}
