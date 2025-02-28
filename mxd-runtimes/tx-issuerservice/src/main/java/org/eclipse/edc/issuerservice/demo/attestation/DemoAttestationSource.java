/*
 *  Copyright (c) 2025 Cofinity-X
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Cofinity-X - initial API and implementation
 *
 */

package org.eclipse.edc.issuerservice.demo.attestation;

import java.util.Map;
import java.util.UUID;

import org.eclipse.edc.issuerservice.spi.issuance.attestation.AttestationContext;
import org.eclipse.edc.issuerservice.spi.issuance.attestation.AttestationSource;
import org.eclipse.edc.spi.result.Result;

public class DemoAttestationSource implements AttestationSource {
    @Override
    public Result<Map<String, Object>> execute(AttestationContext attestationContext) {
        var pid = attestationContext.participantId();
        return Result.success(
                Map.of("onboarding", Map.of("signedDocuments", true),
                        "participant", Map.of("name", pid),
                        "credential", Map.of("id", UUID.randomUUID().toString())
                ));
    }
}
