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

import org.eclipse.mxd.model.TransfersResponse;
import org.eclipse.mxd.util.Constants;
import org.eclipse.mxd.util.HibernateUtil;
import org.eclipse.mxd.entity.Content;
import org.eclipse.mxd.entity.Transfer;
import org.eclipse.mxd.repository.TransferRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class TransferRepositoryImpl implements TransferRepository {

    private static final Logger logger = Logger.getLogger(TransferRepositoryImpl.class.getName());

    @Override
    public String createTransferWithID(String asset, String contents, String id) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                Transfer transfer = new Transfer();
                transfer.setTransferID(id);
                transfer.setAsset(asset);
                transfer.setCreatedDate(new Date());
                transfer.setUpdatedDate(new Date());
                transfer.setContents(contents);
                session.getTransaction().begin();
                session.persist(transfer);
                session.getTransaction().commit();
                session.close();
                if (transfer.getTransferID() != null) {
                    return transfer.getTransferID();
                } else {
                    return Constants.EMPTYSTRING;
                }
            }


        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return Constants.EMPTYSTRING;
    }

    @Override
    public TransfersResponse getTransferById(String id) {
        Transfer transfer = new Transfer();
        TransfersResponse transfersData = new TransfersResponse();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                transfer = session.find(Transfer.class, id);
              logger.info("line 83 transferRepoImpl "+transfer.toString());
                session.close();
            }
            transfersData = new TransfersResponse(transfer.getAsset(), transfer.getContents(), transfer.getTransferID(), transfer.getCreatedDate(), transfer.getUpdatedDate());
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return transfersData;
    }

    @Override
    public void updateTransfer(Long id, String asset) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                Transfer transfer = session.find(Transfer.class, id);
                transfer.setAsset(asset);
                session.close();
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void deleteTransfer(Long id) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                Transfer transfer = session.find(Transfer.class, id);
                session.getTransaction().begin();
                session.remove(transfer);
                session.getTransaction().commit();
                session.close();
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public List<TransfersResponse> getAllTransfers() {
        List<TransfersResponse> response = new ArrayList<TransfersResponse>();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                Query query = session.createQuery("select e from Content e", Content.class);
                List<Transfer> transferList = query.getResultList();
                transferList.forEach(data -> {
                    TransfersResponse transfersResponse = new TransfersResponse(data.getAsset(), data.getContents(), data.getCreatedDate(), data.getCreatedDate());
                    response.add(transfersResponse);
                });
                session.close();
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return response.isEmpty() ? null : response;
    }
}
