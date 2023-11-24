package org.eclipse.mxd.service.repository;

import org.eclipse.mxd.database.DatabaseConnector;
import org.eclipse.mxd.model.ContentsModel;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ContentsAPIServiceRepository {

    @EJB
    private DatabaseConnector connection;

    public int createAsset(String assetName) {
        try {
            String query = "INSERT INTO assets (asset, createdDate, updatedDate) VALUES (?, now(), now()) RETURNING id";

            try (PreparedStatement statement = connection.getConnect().prepareStatement(query)) {
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
            String query = "SELECT * FROM assets WHERE id = ?";

            try (PreparedStatement statement = connection.getConnect().prepareStatement(query)) {
                statement.setInt(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        contentsModel =new ContentsModel(resultSet.getInt("id"), resultSet.getString("asset"), resultSet.getTimestamp("createdDate"), resultSet.getTimestamp("updatedDate"));
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
            String query = "UPDATE assets SET asset = ?, updatedDate = now() WHERE id = ?";

            try (PreparedStatement statement = connection.getConnect().prepareStatement(query)) {
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
            String query = "DELETE FROM assets WHERE id = ?";

            try (PreparedStatement statement = connection.getConnect().prepareStatement(query)) {
                statement.setInt(1, id);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  List<ContentsModel> getALLAsset() {
        List<ContentsModel> contentsList = new ArrayList<>();

        try {
            String query = "SELECT * FROM assets";

            try (PreparedStatement statement = connection.getConnect().prepareStatement(query)) {

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        ContentsModel contentsModel = new ContentsModel(
                                resultSet.getInt("id"),
                                resultSet.getString("asset"),
                                resultSet.getTimestamp("createdDate"),
                                resultSet.getTimestamp("updatedDate")
                        );
                        contentsList.add(contentsModel);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contentsList.isEmpty() ? null : (List<ContentsModel>) contentsList;

    }



}
