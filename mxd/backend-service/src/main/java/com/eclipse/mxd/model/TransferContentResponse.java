package com.eclipse.mxd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferContentResponse {

    @JsonProperty("content")
    private JsonNode asset;

    public JsonNode getAsset() {
        return asset;
    }

    public void setAsset(JsonNode asset) {
        this.asset = asset;
    }

    public TransferContentResponse(JsonNode asset) {
        this.asset = asset;
    }

    public TransferContentResponse() {
    }

    @Override
    public String toString() {
        return "TransferResponse{" +
                "asset=" + asset +
                '}';
    }
}


