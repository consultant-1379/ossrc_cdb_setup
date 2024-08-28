package com.ericsson.ci.cloud.ossrc_cdb_setup.test.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InstallStep {
	
	String name;
	Date startTime;
	Date endTime;
	
	public static SimpleDateFormat endTimeFormatter = new SimpleDateFormat("MMM  dd HH:mm");
	public static SimpleDateFormat startTimeFormatter = new SimpleDateFormat("MMM_dd HH:mm:ss");

	//EndTime Format is Jun  3 17:57
	//StartTime Format in logs is [Jun_3 17:00:56]
	
	
	public InstallStep(String name,Date startTime, Date endTime ) {
		// TODO Auto-generated constructor stub
		this.name=name;
		this.startTime=startTime;
		this.endTime=endTime;
	}
	
	public InstallStep(String name) {
		// TODO Auto-generated constructor stub
		this.name=name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		try {
			this.startTime = startTimeFormatter.parse(startTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		try {
			this.endTime = endTimeFormatter.parse(endTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "InstallStep [name=" + name + ", startTime=" + startTime + ", endTime=" + endTime + "]";
	}
	
	public String getinHTMLData(){
		/*[ 'Washington', new Date(1789, 3, 30,10,50,0,0), new Date(1789,
				 * 3, 30,11,50,0,0) ],*/
		String data="\n[ '"+name+"', "+getDateHTML(startTime)+", "+getDateHTML(endTime)+" ],";
		return data;
		
	}
	
	public String getDateHTML(Date date){
		
		String dateString = "new Date("+2016+", "+date.getMonth()+", "+date.getDate()+", "+date.getHours()+","+date.getMinutes()+",0,0)";
		return dateString;
	}
	
	
}
