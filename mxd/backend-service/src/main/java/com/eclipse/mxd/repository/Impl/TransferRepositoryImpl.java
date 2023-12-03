package com.eclipse.mxd.repository.Impl;

import com.eclipse.mxd.util.HibernateUtil;
import com.eclipse.mxd.entity.Content;
import com.eclipse.mxd.entity.Transfer;
import com.eclipse.mxd.model.TransfersModel;
import com.eclipse.mxd.repository.TransferRepository;
import com.eclipse.mxd.util.DateTimeUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class TransferRepositoryImpl implements TransferRepository {

    private static final Logger logger = Logger.getLogger(TransferRepositoryImpl.class.getName());

    @Override
    public Long createTransferWithID(String asset, String contents, String id) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            if (session != null) {
                    Transfer transfer = new Transfer();
                    transfer.setTransferid(id);
                    transfer.setAsset(asset);
                     transfer.setCreatedDate(DateTimeUtil.getCurrentDateTimeInAsiaKolkata());
                     transfer.setUpdatedDate(DateTimeUtil.getCurrentDateTimeInAsiaKolkata());
                    transfer.setContents(contents);
                    session.getTransaction().begin();
                    session.persist(transfer);
                    session.getTransaction().commit();
                    session.close();
                    if(transfer.getId() != null){
                        return  transfer.getId();
                    }else {
                        return -1L;
                    }
                }


        }catch (Exception e){
            logger.info(e.getMessage());
        }finally {

        }

        return null;
    }

    @Override
    public TransfersModel getTransferById(String id) {
        Transfer transfer = new Transfer();
        TransfersModel transfersData = new TransfersModel();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
                if (session != null) {
                    transfer =  session.createQuery("SELECT t FROM Transfer t WHERE t.transferid = :transferid", Transfer.class)
                            .setParameter("transferid", id)
                            .getSingleResult();
                    session.close();
                }
            transfersData = new TransfersModel(transfer.getId(),transfer.getAsset(),transfer.getContents(),transfer.getCreatedDate(), transfer.getUpdatedDate());
        }catch (Exception e){
            logger.info(e.getMessage());
        }
        return transfersData;
    }

    @Override
    public void updateTransfer(Long id, String newAsset) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
                if (session != null) {
                    Transfer transfer =  session.find(Transfer.class, id);
                    transfer.setAsset(newAsset);
                    session.close();
                }

        }catch (Exception e){
            logger.info(e.getMessage());
        }finally {

        }
    }

    @Override
    public void deleteTransfer(Long id) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
                if (session != null) {
                    Transfer transfer =  session.find(Transfer.class, id);
                    session.getTransaction().begin();
                    session.remove(transfer);
                    session.getTransaction().commit();
                    session.close();
                }

        }catch (Exception e){
            logger.info(e.getMessage());
        }finally {

        }
    }

    @Override
    public List<TransfersModel> getAllTransfers() {
        List<TransfersModel> response = new ArrayList<TransfersModel>();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
                if (session != null) {
                    Query query = session.createQuery("select e from Content e", Content.class);
                    List<Transfer> transferList =  query.getResultList();
                    transferList.forEach(data->{
                        TransfersModel transfersModel = new TransfersModel(data.getId(),data.getAsset(), data.getContents(),data.getCreatedDate(), data.getCreatedDate());
                        response.add(transfersModel);
                    });
                    session.close();
                }

        }catch (Exception e){
            logger.info(e.getMessage());
        }finally {

        }
        return response.isEmpty() ? null : response;
    }


}