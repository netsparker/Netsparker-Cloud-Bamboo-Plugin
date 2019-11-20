package com.netsparker.tasks;

import com.atlassian.bamboo.build.PlanResultsAction;
import com.netsparker.model.ScanReport;
import org.apache.log4j.Logger;

public class NetsparkerCloudReport extends PlanResultsAction {

    String buildNumber;
    String scanTaskID;
    String hasError;
    String errorMessage;
    String isReportGenerated;

    private static final Logger log = Logger.getLogger(NetsparkerCloudReport.class);

    @Override
    public String execute() throws Exception {
        String result = super.execute();
        try {
            final NetsparkerCloudScanHelper netsparkerCloudScanHelper = new NetsparkerCloudScanHelper();
            final String scanTaskID = netsparkerCloudScanHelper.GetScanTaskID(getBuildKey(), getBuildNumberString());
            final ScanReport scanReport = netsparkerCloudScanHelper.GetScanReport(scanTaskID);
            this.scanTaskID = scanTaskID;
            isReportGenerated = String.valueOf(scanReport.isReportGenerated());
            hasError = "false";
            errorMessage = "";
        } catch (Exception ex) {
            this.scanTaskID = scanTaskID;
            hasError = "true";
            isReportGenerated = "false";
            errorMessage = ex.getMessage();
            log.debug(ex.getMessage());
        }

        return result;
    }

    //these getters called from ui like ${IsReportGenerated}

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
