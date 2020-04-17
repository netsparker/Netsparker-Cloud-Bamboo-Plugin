package com.netsparker.model;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class TestConnectionModel {

	private String apiURL;
	private String apiToken;

	public String getApiURL() {
		return apiURL;
	}

	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}
}
