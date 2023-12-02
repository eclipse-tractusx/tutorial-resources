package com.eclipse.mxd.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Transfer_SEQ")
    @SequenceGenerator(name = "Transfer_SEQ", sequenceName = "Transfer_SEQ", allocationSize = 1)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String asset;
    @Column(columnDefinition = "TEXT")
    private String contents;

    private String transferid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date updatedDate;

    public Transfer() {

    }

    public Transfer(Long id, String asset, String contents, String transferid, Date createdDate, Date updatedDate) {
        this.id = id;
        this.asset = asset;
        this.contents = contents;
        this.transferid = transferid;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public Long getId() {
        return id;
    }

    public String getAsset() {
        return asset;
    }

    public String getContents() {
        return contents;
    }

    public String getTransferid() {
        return transferid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setTransferid(String transferid) {
        this.transferid = transferid;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", asset='" + asset + '\'' +
                ", contents='" + contents + '\'' +
                ", transferid='" + transferid + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
