package com.netsparker.model;

import net.sf.corn.httpclient.HttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ScanReport{
	private final HttpResponse reportRequestResponse;
	private final boolean hasError;
	
	public ScanReport(final HttpResponse reportRequestResponse) {
		this.reportRequestResponse = reportRequestResponse;
		this.hasError = reportRequestResponse.hasError();
	}
	
	public boolean isReportGenerated() {
		//when report stored, it will be loaded from disk for later requests. There is an exception potential.
		try {
			return getContentType().equalsIgnoreCase("text/html");
		} catch (Exception ex) {
			return false;
		}
	}
	
	private String getContentType() {
		return reportRequestResponse.getHeaderFields().get("Content-Type").get(0);
	}
	
	public String getContent() throws ParseException {
		String content;
		try {
			String contentData = reportRequestResponse.getData();
			if (isReportGenerated()) {
				content = contentData;
			} else {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(contentData);
				content = (String) obj.get("Message");
			}
			
		} catch (Exception ex) {
			content = "Scan report is not available because the scan request failed or the scan is not exist anymore.";
		}
		return content;
	}
}