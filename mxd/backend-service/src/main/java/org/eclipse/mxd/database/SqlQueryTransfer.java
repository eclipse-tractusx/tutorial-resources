package org.eclipse.mxd.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mxd.model.TransfersModel;
public class SqlQueryTransfer {
	
	  public static int createTransferWithID(String asset, String contents, Connection connection,String id) {
	        try {
	            String query = "INSERT INTO transfer (id, asset, contents, createdDate, updatedDate) VALUES (?,?, ?, now(), now()) RETURNING id";

	            try (PreparedStatement statement = connection.prepareStatement(query)) {
					statement.setString(1, id);
	                statement.setString(2, asset);
	                statement.setString(3, contents);

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

	    public static TransfersModel getTransferById(int id, Connection connection) {
	        try {
	        	TransfersModel transfersModel;
	            String query = "SELECT * FROM transfer WHERE id = ?";

	            try (PreparedStatement statement = connection.prepareStatement(query)) {
	                statement.setInt(1, id);

	                try (ResultSet resultSet = statement.executeQuery()) {
	                    if (resultSet.next()) {
	                    	transfersModel = new TransfersModel(
	                                resultSet.getInt("id"),
	                                resultSet.getString("asset"),
	                                resultSet.getString("contents"),
	                                resultSet.getTimestamp("createdDate"),
	                                resultSet.getTimestamp("updatedDate")
	                        );
	                        return transfersModel;
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        return null;
	    }

	    public static void updateTransfer(int id, String newAsset, String newContents, Connection connection) {
	        try {
	            String query = "UPDATE transfer SET asset = ?, contents = ?, updatedDate = now() WHERE id = ?";

	            try (PreparedStatement statement = connection.prepareStatement(query)) {
	                statement.setString(1, newAsset);
	                statement.setString(2, newContents);
	                statement.setInt(3, id);

	                statement.executeUpdate();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public static void deleteTransfer(int id, Connection connection) {
	        try {
	            String query = "DELETE FROM transfer WHERE id = ?";

	            try (PreparedStatement statement = connection.prepareStatement(query)) {
	                statement.setInt(1, id);

	                statement.executeUpdate();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public static List<TransfersModel> getAllTransfers(Connection connection) {
	        List<TransfersModel> transfers = new ArrayList<>();

	        try {
	            String query = "SELECT * FROM transfer";

	            try (PreparedStatement statement = connection.prepareStatement(query)) {

	                try (ResultSet resultSet = statement.executeQuery()) {
	                    while (resultSet.next()) {
	                    	TransfersModel contentsModel = new TransfersModel(
	                                resultSet.getInt("id"),
	                                resultSet.getString("asset"),
	                                resultSet.getString("contents"),
	                                resultSet.getTimestamp("createdDate"),
	                                resultSet.getTimestamp("updatedDate")
	                        );
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
