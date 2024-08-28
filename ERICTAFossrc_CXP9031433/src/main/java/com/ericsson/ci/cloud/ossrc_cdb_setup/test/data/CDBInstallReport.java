package com.ericsson.ci.cloud.ossrc_cdb_setup.test.data;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.InstallLogOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.InstallLogOperatorCli;
import com.ericsson.cifwk.taf.data.DataHandler;

public class CDBInstallReport {

	String logPath; 
	
	String regexLogs="[\\s\\S]([a-zA-Z]+[\\s]+[0-9]{1,2} [:0-9]{5})[\\s\\S]([\\S]+.log)";
	String startTimeRegex="([\\S\\s]+Waiting for required process to complete \\((\\S+)_complete\\):\\sOK)+[\\s\\S]{1}\\[([a-zA-Z]{3}_[0-9]{1,2} [0-9:]+)\\]";
	
	//Time Format is Jun  3 17:57
	//Time Format in logs is [Jun_3 17:00:56]
	
	public static String htmlReport = "\n<html>" + "\n<head>"
			+ "\n<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>"
			+ "\n<script type=\"text/javascript\">" + "\n google.charts.load('current', {'packages':['timeline']});"
			+ "\ngoogle.charts.setOnLoadCallback(drawChart);" + "\nfunction drawChart() {"
			+ "\nvar container = document.getElementById('timeline');"
			+ "\nvar chart = new google.visualization.Timeline(container);"
			+ "\nvar dataTable = new google.visualization.DataTable();"
			+ "\n    dataTable.addColumn({ type: 'string', id: 'Install Steps' });"
	// dataTable.addColumn({ type: 'string', id: 'name' });
			+ "\n    dataTable.addColumn({ type: 'date', id: 'Start' });"
			+ "\n    dataTable.addColumn({ type: 'date', id: 'End' });" + "\n    dataTable.addRows([%s"
			/*
			 * [ 'Washington', new Date(1789, 3, 30,10,50,0,0), new Date(1789,
			 * 3, 30,11,50,0,0) ], [ 'Adams', new Date(1789, 3, 30,11,50,0,0),
			 * new Date(1789, 3, 30,13,50,0,0) ], [ 'Jefferson', new Date(1789,
			 * 3, 30,11,0,0,0), new Date(1789, 3, 30,14,30,0,0) ]]);
			 */

			+ "\nchart.draw(dataTable);" + "\n      }" + "\n    </script>" + "\n  </head>"
			+ "\n<h3 align=center style=\"font-family : Arial; font-weight:bold; text-align:center;\">CDB log TimeLine Report</h3>" + "\n  <body>"
			+ "\n<div id=\"timeline\" style=\"height: 1000;\"></div>" + "\n  </body>" + "\n</html>";
	
	public CDBInstallReport() {
		// TODO Auto-generated constructor stub
		logPath=(String)DataHandler.getAttribute("logPath");
	}
	
	public void prepareAndSendCDBReport() {
		// TODO Auto-generated method stub
		InstallLogOperator installLogOperator= new InstallLogOperatorCli();
		List<InstallStep> installSteps= installLogOperator.getInstallStepsInfo();
		
		System.out.println(installSteps);
		
		String fullHtmlReport = String.format(htmlReport, getTimeLineChartData(installSteps));
		
		System.out.println("------------------------");
		System.out.println("------------------------");
		System.out.println(fullHtmlReport);
		KGBStatusManager.writeToFile(fullHtmlReport, "CDBStepsTimeLineReport.html");
		
		
	}
	
	public String getTimeLineChartData(List<InstallStep> installSteps) {
		StringBuffer buffer = new StringBuffer("");
		for (InstallStep installStep : installSteps) {
			buffer.append(installStep.getinHTMLData());
		}
		 
		buffer.append("]);");

		return buffer.toString();
	}

}
