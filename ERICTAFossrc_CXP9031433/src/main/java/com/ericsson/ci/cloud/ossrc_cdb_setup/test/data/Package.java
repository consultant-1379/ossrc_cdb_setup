package com.ericsson.ci.cloud.ossrc_cdb_setup.test.data;

import java.util.List;

import groovy.util.ConfigObject;

public class Package implements Comparable<Package>{
	
	private String deliveryDrop;
	private String group;
	private String mediaCategory;
	private String mediaPath;
	private String name;
	private String number;
	private String platform;
	private String type;
	private String url;
	private String version;
	
	public Package() {
		// TODO Auto-generated constructor stub
	}
	
	public Package(ConfigObject configObject) {
		// TODO Auto-generated constructor stub
		this.deliveryDrop=(String)configObject.get("deliveryDrop");
		this.group=(String)configObject.get("group");
		this.mediaCategory=(String)configObject.get("mediaCategory");
		this.mediaPath=(String)configObject.get("mediaPath");
		this.name=(String)configObject.get("name");
		this.number=(String)configObject.get("number");
		this.platform=(String)configObject.get("platform");
		this.type=(String)configObject.get("type");
		this.url=(String)configObject.get("url");
		this.version=(String)configObject.get("version");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Package other = (Package) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "Package [deliveryDrop=" + deliveryDrop + ", name=" + name + ", number=" + number + ", platform="
				+ platform + ", type=" + type + ", version=" + version + "]";
	}

	public String getDeliveryDrop() {
		return deliveryDrop;
	}

	public void setDeliveryDrop(String deliveryDrop) {
		this.deliveryDrop = deliveryDrop;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getMediaCategory() {
		return mediaCategory;
	}

	public void setMediaCategory(String mediaCategory) {
		this.mediaCategory = mediaCategory;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public int compareTo(Package package2) {
		// TODO Auto-generated method stub
		String []version1=this.version.split("\\.");
		String []version2=package2.version.split("\\.");
		
		int length = version1.length; 
		length =(version1.length>version2.length)?version1.length:version2.length;

		for (int i = 0; i < length; i++) {
			String s0 = null;
			if (i < version1.length)
				s0 = version1[i];
			Integer i0 = 0;
			try {
				i0 = (version1 == null) ? 0 : Integer.parseInt(s0);
			} catch (Exception e) {

			}
			String s1 = null;
			if (i < version2.length)
				s1 = version2[i];
			Integer i1 = 0;
			try {
				i1 = (s1 == null) ? 0 : Integer.parseInt(s1);
			} catch (Exception e) {

			}
			if (i0.compareTo(i1) < 0)
				return -1;
			else if (i1.compareTo(i0) < 0)
				return 1;
		}
		
		return 0;
	}
    
	
	
}

