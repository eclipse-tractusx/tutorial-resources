/*******************************************************************************
 *
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

public class ContentRequest {

    private String id;
    private String data;

    private ContentRequest() {
    }

    public ContentRequest(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ",data=" + data +
                "}";
    }

    public static class Builder {
        private String id;
        private String data;

        public Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public ContentRequest build() {
            ContentRequest contentModel = new ContentRequest();
            contentModel.id = this.id;
            contentModel.data = this.data;
            return contentModel;
        }
    }
}
