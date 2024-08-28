package com.ericsson.ci.cloud.ossrc_cdb_setup.test.data;

import java.util.ArrayList;
import java.util.List;

public class Drop implements Comparable<Drop>{
	
	public String version;
	public List<Package> packages;
	
	public Drop(String version) {
		// TODO Auto-generated constructor stub
		this.version=version;
		packages= new ArrayList<Package>();
	}
	
	public void addPackageList(Package packageObj) {
		packages.add(packageObj);
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return version;
	}

	@Override
	public int compareTo(Drop drop2) {
		// TODO Auto-generated method stub
		String []version1=this.version.split("\\.");
		String []version2=drop2.version.split("\\.");
		
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
