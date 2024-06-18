/********************************************************************************
 *  Copyright (c) 2024 SAP SE
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       SAP SE - initial API and implementation
 *
 ********************************************************************************/

package org.eclipse.tractusx.mxd.backendservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.spi.entity.Entity;

import java.util.Map;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(builder = Transfer.Builder.class)
public class Transfer extends Entity {

    private String endpoint;
    private String authKey;
    private String authCode;
    private Map<String, Object> properties;

    private Transfer() {
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAuthKey() {
        return authKey;
    }

    public String getAuthCode() {
        return authCode;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Builder toBuilder() {
        return Transfer.Builder.newInstance()
                .id(id)
                .authCode(authCode)
                .authKey(authKey)
                .endpoint(endpoint)
                .properties(properties)
                .createdAt(createdAt);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends Entity.Builder<Transfer, Builder> {

        protected Builder(Transfer transfer) {
            super(transfer);
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder(new Transfer());
        }

        public Builder endpoint(String endpoint) {
            entity.endpoint = endpoint;
            return self();
        }

        public Builder authKey(String authKey) {
            entity.authKey = authKey;
            return self();
        }

        public Builder authCode(String authCode) {
            entity.authCode = authCode;
            return self();
        }

        public Builder properties(Map<String, Object> properties) {
            entity.properties = properties;
            return self();
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public Transfer build() {
            super.build();
            requireNonNull(entity.id);
            requireNonNull(entity.endpoint);
            requireNonNull(entity.authKey);
            requireNonNull(entity.authCode);
            return entity;
        }
    }
}