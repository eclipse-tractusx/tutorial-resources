package com.eclipse.mxd.sercvice.Impl;

import com.eclipse.mxd.repository.ContentRamdomRepository;
import com.eclipse.mxd.repository.Impl.ContentRamdomRepositoryImpl;
import com.eclipse.mxd.sercvice.ContentRamdomService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

@ApplicationScoped
public class ContentRamdomServiceImpl implements ContentRamdomService {

    private static final Logger logger = Logger.getLogger(ContentRamdomServiceImpl.class.getName());

    private final ContentRamdomRepository  contentRamdomRepository = new ContentRamdomRepositoryImpl();

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
