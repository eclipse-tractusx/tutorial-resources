package org.eclipse.mxd.controller;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.eclipse.mxd.service.ContentsAPIService;
import org.eclipse.mxd.service.impl.ContentsAPIServiceImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;

@Path("api/v1/contents")
@ApplicationScoped
public class ContentsController {

	@Inject
	private ContentsAPIService contentsAPIService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		return this.contentsAPIService.getAll();
	}

	@GET
	@Path("/getpath")
	public String getAppPath(@Context UriInfo uriInfo) {
		return this.contentsAPIService.getAppAbsolutePath(uriInfo);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") int id) {
		return contentsAPIService.getById(id);
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postContent(String receivedModel, @Context UriInfo uriInfo) {
		return this.contentsAPIService.postContent(receivedModel, uriInfo);
	}
}
