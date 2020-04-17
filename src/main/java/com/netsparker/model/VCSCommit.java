package com.netsparker.model;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VCSCommit {

    public static VCSCommit Empty() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        String dateString = dateFormat.format(new Date());
        return new VCSCommit("", "", "", "", "", false, "", "", "", dateString);
    }

    public VCSCommit(String bambooVersion, String pluginVersion, String buildId,
            String buildConfigurationName, String buildURL, boolean buildHasChange,
            String versionControlName, String Committer, String vcsVersion, String ciTimestamp) {
        this.ciBuildServerVersion = bambooVersion;
        this.ciNcPluginVersion = pluginVersion;
        this.buildId = buildId;
        this.buildConfigurationName = buildConfigurationName;
        this.buildURL = buildURL;
        this.buildHasChange = buildHasChange;
        this.versionControlName = versionControlName;
        this.committer = Committer;
        this.vcsVersion = vcsVersion;
        this.ciTimestamp = ciTimestamp;
    }

    private final String ciBuildServerVersion;
    private final String ciNcPluginVersion;
    private final String buildId;
    private final String buildConfigurationName;
    private final String buildURL;
    private final boolean buildHasChange;
    private final String versionControlName;
    private final String committer;
    private final String vcsVersion;
    private final String ciTimestamp;

    public void setRootURL(String rootURL) {
        if (rootURL == null) {
            return;
        }
    }

    public String getCiBuildServerVersion() {
        return ciBuildServerVersion;
    }

    public String getCiNcPluginVersion() {
        return ciNcPluginVersion;
    }

    public String getBuildId() {
        return buildId;
    }

    public String getBuildConfigurationName() {
        return buildConfigurationName;
    }

    public String getBuildURL() {
        return buildURL;
    }

    public boolean BuildHasChange() {
        return buildHasChange;
    }

    public String getVersionControlName() {
        return versionControlName;
    }

    public String getCommitter() {
        return committer;
    }

    public String getVcsVersion() {
        return vcsVersion;
    }

    public String getCiTimestamp() {
        return ciTimestamp;
    }

    public void addVcsCommitInfo(List<NameValuePair> params) {
        params.add(new BasicNameValuePair("VcsCommitInfoModel.CiBuildId", buildId));
        params.add(new BasicNameValuePair("VcsCommitInfoModel.IntegrationSystem", "Bamboo"));
        params.add(new BasicNameValuePair("VcsCommitInfoModel.CiBuildServerVersion",
                ciBuildServerVersion));
        params.add(
                new BasicNameValuePair("VcsCommitInfoModel.CiNcPluginVersion", ciNcPluginVersion));
        params.add(new BasicNameValuePair("VcsCommitInfoModel.CiBuildConfigurationName",
                buildConfigurationName));
        params.add(new BasicNameValuePair("VcsCommitInfoModel.CiBuildUrl", buildURL));
        params.add(new BasicNameValuePair("VcsCommitInfoModel.CiBuildHasChange",
                String.valueOf(buildHasChange)));
        params.add(new BasicNameValuePair("VcsCommitInfoModel.CiTimestamp", ciTimestamp));
        params.add(new BasicNameValuePair("VcsCommitInfoModel.VcsName", versionControlName));
        params.add(new BasicNameValuePair("VcsCommitInfoModel.VcsVersion", vcsVersion));
        params.add(new BasicNameValuePair("VcsCommitInfoModel.Committer", committer));
    }
}
