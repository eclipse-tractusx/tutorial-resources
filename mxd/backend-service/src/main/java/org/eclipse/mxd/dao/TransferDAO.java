package org.eclipse.mxd.dao;

import org.eclipse.mxd.database.DatabaseConnector;
import org.eclipse.mxd.model.TransfersModel;
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
public class TransferDAO {

	@Inject
	DatabaseConnector dbConnector;

	// Constants for SQL queries
	private static final String CREATE_TRANSFER_WITH_ID = QueryConstants.CREATE_TRANSFER_WITH_ID;
	private static final String GET_TRANSFER_BY_ID = QueryConstants.GET_TRANSFER_BY_ID;
	private static final String UPDATE_TRANSFER = QueryConstants.UPDATE_TRANSFER;
	private static final String DELETE_TRANSFER = QueryConstants.DELETE_TRANSFER;
	private static final String GET_ALL_TRANSFERS = QueryConstants.GET_ALL_TRANSFERS;

	private Connection getConnection() {
		return dbConnector.getConnection();
	}

	public String createTransferWithID(String asset, String contents, String id) {
		try {
			try (PreparedStatement statement = getConnection().prepareStatement(CREATE_TRANSFER_WITH_ID)) {
				statement.setString(1, id);
				statement.setString(2, asset);
				statement.setString(3, contents);

				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						return id;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public TransfersModel getTransferById(String id) {
		try {
			TransfersModel transfersModel;
			try (PreparedStatement statement = getConnection().prepareStatement(GET_TRANSFER_BY_ID)) {
				statement.setString(1, id);

				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						transfersModel = new TransfersModel(resultSet.getInt("id"), resultSet.getString("asset"),
								resultSet.getString("contents"), resultSet.getString("transferid"),
								resultSet.getTimestamp("createdDate"), resultSet.getTimestamp("updatedDate"));
						return transfersModel;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void updateTransfer(int id, String newAsset, String newContents) {
		try {
			try (PreparedStatement statement = getConnection().prepareStatement(UPDATE_TRANSFER)) {
				statement.setString(1, newAsset);
				statement.setString(2, newContents);
				statement.setInt(3, id);

				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteTransfer(int id) {
		try {
			try (PreparedStatement statement = getConnection().prepareStatement(DELETE_TRANSFER)) {
				statement.setInt(1, id);

				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<TransfersModel> getAllTransfers() {
		List<TransfersModel> transfers = new ArrayList<>();

		try {
			try (PreparedStatement statement = getConnection().prepareStatement(GET_ALL_TRANSFERS)) {
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						TransfersModel contentsModel = new TransfersModel(resultSet.getInt("id"),
								resultSet.getString("asset"), resultSet.getString("contents"),
								resultSet.getString("transferid"), resultSet.getTimestamp("createdDate"),
								resultSet.getTimestamp("updatedDate"));
						transfers.add(contentsModel);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return transfers.isEmpty() ? null : transfers;
	}
}
