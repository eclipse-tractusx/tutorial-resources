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
import org.eclipse.tractusx.mxd.backendservice.entity.Content;
import org.eclipse.tractusx.mxd.backendservice.entity.ContentResponse;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@ExtensionPoint
public interface ContentStoreService {

    String CONTENT_EXISTS_TEMPLATE = "Content with ID %s already exists";
    String CONTENT_NOT_FOUND_TEMPLATE = "Content with ID %s not found";

    /**
     * Returns all the definitions in the store that are covered by a given {@link QuerySpec}.
     * <p>
     * Note: supplying a sort field that does not exist on the {@link Content} may cause some implementations
     * to return an empty Stream, others will return an unsorted Stream, depending on the backing storage
     * implementation.
     */
    @NotNull
    Stream<ContentResponse> findAll(QuerySpec spec);

    /**
     * Returns the definition with the given id, if it exists.
     *
     * @param contentId the id.
     * @return the definition with the given id, or null.
     */
    StoreResult<ContentResponse> findById(String contentId);

    /**
     * Stores the content  a content with the same ID doesn't already exists.
     *
     * @param content {@link Content} to store.
     * @return {@link StoreResult#success()} if the content was stored, {@link StoreResult#alreadyExists(String)} if a contract
     * definition with the same ID already exists.
     */
    String save(Object content);

    /**
     * Update the content if a content with the same ID exists.
     *
     * @param content {@link Content} to update.
     * @return {@link StoreResult#success()} if the content was updates, {@link StoreResult#notFound(String)} if a contract
     * definition identified by the ID was not found.
     */
    StoreResult<Void> update(Content content);

    /**
     * Deletes the content with the given id.
     *
     * @param id A String that represents the {@link Content} ID, in most cases this will be a UUID.
     * @return {@link StoreResult#success()}} if the content was deleted, {@link StoreResult#notFound(String)} if the content was not found in the store.
     */
    StoreResult<Content> deleteById(String id);

}
