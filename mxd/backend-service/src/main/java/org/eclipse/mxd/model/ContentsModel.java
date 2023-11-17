package org.eclipse.mxd.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonRootName("contents")
public class ContentsModel {
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
	@JsonProperty("createdDate")
	private java.sql.Timestamp createdDate;
	@JsonProperty("updatedDate")
	private java.sql.Timestamp updatedDate;

	public ContentsModel(int id, String asset, java.sql.Timestamp createdDate, java.sql.Timestamp updatedDate) {
		this.id = id;
		this.asset = asset;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}

	@Override
	public String toString() {
		return "Asset{" + "id=" + id + ", asset='" + asset + '\'' + ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + '}';
	}
}
