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
import org.eclipse.edc.sql.translation.SqlQueryStatement;
import org.eclipse.edc.sql.translation.PostgresqlOperatorTranslator;

import static java.lang.String.format;

public class ContentStatementsServiceImpl implements ContentStatementsService {

    @Override
    public String getFindByTemplate() {
        return format("SELECT * FROM %s WHERE %s = ?", getContentTable(), getIdColumn());
    }

    @Override
    public String getInsertTemplate() {
        return executeStatement()
                .column(getIdColumn())
                .column(getAssetColumn())
                .column(getCreatedDateColumn())
                .column(getUpdatedDateColumn())
                .insertInto(getContentTable());
    }

    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        var select = format("SELECT * FROM %s", getContentTable());
        return new SqlQueryStatement(select, querySpec, null, new PostgresqlOperatorTranslator());
    }
}
