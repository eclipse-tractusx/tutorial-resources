package com.eclipse.mxd.controller;

import com.eclipse.mxd.model.TransferRequest;
import com.eclipse.mxd.sercvice.Impl.TransferServiceImpl;
import com.eclipse.mxd.sercvice.TransferService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/transfer")
@ApplicationScoped
public class TransferController {

   TransferService transfersAPIService = new TransferServiceImpl();

    @POST
    @Path("/")
    public Response acceptTransfer(String transferRequest) {
        return this.transfersAPIService.acceptTransfer(transferRequest);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfer(@PathParam("id") String id) {
        return this.transfersAPIService.getTransfer(id);
    }

    @GET
    @Path("/{id}/contents")
    public Response getTransferContents(@PathParam("id") String id) {
        System.out.println("controller line no 39");
        return this.transfersAPIService.getTransferContents(id);
    }

}
