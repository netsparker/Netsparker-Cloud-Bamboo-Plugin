package com.netsparker.model;

import net.sf.corn.httpclient.HttpForm;
import net.sf.corn.httpclient.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class WebsiteModelRequest extends ScanRequestBase{
	public static final String Websites_JSON_MODEL_Literal = "netsparkerCloudWebsitesJsonModel";
	
	private final String json = "application/json";
	private HttpResponse response;
	private ArrayList<WebsiteModel> websiteModels = new ArrayList<>();
	private String websitesJsonModel = "";

	
	public WebsiteModelRequest(String apiURL, String apiToken) {
		super(apiURL, apiToken);
		try {
			pluginWebSiteModelsUri = getPluginWebSiteModelsEndpoint();
		}catch (Exception ex){
		hasError=true;
		errorMessage=ex.getMessage();
		}
	}
	
	private URI pluginWebSiteModelsUri;
	
	public ArrayList<WebsiteModel> getWebsiteModels() {
		return websiteModels;
	}
	
	
	public HttpResponse getPluginWebSiteModels() {
		try {
			HttpForm client = new HttpForm(pluginWebSiteModelsUri);
			// Basic Authentication
			client.setCredentials("", ApiToken);
			client.setAcceptedType(json);
			response = client.doGet();
			if (response.getCode() == 200) {
				parseWebsiteData();
			}
		} catch (Exception ex) {
			hasError = true;
			errorMessage = ex.getMessage();
		}
		return response;
	}
	
	public String getWebsitesJsonModel() {
		try {
			getPluginWebSiteModels();
		} catch (Exception ex) {
			hasError = true;
			errorMessage = ex.getMessage();
		}
		
		getPluginWebSiteModels();
		return websitesJsonModel;
	}
	
	private URI getPluginWebSiteModelsEndpoint() throws MalformedURLException, URISyntaxException {
		String relativePath = "api/1.0/scans/PluginWebSiteModels";
		return new URL(ApiURL, relativePath).toURI();
	}
	
	private ArrayList<WebsiteModel> parseWebsiteData() throws ParseException {
		websitesJsonModel = response.getData();
		return parseWebsiteData(websitesJsonModel);
	}
	
	private ArrayList<WebsiteModel> parseWebsiteData(String websiteData) throws ParseException {
		JSONParser parser = new JSONParser();
		Object jsonData = parser.parse(websiteData);
		
		JSONArray WebsiteModelObjects = (JSONArray) jsonData;
		websiteModels = new ArrayList<>();
		
		for (Object wmo : WebsiteModelObjects) {
			if (wmo instanceof JSONObject) {
				JSONObject websiteModelObject = (JSONObject) wmo;
				
				WebsiteModel websiteModel = new WebsiteModel();
				websiteModel.setId((String) websiteModelObject.get("Id"));
				websiteModel.setName((String) websiteModelObject.get("Name"));
				websiteModel.setUrl((String) websiteModelObject.get("Url"));
				
				JSONArray WebsiteProfileModelObjects = (JSONArray) websiteModelObject.get("WebsiteProfiles");
				ArrayList<WebsiteProfileModel> profiles = new ArrayList<>();
				for (Object wmpo : WebsiteProfileModelObjects) {
					JSONObject websiteProfileModelObject = (JSONObject) wmpo;
					
					WebsiteProfileModel websiteProfileModel = new WebsiteProfileModel();
					websiteProfileModel.setId((String) websiteProfileModelObject.get("Id"));
					websiteProfileModel.setName((String) websiteProfileModelObject.get("Name"));
					
					profiles.add(websiteProfileModel);
				}
				
				websiteModel.setProfiles(profiles);
				websiteModels.add(websiteModel);
			}
		}
		
		return websiteModels;
	}
}
