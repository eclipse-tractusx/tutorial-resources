package org.eclipse.mxd.service;

import org.eclipse.mxd.database.DatabaseConnector;
import org.eclipse.mxd.database.SqlQueryTransfer;
import org.eclipse.mxd.model.ReceivedModel;
import org.eclipse.mxd.model.TransfersModel;
import org.eclipse.mxd.service.repository.TransfersAPIServiceRepositroy;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

@Stateless
public class TransfersAPIService {

    @Inject
    private TransfersAPIServiceRepositroy transfersAPIServiceRepositroy;

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
            SqlQueryTransfer.createTransferWithID(receivedModel.toString(),response.toString(), conn,receivedModel.getId());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Response getTransfer(int id) {
        try {
            TransfersModel contents = transfersAPIServiceRepositroy.getTransferById(id);
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

    public Response getTransferContents(int id) {
        try {
            TransfersModel contents = transfersAPIServiceRepositroy.getTransferById(id);
            if (contents != null) {
                return Response.ok(contents.getContents()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Asset not found").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Internal server error").build();
        }
    }

    public Response test() {

        String host = System.getenv("backend-service-host");
        String username = System.getenv("backend-service-username");
        String password = System.getenv("backend-service-password");

        // Get all environment variables
//        Map<String, String> envVariables = System.getenv();
//
//        // Convert the map to a string representation
//        StringBuilder responseBuilder = new StringBuilder("Environment Variables:\n");
//        envVariables.forEach((key, value) -> responseBuilder.append(key).append("=").append(value).append("\n"));
//
//        // Build and return the response
//        String responseText = responseBuilder.toString();
        return Response.ok("host : "+host+" username : "+username+" password : "+password).build();


    }

}
