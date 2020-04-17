package com.netsparker.model;

import com.netsparker.utility.AppCommon;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class WebsiteModelRequest extends ScanRequestBase {
	public static final String Websites_JSON_MODEL_Literal = "netsparkerCloudWebsitesJsonModel";
	private String websitesJsonModel = "";

	private ArrayList<WebsiteModel> websiteModels = new ArrayList<>();
	private HttpResponse response;

	public WebsiteModelRequest(String apiURL, String apiToken)
			throws MalformedURLException, NullPointerException, URISyntaxException {
		super(apiURL, apiToken);
		pluginWebSiteModelsUri = new URL(ApiURL, "api/1.0/scans/PluginWebSiteModels").toURI();
	}

	private final URI pluginWebSiteModelsUri;

	public ArrayList<WebsiteModel> getWebsiteModels() {
		return websiteModels;
	}

	public HttpResponse getPluginWebSiteModels() throws IOException, JSONException {
		final HttpClient httpClient = getHttpClient();
		final HttpGet httpGet = new HttpGet(pluginWebSiteModelsUri);
		httpGet.setHeader("Accept", json);
		httpGet.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader());

		response = httpClient.execute(httpGet);
		if (response.getStatusLine().getStatusCode() == 200) {
			parseWebsiteData();
		}
		return response;
	}

	private void parseWebsiteData() throws IOException, JSONException {
		String data = AppCommon.parseResponseToString(response);
		websitesJsonModel = data;

		websiteModels = new ArrayList<>();

		JSONArray jsonObjectArr = new JSONArray(data.trim());

		for (int w = 0; w < jsonObjectArr.length(); w++) {
			JSONObject websiteModelJson = jsonObjectArr.getJSONObject(w);

			WebsiteModel websiteModel = new WebsiteModel();
			websiteModel.setId((String) websiteModelJson.get("Id"));
			websiteModel.setName((String) websiteModelJson.get("Name"));
			websiteModel.setUrl((String) websiteModelJson.get("Url"));

			JSONArray websiteProfileModelJsonArr =
					(JSONArray) websiteModelJson.get("WebsiteProfiles");

			ArrayList<WebsiteProfileModel> profiles = new ArrayList<>();

			for (int i = 0; i < websiteProfileModelJsonArr.length(); i++) {
				JSONObject websiteProfileModelJson = websiteProfileModelJsonArr.getJSONObject(i);

				WebsiteProfileModel websiteProfileModel = new WebsiteProfileModel();
				websiteProfileModel.setId((String) websiteProfileModelJson.get("Id"));
				websiteProfileModel.setName((String) websiteProfileModelJson.get("Name"));

				profiles.add(websiteProfileModel);
			}

			websiteModel.setProfiles(profiles);
			websiteModels.add(websiteModel);
		}
	}

	public String getWebsitesJsonModel() {
		try {
			getPluginWebSiteModels();
		} catch (Exception ex) {
			hasError = true;
			errorMessage = ex.getMessage();
		}

		return websitesJsonModel;
	}

}
