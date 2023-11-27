package org.eclipse.mxd.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonRootName("contents")
public class ContentsModel {

	@JsonProperty("id")
	private int id;
	@JsonProperty("asset")
	private String asset;
	@JsonProperty("createdDate")
	private java.util.Date createdDate;
	@JsonProperty("updatedDate")
	private java.util.Date updatedDate;

	public ContentsModel(int id, String asset, Date createdDate, Date updatedDate) {
		this.id = id;
		this.asset = asset;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}

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

	public java.util.Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(java.sql.Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public java.util.Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(java.sql.Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public String toString() {
		return "Asset{" + "id=" + id + ", asset='" + asset + '\'' + ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + '}';
	}
}
