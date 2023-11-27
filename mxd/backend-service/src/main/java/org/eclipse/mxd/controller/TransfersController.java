package org.eclipse.mxd.controller;

import org.eclipse.mxd.model.ReceivedModel;
import org.eclipse.mxd.service.TransfersAPIService;
import org.eclipse.mxd.service.impl.TransfersAPIServiceImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("api/v1/transfers")
@ApplicationScoped
public class TransfersController {

	@Inject
	private TransfersAPIService transfersAPIService;

	@GET
	public Response test() {
		return this.transfersAPIService.test();
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response acceptTransfer(ReceivedModel receivedModel) {

		return this.transfersAPIService.acceptTransfer(receivedModel);
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
