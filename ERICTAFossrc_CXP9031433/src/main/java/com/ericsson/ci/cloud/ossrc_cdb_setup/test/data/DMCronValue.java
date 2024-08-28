package com.ericsson.ci.cloud.ossrc_cdb_setup.test.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

public class DMCronValue {
	
	private String cronString;
	private String cronComment;
	private boolean isEnabled=true;
	private String cronType;
	
	private String minute;
	private String hour;
	private String date;
	private String month;
	private String day;
	private String script;
	private String server;
	private String mwsserver;
	private String shipment;
	
	
	Map<String,String> arguments=new HashMap<String,String>();
	
	public DMCronValue(String cronString, String cronComment) {
		// TODO Auto-generated constructor stub
		this.cronString=cronString;
		this.cronComment=cronComment;
		parseCronString(this.cronString);
		
	}
	
	private void parseCronString(String cronString) {
		
		if(cronString.startsWith("#")) {
			isEnabled=false;
			cronString=cronString.replaceAll("#", "");
			cronString=cronString.trim();
		}
		
		String []cronValues= cronString.split(" ");
		this.minute=cronValues[0];
		this.hour=cronValues[1];
		this.date=cronValues[2];
		this.month=cronValues[3];
		this.day=cronValues[4];
		this.script=cronValues[5];
		
		for (int i = 6; i < cronValues.length; i++) {
			String param= cronValues[i];
			String paramValue=cronValues[++i];
			arguments.put(param, paramValue);
			
		}
		
		if (arguments.get("-o") != null && arguments.get("-c") != null
				&& arguments.get("-z") != null && arguments.get("-f") != null
				&& arguments.get("-o").contains("YES")
				&& arguments.get("-c").contains("YES")
				&& arguments.get("-z").contains("NO")
				&& arguments.get("-f").contains("YES")) {

			this.cronType = "Media Rebuild";

		} else if (arguments.get("-x") != null && arguments.get("-v") != null
				&& arguments.get("-a") != null && arguments.get("-t") != null
				&& arguments.get("-x").contains("YES")
				&& arguments.get("-v").contains("N")
				&& arguments.get("-a").contains("NO")
				&& arguments.get("-t").contains("EU")) {

			this.cronType = "PLM cron for x86";

		} else if (arguments.get("-x") != null && arguments.get("-vs") != null
				&& arguments.get("-vx") != null && arguments.get("-a") != null && arguments.get("-t") != null
				&& arguments.get("-x").contains("YES")
				&& arguments.get("-vs").contains("Z")
				&& arguments.get("-vx").contains("Z")
				&& arguments.get("-a").contains("NO")
				&& arguments.get("-t").contains("EU")) {

			this.cronType = "PLM cron for Sparc + x86";

		} else if (arguments.get("-b") != null && arguments.get("-x") != null
				&& arguments.get("-v") != null && arguments.get("-a") != null && arguments.get("-t") != null
				&& arguments.get("-b").contains("YES")
				&& arguments.get("-x").contains("NO")
				&& arguments.get("-v").contains("C")
				&& arguments.get("-a").contains("NO")
				&& arguments.get("-t").contains("EU")) {

			this.cronType = "EU Build only";

		} else if (arguments.get("-m") != null && arguments.get("-x") != null
				&& arguments.get("-i") != null && arguments.get("-v") != null && arguments.get("-a") != null && arguments.get("-t") != null
				&& arguments.get("-m").contains("cloud")
				&& arguments.get("-x").contains("NO")
				&& arguments.get("-i").contains("NO")
				&& arguments.get("-v").contains("B")
				&& arguments.get("-a").contains("NO")
				&& arguments.get("-t").contains("EU")) {

			this.cronType = "EU build + Cloud VCDB";

		} else if (arguments.get("-n") != null && arguments.get("-n").contains("3")) {

			this.cronType = "Sprint Upgrade";

		} else if (arguments.get("-j") != null && arguments.get("-j").contains("YES")) {

			this.cronType = "Only Install";

		} else if(arguments.get("-b")!=null && arguments.get("-b").contains("YES")) {
			
			this.cronType="Only Build";
			
		} else {
			
			this.cronType="Build & Install";
		}
		
		this.server=arguments.get("-m");
		
		if(this.server !=null && this.server.contains("cloud")) {
			this.cronType+=" (Cloud)";
		}
		
		this.mwsserver=arguments.get("-N");
		this.shipment=arguments.get("-s");
		
	//	System.out.println(arguments  + "  "+this.cronType);
	}
	
