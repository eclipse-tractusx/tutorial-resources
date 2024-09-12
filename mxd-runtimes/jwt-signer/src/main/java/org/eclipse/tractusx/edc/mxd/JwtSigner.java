/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.eclipse.tractusx.edc.mxd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.edc.iam.did.spi.document.DidDocument;
import org.eclipse.edc.security.token.jwt.CryptoConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class JwtSigner {

    public static final String DID_WEB_DATASPACE_ISSUER_KEY_ID = "did:web:dataspace-issuer#key-1";
    private static final String MEMBERSHIP_CREDENTIAL_TEMPLATE = """
            {
                        "@context": [
                            "https://www.w3.org/2018/credentials/v1",
                            "https://w3id.org/security/suites/jws-2020/v1",
                            "https://www.w3.org/ns/did/v1",
                            {
                                "mxd-credentials": "https://w3id.org/mxd/credentials/",
                                "membership": "mxd-credentials:membership",
                                "membershipType": "mxd-credentials:membershipType",
                                "website": "mxd-credentials:website",
                                "contact": "mxd-credentials:contact",
                                "since": "mxd-credentials:since"
                            }
                        ],
                        "id": "http://org.yourdataspace.com/credentials/2347",
                        "type": [
                            "VerifiableCredential",
                            "MembershipCredential"
                        ],
                        "issuer": "did:web:dataspace-issuer",
                        "issuanceDate": "2023-08-18T00:00:00Z",
                        "credentialSubject": {
                            "id": "%s",
                            "holderIdentifier": "%s",
                            "contractTemplate": "https://public.catena-x.org/contracts/Membership.v1.pdf",
                            "contractVersion": "1.0.0"
                        }
                    }
            """;

    private static final String FRAMEWORK_CREDENTIAL_TEMPLATE = """
            {
              "@context": [
                "https://www.w3.org/2018/credentials/v1",
                "https://w3id.org/catenax/credentials/v1.0.0"
              ],
              "id": "1f36af58-0fc0-4b24-9b1c-e37d59668089",
              "type": [
                "VerifiableCredential",
                "DataExchangeGovernanceCredential"
              ],
              "issuer": "did:web:com.example.issuer",
              "issuanceDate": "2021-06-16T18:56:59Z",
              "expirationDate": "2022-06-16T18:56:59Z",
              "credentialSubject":
              {
                "id": "%s",
                "holderIdentifier": "%s",
                "group": "UseCaseFramework",
                "useCase": "DataExchangeGovernance",
                "contractTemplate": "https://catena-x.net/en/catena-x-introduce-implement/governance-framework-for-data-space-operations",
                "contractVersion": "a.b"
              }
            }
            
            """;
    private static final Path rootDir = Paths.get(System.getProperty("user.dir"), "../../mxd/assets");
    private final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        var jwtSigner = new JwtSigner();
        System.out.println("Generating Issuer Key Pair");
        var issuerKey = jwtSigner.regenerateIssuerKey();
        System.out.println("Updating Issuer's DID document with new public key");
        jwtSigner.updateIssuerDid(issuerKey);
        System.out.println("Re-issue participant credentials");
        jwtSigner.signMembershipCredential("did:web:alice-ih%3A7083:alice", "BPNL000000000001", "alice.membership.jwt", issuerKey);
        jwtSigner.signFrameworkCredential("did:web:alice-ih%3A7083:alice", "BPNL000000000001", "alice.dataexchangegov.jwt", issuerKey);
        jwtSigner.signMembershipCredential("did:web:bob-ih%3A7083:bob", "BPNL000000000002", "bob.membership.jwt", issuerKey);
        jwtSigner.signFrameworkCredential("did:web:bob-ih%3A7083:bob", "BPNL000000000002", "bob.dataexchangegov.jwt", issuerKey);

        jwtSigner.verify("alice.membership.jwt", issuerKey.toPublicJWK());
        jwtSigner.verify("bob.membership.jwt", issuerKey.toPublicJWK());
    }

    private void verify(String credentialName, JWK issuerKey) throws IOException, ParseException, JOSEException {
        var jwt = SignedJWT.parse(Files.readString(rootDir.resolve(credentialName)));

        jwt.verify(CryptoConverter.createVerifier(issuerKey));
    }

    private JWK regenerateIssuerKey() throws JOSEException, IOException {
        var okp = new OctetKeyPairGenerator(Curve.Ed25519)
                .keyID(DID_WEB_DATASPACE_ISSUER_KEY_ID)
                .generate();

        var filePath = rootDir.resolve("issuer.key.json");
        Files.write(filePath, okp.toJSONString().getBytes());

        var filePath2 = rootDir.resolve("issuer.pub.json");
        Files.write(filePath2, okp.toPublicJWK().toJSONString().getBytes());
        return okp;
    }

    private void updateIssuerDid(JWK newKey) throws IOException {
        var didPath = rootDir.resolve("issuer.did.json");
        var doc = mapper.readValue(didPath.toFile(), DidDocument.class);

        var jwk = newKey.toPublicJWK().toJSONObject();

        var publicKeyJwk = doc.getVerificationMethod().stream()
                .findFirst()
                .orElseThrow()
                .getPublicKeyJwk();
        publicKeyJwk.clear();
        publicKeyJwk.putAll(jwk);

        mapper.writeValue(didPath.toFile(), doc);
    }

    private void signMembershipCredential(String did, String participantId, String name, JWK signingKey) throws JOSEException, IOException {
        signCredential(did, participantId, name, signingKey, MEMBERSHIP_CREDENTIAL_TEMPLATE);
    }

    private void signFrameworkCredential(String did, String participantId, String name, JWK signingKey) throws JOSEException, IOException {
        signCredential(did, participantId, name, signingKey, FRAMEWORK_CREDENTIAL_TEMPLATE);
    }

    private void signCredential(String did, String participantId, String name, JWK signingKey, String credentialTemplate) throws JOSEException, IOException {
        var header = new JWSHeader.Builder(JWSAlgorithm.EdDSA)
                .keyID(DID_WEB_DATASPACE_ISSUER_KEY_ID)
                .type(JOSEObjectType.JWT)
                .build();


        var credential = mapper.readValue(credentialTemplate.formatted(did, participantId), Map.class);

        var claims = new JWTClaimsSet.Builder()
                .audience(did)
                .subject(did)
                .issuer("did:web:dataspace-issuer")
                .claim("vc", credential)
                .issueTime(Date.from(Instant.now()))
                .build();

        var jwt = new SignedJWT(header, claims);
        jwt.sign(CryptoConverter.createSigner(signingKey));

        var filePath = rootDir.resolve(name);
        Files.write(filePath, jwt.serialize().getBytes());
    }

}
