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

package org.eclipse.tractusx.mxd.backendservice.statements;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.statement.SqlStatements;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

public interface TransferStatementsService extends SqlStatements {

    default String getTransferTable() {
        return "transfer";
    }

    default String getTransferIdColumn() {
        return "transferid";
    }

    default String getAssetColumn() {
        return "asset";
    }

    default String getContentsColumn() {
        return "contents";
    }

    default String getCreatedDateColumn() {
        return "createddate";
    }

    default String getUpdatedDateColumn() {
        return "updateddate";
    }

    String getDeleteByIdTemplate();

    String getFindByTemplate();

    String getInsertTemplate();

    String getCountTemplate();

    String getUpdateTemplate();

    SqlQueryStatement createQuery(QuerySpec querySpec);

}
