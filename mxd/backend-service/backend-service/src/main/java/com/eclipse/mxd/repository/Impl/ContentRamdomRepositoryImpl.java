package com.eclipse.mxd.repository.Impl;

import com.eclipse.mxd.model.ContentRendomResponse;
import com.eclipse.mxd.repository.ContentRamdomRepository;
import com.eclipse.mxd.util.RendomWordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.Random;
import java.util.logging.Logger;

@ApplicationScoped
public class ContentRamdomRepositoryImpl implements ContentRamdomRepository {

    private static final Logger logger = Logger.getLogger(ContentRamdomRepositoryImpl.class.getName());

    @Override
    public Response getRandomContent() {
        try {
            Random random = new Random();
            int random_number = random.nextInt(100) + 1;
            String title = RendomWordUtil.generateRandomWord();
            String text = RendomWordUtil.generateRandomWord();

            ContentRendomResponse contentRendomResponse = new ContentRendomResponse();
            contentRendomResponse.setId(random_number);
            contentRendomResponse.setText(text);
            contentRendomResponse.setTitle(title);

            ObjectMapper objectMapper = new ObjectMapper();
            return Response.ok(objectMapper.writeValueAsString(contentRendomResponse)).build();

        }catch (Exception e){
            logger.info(e.getMessage());
            return Response.ok(e.getMessage()).build();
        }
    }
}
