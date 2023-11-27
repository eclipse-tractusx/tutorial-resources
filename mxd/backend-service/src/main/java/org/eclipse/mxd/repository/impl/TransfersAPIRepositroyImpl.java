package org.eclipse.mxd.repository.impl;

import org.eclipse.mxd.dao.TransferDAO;
import org.eclipse.mxd.entity.TransfersEntity;
import org.eclipse.mxd.model.TransfersModel;
import org.eclipse.mxd.repository.TransfersAPIRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;

import javax.transaction.Transactional;
import java.util.List;

@Stateless
public class TransfersAPIRepositroyImpl implements TransfersAPIRepository {

	@Inject
	TransferDAO transferDAO;

	public String createTransferWithID(String asset, String contents, String id) {
		
		
		return transferDAO.createTransferWithID(asset, contents, id);
	}

	public TransfersModel getTransferById(String id) {
		return transferDAO.getTransferById(id);
	}

	@Transactional
	public void updateTransfer(int id, String newAsset, String newContents) {
		transferDAO.updateTransfer(id, newAsset, newContents);
	}

	@Transactional
	public void deleteTransfer(int id) {
		transferDAO.deleteTransfer(id);
	}

	public List<TransfersModel> getAllTransfers() {
		return transferDAO.getAllTransfers();
	}

	@Override
	public void updateTransfer(int id, String newAsset) {
		transferDAO.updateTransfer(id, newAsset, newAsset);
	}

}
