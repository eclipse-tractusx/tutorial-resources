package com.eclipse.mxd.service.Impl;

import com.eclipse.mxd.repository.ContentRamdomRepository;
import com.eclipse.mxd.repository.Impl.ContentRandomRepositoryImpl;
import com.eclipse.mxd.service.ContentRandomService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

@ApplicationScoped
public class ContentRandomServiceImpl implements ContentRandomService {

    private static final Logger logger = Logger.getLogger(ContentRandomServiceImpl.class.getName());

    private final ContentRamdomRepository  contentRamdomRepository = new ContentRandomRepositoryImpl();

    @Override
    public Response getRandomContent() {
        try {
            return contentRamdomRepository.getRandomContent();
        }catch (Exception e){
            logger.info(e.getMessage());
            return Response.ok(e.getMessage()).build();
        }
    }
}
