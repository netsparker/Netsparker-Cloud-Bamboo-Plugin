package com.netsparker.model;

import com.netsparker.utility.AppCommon;
import net.sf.corn.httpclient.HttpForm;
import net.sf.corn.httpclient.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

public class ScanRequestResult extends ScanRequestBase{
	public static final String IsError_Literal = "IsError";
	public static final String HTTPStatusCode_Literal = "HTTPStatusCode";
	public static final String Message_Literal = "Message";
	public static final String Data_Literal = "Data";
	public static final String ScanTaskID_Literal = "ScanTaskID";
	public static final String BuildID_Literal = "BuildID";
	
	public static ScanRequestResult ErrorResult() {
		return new ScanRequestResult();
	}
	
	private final int httpStatusCode;
	private final String data;
	
	private String scanTaskID = "";
	private boolean hasError;
	private String errorMessage;
	
	//Response from Netsparker Cloud API
	private ScanReport report = null;
	private Date previousRequestTime;
	
	private ScanRequestResult() {
		super();
		hasError=true;
		data = null;
		httpStatusCode = 0;
	}
	
	public ScanRequestResult(HttpResponse response, String apiURL, String apiToken) throws Exception {
		super(apiURL, apiToken);
		
		hasError = response.hasError();
		httpStatusCode = response.getCode();
		data = response.getData();
		
		try {
			hasError = !(boolean) AppCommon.ParseJsonValue(data, "IsValid");
			if (!hasError) {
				scanTaskID = (String) AppCommon.ParseJsonValue(data, "ScanTaskId");
			} else {
				errorMessage = (String) AppCommon.ParseJsonValue(data, "ErrorMessage");
			}
		} catch (Exception ex) {
			hasError = true;
			errorMessage = "Scan request result is not parsable.";
		}
	}
	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public boolean isHasError() {
		return hasError;
	}
	
	public String getScanTaskID() {
		return scanTaskID;
	}
	
	public boolean isReportGenerated() {
		//If scan request is failed we don't need additional check.
		if (isHasError()) {
			return false;
		} else if (isReportAvailable()) {
			return true;
		} else if (canAskForReportFromNCCloud()) {//If report is not requested or report wasn't ready in previous request we must check again.
			try {
				final ScanReport report = getReport();
				return report.isReportGenerated();
			} catch (Exception ex) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean canAskForReportFromNCCloud() {
		Date now = new Date();
		//Is report not requested or have request threshold passed
		//And report isn't generated yet
		boolean isTimeThresholdPassed = previousRequestTime == null || now.getTime() - previousRequestTime.getTime() >= 60 * 1000;//1 min
		return !isReportAvailable() && isTimeThresholdPassed;
	}
	
	
	private boolean isReportAvailable() {
		return report != null && report.isReportGenerated();
	}
	
	
	public ScanReport getReport() throws IOException, URISyntaxException {
		// if report is not generated and requested yet, request it from ncCloudserver.
		if (canAskForReportFromNCCloud()) {
			final ScanReport reportFromNcCloud = getReportFromNcCloud();
			previousRequestTime = new Date();
			return reportFromNcCloud;
		}
		return report;
	}
	
	private ScanReport getReportFromNcCloud() throws IOException, URISyntaxException {
		ReportType reportType = ReportType.ExecutiveSummary;
		String reportFormatCode = "3";
		
		String reportEndPoint_RelativeURL = "api/1.0/scans/report/%s";
		HttpForm client = new HttpForm(new URL(ApiURL, String.format(reportEndPoint_RelativeURL, scanTaskID)).toURI());
		//default is XML. We expect "text/html";
		client.setAcceptedType("text/html");
		// Basic Authentication
		client.setCredentials("", ApiToken);
		client.putFieldValue("Type", reportType.getNumberAsString());
		client.putFieldValue("Format", reportFormatCode);
		
		HttpResponse response = client.doGet();
		ScanReport report = new ScanReport(response);
		this.report = report;
		
		return report;
	}
}
