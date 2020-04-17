package com.netsparker.model;

import com.netsparker.utility.AppCommon;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScanRequestResult extends ScanRequestBase {
	public static ScanRequestResult errorResult(String errorMessage) {
		return new ScanRequestResult(errorMessage);
	}

	private String scanReportEndpoint;

	private final int httpStatusCode;
	private String data;
	private String scanTaskID;
	private boolean isError;
	private String errorMessage;

	private ScanReport report = null;
	private Date previousRequestTime;
	private final String scanReportRelativeUrl = "api/1.0/scans/report/";

	private ScanRequestResult(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
		httpStatusCode = 0;
		isError = true;
		data = "";
	}

	public ScanRequestResult(HttpResponse response, String apiURL, String apiToken)
			throws MalformedURLException {
		super(apiURL, apiToken);
		httpStatusCode = response.getStatusLine().getStatusCode();
		isError = httpStatusCode != 201;

		if (!isError) {
			try {
				data = AppCommon.parseResponseToString(response);

				JSONObject jsonObj = new JSONObject(data);

				isError = !jsonObj.getBoolean("IsValid");

				if (!isError) {
					scanTaskID = jsonObj.getString("ScanTaskId");
				} else {
					errorMessage = jsonObj.getString("ErrorMessage");
				}
			} catch (Exception ex) {
				isError = true;
				errorMessage = "Scan request result is not readable::: " + ex.toString();
			}
		}

		setScanReportEndpoint();
	}

	public ScanRequestResult(String apiURL, String apiToken, String scanTaskID)
			throws MalformedURLException {
		super(apiURL, apiToken);

		this.scanTaskID = scanTaskID;

		httpStatusCode = 201;
		setScanReportEndpoint();

	}

	private void setScanReportEndpoint() throws MalformedURLException {

		Map<String, String> queryparams = new HashMap<>();
		queryparams.put("Type", "ExecutiveSummary");
		queryparams.put("Format", "Html");
		queryparams.put("Id", scanTaskID);

		scanReportEndpoint = new URL(ApiURL, scanReportRelativeUrl).toString() + "?"
				+ AppCommon.mapToQueryString(queryparams);
	}

	public String getScanTaskID() {
		return scanTaskID;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean isError() {
		return isError;
	}

	public ScanReport getReport() {
		// if report is not generated and requested yet, request it from Netparker Cloud server.
		if (canAskForReportFromNCCloud()) {
			final ScanReport reportFromNcCloud = getReportFromNcCloud();
			previousRequestTime = new Date();

			this.report = reportFromNcCloud;

			return this.report;
		}

		return report;
	}

	private boolean canAskForReportFromNCCloud() {
		Date now = new Date();
		// Is report not requested or have request threshold passed
		// And report isn't generated yet
		boolean isTimeThresholdPassed = previousRequestTime == null
				|| now.getTime() - previousRequestTime.getTime() >= 60 * 1000;// 1 min

		return isTimeThresholdPassed || !isReportAvailable();
	}

	private boolean isReportAvailable() {
		return report != null && report.isReportGenerated();
	}

	private ScanReport getReportFromNcCloud() {
		ScanReport reportFromApi;

		if (!isError) {
			try {
				final HttpClient httpClient = getHttpClient();
				final HttpGet httpGet = new HttpGet(scanReportEndpoint);
				httpGet.setHeader(HttpHeaders.AUTHORIZATION, getAuthHeader());

				HttpResponse response = httpClient.execute(httpGet);

				reportFromApi = new ScanReport(response, scanReportEndpoint);
			} catch (IOException ex) {
				String reportRequestErrorMessage =
						"Report result is not readable::: " + ex.toString();
				reportFromApi = new ScanReport(false, "", true, reportRequestErrorMessage,
						scanReportEndpoint);
			}
		} else {
			reportFromApi = new ScanReport(true, errorMessage, false, "", scanReportEndpoint);
		}

		this.report = reportFromApi;

		return reportFromApi;
	}

}
