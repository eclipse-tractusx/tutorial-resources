package com.eclipse.mxd.repository.Impl;

import com.eclipse.mxd.HibernateUtil;
import com.eclipse.mxd.entity.Content;
import com.eclipse.mxd.model.ContentsModel;
import com.eclipse.mxd.repository.ContentServiceRepository;
import com.eclipse.mxd.util.DateTimeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class ContentRepositoryImpl implements ContentServiceRepository {

    private static final Logger logger = Logger.getLogger(ContentRepositoryImpl.class.getName());

    @Override
    public Long createAsset(String assetName) {
        try {
            Content content =  saveContent(assetName);
            if(content.getId() != null){
                logger.info(content.toString());
                return content.getId();
            }else{
                return -1L;
            }
        }catch (Exception e){
            logger.info(e.getMessage());
            return -1L;
        }
    }

    public Content saveContent(String assetName) {
        Content content = new Content();
       try {
           Session session = HibernateUtil.getSessionFactory().openSession();
               if (session != null) {
                   content.setAsset(assetName);
                   content.setCreatedDate(DateTimeUtil.getCurrentDateTimeInAsiaKolkata());
                   content.setUpdatedDate(DateTimeUtil.getCurrentDateTimeInAsiaKolkata());
                   session.getTransaction().begin();
                   session.persist(content);
                   session.getTransaction().commit();
                   session.close();
               }

       }catch (Exception e){
           logger.info(e.getMessage());
       }
        return content;
    }

    @Override
    public ContentsModel getAssetById(Long id) {
        ContentsModel response=new ContentsModel();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

                if (session != null) {
                    Content content = session.find(Content.class, id);
                   response = new ContentsModel(content.getId(), content.getAsset(), content.getCreatedDate(), content.getUpdatedDate());
                    session.close();
                }
        }
        catch (Exception e)
        {
            logger.info(e.getMessage());
        }
        return response;
    }

    @Override
    public void updateAsset(Long id, String newAssetName) {
      try {
          Session   session = HibernateUtil.getSessionFactory().openSession();
              if (session != null) {
                  Content content = session.find(Content.class, id);
                  content.setAsset(newAssetName);
                  session.getTransaction().begin();
                  session.persist(content);
                  session.getTransaction().commit();
                  session.close();


          }
      }catch (Exception e){
          logger.info(e.getMessage());
      }
    }

    @Override
    public void deleteAsset(Long id) {
        try {
            Session   session = HibernateUtil.getSessionFactory().openSession();
                // Checking if the EntityManager is successfully created
                if (session != null) {
                    Content content =  session.find(Content.class, id);
                    session.getTransaction().begin();
                    session.remove(content);
                    session.getTransaction().begin();
                    session.close();
                }

        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }

    @Override
    public List<ContentsModel> getAllAssets() {
        List<ContentsModel> response = new ArrayList<ContentsModel>();
        try {
            Session   session = HibernateUtil.getSessionFactory().openSession();
                if (session != null) {
                    Query query = session.createQuery("select e from Content e", Content.class);
                    List<Content> contentList = query.getResultList();
                    contentList.forEach(data -> {
                       ContentsModel contentsModel = new ContentsModel(data.getId(), data.getAsset(), data.getCreatedDate(), data.getCreatedDate());
                        response.add(contentsModel);
                    });
                    session.close();

            }
        }catch (Exception e){
            logger.info(e.getMessage());
        }
        return response.isEmpty() ? null : response;
    }

}
