package com.eclipse.mxd.sercvice.Impl;

import com.eclipse.mxd.entity.Content;
import com.eclipse.mxd.model.ContentM;
import com.eclipse.mxd.model.ContentsModel;
import com.eclipse.mxd.repository.ContentServiceRepository;
import com.eclipse.mxd.repository.Impl.ContentRepositoryImpl;
import com.eclipse.mxd.sercvice.ContentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@ApplicationScoped
public class ContentServiceImpl implements ContentService {

    private static final Logger logger = Logger.getLogger(ContentServiceImpl.class.getName());

    private final ContentServiceRepository contentServiceRepository = new ContentRepositoryImpl();

    @Override
    public Response getAll() {
        try {
            List<ContentsModel> contentResponse = this.contentServiceRepository.getAllAssets();
            if (!contentResponse.isEmpty()) {
                List<ContentM> contentsModels=new ArrayList<ContentM>();
                for(ContentsModel contentsModel:contentResponse)
                {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(contentsModel.getAsset());
                    ContentM contentsModel1=new ContentM();
                    contentsModel1.setId(contentsModel.getId());
                    contentsModel1.setAsset(jsonNode);
                    contentsModel1.setCreatedDate(contentsModel.getCreatedDate());
                    contentsModel1.setUpdatedDate(contentsModel.getUpdatedDate());
                    contentsModels.add(contentsModel1);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                return Response.ok(objectMapper.writeValueAsString(contentsModels)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Asset not found").build();
            }
        }catch (Exception e){
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public Response getById(Long id) {
       try {
          ContentsModel content =  this.contentServiceRepository.getAssetById(id);
           if (content != null) {
               ObjectMapper objectMapper = new ObjectMapper();
               JsonNode jsonNode = objectMapper.readTree(content.getAsset());
               ContentM contentsModel1=new ContentM();
               contentsModel1.setId(content.getId());
               contentsModel1.setCreatedDate(content.getCreatedDate());
               contentsModel1.setUpdatedDate(content.getUpdatedDate());
               contentsModel1.setAsset(jsonNode);
               return Response.ok(objectMapper.writeValueAsString(contentsModel1)).build();
           } else {
               return Response.status(Response.Status.NOT_FOUND).entity("Asset not found").build();
           }
       }catch (Exception e){
           logger.info(e.getMessage());
           return Response.serverError().entity("Internal server error").build();
       }
    }

    @Override
    public Response postContent(String requestBody, UriInfo uriInfo) {
        try {
           Long id = contentServiceRepository.createAsset(requestBody);
            if(id != null){
                logger.info(uriInfo.getAbsolutePath()+"");
                return Response.ok(uriInfo.getAbsolutePath()+""+id).build();
            }else{
                return Response.status(Response.Status.NOT_FOUND).entity("Asset Not Created").build();
            }
        }catch (Exception e){
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }
}
