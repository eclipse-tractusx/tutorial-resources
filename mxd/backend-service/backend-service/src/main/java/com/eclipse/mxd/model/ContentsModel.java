package com.eclipse.mxd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentsModel {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("asset")
    private String asset;

    @JsonProperty("createdDate")
    private Date createdDate;

    @JsonProperty("updatedDate")
    private Date updatedDate;

    public ContentsModel(Long id, String asset, Date createdDate, Date updatedDate) {
        this.id = id;
        this.asset = asset;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public ContentsModel() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(java.sql.Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(java.sql.Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    // Helper method to clean up JSON formatting
    public String getCleanAssetJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(asset);
            String cleanJson = objectMapper.writeValueAsString(jsonNode);



            // Remove additional escape characters
            cleanJson = cleanJson.replaceAll("\\\\", "");
            cleanJson = cleanJson.replace("\\", "");
            cleanJson = cleanJson.replaceFirst("^\\\\+", "").replaceFirst("\\\\+$", "");

            return cleanJson;
        } catch (IOException e) {
            // Handle exception
            return asset; // Return original asset string if parsing fails
        }
    }

    @Override
    public String toString() {
        return "Asset{" + "id=" + id + ", asset='" + asset + '\'' + ", createdDate=" + createdDate + ", updatedDate="
                + updatedDate + '}';
    }
}
