package org.eclipse.mxd.repository.impl;

import org.eclipse.mxd.dao.ContentsDAO;
import org.eclipse.mxd.model.ContentsModel;
import org.eclipse.mxd.repository.ContentsAPIServiceRepository;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@Stateless
public class ContentsAPIRepositoryImpl implements ContentsAPIServiceRepository {

	@Inject
	ContentsDAO contentsDAO;

	@Transactional
	public int createAsset(String assetName) {
		return contentsDAO.createAsset(assetName);
	}

	public ContentsModel getAssetById(int id) {
		return contentsDAO.getAssetById(id);
	}

	@Transactional
	public void updateAsset(int id, String newAssetName) {
		contentsDAO.updateAsset(id, newAssetName);
	}

	@Transactional
	public void deleteAsset(int id) {
		contentsDAO.deleteAsset(id);
	}

	public List<ContentsModel> getAllAssets() {
		return contentsDAO.getALLAsset();
	}
}
