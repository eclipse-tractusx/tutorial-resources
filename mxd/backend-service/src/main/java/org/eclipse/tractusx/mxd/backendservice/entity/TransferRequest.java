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

import java.util.Map;

public class TransferRequest {

    private String id;
    private String endpoint;
    private String authKey;
    private String authCode;
    private Map<String, Object> properties;

    private TransferRequest() {
    }

    private TransferRequest(String id, String endpoint, String authKey, String authCode, Map<String, Object> properties) {
        this.id = id;
        this.endpoint = endpoint;
        this.authKey = authKey;
        this.authCode = authCode;
        this.properties = properties;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
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

    @Override
    public String toString() {
        return "TransferRequest{" +
                "id='" + id + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", authKey='" + authKey + '\'' +
                ", authCode='" + authCode + '\'' +
                ", properties=" + properties +
                '}';
    }

    public static class Builder {
        private String id;
        private String endpoint;
        private String authKey;
        private String authCode;
        private Map<String, Object> properties;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder authKey(String authKey) {
            this.authKey = authKey;
            return this;
        }

        public Builder authCode(String authCode) {
            this.authCode = authCode;
            return this;
        }

        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public TransferRequest build() {
            return new TransferRequest(id, endpoint, authKey, authCode, properties);
        }
    }
}