package com.eclipse.mxd.service.Impl;

import com.eclipse.mxd.model.ContentModelResponse;
import com.eclipse.mxd.model.ContentsModel;
import com.eclipse.mxd.repository.ContentServiceRepository;
import com.eclipse.mxd.repository.Impl.ContentRepositoryImpl;
import com.eclipse.mxd.service.ContentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
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
            if (contentResponse != null && !contentResponse.isEmpty()) {
                List<ContentModelResponse> contentsModels = new ArrayList<ContentModelResponse>();
                for (ContentsModel contentsModel : contentResponse) {
                    JsonNode jsonNode = new ObjectMapper().readTree(contentsModel.getAsset());
                    ContentModelResponse contentsModel1 = new ContentModelResponse();
                    contentsModel1.setId(contentsModel.getId());
                    contentsModel1.setAsset(jsonNode);
                    contentsModel1.setCreatedDate(contentsModel.getCreatedDate());
                    contentsModel1.setUpdatedDate(contentsModel.getUpdatedDate());
                    contentsModels.add(contentsModel1);
                }
                return Response.ok(new ObjectMapper().writeValueAsString(contentsModels)).build();

            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Asset Not Found").build();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public Response getById(Long id) {
        try {
            ContentsModel content = this.contentServiceRepository.getAssetById(id);
            if (content != null && content.getId()!=null) {
                JsonNode jsonNode = new ObjectMapper().readTree(content.getAsset());
                ContentModelResponse contentModelRes = new ContentModelResponse();
                contentModelRes.setId(content.getId());
                contentModelRes.setCreatedDate(content.getCreatedDate());
                contentModelRes.setUpdatedDate(content.getUpdatedDate());
                contentModelRes.setAsset(jsonNode);
                return Response.ok(new ObjectMapper().writeValueAsString(contentModelRes)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Asset Not Found With ID").build();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public Response postContent(String requestBody, UriInfo uriInfo) {
        try {
            Long id = contentServiceRepository.createAsset(requestBody);
            if (id != null) {
                logger.info(uriInfo.getAbsolutePath() + "");
                return Response.ok(uriInfo.getAbsolutePath() + "" + id).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Asset Not Created").build();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }
}
