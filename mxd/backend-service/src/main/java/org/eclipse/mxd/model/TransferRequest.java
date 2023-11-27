package org.eclipse.mxd.model;

public class TransferRequest {

	private String id;
	private String endpoint;
	private String authKey;
	private String authCode;
	private PropertiesModel properties;

	public TransferRequest(String id, String endpoint, String authKey, String authCode, PropertiesModel properties) {
		this.id = id;
		this.endpoint = endpoint;
		this.authKey = authKey;
		this.authCode = authCode;
		this.properties = properties;
	}

	public TransferRequest() {
	}

	public String getId() {
		return id;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getAuthKey() {
		return authKey;
	}

	public String getAuthCode() {
		return authCode;
	}

	public PropertiesModel getProperties() {
		return properties;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public void setProperties(PropertiesModel properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "ReceivedModel{" + "id='" + id + '\'' + ", endpoint='" + endpoint + '\'' + ", authKey='" + authKey + '\''
				+ ", authCode='" + authCode + '\'' + ", properties=" + properties + '}';
	}
}
