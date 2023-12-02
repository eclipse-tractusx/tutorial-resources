package com.eclipse.mxd.controller;

import com.eclipse.mxd.repository.ContentServiceRepository;
import com.eclipse.mxd.repository.Impl.ContentRepositoryImpl;
import com.eclipse.mxd.sercvice.ContentRamdomService;
import com.eclipse.mxd.sercvice.ContentService;
import com.eclipse.mxd.sercvice.Impl.ContentRamdomServiceImpl;
import com.eclipse.mxd.sercvice.Impl.ContentServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;


@Path("/api/v1/content")
@ApplicationScoped
public class ContentController {

    private  final  ContentService contentService = new ContentServiceImpl();

    private  final ContentRamdomService contentRamdomService = new ContentRamdomServiceImpl();

    private final ContentServiceRepository contentServiceRepository = new ContentRepositoryImpl();
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
    public Response getRandomContent(){
        return  this.contentRamdomService.getRandomContent();
    }
}