	//"2 3 4 5 0,1,2,3,4 /view/ossrcdm_view/vobs/ossrc/del-mgt/isobuild/bin/evo/master_wrapper -r O16_2 -s 16.2.2 -t II -p i386 -m atrcxb976 -d NO -e hp -l LLSV3 -o YES -c YES -i NO -g test -I 159.107.173.42 -N ieatmws80 -P shroot";
	
	
	public String getCronPeriod() {
		StringBuffer buffer = new StringBuffer();
		
		if (this.minute.contains("*") && this.hour.contains("*"))
		{
			buffer.append("Every minute");
		}else if (this.minute.contains("*") && !this.hour.contains("*"))
		{
			buffer.append("Every minute of "+this.hour +" hour");
		}else if (!this.minute.contains("*") && this.hour.contains("*"))
		{
			buffer.append(" at "+this.minute+" minute of every hour");
		}else if (!this.minute.contains("*") && !this.hour.contains("*")) 
		{
			buffer.append(" at "+this.hour+":"+this.minute);
		}
		
		if(this.day.contains("*") && this.date.contains("*") && this.month.contains("*") )
		{
			buffer.append(" everyday");
		}else if (!this.day.contains("*") && this.date.contains("*") && this.month.contains("*") )
		{
			buffer.append(" on");
			String[] days=this.day.split(",");
			for (int i = 0; i < days.length-1; i++) {
				buffer.append(" "+getDay(days[i])+",");
			}
			buffer.append(" "+getDay(days[days.length-1]));
		}else if(this.date.contains("*") && !this.month.contains("*"))
		{
			buffer.append(" everyday of "+getMonth(this.month));
		}else if(!this.date.contains("*") && !this.month.contains("*"))
		{
			buffer.append(" on "+getDate(this.date)+" of "+getMonth(this.month));
		}else if(!this.date.contains("*") && this.month.contains("*"))
		{
			buffer.append(" on "+getDate(this.date)+" of "+getMonth(this.month));
		}
		
		
	//	System.out.println(buffer);
		return buffer.toString();
		
	}
	
	private String getDay(String n) {
		switch (n) {
		case "0": return "Sunday";
		case "1": return "Monday";
		case "2": return "Tuesday";
		case "3": return "Wednesday";
		case "4": return "Thursday";
		case "5": return "Friday";
		case "6": return "Saturday";
		default:
			return "everyday";
		}
	}
	
	private String getMonth(String n) {
		switch (n) {
		case "1": return "January";
		case "2": return "February";
		case "3": return "March";
		case "4": return "April";
		case "5": return "May";
		case "6": return "June";
		case "7": return "July";
		case "8": return "August";
		case "9": return "September";
		case "10": return "October";
		case "11": return "November";
		case "12": return "December";
		default:
			return "every month";
		}
	}
	
	private String getDate(String n) {
		switch (n) {
		case "1": return "1st";
		case "2": return "2nd";
		default:
			return n+"th";
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String cronInfo = "\nCron Comment \t:" + this.cronComment
				+ "\nIs Enabled \t:" + this.isEnabled 
				+ "\nCron Type \t:" + this.cronType 
				+ "\nCron Period \t:" + getCronPeriod()
				+ "\nCron Value \t:" + this.cronString 
				+ "\nTarget Server \t:"	+ this.server
				+ "\nMWS Server \t:"	+ this.mwsserver;
	
		return cronInfo;
	}
	
	public String getHTMLRowFormat() {
		
		StringBuffer buffer= new StringBuffer("");
		if(isEnabled) {
		buffer.append("<tr>\n"
				+ "<th align=center rowspan =5 bgcolor=#066F21>"+this.cronType+"</th>\n");
		}else {
		buffer.append("<tr>\n"
				+ "<th align=center rowspan =5 bgcolor=#FF0000>"+this.cronType+"</th>\n");
		}
		
		buffer.append("<td>Cron Comment</td><td>"+this.cronComment+"</td> </tr>\n");
		//buffer.append("<td>Is Enabled</td><td>"+this.isEnabled+"</td> </tr>\n");
		buffer.append("<td>Cron Period</td><td>"+getCronPeriod()+"</td> </tr>\n");
		buffer.append("<td>Cron Value</td><td>"+this.cronString+"</td> </tr>\n");
		buffer.append("<td>Target Server</td><td>"+this.server+"</td> </tr>\n");
		buffer.append("<td>MWS Server</td><td>"+this.mwsserver+"</td> </tr>\n");
		buffer.append("<tr><td colspan=3 bgcolor=#1E025C></td></tr>");
		return buffer.toString();
	}
	
	public String getShipment() {
		return shipment;
	}
	

}
