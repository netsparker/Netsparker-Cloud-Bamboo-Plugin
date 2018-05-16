package com.netsparker.model;

import com.netsparker.utility.AppCommon;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class ScanRequestBase{
	public static final String API_URL_Literal = "netsparkerCloudServerURL";
	public static final String API_TOKEN_Literal = "netsparkerCloudApiToken";
	public static final String ERROR_MESSAGE_Literal = "netsparkerCloudErrorMessage";
	
	protected boolean hasError = false;
	protected String errorMessage = "";
	
	public boolean HasError() {
		return hasError;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public ScanRequestBase(String apiURL, String apiToken) {
		try{
			this.ApiURL = AppCommon.getBaseURL(apiURL);
		}catch (Exception ex){
			hasError=true;
			errorMessage=ex.getMessage();
		}

		this.ApiToken = apiToken;
	}
	
	public ScanRequestBase() {
		this.ApiURL = null;
		this.ApiToken = null;
	}
	
	public  URL ApiURL;
	public  String ApiToken;
}