package org.eclipse.mxd.controller;

import org.eclipse.mxd.model.ReceivedModel;
import org.eclipse.mxd.service.TransfersAPIService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("transfers")
@ApplicationScoped
public class TransfersController {


    @Inject
    private TransfersAPIService transfersAPIService;

    @GET
    public Response test() {
            return  this.transfersAPIService.test();
    }


    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public void acceptTransfer(ReceivedModel receivedModel){
         this.transfersAPIService.acceptTransfer(receivedModel);
    }


    @GET
    @Path("/{id}")
    public Response getTransfer(@PathParam("id") int id) {
        return this.transfersAPIService.getTransfer(id);
    }

    @GET
    @Path("/{id}/contents")
    public Response getTransferContents(@PathParam("id") int id) {
        return this.transfersAPIService.getTransferContents(id);
    }

}
