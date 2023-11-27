package org.eclipse.mxd.repository;

import org.eclipse.mxd.model.TransfersModel;

import java.util.List;

public interface TransfersAPIRepository {

	String createTransferWithID(String asset, String contents, String id);

	TransfersModel getTransferById(String id);

	void updateTransfer(int id, String newAsset);

	void deleteTransfer(int id);

	List<TransfersModel> getAllTransfers();
}
