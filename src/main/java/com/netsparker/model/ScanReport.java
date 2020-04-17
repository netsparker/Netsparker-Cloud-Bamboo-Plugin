package com.netsparker.model;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.netsparker.utility.AppCommon;
import com.opensymphony.webwork.dispatcher.json.JSONObject;
import org.apache.http.HttpResponse;

import java.io.IOException;

public class ScanReport {
	private final HttpResponse reportRequestResponse;
	private final boolean scanRequestHasError;
	private final String scanRequestErrorMessage;
	private final boolean reportRequestHasError;
	private final String reportRequestErrorMessage;
	private final String requestURI;
	private static String reportHtmlAsString = null;

	public ScanReport(HttpResponse reportRequestResponse, String requestURI) {
		this.reportRequestResponse = reportRequestResponse;
		this.scanRequestHasError = false;
		this.scanRequestErrorMessage = "";
		this.reportRequestHasError = false;
		this.reportRequestErrorMessage = "";
		this.requestURI = requestURI;
	}

	public ScanReport(boolean scanRequestHasError, String scanRequestErrorMessage,
			boolean reportRequestHasError, String reportRequestErrorMessage, String requestURI) {
		this.reportRequestResponse = null;
		this.scanRequestHasError = scanRequestHasError;
		this.scanRequestErrorMessage = scanRequestErrorMessage;
		this.reportRequestHasError = reportRequestHasError;
		this.reportRequestErrorMessage = reportRequestErrorMessage;
		this.requestURI = requestURI;
	}

	private String getContentType() {
		return reportRequestResponse.getHeaders("Content-Type")[0].getValue();
	}

	public boolean isReportGenerated() {
		// when report stored, it will be loaded from disk for later requests. There is an exception
		// potential.
		try {
			return getContentType().equalsIgnoreCase("text/html");
		} catch (Exception ex) {
			return false;
		}
	}

	public String getContent() {
		String content = "";
		try {
			if (scanRequestHasError) {
				content = ExceptionContent(content, scanRequestErrorMessage);
			} else if (reportRequestHasError) {
				content = ExceptionContent(content, reportRequestErrorMessage);
			} else {

				String contentData = null;

				try {
					contentData = AppCommon.parseResponseToString(reportRequestResponse);

					ScanReport.reportHtmlAsString = contentData;
				} catch (IOException ex) {
					contentData = reportHtmlAsString;
				}

				if (isReportGenerated()) {
					content = contentData;
				} else {
					JSONObject data = new Gson().fromJson(contentData, JSONObject.class);
					content = data.getString("Message");
				}
			}
		} catch (JsonParseException ex) {
			content = ExceptionContent("Report result is not parsable.", ex.toString());
		} catch (Exception ex) {
			content = ExceptionContent(content, ex.toString());
		}

		return content;
	}

	private String ExceptionContent(String content, String ExceptionMessage) {
		if (content != null && !content.isEmpty()) {
			content = "<p>" + content + "</p>";
		} else {
			content = "<p>Something went wrong.</p>";
		}
		if (requestURI != null) {
			content = content + "<p>Request URL: " + requestURI + "</p>";
		}
		if (reportRequestResponse != null && reportRequestResponse.getStatusLine() != null) {
			content = content + "<p>HttpStatusCode: "
					+ reportRequestResponse.getStatusLine().getStatusCode() + "</p>";
		}
		if (ExceptionMessage != null) {
			content = content + "<p>Exception Message:: " + ExceptionMessage + "</p>";
		}

		return content;
	}
}
