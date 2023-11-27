package org.eclipse.mxd.service.impl;

import org.eclipse.mxd.model.ContentsModel;
import org.eclipse.mxd.repository.ContentsAPIServiceRepository;
import org.eclipse.mxd.service.ContentsAPIService;

import com.google.gson.Gson;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class ContentsAPIServiceImpl implements ContentsAPIService {
	
	private static final Logger logger = Logger.getLogger(ContentsAPIServiceImpl.class.getName());

	@Inject
	private ContentsAPIServiceRepository contentsRepository;

	public Response getAll() {
		try {
			List<ContentsModel> contents = contentsRepository.getAllAssets();
			if (contents != null) {
				return Response.ok(contents).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Asset not found").build();
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
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
			logger.info(e.getMessage());
			return Response.serverError().entity("Internal server error").build();
		}
	}

	public Response postContent(String requestBody, UriInfo uriInfo) {
		try {
			Gson gson = new Gson();
			String contentRequestJson = gson.toJson(requestBody);
			int id = contentsRepository.createAsset(contentRequestJson);
			if (id != -1) {
				System.out.println(uriInfo.getAbsolutePath());
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
