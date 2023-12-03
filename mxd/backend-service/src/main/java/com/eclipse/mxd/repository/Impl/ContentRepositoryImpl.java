/*******************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *    Contributors:Ravinder Kumar
 *    Backend-API and implementation
 * 
 ******************************************************************************/
package com.eclipse.mxd.repository.Impl;

import com.eclipse.mxd.util.HibernateUtil;
import com.eclipse.mxd.entity.Content;
import com.eclipse.mxd.model.ContentsModel;
import com.eclipse.mxd.repository.ContentServiceRepository;
import com.eclipse.mxd.util.DateTimeUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class ContentRepositoryImpl implements ContentServiceRepository {

    private static final Logger logger = Logger.getLogger(ContentRepositoryImpl.class.getName());

    @Override
    public Long createAsset(String assetName) {
        try {
            Content content = saveContent(assetName);
            if (content.getId() != null) {
                logger.info(content.toString());
                return content.getId();
            } else {
                return -1L;
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return -1L;
        } finally {

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

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return content;
    }

    @Override
    public ContentsModel getAssetById(Long id) {
        ContentsModel response = new ContentsModel();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();

            if (session != null) {
                Content content = session.find(Content.class, id);
                response = new ContentsModel(content.getId(), content.getAsset(), content.getCreatedDate(), content.getUpdatedDate());
                session.close();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return response;
    }

    @Override
    public void updateAsset(Long id, String newAssetName) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                Content content = session.find(Content.class, id);
                session.getTransaction().begin();
                session.persist(content);
                session.getTransaction().commit();
                session.close();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void deleteAsset(Long id) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                Content content = session.find(Content.class, id);
                session.getTransaction().begin();
                session.remove(content);
                session.getTransaction().begin();
                session.close();
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public List<ContentsModel> getAllAssets() {
        List<ContentsModel> response = new ArrayList<ContentsModel>();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                Query query = session.createQuery("select e from Content e", Content.class);
                List<Content> contentList = query.getResultList();
                contentList.forEach(data -> {
                    ContentsModel contentsModel = new ContentsModel(data.getId(), data.getAsset(), data.getCreatedDate(), data.getCreatedDate());
                    response.add(contentsModel);
                });
                session.close();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return response.isEmpty() ? null : response;
    }


}
