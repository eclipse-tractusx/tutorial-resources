package org.eclipse.mxd.repository;

import org.eclipse.mxd.model.ContentsModel;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ContentsAPIServiceRepository {

    int createAsset(String assetName);

    ContentsModel getAssetById(int id);

    void updateAsset(int id, String newAssetName);

    void deleteAsset(int id);

    List<ContentsModel> getAllAssets();
}
