package org.eclipse.mxd.service.impl;

import org.eclipse.mxd.model.ContentsModel;
import org.eclipse.mxd.repository.ContentsAPIServiceRepository;
import org.eclipse.mxd.service.ContentsAPIService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Stateless
public class ContentsAPIServiceImpl implements ContentsAPIService {

   @Inject
   private ContentsAPIServiceRepository contentsRepository;
   
   
   public String getAppAbsolutePath(UriInfo uriInfo) {
	   
	   String baseUri = uriInfo.getBaseUri().toString();
       String requestUri = uriInfo.getRequestUri().toString();

       return "Base URI: " + baseUri + "\nRequest URI: " + requestUri;
   }

    public Response getAll() {
        try {
            List<ContentsModel> contents = contentsRepository.getAllAssets();
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

    public Response getById(int id) {
        try {
            ContentsModel contents = contentsRepository.getAssetById(id);
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

    public Response postContent(String receivedModel,UriInfo uriInfo) {
        System.out.println("Received request body: " + receivedModel.toString());
        try {

            int id= contentsRepository.createAsset(receivedModel.toString());
            if (id != -1) {

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
