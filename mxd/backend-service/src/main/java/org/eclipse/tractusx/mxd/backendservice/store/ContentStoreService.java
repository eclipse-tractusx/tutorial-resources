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

package org.eclipse.tractusx.mxd.backendservice.store;

import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.tractusx.mxd.backendservice.entity.ContentResponse;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@ExtensionPoint
public interface ContentStoreService {

    String CONTENT_NOT_FOUND_TEMPLATE = "Content with ID %s not found";

    @NotNull
    Stream<ContentResponse> findAll(QuerySpec spec);

    StoreResult<ContentResponse> findById(String contentId);

    String save(Object content);

}
