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
package org.eclipse.mxd.repository.Impl;

import org.eclipse.mxd.util.HibernateUtil;
import org.eclipse.mxd.entity.Content;
import org.eclipse.mxd.model.ContentsRequest;
import org.eclipse.mxd.repository.ContentsRepository;
import org.eclipse.mxd.util.DateTimeUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class ContentsRepositoryImpl implements ContentsRepository {

    private static final Logger logger = Logger.getLogger(ContentsRepositoryImpl.class.getName());

    @Override
    public Long createAsset(String asset) {
        try {
            Content content = saveContent(asset);
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

    public Content saveContent(String asset) {
        Content content = new Content();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                content.setAsset(asset);
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
    public ContentsRequest getAssetById(Long id) {
        ContentsRequest response = new ContentsRequest();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                Content content = session.find(Content.class, id);
                response = new ContentsRequest(content.getId(), content.getAsset(), content.getCreatedDate(), content.getUpdatedDate());
                session.close();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return response;
    }

    @Override
    public void updateAsset(Long id, String asset) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                Content content = session.find(Content.class, id);
                content.setAsset(asset);
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
    public List<ContentsRequest> getAllAssets() {
        List<ContentsRequest> response = new ArrayList<ContentsRequest>();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                Query query = session.createQuery("select e from Content e", Content.class);
                List<Content> contentList = query.getResultList();
                contentList.forEach(data -> {
                    ContentsRequest contentsRequest = new ContentsRequest(data.getId(), data.getAsset(), data.getCreatedDate(), data.getCreatedDate());
                    response.add(contentsRequest);
                });
                session.close();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return response.isEmpty() ? null : response;
    }
}
