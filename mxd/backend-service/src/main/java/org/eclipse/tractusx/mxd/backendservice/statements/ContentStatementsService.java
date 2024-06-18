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

public interface ContentStatementsService extends SqlStatements {

    default String getContentTable() {
        return "content";
    }

    default String getIdColumn() {
        return "id";
    }

    default String getAssetColumn() {
        return "asset";
    }

    default String getCreatedDateColumn() {
        return "createddate";
    }

    default String getUpdatedDateColumn() {
        return "updateddate";
    }

    String getFindByTemplate();

    String getInsertTemplate();

    SqlQueryStatement createQuery(QuerySpec querySpec);

}
