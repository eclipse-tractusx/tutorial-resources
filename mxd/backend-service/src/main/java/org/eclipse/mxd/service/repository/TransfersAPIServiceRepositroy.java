package org.eclipse.mxd.service.repository;

import org.eclipse.mxd.database.DatabaseConnector;
import org.eclipse.mxd.model.TransfersModel;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TransfersAPIServiceRepositroy {

    @EJB
    private DatabaseConnector connection;


    public int createTransferWithID(String asset, String contents, String id) {
        try {
            String query = "INSERT INTO transfer (id, asset, contents, createdDate, updatedDate) VALUES (?,?, ?, now(), now()) RETURNING id";

            try (PreparedStatement statement = connection.getConnect().prepareStatement(query)) {
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

    public TransfersModel getTransferById(int id) {
        try {
            TransfersModel transfersModel;
            String query = "SELECT * FROM transfer WHERE id = ?";

            try (PreparedStatement statement = connection.getConnect().prepareStatement(query)) {
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

    public void updateTransfer(int id, String newAsset, String newContents) {
        try {
            String query = "UPDATE transfer SET asset = ?, contents = ?, updatedDate = now() WHERE id = ?";

            try (PreparedStatement statement = connection.getConnect().prepareStatement(query)) {
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
            String query = "DELETE FROM transfer WHERE id = ?";

            try (PreparedStatement statement = connection.getConnect().prepareStatement(query)) {
                statement.setInt(1, id);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TransfersModel> getAllTransfers(Connection connection) {
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
