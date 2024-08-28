package com.ericsson.ci.cloud.ossrc_cdb_setup.test.data;

public class PackageInfo {

	public PackageInfo(String packageName, String version) {
		super();
		this.packageName = packageName;
		this.version = version;
	}

	private String packageName;
	private String version;
	private String groupId;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return " " + packageName + " " + version + " " + groupId;
	}

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
