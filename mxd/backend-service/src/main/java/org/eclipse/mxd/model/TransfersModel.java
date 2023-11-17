package org.eclipse.mxd.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonRootName("transfer")
public class TransfersModel {
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public TransfersModel(int id, String asset, String contents, Timestamp createdDate, Timestamp updatedDate) {
		super();
		this.id = id;
		this.asset = asset;
		this.contents = contents;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public java.sql.Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(java.sql.Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public java.sql.Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(java.sql.Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	@JsonProperty("id")
	private int id;
	@JsonProperty("asset")
	private String asset;
	@JsonProperty("contents")
	private String contents;
	@JsonProperty("createdDate")
	private java.sql.Timestamp createdDate;
	@JsonProperty("updatedDate")
	private java.sql.Timestamp updatedDate;

	
}
