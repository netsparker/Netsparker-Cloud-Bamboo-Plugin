package com.netsparker.model;

import com.netsparker.utility.AppCommon;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class ScanRequestBase {
    public static final String API_URL_Literal = "netsparkerCloudServerURL";
    public static final String API_TOKEN_Literal = "netsparkerCloudApiToken";
    public static final String ERROR_MESSAGE_Literal = "netsparkerCloudErrorMessage";
    protected static final String json = "application/json";
    public final URL ApiURL;
    public final String ApiToken;
    protected boolean hasError = false;
    protected String errorMessage = "";

    public ScanRequestBase(String apiURL, String apiToken) throws MalformedURLException {
        this.ApiURL = AppCommon.getBaseURL(apiURL);
        this.ApiToken = apiToken;
    }

    public ScanRequestBase() {
        this.ApiURL = null;
        this.ApiToken = null;
    }

    public boolean HasError() {
        return hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    protected HttpClient getHttpClient() {
        return HttpClientBuilder
                .create()
                .build();
    }

    protected String getAuthHeader() {
        String auth = ":" + ApiToken;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.ISO_8859_1);

        return authHeader;
    }
}