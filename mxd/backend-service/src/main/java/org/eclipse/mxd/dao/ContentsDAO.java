package org.eclipse.mxd.dao;

import org.eclipse.mxd.database.DatabaseConnector;
import org.eclipse.mxd.model.ContentsModel;
import org.eclipse.mxd.util.QueryConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ContentsDAO {

	@Inject
	DatabaseConnector dbConnector;

	private static final String CREATE_ASSET = QueryConstants.CREATE_ASSET;
	private static final String GET_ASSET_BY_ID = QueryConstants.GET_ASSET_BY_ID;
	private static final String UPDATE_ASSET = QueryConstants.UPDATE_ASSET;
	private static final String DELETE_ASSET = QueryConstants.DELETE_ASSET;
	private static final String GET_ALL_ASSETS = QueryConstants.GET_ALL_ASSETS;

	public int createAsset(String assetName) {
		try {
			try (PreparedStatement statement = getConnection().prepareStatement(CREATE_ASSET)) {
				statement.setString(1, assetName);

				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public ContentsModel getAssetById(int id) {
		try {
			ContentsModel contentsModel;
			try (PreparedStatement statement = getConnection().prepareStatement(GET_ASSET_BY_ID)) {
				statement.setInt(1, id);

				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						contentsModel = new ContentsModel(resultSet.getInt("id"), resultSet.getString("asset"),
								resultSet.getTimestamp("createdDate"), resultSet.getTimestamp("updatedDate"));
						return contentsModel;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void updateAsset(int id, String newAssetName) {
		try {
			try (PreparedStatement statement = getConnection().prepareStatement(UPDATE_ASSET)) {
				statement.setString(1, newAssetName);
				statement.setInt(2, id);

				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteAsset(int id) {
		try {
			try (PreparedStatement statement = getConnection().prepareStatement(DELETE_ASSET)) {
				statement.setInt(1, id);

				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<ContentsModel> getALLAsset() {
		List<ContentsModel> contentsList = new ArrayList<>();

		try {
			try (PreparedStatement statement = getConnection().prepareStatement(GET_ALL_ASSETS)) {
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						ContentsModel contentsModel = new ContentsModel(resultSet.getInt("id"),
								resultSet.getString("asset"), resultSet.getTimestamp("createdDate"),
								resultSet.getTimestamp("updatedDate"));
						contentsList.add(contentsModel);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return contentsList.isEmpty() ? null : contentsList;
	}

	private Connection getConnection() {
		return dbConnector.getConnection();
	}
}
