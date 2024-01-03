/*******************************************************************************
 *
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 ******************************************************************************/

package org.eclipse.tractusx.mxd.backendservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.spi.entity.Entity;

import java.util.Map;


@JsonDeserialize(builder = Transfer.Builder.class)
public class Transfer extends Entity {

    private String endpoint;
    private String authKey;
    private String authCode;
    private Map<String, Object> properties;


    @JsonCreator
    public Transfer(String id, String endpoint, String authKey, String authCode, Map<String, Object> properties) {
        this.id = id;
        this.endpoint = endpoint;
        this.authKey = authKey;
        this.authCode = authCode;
        this.properties = properties;
    }


    public Transfer() {
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "Transfer{" +
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

        public Builder() {
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

        public Transfer build() {
            return new Transfer(id, endpoint, authKey, authCode, properties);
        }

    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builders {
        private final Transfer instance;

        private Builders() {
            instance = new Transfer();
        }

        @JsonCreator
        public static Transfer.Builders newInstance() {
            return new Transfer.Builders();
        }

        public Transfer.Builders id(String id) {
            instance.id = id;
            return this;
        }

        public Transfer.Builders endpoint(String endpoint) {
            instance.endpoint = endpoint;
            return this;
        }

        public Transfer build() {
            return instance;
        }
    }
}
