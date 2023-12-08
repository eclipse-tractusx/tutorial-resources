
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
 *    Contributors:Ravinder Kumar
 *    Backend-API and implementation
 *
 ******************************************************************************/

package org.eclipse.mxd.model;

import java.util.Map;

public class TransfersRequest {

    private String id;
    private String endpoint;
    private String authKey;
    private String authCode;

    private Map<String, Object> properties;

    public TransfersRequest(String id, String endpoint, String authKey, String authCode, Map<String, Object> properties) {
        this.id = id;
        this.endpoint = endpoint;
        this.authKey = authKey;
        this.authCode = authCode;
        this.properties = properties;
    }

    public TransfersRequest() {
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

    public void setId(String id) {
        this.id = id;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "TransfersRequest{" +
                "id='" + id + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", authKey='" + authKey + '\'' +
                ", authCode='" + authCode + '\'' +
                ", properties=" + properties +
                '}';
    }
}
