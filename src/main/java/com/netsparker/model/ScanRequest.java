package com.netsparker.model;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ScanRequest extends ScanRequestBase {
    public static final String SCAN_TYPE_Literal = "netsparkerCloudScanType";
    public static final String WEBSITE_ID_Literal = "netsparkerCloudWebsiteID";
    public static final String PROFILE_ID_Literal = "netsparkerCloudProfileID";
	public final ScanType scanType;
	public final String websiteId;
	public final String profileId;
	public final VCSCommit vcsCommit;
	public final URI scanUri;
	public final URI testUri;
	private final String json = "application/json";

	public ScanRequest(String apiURL, String apiToken, String scanType, String websiteId, String profileId, VCSCommit vcsCommit) throws MalformedURLException, NullPointerException, URISyntaxException {
		super(apiURL, apiToken);
		this.scanType = ScanType.valueOf(scanType);
		this.websiteId = websiteId;
		this.profileId = profileId;
		this.vcsCommit = vcsCommit;
		scanUri = getRequestEndpoint();
		testUri = getTestEndpoint();
	}

	public HttpResponse scanRequest() throws IOException {
		HttpClient client = getHttpClient();
		final HttpPost httpPost = new HttpPost(scanUri);
		httpPost.setHeader("Accept", json);
		httpPost.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader());

		List<NameValuePair> params = new ArrayList<>();
		setScanParams(params);
		vcsCommit.addVcsCommitInfo(params);
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse response = client.execute(httpPost);

		return response;
	}

	private URI getRequestEndpoint() throws MalformedURLException, URISyntaxException {
		String relativePath = "api/1.0/scans/CreateFromPluginScanRequest";
		return new URL(ApiURL, relativePath).toURI();
	}

	private URI getTestEndpoint() throws MalformedURLException, URISyntaxException {
		String relativePath = "api/1.0/scans/VerifyPluginScanRequest";
		return new URL(ApiURL, relativePath).toURI();
	}

	private void setScanParams(List<NameValuePair> params) {
		switch (scanType) {
			case FullWithPrimaryProfile:
				params.add(new BasicNameValuePair("WebsiteId", websiteId));
				params.add(new BasicNameValuePair("ScanType", "FullWithPrimaryProfile"));
				break;
			case FullWithSelectedProfile:
				params.add(new BasicNameValuePair("WebsiteId", websiteId));
				params.add(new BasicNameValuePair("ProfileId", profileId));
				params.add(new BasicNameValuePair("ScanType", "FullWithSelectedProfile"));
				break;
			default:
				break;
		}
	}
}

