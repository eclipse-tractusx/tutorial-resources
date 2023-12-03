package com.eclipse.mxd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferResponse {

    @JsonProperty("asset")
    private JsonNode asset;

    public TransferResponse(JsonNode asset) {
        this.asset = asset;
    }

    public TransferResponse() {
    }

    public JsonNode getAsset() {
        return asset;
    }

    public void setAsset(JsonNode asset) {
        this.asset = asset;
    }

    @Override
    public String toString() {
        return "TransferResponse{" +
                "asset=" + asset +
                '}';
    }
}


