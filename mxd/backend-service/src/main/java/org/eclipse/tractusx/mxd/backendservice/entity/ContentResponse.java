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

public class ContentResponse {

    private String id;
    private String data;

    private ContentResponse() {
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
