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

public class ContentResponse {

    private String id;
    private String data;

    private ContentResponse() {
    }

    public ContentResponse(String id, String data) {
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

        public ContentResponse.Builder setId(String id) {
            this.id = id;
            return this;
        }

        public ContentResponse.Builder setData(String data) {
            this.data = data;
            return this;
        }

        public ContentResponse build() {
            ContentResponse contentModel = new ContentResponse();
            contentModel.id = this.id;
            contentModel.data = this.data;
            return contentModel;
        }
    }
}
