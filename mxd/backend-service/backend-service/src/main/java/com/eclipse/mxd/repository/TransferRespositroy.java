package com.eclipse.mxd.repository;

import com.eclipse.mxd.model.TransfersModel;

import java.util.List;

public interface TransferRespositroy {

    Long createTransferWithID(String asset, String contents, String id);

    TransfersModel getTransferById(String id);

    void updateTransfer(Long id, String newAsset);

    void deleteTransfer(Long id);

    List<TransfersModel> getAllTransfers();

}
