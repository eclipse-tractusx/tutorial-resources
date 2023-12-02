package com.eclipse.mxd.sercvice;


import com.eclipse.mxd.model.TransferRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class HttpServiceConnection {
    private static final Logger logger = Logger.getLogger(HttpServiceConnection.class.getName());

    public static String getUrlAssets(TransferRequest receivedModel) {
        String res = null;
        try {
            URL url = new URL(receivedModel.getEndpoint());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(receivedModel.getAuthKey(), receivedModel.getAuthCode());
            connection.setRequestProperty("User-Agent", "Java HttpURLConnection Bot");
            connection.setRequestProperty("Content-Type", "application/json");
            int responseCode = connection.getResponseCode();
            logger.info("Response Code: " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.info("Response Body: " + response.toString());
            connection.disconnect();
            return response.toString();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return res;
    }
}
