
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


package org.eclipse.tractusx.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Transfer {


    @Id
    private String transferID;

    @Column(columnDefinition = "TEXT")
    private String asset;
    @Column(columnDefinition = "TEXT")
    private String contents;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date updatedDate;

    public Transfer() {

    }


    public Transfer(String asset, String contents, String transferID, Date createdDate, Date updatedDate) {
        this.asset = asset;
        this.contents = contents;
        this.transferID = transferID;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getTransferID() {
        return transferID;
    }

    public void setTransferID(String transferID) {
        this.transferID = transferID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferID='" + transferID + '\'' +
                ", asset='" + asset + '\'' +
                ", contents='" + contents + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
