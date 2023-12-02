package com.eclipse.mxd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentM {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("asset")
    private JsonNode asset;

    @JsonProperty("createdDate")
    private Date createdDate;

    @JsonProperty("updatedDate")
    private Date updatedDate;

    public ContentM(Long id, JsonNode asset, Date createdDate, Date updatedDate) {
        this.id = id;
        this.asset = asset;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public ContentM() {
    }


    public JsonNode getAsset() {
        return asset;
    }

    public void setAsset(JsonNode asset) {
        this.asset = asset;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}