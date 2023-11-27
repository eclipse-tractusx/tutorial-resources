package org.eclipse.mxd.controller;

import org.eclipse.mxd.model.TransferRequest;
import org.eclipse.mxd.service.TransfersAPIService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("api/v1/transfer")
@ApplicationScoped
public class TransfersController {

	@Inject
	private TransfersAPIService transfersAPIService;

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response acceptTransfer(TransferRequest transferRequest) {
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
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTransferContents(@PathParam("id") String id) {
		return this.transfersAPIService.getTransferContents(id);
	}

}
