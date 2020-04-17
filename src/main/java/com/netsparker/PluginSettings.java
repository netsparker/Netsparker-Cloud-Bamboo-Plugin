package com.netsparker;

import com.atlassian.bamboo.bandana.PlanAwareBandanaContext;
import com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor;
import com.atlassian.bamboo.configuration.AdministrationConfigurationManager;
import com.atlassian.bamboo.configuration.AdministrationConfigurationPersister;
import com.atlassian.bamboo.ww2.BambooActionSupport;
import com.atlassian.bamboo.ww2.aware.permissions.GlobalAdminSecurityAware;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.sal.api.component.ComponentLocator;
import com.netsparker.model.ScanRequestBase;
import com.netsparker.utility.AppCommon;


public class PluginSettings extends BambooActionSupport implements GlobalAdminSecurityAware {

	private String apiUrl;
	private String apiToken;
	private BandanaManager bandanaManager;

	public PluginSettings() {
		super();
		setAdministrationConfigurationAccessor(
				ComponentLocator.getComponent(AdministrationConfigurationAccessor.class));
		setAdministrationConfigurationManager(
				ComponentLocator.getComponent(AdministrationConfigurationManager.class));
		setAdministrationConfigurationPersister(
				ComponentLocator.getComponent(AdministrationConfigurationPersister.class));
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}


	public String doEdit() {
		setApiUrl(getValue(ScanRequestBase.API_URL_Literal));
		setApiToken(getValue(ScanRequestBase.API_TOKEN_Literal));

		return INPUT;
	}

	public String doSave() {
		boolean hasError = false;
		if (!AppCommon.IsUrlValid(getApiUrl())) {
			hasError = true;
			addActionError("Please enter valid URL.");
		}
		if (AppCommon.IsNullOrEmpty(getApiToken())) {
			hasError = true;
			addActionError("API Token can't be empty.");
		}

		if (hasError) {
			return ERROR;
		}
		bandanaManager.init();
		setValue(ScanRequestBase.API_URL_Literal, getApiUrl());
		setValue(ScanRequestBase.API_TOKEN_Literal, getApiToken());
		addActionMessage("Global settings updated.");

		return SUCCESS;
	}

	public void setBandanaManager(BandanaManager bandanaManager) {
		this.bandanaManager = bandanaManager;
	}

	private String getValue(String key) {
		Object value = bandanaManager.getValue(PlanAwareBandanaContext.GLOBAL_CONTEXT,
				"com.netsparker:" + key);
		return (String) value;
	}

	private void setValue(String key, String value) {
		bandanaManager.setValue(PlanAwareBandanaContext.GLOBAL_CONTEXT, "com.netsparker:" + key,
				value);
	}
}