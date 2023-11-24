package org.eclipse.mxd.service;

import org.eclipse.mxd.model.ContentsModel;
import org.eclipse.mxd.service.repository.ContentsAPIServiceRepository;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Stateless
public class ContentsAPIService {

	

   @EJB
   private ContentsAPIServiceRepository rontentsAPIServiceRepository;
   
   
   public String getAppAbsolutePath(UriInfo uriInfo) {
	   
	   String baseUri = uriInfo.getBaseUri().toString();
       String requestUri = uriInfo.getRequestUri().toString();

       return "Base URI: " + baseUri + "\nRequest URI: " + requestUri;
   }

    public Response getAll() {
        try {
            List<ContentsModel> contents = rontentsAPIServiceRepository.getALLAsset();
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
            ContentsModel contents = rontentsAPIServiceRepository.getAssetById(id);
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

    public Response postDataRequest(String receivedModel,UriInfo uriInfo) {
        System.out.println("Received request body: " + receivedModel.toString());
        try {

            int id= rontentsAPIServiceRepository.createAsset(receivedModel.toString());
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
