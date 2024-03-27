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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.spi.entity.Entity;

import java.time.Clock;

@JsonDeserialize(builder = Content.Builder.class)
public class Content extends Entity {

    public String data;

    @JsonCreator
    public Content(String id, String data, Clock clock, long createdAt) {
        this.id = id;
        this.data = data;
        this.clock = clock;
        this.createdAt = createdAt;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Content{" +
                "data='" + data + '\'' +
                ", id='" + id + '\'' +
                ", clock=" + clock +
                ", createdAt=" + createdAt +
                '}';
    }

    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        protected Clock clock;
        protected long createdAt;
        private String data;
        private String id;

        // Default constructor
        public Builder() {
        }

        // Setter methods for each field
        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        // Build method to create an instance of Content
        public Content build() {
            return new Content(id, data, clock, createdAt);
        }
    }
}
