package org.eclipse.mxd.api;

import org.eclipse.mxd.database.DatabaseConnector;
import org.eclipse.mxd.database.SqlQuery;
import org.eclipse.mxd.model.ContentsModel;
import org.eclipse.mxd.model.ReceivedModel;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

@Path("transfers")
@ApplicationScoped
public class TransfersAPI {

    @GET
    public Response test() {
        return Response.ok("Hello World").build();
    }


    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public void acceptTransfer(ReceivedModel receivedModel){
        System.out.println("Received request body: " + receivedModel.toString());
    try {

        URL url = new URL(receivedModel.getEndpoint());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty(receivedModel.getAuthKey(), receivedModel.getAuthCode());
        connection.setRequestProperty("User-Agent", "Java HttpURLConnection Bot");
        connection.setRequestProperty("Content-Type", "application/json");

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println("Response Body: " + response.toString());
        connection.disconnect();

        Connection conn = DatabaseConnector.connect();
        SqlQuery.createAsset(receivedModel.toString(), conn);

    }catch (Exception e){
        e.printStackTrace();
    }

    }


    @GET
    @Path("/{id}")
    public Response getTransfer(@PathParam("id") int id) {
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

    @GET
    @Path("/{id}/contents")
    public Response getTransferContents(@PathParam("id") int id) {
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

}
