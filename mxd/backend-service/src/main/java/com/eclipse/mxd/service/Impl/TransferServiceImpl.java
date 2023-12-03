package com.eclipse.mxd.service.Impl;

import com.eclipse.mxd.model.TransferContentResponse;
import com.eclipse.mxd.model.TransferRequest;
import com.eclipse.mxd.model.TransferResponse;
import com.eclipse.mxd.model.TransfersModel;
import com.eclipse.mxd.repository.Impl.TransferRepositoryImpl;
import com.eclipse.mxd.repository.TransferRepository;
import com.eclipse.mxd.service.HttpServiceConnection;
import com.eclipse.mxd.service.TransferService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;


@ApplicationScoped
public class TransferServiceImpl implements TransferService {

    private static final Logger logger = Logger.getLogger(TransferServiceImpl.class.getName());

    private final TransferRepository transferRepository = new TransferRepositoryImpl();

    @Override
    public Response acceptTransfer(String transferRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TransferRequest transferRequestData = objectMapper.readValue(transferRequest, TransferRequest.class);
            String assetsUrl = HttpServiceConnection.getUrlAssets(transferRequestData);
            Gson gson = new Gson();
            String transferRequestJson = gson.toJson(transferRequest);
            Long id = this.transferRepository.createTransferWithID(transferRequestJson, assetsUrl,
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
            TransfersModel transfersModel = this.transferRepository.getTransferById(id);
            if (transfersModel != null && transfersModel.getId()!=null)  {
                JsonNode jsonNode = new ObjectMapper().readTree(transfersModel.getAsset());
                TransferResponse transferResponse = new TransferResponse();
                transferResponse.setAsset(jsonNode);
                return Response.ok(new ObjectMapper().writeValueAsString(transferResponse)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Transfer Not Found").build();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public Response getTransferContents(String id) {
        try {
            TransfersModel transfersModel = this.transferRepository.getTransferById(id);
            if (transfersModel != null && transfersModel.getContents()!=null) {
                JsonNode jsonNode = new ObjectMapper().readTree(transfersModel.getContents());
                TransferContentResponse transferContentResponse = new TransferContentResponse();
                transferContentResponse.setAsset(jsonNode);
                return Response.ok(new ObjectMapper().writeValueAsString(transferContentResponse)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Transfer Contents Not Found").build();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }
}
