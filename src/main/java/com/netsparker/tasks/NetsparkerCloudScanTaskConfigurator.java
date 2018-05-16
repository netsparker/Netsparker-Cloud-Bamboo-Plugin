package com.netsparker.tasks;

import java.util.Map;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.configuration.SystemInfo;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;

import com.atlassian.spring.container.LazyComponentReference;
import com.netsparker.ConfigManager;
import com.netsparker.model.ScanRequest;
import com.netsparker.model.ScanType;
import com.netsparker.model.WebsiteModelRequest;
import com.netsparker.utility.AppCommon;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetsparkerCloudScanTaskConfigurator extends AbstractTaskConfigurator{
	
	private ConfigManager configManager = new ConfigManager();
	
	@Override
	public void populateContextForCreate(@NotNull Map<String, Object> context) {
		super.populateContextForCreate(context);
		populateForAll(context);
	}
	
	@Override
	public void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition) {
		super.populateContextForEdit(context, taskDefinition);
		final Map<String, String> existingConfiguration = taskDefinition.getConfiguration();
		populateForAll(context);
		
		context.put(ScanRequest.SCAN_TYPE_Literal, existingConfiguration.get(ScanRequest.SCAN_TYPE_Literal));
		context.put(ScanRequest.WEBSITE_ID_Literal, existingConfiguration.get(ScanRequest.WEBSITE_ID_Literal));
		context.put(ScanRequest.PROFILE_ID_Literal, existingConfiguration.get(ScanRequest.PROFILE_ID_Literal));
	}
	
	private void populateForAll(@NotNull Map<String, Object> context) {
		final WebsiteModelRequest websiteModelRequest = new WebsiteModelRequest(configManager.getApiUrl(), configManager.getApiToken());
		if (websiteModelRequest.HasError()) {
			if (!configManager.hasCredentials()) {
				context.put(WebsiteModelRequest.ERROR_MESSAGE_Literal, "Please, validate your credentials from Netsparker Cloud global settings.");
			} else {
				context.put(WebsiteModelRequest.ERROR_MESSAGE_Literal, websiteModelRequest.getErrorMessage());
			}
		} else {
			final String websitesJsonModel = websiteModelRequest.getWebsitesJsonModel();
			if (websiteModelRequest.HasError()) {
				String errorMessage = websiteModelRequest.getErrorMessage() == null ?
						"Unable to retrieve scan settins." :
						websiteModelRequest.getErrorMessage();
				context.put(WebsiteModelRequest.ERROR_MESSAGE_Literal, errorMessage);
			} else {
				context.put(WebsiteModelRequest.Websites_JSON_MODEL_Literal, websitesJsonModel);
			}
		}
	}
	
	@Override
	public void validate(@NotNull ActionParametersMap params, @NotNull ErrorCollection errorCollection) {
		super.validate(params, errorCollection);
		
		if (!AppCommon.IsUrlValid(configManager.getApiUrl())) {
			errorCollection.addError(ScanRequest.API_URL_Literal, "Server URL is not valid.");
		}
		
		if (AppCommon.IsNullOrEmpty(configManager.getApiToken())) {
			errorCollection.addError(ScanRequest.API_TOKEN_Literal, "Api token is not valid.");
		}
		
		String scanTypeString = params.getString(ScanRequest.SCAN_TYPE_Literal);
		boolean isScanTypeValid = true;
		ScanType scanType = ScanType.FullWithPrimaryProfile;
		
		try {
			scanType = ScanType.valueOf(scanTypeString);
		} catch (Exception ex) {
			isScanTypeValid = false;
		}
		
		if (!isScanTypeValid) {
			errorCollection.addError(ScanRequest.SCAN_TYPE_Literal, "Scan type is not valid.");
		}
		String websiteIDString = params.getString(ScanRequest.WEBSITE_ID_Literal);
		if (!AppCommon.IsGUIDValid(websiteIDString)) {
			errorCollection.addError(ScanRequest.WEBSITE_ID_Literal, "Website is not valid.");
		}
		
		boolean isProfileRequired = scanType != ScanType.FullWithPrimaryProfile;
		String profileIDString = params.getString(ScanRequest.PROFILE_ID_Literal);
		if (isProfileRequired && !AppCommon.IsGUIDValid(profileIDString)) {
			errorCollection.addError(ScanRequest.PROFILE_ID_Literal, "Scan profile is not valid.");
		}
	}
	
	
	@NotNull
	@Override
	public Map<String, String> generateTaskConfigMap(@NotNull ActionParametersMap params, @Nullable TaskDefinition previousTaskDefinition) {
		final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
		//LazyComponentReference<SystemInfo> systemInfoReference = new LazyComponentReference<SystemInfo>("systemInfo");
		//SystemInfo systemInfo = systemInfoReference.get();
		
		config.put(ScanRequest.SCAN_TYPE_Literal, params.getString(ScanRequest.SCAN_TYPE_Literal));
		config.put(ScanRequest.WEBSITE_ID_Literal, params.getString(ScanRequest.WEBSITE_ID_Literal));
		config.put(ScanRequest.PROFILE_ID_Literal, params.getString(ScanRequest.PROFILE_ID_Literal));
		
		return config;
	}
}