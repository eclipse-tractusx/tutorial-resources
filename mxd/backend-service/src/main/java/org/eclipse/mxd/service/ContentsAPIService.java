package org.eclipse.mxd.service;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public interface ContentsAPIService {

    String getAppAbsolutePath(UriInfo uriInfo);

    Response getAll();

    Response getById(int id);

    Response postContent(String receivedModel, UriInfo uriInfo);
}
