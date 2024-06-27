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

import java.util.Date;

public class TransferResponse {

    private String asset;
    private String contents;
    private String transferID;
    private Date createdDate;
    private Date updatedDate;

    public TransferResponse() {
    }

    public TransferResponse(String asset, String contents, String transferID, Date createdDate, Date updatedDate) {
        this.asset = asset;
        this.contents = contents;
        this.transferID = transferID;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAsset() {
        return asset;
    }

    public String getContents() {
        return contents;
    }

    public String getTransferID() {
        return transferID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return "TransferResponse{" +
                "asset='" + asset + '\'' +
                ", contents='" + contents + '\'' +
                ", transferID='" + transferID + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }

    public static class Builder {
        private String asset;
        private String contents;
        private String transferID;
        private Date createdDate;
        private Date updatedDate;

        public Builder asset(String asset) {
            this.asset = asset;
            return this;
        }

        public Builder contents(String contents) {
            this.contents = contents;
            return this;
        }

        public Builder transferID(String transferID) {
            this.transferID = transferID;
            return this;
        }

        public Builder createdDate(Date createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder updatedDate(Date updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public TransferResponse build() {
            return new TransferResponse(asset, contents, transferID, createdDate, updatedDate);
        }
    }
}