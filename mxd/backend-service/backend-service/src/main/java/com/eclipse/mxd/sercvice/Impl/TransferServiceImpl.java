package com.eclipse.mxd.sercvice.Impl;

import com.eclipse.mxd.model.TransferRequest;
import com.eclipse.mxd.model.TransferResponse;
import com.eclipse.mxd.model.TransfersModel;
import com.eclipse.mxd.repository.Impl.TransferRepositroyImpl;
import com.eclipse.mxd.repository.TransferRespositroy;
import com.eclipse.mxd.sercvice.HttpServiceConnection;
import com.eclipse.mxd.sercvice.TransferService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;


@ApplicationScoped
public class TransferServiceImpl implements TransferService {

    private static final Logger logger = Logger.getLogger(TransferServiceImpl.class.getName());

    private final TransferRespositroy transferRespositroy = new TransferRepositroyImpl();

    @Override
    public Response acceptTransfer(String transferRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TransferRequest transferRequestData = objectMapper.readValue(transferRequest, TransferRequest.class);
            String assestsUrl = HttpServiceConnection.getUrlAssets(transferRequestData);
            Gson gson = new Gson();
            String transferRequestJson = gson.toJson(transferRequest);
             Long id = this.transferRespositroy.createTransferWithID(transferRequestJson, assestsUrl,
                     transferRequestData.getId());
            return Response.ok(id).build();
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("Not Created").build();
        }
    }

    @Override
    public Response getTransfer(String id) {
        try {
            TransfersModel transfersModel = this.transferRespositroy.getTransferById(id);
            if (transfersModel != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(transfersModel.getAsset());
                TransferResponse transferResponse = new TransferResponse();
                transferResponse.setAsset(jsonNode);
                return Response.ok(objectMapper.writeValueAsString(transferResponse)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Transfer not found").build();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public Response getTransferContents(String id) {
        try {
            TransfersModel transfersModel = this.transferRespositroy.getTransferById(id);
            if (transfersModel != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(transfersModel.getAsset());
                TransferResponse transferResponse = new TransferResponse();
                transferResponse.setAsset(jsonNode);
                return Response.ok(objectMapper.writeValueAsString(transferResponse)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Transfer not found").build();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }
}
