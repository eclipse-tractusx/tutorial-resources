package com.eclipse.mxd.repository.Impl;

import com.eclipse.mxd.model.ContentRandomResponse;
import com.eclipse.mxd.repository.ContentRamdomRepository;
import com.eclipse.mxd.util.RandomWordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.Random;
import java.util.logging.Logger;

@ApplicationScoped
public class ContentRandomRepositoryImpl implements ContentRamdomRepository {
    private static final Logger logger = Logger.getLogger(ContentRandomRepositoryImpl.class.getName());

    @Override
    public Response getRandomContent() {
        try {
            Random random = new Random();
            int random_number = random.nextInt(100) + 1;
            String title = RandomWordUtil.generateRandomWord();
            String text = RandomWordUtil.generateRandomWord();
            ContentRandomResponse contentRandomResponse = new ContentRandomResponse();
            contentRandomResponse.setId(random_number);
            contentRandomResponse.setText(text);
            contentRandomResponse.setTitle(title);
            ObjectMapper objectMapper = new ObjectMapper();
            return Response.ok(objectMapper.writeValueAsString(contentRandomResponse)).build();

        }catch (Exception e){
            logger.info("error "+e.getMessage());
        }

        return Response.ok("Random Json Not Generated !").build();
    }

}
