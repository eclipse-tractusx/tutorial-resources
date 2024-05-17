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
import org.eclipse.tractusx.mxd.backendservice.entity.Transfer;
import org.eclipse.tractusx.mxd.backendservice.entity.TransferResponse;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@ExtensionPoint
public interface TransferStoreService {

    String CONTRACT_EXISTS = "Transfer with ID %s already exists";
    String CONTRACT_NOT_FOUND = "Transfer with ID %s not found";

    /**
     * Returns all the definitions in the store that are covered by a given {@link QuerySpec}.
     * <p>
     * Note: supplying a sort field that does not exist on the {@link Content} may cause some implementations
     * to return an empty Stream, others will return an unsorted Stream, depending on the backing storage
     * implementation.
     */
    @NotNull
    Stream<TransferResponse> findAll(QuerySpec spec);

    /**
     * Returns the definition with the given id, if it exists.
     *
     * @param contentId the id.
     * @return the definition with the given id, or null.
     */
    StoreResult<TransferResponse> findById(String contentId);

    /**
     * Stores the transfer if a transfer with the same ID doesn't already exists.
     *
     * @param content {@link Content} to store.
     * @return {@link StoreResult#success()} if the transfer was stored, {@link StoreResult#alreadyExists(String)} if a contract
     * definition with the same ID already exists.
     */
    StoreResult<Void> save(Transfer transfer, String content);

    /**
     * Update the transfer if a transfer with the same ID exists.
     *
     * @param transfer {@link Content} to update.
     * @return {@link StoreResult#success()} if the transfer was updates, {@link StoreResult#notFound(String)} if a contract
     * definition identified by the ID was not found.
     */
    StoreResult<Void> update(Transfer transfer);

    /**
     * Deletes the transfer with the given id.
     *
     * @param id A String that represents the {@link Content} ID, in most cases this will be a UUID.
     * @return {@link StoreResult#success()}} if the transfer was deleted, {@link StoreResult#notFound(String)} if the transfer
     * was not found in the store.
     */
    StoreResult<Transfer> deleteById(String id);

}
