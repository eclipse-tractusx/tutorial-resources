package com.eclipse.mxd.controller;

import com.eclipse.mxd.service.ContentRandomService;
import com.eclipse.mxd.service.ContentService;
import com.eclipse.mxd.service.Impl.ContentRandomServiceImpl;
import com.eclipse.mxd.service.Impl.ContentServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;


@Path("/v1/content")
@ApplicationScoped
public class ContentController {
    private final ContentService contentService = new ContentServiceImpl();

    private final ContentRandomService contentRandomService = new ContentRandomServiceImpl();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return this.contentService.getAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id) {
        return contentService.getById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postContent(String requestBody, @Context UriInfo uriInfo) {
        return this.contentService.postContent(requestBody, uriInfo);
    }

    @GET
    @Path("/random")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRandomContent() {
        return this.contentRandomService.getRandomContent();
    }
}
