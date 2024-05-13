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

@JsonDeserialize(builder = Content.Builder.class)
public class Content extends Entity {

    private String data;

    private Content() {
    }

    public String getData() {
        return data;
    }

    public Builder toBuilder() {
        return Content.Builder.newInstance()
                .id(id)
                .data(data)
                .clock(clock)
                .createdAt(createdAt);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends Entity.Builder<Content, Builder> {

        protected Builder(Content content) {
            super(content);
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder(new Content());
        }

        public Builder data(String data) {
            entity.data = data;
            return self();
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        public Content build() {
            super.build();

            return entity;
        }
    }
}
