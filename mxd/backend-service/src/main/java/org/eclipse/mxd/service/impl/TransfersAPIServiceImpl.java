package org.eclipse.mxd.service.impl;

import org.eclipse.mxd.model.ReceivedModel;
import org.eclipse.mxd.model.TransfersModel;
import org.eclipse.mxd.repository.TransfersAPIRepository;
import org.eclipse.mxd.service.HttpServiceConnection;
import org.eclipse.mxd.service.TransfersAPIService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Stateless
public class TransfersAPIServiceImpl implements TransfersAPIService {

    @Inject
    private TransfersAPIRepository transfersAPIRepository;

    @Override
    public Response acceptTransfer(ReceivedModel receivedModel) {
    	String assestsUrl=HttpServiceConnection.getUrlAssets(receivedModel);
        try {
            // Assuming receivedModel has appropriate fields such as asset, contents, id
            String id = transfersAPIRepository.createTransferWithID(receivedModel.toString(),assestsUrl , receivedModel.getId());
            // Additional logic if needed
            return Response.ok(id).build();
            
        } catch (Exception e) {
        	 return Response.status(Response.Status.BAD_REQUEST).entity("Not Created").build();
        }
    }

    @Override
    public Response getTransfer(String id) {
        try {
            TransfersModel transfersModel = transfersAPIRepository.getTransferById(id);
            if (transfersModel != null) {
                return Response.ok(transfersModel).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Transfer not found").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public Response getTransferContents(String id) {
        try {
            TransfersModel transfersModel = transfersAPIRepository.getTransferById(id);
            if (transfersModel != null) {
                return Response.ok(transfersModel.getContents()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Transfer not found").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public Response test() {
        try {
            // Additional logic if needed
            return Response.ok("Test response").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Internal server error").build();
        }
    }
}
