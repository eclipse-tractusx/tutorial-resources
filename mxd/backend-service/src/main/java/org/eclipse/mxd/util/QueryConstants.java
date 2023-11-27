package org.eclipse.mxd.util;

public class QueryConstants {

	public static final String CREATE_ASSET = "INSERT INTO assets (asset, createdDate, updatedDate) VALUES (?, now(), now()) RETURNING id";

    public static final String GET_ASSET_BY_ID = "SELECT * FROM assets WHERE id = ?";

    public static final String UPDATE_ASSET = "UPDATE assets SET asset = ?, updatedDate = now() WHERE id = ?";

    public static final String DELETE_ASSET = "DELETE FROM assets WHERE id = ?";

    public static final String GET_ALL_ASSETS = "SELECT * FROM assets";
    
    public static final String CREATE_TRANSFER_WITH_ID = "INSERT INTO transfer (transferid, asset, contents, createdDate, updatedDate) VALUES (?, ?, ?, now(), now()) RETURNING id";

    public static final String GET_TRANSFER_BY_ID = "SELECT * FROM transfer WHERE transferid = ?";

    public static final String UPDATE_TRANSFER = "UPDATE transfer SET asset = ?, contents = ?, updatedDate = now() WHERE id = ?";

    public static final String DELETE_TRANSFER = "DELETE FROM transfer WHERE id = ?";

    public static final String GET_ALL_TRANSFERS = "SELECT * FROM transfer";
}
