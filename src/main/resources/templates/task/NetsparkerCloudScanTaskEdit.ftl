<div id="netsparkerCloudErrorMessage" class="aui-message aui-message-error" style="display: none ">
    <p class="title">
    ${netsparkerCloudErrorMessage!""}
    </p>
</div>
<br>
<div class="field-group" style="display: none">
    <label for="netsparkerCloudScanType">Scan Type
        <span class="aui-icon icon-required">(required)</span>
    </label>
    <input type="hidden" id="netsparkerCloudScanType_Dummy" value="${netsparkerCloudScanType!""}">
    <select id="netsparkerCloudScanType" name="netsparkerCloudScanType">
        <option value="">-- Please select a scan type --</option>
        <option value="Incremental">Incremental</option>
        <option value="FullWithPrimaryProfile">Full (With primary profile)</option>
        <option value="FullWithSelectedProfile">Full (With selected profile)</option>
    </select>
    <span class="aui-icon icon-inline-help"><span>Help</span></span>
    <div class="error">${netsparkerCloudScanTypeError!""}</div>
    <div class="field-help description hidden">
        <b>Incremental</b>
        <hr>
        The website's profile is used for retrieving the scan settings.<br>
        The last scan with the same scan setting will be used as a base for the incremental scan.<br><br>
        <b>Full (With primary profile)</b>
        <hr>
        Performs full scan with primary profile.<br>
        If no primary profile have been defined yet, default Netsparker Cloud scan settings will be used.<br><br>
        <b>Full (With selected profile)</b>
        <hr>
        Performs full scan with provided profile settings.
    </div>
</div>
<div class="field-group" style="display: none">
    <label for="netsparkerCloudWebsiteID">Website Deploy URL
        <span class="aui-icon icon-required">(required)</span>
    </label>
    <input type="hidden" name="netsparkerCloudWebsiteID" id="netsparkerCloudWebsiteID"
           value="${netsparkerCloudWebsiteID!""}">
    <select id="netsparkerCloudWebsiteID_dummy">
        <option value="">-- Please select a website URL --</option>
    </select>
    <span class="aui-icon icon-inline-help"><span>Help</span></span>
    <div class="error">${netsparkerCloudWebsiteIDErrorError!""}</div>
    <div class="field-help description hidden">
        This address will be scanned.
    </div>
</div>
<div class="field-group" style="display: none">
    <label for="netsparkerCloudProfileID">Profile Name
        <span class="aui-icon icon-required">(required)</span>
    </label>
    <input type="hidden" name="netsparkerCloudProfileID" id="netsparkerCloudProfileID"
           value="${netsparkerCloudProfileID!""}">
    <select id="netsparkerCloudProfileID_dummy">
        <option value="">-- Please select a profile name --</option>
    </select>
    <span class="aui-icon icon-inline-help"><span>Help</span></span>
    <div class="error">${netsparkerCloudProfileIDError!""}</div>
    <div class="field-help description hidden">
        This profile setting will be used in the scan.
    </div>
</div>

