package org.eclipse.mxd.database;

import org.eclipse.mxd.model.ContentsModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlQuery {

    public static int createAsset(String assetName, Connection connection) {
        try {
            String query = "INSERT INTO assets (asset, createdDate, updatedDate) VALUES (?, now(), now()) RETURNING id";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
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

    public static ContentsModel getAssetById(int id, Connection connection) {
        try {
            String query = "SELECT * FROM assets WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return new ContentsModel(resultSet.getInt("id"), resultSet.getString("asset"), resultSet.getTimestamp("createdDate"), resultSet.getTimestamp("updatedDate"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void updateAsset(int id, String newAssetName, Connection connection) {
        try {
            String query = "UPDATE assets SET asset = ?, updatedDate = now() WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newAssetName);
                statement.setInt(2, id);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAsset(int id, Connection connection) {
        try {
            String query = "DELETE FROM assets WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}