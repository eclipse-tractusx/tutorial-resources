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

package org.eclipse.tractusx.mxd.backendservice.statements;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

import static java.lang.String.format;

public class TransferStatementsServiceImpl implements TransferStatementsService {

    @Override
    public String getDeleteByIdTemplate() {
        return executeStatement().delete(getTransferTable(), getTransferIdColumn());
    }

    @Override
    public String getFindByTemplate() {
        return format("SELECT * FROM %s WHERE %s = ?", getTransferTable(), getTransferIdColumn());
    }

    @Override
    public String getInsertTemplate() {
        return executeStatement()
                .column(getTransferIdColumn())
                .column(getAssetColumn())
                .column(getContentsColumn())
                .column(getCreatedDateColumn())
                .column(getUpdatedDateColumn())
                .insertInto(getTransferTable());
    }

    @Override
    public String getCountTemplate() {
        return format("SELECT COUNT (%s) FROM %s WHERE %s = ?",
                getTransferIdColumn(),
                getTransferTable(),
                getTransferIdColumn());
    }

    @Override
    public String getUpdateTemplate() {
        return executeStatement()
                .column(getTransferIdColumn())
                .column(getAssetColumn())
                .column(getContentsColumn())
                .column(getCreatedDateColumn())
                .column(getUpdatedDateColumn())
                .insertInto(getTransferTable());
    }

    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        var select = format("SELECT * FROM %s", getTransferTable());
        return new SqlQueryStatement(select, querySpec, null);
    }
}
