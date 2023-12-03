package com.eclipse.mxd.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Content_SEQ")
    @SequenceGenerator(name = "Content_SEQ", sequenceName = "Content_SEQ", allocationSize = 1)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String asset;
    @Column
    private Date createdDate;
    @Column
    private Date updatedDate;

    public Content() {

    }

    public Content(Long id, String asset, Date createdDate, Date updatedDate) {
        this.id = id;
        this.asset = asset;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getId() {
        return id;
    }

    public String getAsset() {
        return asset;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return "ContentEntity{" + "id=" + id + ", asset='" + asset + '\'' + ", createdDate=" + createdDate
                + ", updatedDate=" + updatedDate + '}';
    }
}
