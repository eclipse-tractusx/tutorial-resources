package org.eclipse.mxd.controller;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.eclipse.mxd.service.ContentsAPIService;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;

@Path("api/v1/content")
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
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") int id) {
		return contentsAPIService.getById(id);
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postContent(String requestBody, @Context UriInfo uriInfo) {
		return this.contentsAPIService.postContent(requestBody, uriInfo);
	}
}
