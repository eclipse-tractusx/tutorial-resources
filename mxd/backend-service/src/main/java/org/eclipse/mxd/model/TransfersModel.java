package org.eclipse.mxd.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonRootName("transfer")
public class TransfersModel {

	@JsonProperty("id")
	private int id;
	@JsonProperty("asset")
	private String asset;
	@JsonProperty("contents")
	private String contents;
	@JsonProperty("transferid")
	private String transferid;
	@JsonProperty("createdDate")
	private java.util.Date createdDate;
	@JsonProperty("updatedDate")
	private java.util.Date updatedDate;
	
	public TransfersModel() {

	}
	
	public TransfersModel(int id, String asset, String contents, String transferid, Date createdDate,
			Date updatedDate) {
		super();
		this.id = id;
		this.asset = asset;
		this.contents = contents;
		this.transferid = transferid;
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

	public java.util.Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(java.util.Date createdDate) {
		this.createdDate = createdDate;
	}

	public java.util.Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(java.util.Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	@Override
	public String toString() {
		return "TransfersModel [id=" + id + ", asset=" + asset + ", contents=" + contents + ", transferid=" + transferid
				+ ", createdDate=" + createdDate + ", updatedDate=" + updatedDate + "]";
	}

}
