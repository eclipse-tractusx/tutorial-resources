package com.eclipse.mxd.model;

import java.util.Date;

public class TransfersModel {

    private Long id;
    private String asset;
    private String contents;
    private String transferid;
    private Date createdDate;
    private Date updatedDate;

    public TransfersModel() {

    }

    public TransfersModel(Long id, String asset, String contents, String transferid, Date createdDate,
                          Date updatedDate) {
        super();
        this.id = id;
        this.asset = asset;
        this.contents = contents;
        this.transferid = transferid;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public TransfersModel(Long id, String asset,String contents, Date createdDate, Date updatedDate) {
        this.id = id;
        this.asset = asset;
        this.contents = contents;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }




    public Long getId() {
        return id;
    }

    public void setId(int Long) {
        this.id = id;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getTransferid() {
        return transferid;
    }

    public void setTransferid(String transferid) {
        this.transferid = transferid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "TransfersModel [id=" + id + ", asset=" + asset + ", contents=" + contents + ", transferid=" + transferid
                + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate + "]";
    }
}
