package org.eclipse.mxd.model;

public class ContentsModel {
    private int id;
    private String asset;
    private java.sql.Timestamp createdDate;
    private java.sql.Timestamp updatedDate;

    public ContentsModel(int id, String asset, java.sql.Timestamp createdDate, java.sql.Timestamp updatedDate) {
        this.id = id;
        this.asset = asset;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "Asset{" + "id=" + id + ", asset='" + asset + '\'' + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate + '}';
    }
}
