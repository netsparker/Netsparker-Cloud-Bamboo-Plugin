<html>
<head>
    <title>Netsparker Enterprise</title>
    <meta name="decorator" content="adminpage">
${webResourceManager.requireResource("com.netsparker.netsparkercloud-bamboo-plugin:netsparker-cloud-web-resources")}
</head>
<body>
<div id="netsparkerCloudSuccessMessage" class="aui-message aui-message-success" style="display:none">
    <p class="title">
        Successfully connected to the Netsparker Enterprise.
    </p>
</div>
<div id="netsparkerCloudErrorMessage" class="aui-message aui-message-error" style="display: none">
    <p class="title">
        Failed to connect to the Netsparker Enterprise.
    </p>
</div>
[@ww.form action="/admin/netsparkercloud/NetsparkerCloudSaveConfiguration.action"
id="NetsparkerCloudConfigurationForm"
submitLabelKey='global.buttons.update'
cancelUri='/admin/administer.action']
<br>
<div class="paddedClearer"></div>
<div style="color: #3f3f3f;display:inline;font-size: 130%;">
    <img src="${req.contextPath}/download/resources/com.netsparker.netsparkercloud-bamboo-plugin:netsparker-cloud-assets/netsparker-cloud-logo.svg"
         alt="Netsparker Enterprise"
         style="vertical-align:top; margin-bottom:1px;display: inline-block;height:1.6em;width: auto;"/>
    <h1 style="zoom:1;color: #3f3f3f;display:inline-block">Netsparker Enterprise</h1>
</div>
<div class="aui-page-panel">
    <div class="aui-page-panel-inner">
        <section class="aui-page-panel-content">
            <h2 style="margin-left: 55px;">API Settings</h2>

            [@ww.textfield name="apiUrl" label='Server URL' description="Netsparker Enterprise URL, like 'https://www.netsparkercloud.com'"/]
            [@ww.password name="apiToken" label='API Token' showPassword='false' description="It can be found at 'Your Account > API Settings' page in the Netsparker Enterprise.<br/>
                         User must have 'Start Scans' permission for the target website."/]
            <br>
            <button type="button" id="netsparkerCloudTestConnectionButton" class="aui-button"
                    style="margin-left: 145px;">
                Test Connection
            </button>
            <div id="netsparkerCloudTestConnectionButtonSpinner"
                 style="display: inline-block;margin: 5px;margin-left:10px;"></div>
        </section>
    </div>
</div>
<br>
[/@ww.form]

<script>
    var ncServerURLInput, ncApiTokenInput;
    var ncTestConnectionButton, ncTestConnectionButtonSpinner;
    var ncTestConnectionSuccessMessage, ncTestConnectionErrorMessage;
    var TestConnectionModel = {};
    var NCResponseData;
    //do noy use $ for Jquery instead use jQuery
    AJS.$(document).ready(function () {
        initializeNcElementsAndParams();
    })

    function initializeNcElementsAndParams() {
        ncServerURLInput = AJS.$("#NetsparkerCloudConfigurationForm_apiUrl");
        ncApiTokenInput = AJS.$("#NetsparkerCloudConfigurationForm_apiToken");

        ncTestConnectionSuccessMessage = AJS.$("#netsparkerCloudSuccessMessage");
        ncTestConnectionErrorMessage = AJS.$("#netsparkerCloudErrorMessage");
        ncTestConnectionButton = AJS.$("#netsparkerCloudTestConnectionButton");
        ncTestConnectionButtonSpinner = AJS.$("#netsparkerCloudTestConnectionButtonSpinner");

        ncTestConnectionButton.click(ncTestConnection);
        ncServerURLInput.attr('placeholder', "Cloud URL, like 'https://www.netsparkercloud.com'");

        updateNcParams();
    }

    function updateNcParams() {
        TestConnectionModel.apiURL = ncServerURLInput.val();
        TestConnectionModel.apiToken = ncApiTokenInput.val();
    }

    function ncTestConnection() {
        updateNcParams();
        ncTestConnectionButtonSpinner.spin()
        ncTestConnectionButton.prop('disabled', true);


        var request = AJS.$.ajax({
            type: "POST",
            url: "${req.contextPath}/rest/plugin/netsparkercloud/api/1.0/testconnection",
            data: JSON.stringify(TestConnectionModel),
            contentType: "application/json",
            dataType: "json"
        });

        request.done(function (data, statusText, xhr) {
            NCResponseData = data;
            if (NCResponseData.netsparkerCloudStatusCode == "200") {
                ncTestConnectionErrorMessage.hide();
                ncTestConnectionSuccessMessage.show();
            } else {
                ncTestConnectionSuccessMessage.hide();
                ncTestConnectionErrorMessage.show();
            }

            ncTestConnectionButtonSpinner.spinStop();
            ncTestConnectionButton.prop('disabled', false);
        });

        request.fail(function (xhr, statusText) {
            ncTestConnectionSuccessMessage.hide();
            ncTestConnectionErrorMessage.show();

            ncTestConnectionButtonSpinner.spinStop();
            ncTestConnectionButton.prop('disabled', false);
        });
    }
</script>
</body>
</html>
