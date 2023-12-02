package com.eclipse.mxd.sercvice;

import com.google.gson.JsonObject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

public interface ContentService {
    Response getAll();

    Response getById(Long id);

    Response postContent(String requestBody, UriInfo uriInfo);
}