<script>
    //do noy use $ for Jquery instead use AJS.$
    var ncScanTypeInput, ncWebsiteIdInput, ncProfileIdInput;
    var ncScanTypeDummyInput, ncWebsiteIdDummySelect, ncProfileIdDummySelect;
    var ncScanTypeContainer, ncWebsiteIdContainer, ncProfileIdContainer;
    var ncWebSiteModels =${netsparkerCloudWebsitesJsonModel!"''"};
    var ncErrorMessageContainer = AJS.$("#netsparkerCloudErrorMessage");
    var ncErrorMessageElement = ncErrorMessageContainer.find(".title");
    var ncScanParams = {};
    var ncInitialScanType, ncInitialWebsiteId, ncInitialProfileId;

    //do noy use $ for Jquery instead use jQuery
    AJS.$(document).ready(function () {
        AJS.inlineHelp();
        if (!Array.isArray(ncWebSiteModels)) {
            ncErrorMessageContainer.show();
        } else if (ncWebSiteModels == false) {
            ncErrorMessageElement.text("You don't have any Netsparker Cloud defined website to initiate a scan.");
            ncErrorMessageContainer.show();
        }
        else {
            initializeNcElementsAndParams();
        }
    })

    function initializeNcElementsAndParams() {
        ncScanTypeInput = AJS.$("#netsparkerCloudScanType");
        ncWebsiteIdInput = AJS.$("#netsparkerCloudWebsiteID");
        ncProfileIdInput = AJS.$("#netsparkerCloudProfileID");

        ncScanTypeDummyInput = AJS.$("#netsparkerCloudScanType_Dummy");
        ncWebsiteIdDummySelect = AJS.$("#netsparkerCloudWebsiteID_dummy");
        ncProfileIdDummySelect = AJS.$("#netsparkerCloudProfileID_dummy");

        ncScanTypeContainer = ncScanTypeInput.closest("div");
        ncScanTypeContainer.show();
        ncWebsiteIdContainer = ncWebsiteIdInput.closest("div");
        ncWebsiteIdContainer.show();
        ncProfileIdContainer = ncProfileIdInput.closest("div");
        ncProfileIdContainer.show();

        ncInitialScanType = ncScanTypeDummyInput.val();
        ncInitialWebsiteId = ncWebsiteIdInput.val();
        ncInitialProfileId = ncProfileIdInput.val();

        ncScanTypeInput.change(ncScanTypeChanged);
        ncWebsiteIdDummySelect.change(ncWebsiteIdChanged);
        ncProfileIdDummySelect.change(ncProfileIdChanged);

        ncInitializeSettings();
    }

    function ncScanTypeChanged() {
        updateNcParamsAndUI();
    }

    function ncWebsiteIdChanged() {
        updateNcParamsAndUI();
        if (ncScanParams.websiteId) {
            ncInitializeProfiles();
        } else {
            ncResetProfileOptions();
        }
    }

    function ncProfileIdChanged() {
        updateNcParamsAndUI();
    }

    function updateNcParamsAndUI() {
        ncScanParams.scanType = ncScanTypeInput.val();

        ncWebsiteIdInput.val(ncWebsiteIdDummySelect.val());
        ncScanParams.websiteId = ncWebsiteIdInput.val();
        if (!ncScanParams.websiteId) {
            ncProfileIdDummySelect.val("");
        }

        ncProfileIdInput.val(ncProfileIdDummySelect.val());
        ncScanParams.profileId = ncProfileIdInput.val();

        ncRenderUIElements();
    }

    function ncRenderUIElements() {
        var ncShowProfile = ncScanParams.scanType != "FullWithPrimaryProfile";
        if (ncShowProfile) {
            ncProfileIdContainer.show();
        } else {
            ncProfileIdContainer.hide();
        }
    }

    function ncInitializeSettings() {
        ncAppendWebsiteOptions();
        ncSelectInitialValues();
    }

    function ncAppendWebsiteOptions() {
        AJS.$.each(ncWebSiteModels, function (index, webSiteModel) {
            var websiteText = webSiteModel.Name + " (" + webSiteModel.Url + ")";
            ncWebsiteIdDummySelect
                    .append(AJS.$("<option></option>")
                            .attr("value", webSiteModel.Id)
                            .text(websiteText));
        });
    }

    function ncSelectInitialValues() {
        if (ncInitialScanType) {
            if (ncScanTypeInput.find("option[value='" + ncInitialScanType + "']").length > 0) {
                ncScanTypeInput.val(ncInitialScanType);
                ncScanTypeInput.change();
                if (ncInitialWebsiteId) {
                    if (ncWebsiteIdDummySelect.find("option[value='" + ncInitialWebsiteId + "']").length > 0) {
                        ncWebsiteIdDummySelect.val(ncInitialWebsiteId);
                        ncWebsiteIdDummySelect.change();
                        if (ncProfileIdDummySelect.find("option[value='" + ncInitialProfileId + "']").length > 0) {
                            ncProfileIdDummySelect.val(ncInitialProfileId);
                            ncProfileIdDummySelect.change();
                        }
                    }
                }
            }
        }
    }

    function ncInitializeProfiles() {
        updateNcParamsAndUI();
        var websiteIndex = ncFindModelIndexesFromSelectValues().websiteIndex;
        if (websiteIndex != -1) {
            var ncProfileModels = ncWebSiteModels[websiteIndex].WebsiteProfiles;
            ncResetProfileOptions(ncProfileModels.length);
            ncAppendProfileOptions(ncProfileModels);
            ncProfileIdDummySelect.prop("selectedIndex", 0).change();
        }
    }

    function ncFindModelIndexesFromSelectValues() {
        var modelIndexes = {};
        modelIndexes.websiteIndex = -1;
        modelIndexes.profileIndex = -1;

        var websiteID = ncWebsiteIdDummySelect.val();
        if (websiteID) {
            for (var i = 0; i < ncWebSiteModels.length; ++i) {
                var websiteModel = ncWebSiteModels[i];
                if (websiteModel.Id == websiteID) {
                    modelIndexes.websiteIndex = i;
                    break;
                }
            }
        }

        if (modelIndexes.websiteIndex != -1) {
            var profileID = ncProfileIdDummySelect.val();
            if (profileID) {
                var profileModels = ncWebSiteModels[modelIndexes.websiteIndex].WebsiteProfiles;
                for (var j = 0; j < profileModels.length; ++j) {
                    var profileModel = profileModels[j];
                    if (profileModel.Id == profileID) {
                        modelIndexes.profileIndex = j;
                        break;
                    }
                }
            }
        }

        return modelIndexes;
    }

    function ncResetProfileOptions(profileCount) {
        var placeholderText;
        if (profileCount > 0) {
            placeholderText = "-- Please select a profile name --";
        } else {
            placeholderText = "-- No profile found --"
        }
        ncProfileIdDummySelect.find('option').remove();
        ncProfileIdDummySelect
                .append(AJS.$("<option></option>")
                        .attr("value", "")
                        .text(placeholderText));
    }

    function ncAppendProfileOptions(ncProfileModels) {
        AJS.$.each(ncProfileModels, function (index, profileModel) {
            var profileText = profileModel.Name;
            ncProfileIdDummySelect
                    .append(AJS.$("<option></option>")
                            .attr("value", profileModel.Id)
                            .text(profileText));
        });
    }
</script>