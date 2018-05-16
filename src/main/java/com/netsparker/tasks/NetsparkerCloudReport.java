package com.netsparker.tasks;

import com.atlassian.bamboo.build.ViewBuildResults;
import com.netsparker.model.ScanReport;

public class NetsparkerCloudReport extends ViewBuildResults{
	
	String buildNumber;
	String scanTaskID;
	String hasError;
	String errorMessage;
	String isReportGenerated;
	
	@Override
	public String doDefault() throws Exception {
		try {
			final NetsparkerCloudScanHelper netsparkerCloudScanHelper = new NetsparkerCloudScanHelper();
			final String scanTaskID = netsparkerCloudScanHelper.GetScanTaskID(getBuildKey(), getBuildNumberString());
			final ScanReport scanReport = netsparkerCloudScanHelper.GetScanReport(scanTaskID);
			this.scanTaskID=scanTaskID;
			isReportGenerated = String.valueOf(scanReport.isReportGenerated());
			hasError = "false";
			errorMessage = "";
		} catch (Exception ex) {
			this.scanTaskID=scanTaskID;
			hasError = "true";
			isReportGenerated = "false";
			errorMessage = ex.getMessage();
		}
		return super.doDefault();
	}
	
	public String getScanTaskID() {
		return scanTaskID;
	}
	
	public String getHasError() {
		return hasError;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String getIsReportGenerated() {
		return isReportGenerated;
	}
	
	
	public String getBuildNumberString() {
		return buildNumber;
	}
	
	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}
	
}
