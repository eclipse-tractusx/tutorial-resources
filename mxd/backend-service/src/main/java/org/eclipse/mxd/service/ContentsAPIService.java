package org.eclipse.mxd.service;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public interface ContentsAPIService {

	Response getAll();

	Response getById(int id);

	Response postContent(String requestBody, UriInfo uriInfo);
}
