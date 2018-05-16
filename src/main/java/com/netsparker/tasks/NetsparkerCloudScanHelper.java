package com.netsparker.tasks;

import com.netsparker.ConfigManager;
import com.netsparker.model.ReportType;
import com.netsparker.model.ScanReport;
import com.netsparker.utility.AppCommon;
import net.sf.corn.httpclient.HttpForm;
import net.sf.corn.httpclient.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


public class NetsparkerCloudScanHelper{
	private final ConfigManager configManager = new ConfigManager();
	
	public String GetScanTaskID(String planKey, String buildNumber) {
		final String scanTaskID = configManager.getScanTaskID(planKey, buildNumber);
		
		return scanTaskID;
	}
	
	public ScanReport GetScanReport(String planKey, String buildNumber) throws IOException, URISyntaxException {
		final String scanTaskID = GetScanTaskID(planKey, buildNumber);
		final ScanReport scanReport = GetScanReport(scanTaskID);
		
		return scanReport;
	}
	
	public ScanReport GetScanReport(String scanTaskID) throws IOException, URISyntaxException {
		final HttpResponse response = GetScanReportResponse(scanTaskID);
		
		return new ScanReport(response);
	}
	
	private HttpResponse GetScanReportResponse(String scanTaskID) throws IOException, URISyntaxException {
		ReportType reportType = ReportType.ExecutiveSummary;
		String reportFormatCode = "3";
		
		String reportEndPoint_RelativeURL = "api/1.0/scans/report/%s";
		HttpForm client = new HttpForm(
				new URL(
						AppCommon.getBaseURL(configManager.getApiUrl()),
						String.format(reportEndPoint_RelativeURL, scanTaskID)
				).toURI());
		
		//default is XML
		client.setAcceptedType("text/html");
		// Basic Authentication
		client.setCredentials("", configManager.getApiToken());
		
		client.putFieldValue("Type", reportType.getNumberAsString());
		client.putFieldValue("Format", reportFormatCode);
		
		HttpResponse response = client.doGet();
		
		return response;
	}
}
