package com.netsparker;

import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.plugin.Plugin;
import com.atlassian.spring.container.ContainerManager;
import com.netsparker.model.ScanRequestBase;
import com.netsparker.utility.AppCommon;


public class ConfigManager{
	private BandanaManager bandanaManager;
	private final String pluginKey = "com.netsparker:";
	private final String scanTaskKey = "ScanTaskID";
	
	public ConfigManager() {
		this.bandanaManager = (BandanaManager) ContainerManager.getComponent("bandanaManager");
		bandanaManager.init();
	}
	
	public boolean hasCredentials() {
		return (AppCommon.IsUrlValid(get(ScanRequestBase.API_URL_Literal)) && AppCommon.IsNullOrEmpty(get(ScanRequestBase.API_TOKEN_Literal)));
	}
	
	public String getApiUrl() {
		return get(ScanRequestBase.API_URL_Literal);
	}
	
	public String getApiToken() {
		return get(ScanRequestBase.API_TOKEN_Literal);
	}
	
	public String getScanTaskID(String planKey, String buildNumber) {
		String buildSpecificKey = planKey+"-"+buildNumber;
		return getBuildParameter(buildSpecificKey, scanTaskKey);
	}
	
	public void setScanTaskID(String planKey, String buildNumber, String scanTaskID) {
		String buildSpecificKey = planKey+"-"+buildNumber;
		setBuildParameter(buildSpecificKey, scanTaskKey, scanTaskID);
	}
	
	private String get(String key) {
		return (String) bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, pluginKey + key);
	}
	
	private void setBuildParameter(String buildSpecificKey, String key, String value) {
		String pluginSpecificBuildKey = pluginKey + ":" + buildSpecificKey;
		String parameterKey = pluginSpecificBuildKey + ":" + key;
		bandanaManager.setValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, parameterKey, value);
	}
	
	private String getBuildParameter(String buildSpecificKey, String key) {
		String pluginSpecificBuildKey = pluginKey + ":" + buildSpecificKey;
		String parameterKey = pluginSpecificBuildKey + ":" + key;
		Object value = bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, parameterKey);
		
		return (String) value;
	}
	
}