package org.eclipse.mxd.api;

import org.eclipse.mxd.database.DatabaseConnector;
import org.eclipse.mxd.database.SqlQueryContents;
import org.eclipse.mxd.model.ContentsModel;
import org.eclipse.mxd.model.ReceivedModel;
import javax.ws.rs.core.UriInfo;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.util.List;
import javax.ws.rs.core.Context;

@Path("contents")
@ApplicationScoped
public class ContentsAPI {

	
	@Context
    private UriInfo uriInfo;
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
    	   try {
               Connection conn = DatabaseConnector.connect();
               List<ContentsModel> contents = SqlQueryContents.getALLAsset(conn);

               if (contents != null) {
                   return Response.ok(contents).build();
               } else {
                   return Response.status(Response.Status.NOT_FOUND).entity("Asset not found").build();
               }
           } catch (Exception e) {
               e.printStackTrace();
               return Response.serverError().entity("Internal server error").build();
           }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") int id) {
        try {
            Connection conn = DatabaseConnector.connect();
            ContentsModel contents = SqlQueryContents.getAssetById(id, conn);

            if (contents != null) {
            	
                return Response.ok(contents.getAsset()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Asset not found").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postDataRequest(String receivedModel) {
        System.out.println("Received request body: " + receivedModel.toString());
        try {
            Connection conn = DatabaseConnector.connect();
           int id= SqlQueryContents.createAsset(receivedModel.toString(), conn);
           if (id != 0) {
           	 
        	//   System.out.println(uriInfo.getPath());
        	  System.out.println(uriInfo.getAbsolutePath());
        	 //  System.out.println(uriInfo.getBaseUri());
               return Response.ok(uriInfo.getAbsolutePath()+""+id).build();
           } else {
               return Response.status(Response.Status.NOT_FOUND).entity("Asset Not Created").build();
           }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Internal server error").build();
        }

    }
}
