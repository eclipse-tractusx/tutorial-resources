/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.tractusx.mxd.dataservice.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.edc.web.spi.ApiErrorDetail;
import org.eclipse.tractusx.mxd.dataservice.model.DataRecord;

import java.util.Collection;

@OpenAPIDefinition
@Tag(name = "Demo Backend Service for the MXD")
public interface DataServiceApi {

    @Operation(description = "Gets all DataRecord",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The DataRecords.",
                            content = @Content(schema = @Schema(implementation = DataRecord.class))),
                    @ApiResponse(responseCode = "401", description = "Not authenticated: principal could not be identified",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    Collection<DataRecord> getAll();

    @Operation(description = "Gets the DataRecord for the given ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The DataRecord.",
                            content = @Content(schema = @Schema(implementation = DataRecord.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Request",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class)))),
                    @ApiResponse(responseCode = "401", description = "Not authenticated: principal could not be identified",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class)))),
                    @ApiResponse(responseCode = "404", description = "A DataRecord with the given ID was not found")
            })
    DataRecord findById(String id);

    @Operation(description = "Creates a new Data Record with the given parameters",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = DataRecord.class))),
            responses = {
                    @ApiResponse(responseCode = "204", description = "The DataRecord was created successfully.",
                            content = @Content(schema = @Schema(implementation = DataRecord.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Request",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class)))),
                    @ApiResponse(responseCode = "401", description = "Not authenticated: principal could not be identified",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    String create(DataRecord dataRecord);


    @Operation(description = "Updates an existing DataRecord with new values.",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = DataRecord.class))),
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400", description = "Invalid Request",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class)))),
                    @ApiResponse(responseCode = "401", description = "Not authenticated: principal could not be identified",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class)))),
                    @ApiResponse(responseCode = "404", description = "A DataRecord with the given ID was not found")
            })
    void update(DataRecord dataRecord);

    @Operation(description = "Deletes a DataRecord by ID",
            responses = {
                    @ApiResponse(responseCode = "204"),
                    @ApiResponse(responseCode = "400", description = "Invalid Request",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class)))),
                    @ApiResponse(responseCode = "401", description = "Not authenticated: principal could not be identified",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class)))),
                    @ApiResponse(responseCode = "404", description = "A DataRecord with the given ID was not found")
            })
    void delete(String id);
}
