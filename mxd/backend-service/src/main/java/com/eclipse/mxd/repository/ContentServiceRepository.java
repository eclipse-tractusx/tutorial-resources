package com.eclipse.mxd.repository;

import com.eclipse.mxd.model.ContentsModel;

import java.util.List;

public interface ContentServiceRepository {

    Long createAsset(String assetName);

    ContentsModel getAssetById(Long id);

    void updateAsset(Long id, String newAssetName);

    void deleteAsset(Long id);

    List<ContentsModel> getAllAssets();
}
