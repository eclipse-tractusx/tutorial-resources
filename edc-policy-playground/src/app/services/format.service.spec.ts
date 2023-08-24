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

import { OutputKind, PolicyConfiguration } from '../models/policy';
import { bpnGroupPolicy, bpnPolicy } from '../stores/policy.store';
import { FormatService } from './format.service';
import { PolicyService } from './policy.service';
import bpnJsonData from '../../../fixtures/policies/bpn.json';
import bpnGroupJsonData from '../../../fixtures/policies/bpnGroup.json';

describe('FormatService', () => {
  let formatService: FormatService;

  const toJsonLd = (policy: PolicyConfiguration, kind: OutputKind) => {
    const jsonLd = formatService.toJsonLd(policy, kind);
    return formatService.formatPolicy(jsonLd);
  };
  beforeEach(() => {
    formatService = new FormatService(new PolicyService());
  });
  it('Should format the BPN Policy', () => {
    expect(toJsonLd(bpnPolicy(), OutputKind.Plain)).toEqual(formatService.formatPolicy(bpnJsonData));
  });

  it('Should format the BPN Group Policy', () => {
    expect(toJsonLd(bpnGroupPolicy(), OutputKind.Plain)).toEqual(formatService.formatPolicy(bpnGroupJsonData));
  });
});
