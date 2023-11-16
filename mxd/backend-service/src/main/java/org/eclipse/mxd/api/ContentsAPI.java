package org.eclipse.mxd.api;

import org.eclipse.mxd.database.DatabaseConnector;
import org.eclipse.mxd.database.SqlQuery;
import org.eclipse.mxd.model.ContentsModel;
import org.eclipse.mxd.model.ReceivedModel;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;

@Path("contents")
@ApplicationScoped
public class ContentsAPI {

    @GET
    public Response test() {
        return Response.ok("Hello World").build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") int id) {
        try {
            Connection conn = DatabaseConnector.connect();
            ContentsModel contents = SqlQuery.getAssetById(id, conn);

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

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public void postDataRequest(ReceivedModel receivedModel) {
        System.out.println("Received request body: " + receivedModel.toString());
        try {
            Connection conn = DatabaseConnector.connect();
            SqlQuery.createAsset(receivedModel.toString(), conn);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
