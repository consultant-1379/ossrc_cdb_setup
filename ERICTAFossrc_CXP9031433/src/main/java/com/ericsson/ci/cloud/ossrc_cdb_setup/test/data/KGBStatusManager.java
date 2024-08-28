package com.ericsson.ci.cloud.ossrc_cdb_setup.test.data;

import groovy.model.FormModel;
import groovy.util.ConfigObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.data.parser.JsonParser;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.utils.FileFinder;

import org.apache.commons.collections.map.HashedMap;

public class KGBStatusManager {

	public static final String header = "\n<html>" + "\n<style media=screen type=text/css>"
			+ "\n#weekNumber {color: #FFFFFF;background-color: #0070C0; font-family : Arial; font-size: 10pt; font-weight:bold; text-align:center; }"
			+ "\n#day        {color: #000000;background-color: #66ffff; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#SprintDay  {color: #000000;background-color: #ccffff; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#OverAllAvg {color: #000000;background-color: #fabe8f; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#AvgVal     {color: #000000;background-color: #ffffcc; font-family : Arial; font-size: 8pt; font-weight:bold; text-align:center; }"
			+ "\n#TA         {color: #000000;background-color: #948a54; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#TAVal      {color: #000000;background-color: #cccc00; font-family : Arial; font-size: 8pt; font-weight:bold; text-align:center; }"
			+ "\n#noRun      {color: #000000;background-color: #ccc0da; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#noRunPass  {color: #000000;background-color: #92d050; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#noRunFail  {color: #000000;background-color: #ff9999; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#fail       {color: #000000;background-color: #ff9999; font-family : Arial; font-size: 8pt; font-weight:bold; text-align:center; }"
			+ "\n#pass       {color: #000000;background-color: #92d050; font-family : Arial; font-size: 8pt; font-weight:bold; text-align:center; }"
			+ "\n#blank      {color: #000000;background-color: #ffffff; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#dayVal      {color: #000000;background-color: #a2b7d5; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#head      {color: #2E4275;background-color: #ffffff; font-family : Arial; font-weight:bold; text-align:center; }"
			+ "\n#head2      {color: #2E4275;background-color: #ffffff; font-family : Arial; font-weight:bold; text-align:left; }"
			+ "\n</style>" + "\n<h1 id=head>OSSRC CI :: Day-wise KGB status</h1>" + "\n<body>"
			+ "\n<table><tr><td><img src=\"https://cifwk-oss.lmera.ericsson.se/static/tmpUploadStore/%sChart.png\" alt=\"\" align=center></td></tr></table>"
			+ "\n<h5 id=head2>LEGEND for Data Table :: F => No. of Failed TC , E => Total TC Executed, DP => Delivered Packages</h5>"
			+ "\n<table cellSpacing=1 cellPadding=4 border=2 style=\"border-collapse:collapse;\">";

	public static final String footer =/* "\n<html>" +*/ "\n</table>" /*+ "\n</body>" + "\n</html>"*/;
	//
	public static final String finalReport = "\n<html> <body><img src=\"https://cifwk-oss.lmera.ericsson.se/static/tmpUploadStore/%sChart.png\" alt=\"\" align=center width=1800 height=2500></body></html>";

	public static final String piechart = "\n<html>" + "\n<style media=screen type=text/css>"
			+ "\n#weekNumber {color: #FFFFFF;background-color: #0070C0; font-family : Arial; font-size: 10pt; font-weight:bold; text-align:center; }"
			+ "\n#day        {color: #000000;background-color: #66ffff; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#SprintDay  {color: #000000;background-color: #ccffff; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#OverAllAvg {color: #000000;background-color: #fabe8f; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#AvgVal     {color: #000000;background-color: #ffffcc; font-family : Arial; font-size: 8pt; font-weight:bold; text-align:center; }"
			+ "\n#TA         {color: #000000;background-color: #948a54; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#TAVal      {color: #000000;background-color: #cccc00; font-family : Arial; font-size: 8pt; font-weight:bold; text-align:center; }"
			+ "\n#noRun      {color: #000000;background-color: #ccc0da; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#noRunPass  {color: #000000;background-color: #92d050; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#noRunFail  {color: #000000;background-color: #ff9999; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#fail       {color: #000000;background-color: #ff9999; font-family : Arial; font-size: 8pt; font-weight:bold; text-align:center; }"
			+ "\n#pass       {color: #000000;background-color: #92d050; font-family : Arial; font-size: 8pt; font-weight:bold; text-align:center; }"
			+ "\n#blank      {color: #000000;background-color: #ffffff; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#dayVal      {color: #000000;background-color: #a2b7d5; font-family : Arial; font-size: 9pt; font-weight:bold; text-align:center; }"
			+ "\n#head      {color: #2E4275;background-color: #ffffff; font-family : Arial; font-weight:bold; text-align:center; }"
			+ "\n#head2      {color: #2E4275;background-color: #ffffff; font-family : Arial; font-weight:bold; text-align:left; }"
			+ "\n</style>" + "\n<h1 id=head>OSSRC CI :: Day-wise KGB status</h1>" + "<head>"
			+ "\n<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>"
			+ "\n<script type=\"text/javascript\">" + "\nvar pieData;" + "\nvar pieChart;" + "\nvar barData;"
			+ "\nvar barChart;" + "\nvar barData_pkg;" + "\nvar barChart_pkg;"
			+ "\ngoogle.charts.load('current', {'packages':['corechart']});"
			+ "\ngoogle.charts.setOnLoadCallback(drawChart);" + "\n function drawChart() {" + "\n %s " // Bar
																										// Chart
																										// Data
			+ "\nvar lineoptions = {"
			+ "\ntitle: 'Test Case Count on each day of the sprint',width: 1500,height: 200,legend: { position: 'bottom', maxLines: 3 },"
			+ "\nseries: {0:{annotations: {textStyle: {fontSize: 12, color: 'black' }}},1: {annotations: {textStyle: {fontSize: 12, color: 'black' }}}},"
			+ "\nbar: { groupWidth: '22' }, isStacked: false,'colors':['#00ff0f','#ff0000'],'chartArea':{left:70,width:1500,top:50}};"
			+ "\nvar linechart = new google.visualization.ColumnChart(document.getElementById('lineChart'));"
			+ "\nlinechart.draw(linedata, lineoptions);" + "\n %s " // Bar chart
																	// data for
																	// no. of
																	// deliveries.
			+ "\n var baroptions_pkg = {"
			+ "\n'title':'No. of Deliveries from TAs in the Sprint',width: 1500,height: 600,legend: { position: 'bottom', maxLines: 3 },"
			+ "\nseries: {0:{annotations: {textStyle: {fontSize: 12, color: 'black' }}},1: {annotations: {textStyle: {fontSize: 12, color: 'black' }}}},"
			+ "\nbar: { groupWidth: '15' }, isStacked: true,'colors':['#0000ff'],'chartArea':{left:300,width:1200,top:50,bottom:50}};"
			+ "\nbarChart_pkg = new google.visualization.BarChart(document.getElementById(\"barChart_pkg\"));"
			+ "\ngoogle.visualization.events.addListener(barChart_pkg, 'select', selectHandler);"
			+ "\nbarChart_pkg.draw(barData_pkg, baroptions_pkg);" + "" + "\nconsole.log('Google.chart.ready');" + "\n}"
			+ "\nfunction selectHandler() {" + "\n var selectedItem = chart.getSelection()[0];"
			+ "\n var value = data.getValue(selectedItem.row, 0);" + "\n alert('The user selected ' + value);" + "\n  }"
			+ "\n</script>" + "\n</head>" + "\n<body>" + "\n<table>"
			+ "\n<tr><td><canvas id=myCanvas width=500 height=270></canvas></td>"
			// + "\n<td><div id=pieChart style=\"width:300;
			// height:300\"></div></td>" + "\n</tr></table>"
			+ "\n<td>" + "\n<table cellSpacing=1 cellPadding=4 border=2 style=\"border-collapse:collapse;\">" + "\n%s"
			+ "\n</table>" + "</td>" + "\n</tr></table>" + "<table>"
			/*
			 * +
			 * "\n<tr><td colspan=3><div id=barChart style=\"width:1800; height:300\"></div></td></tr>"
			 */
			+ "<tr><td colspan=3><div id=lineChart style=\"width:1800; height:200\"></div></td></tr>"
			+ "\n<tr><td colspan=3><div id=barChart_pkg style=\"width:1800; height:600\"></div></td></tr></table>"
			+ "\n<h5 id=head2>LEGEND for Data Table :: F => No. of Failed TC , E => Total TC Executed, DP => Delivered Packages</h5>"
			+ "\n<table cellSpacing=1 cellPadding=4 border=2 style=\"border-collapse:collapse;\">"
			+ "%s"
			
			+ "\n<script>" + "\nvar canvas = document.getElementById('myCanvas');"
			+ "\nvar context = canvas.getContext('2d');" + "\nvar rectWidth = 440;" + "\nvar rectHeight = 160;"
			+ "\nvar rectX = 50;" + "\nvar rectY = 50;" + "\nvar cornerRadius = 50;" + "\ncontext.beginPath();"
			+ "\ncontext.moveTo(rectX+cornerRadius/2, rectY);"
			+ "\ncontext.lineTo(rectX + rectWidth - cornerRadius/2, rectY);"
			+ "\ncontext.arcTo(rectX + rectWidth, rectY,        rectX + rectWidth, rectY + cornerRadius/2, cornerRadius/2);"
			+ "\ncontext.lineTo(rectX + rectWidth, rectY + rectHeight-cornerRadius/2);"
			+ "\ncontext.arcTo(rectX + rectWidth, rectY + rectHeight,rectX + rectWidth -cornerRadius/2, rectY + rectHeight, cornerRadius/2);"
			+ "\ncontext.lineTo(rectX + cornerRadius/2, rectY + rectHeight);"
			+ "\ncontext.arcTo(rectX, rectY  + rectHeight, rectX, rectY + rectHeight-cornerRadius/2 , cornerRadius/2);"
			+ "\ncontext.lineTo(rectX, rectY + cornerRadius/2);"
			+ "\ncontext.arcTo(rectX, rectY,rectX+cornerRadius/2, rectY  , cornerRadius/2);" + "\ncontext.stroke();"
			+ "\nvar grd = context.createRadialGradient(rectX+rectWidth/2, rectY+rectHeight/2, 0, rectX+rectWidth/2, rectY+rectHeight/2, 200);"
			+ "\ngrd.addColorStop(0, '#8ED6FF');" + "\ngrd.addColorStop(1, '#004CB3');" + "\ncontext.fillStyle = grd;"
			+ "\ncontext.fill();" + "\ncontext.font = '25pt Arial';" + "\ncontext.fillStyle = 'white';"
			+ "\ncontext.fillText('OSSRC CI KGB Status', 75, 120);" + "\ncontext.font = '20pt Calibri';"
			+ "\ncontext.fillText('   Sprint :: %s', 150, 160);" + "\ncontext.fillText('         Day :: %d', 130, 200);"
			+ "\ncontext.rect(0, 0, canvas.width, canvas.height);" + "\n</script></body></html>";

	private Map<String, Map<Date, DailyStatus>> dailyStatusList = new TreeMap<String, Map<Date, DailyStatus>>(); // teamName,Date,DailyStatus
	// private Map<String, DailyStatus> totalDailyStatusList = new
	// TreeMap<String, DailyStatus>(); // Date,TotalStatus
	private Map<Date, Boolean> catalogReleasedStatus = new TreeMap<Date, Boolean>(); // Date,isDelivered
	private Map<String, String> teamCache = new HashedMap(); // packageName,teamName
	private final Set<String> otherPackageSet = new HashSet<String>();
	List<String> passedTeams = new ArrayList<String>();
	List<String> failedTeams = new ArrayList<String>();
	List<String> notRunTeams = new ArrayList<String>();
//	String []TAList={"APROV","CCFM","CCOPT","CCPM","CCSHM","CN","DEPL","GR","IP_CM","PF","PF_SEC","WL","WLR_CM_APPS","WLR_CM_CONFIG"};
//	String []TAList={"TENG","FM","EBA","PMS","SHM","NSS","GMA","MMA","NMA","NSD","COMINF","BSM","EAM","ARNE","SCK","SECURITY","USCK","CEX","PCI","RPS","RRPM","BCG","CMS","CSMP","MOM"};
	String []TAList={"BSIM-TENG","FM","EBA","PMS","SHM","NSS","GMA","MMA","NMA","NSD-EPC","COMINF","BSM","EAM","ARNE","SCK","SECURITY","USCK","CEX","PCI","RPS","RRPM","BCG","CMS","CSMP","MOM","CMS-PLUGINS"};

	SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd");
	SimpleDateFormat catalogFormatter = new SimpleDateFormat("dd-MMM-yyyy");

	public String startDate = (String) DataHandler.getAttribute("startDate");
	public String endDate = (String) DataHandler.getAttribute("endDate");
	public String sprint = (String) DataHandler.getAttribute("sprint");
	public String nthDate = null;
	public Date nthDate_Date = null;
	public int dayth = 0;

	private static final Host hostHub = DataHandler.getHostByType(HostType.CIFWK);
	private static final CLICommandHelper cliCommandHelperHub = new CLICommandHelper(hostHub,
			hostHub.getUsers(UserType.CUSTOM).get(0));

	public KGBStatusManager() {
		nthDate = (String) DataHandler.getAttribute("nthDate");
		for (String tal : TAList) {
			dailyStatusList.put(tal, null);
		}
		boolean isDate = true;
		try {
			nthDate_Date = formatter.parse(nthDate);
		} catch (Exception e) {
			isDate = false;
			e.printStackTrace();
		}
		if (!isDate) {
			Date date = new Date();
			nthDate = formatter.format(new Date());
		}
		try {
			nthDate_Date = formatter.parse(nthDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getChartInHtml() {
		StringBuffer buffer = new StringBuffer("");
		return buffer.toString();
	}

	public String getPieChartData() {
		StringBuffer buffer = new StringBuffer("");
		Set<String> TAs = dailyStatusList.keySet(); //team names
		// buffer.append("\npieData.addColumn('string', 'KGB status');" +
		// "pieData.addColumn('number', 'No. of TA');"
		// + "pieData.addRows([");
		Date nDate = null;
		try {
			nDate = formatter.parse(nthDate); //current date
			System.out.println("nthDate Value : "+nthDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int kbgNotRun = 0;
		int kgbPass = 0;
		int kgbFail = 0;
		for (String ta : TAs) {
			DailyStatus dailyStatus = dailyStatusList.get(ta)!=null?dailyStatusList.get(ta).get(nDate):null; //get will return Date and DailyStatus details
			if (dailyStatus != null) {
				if (dailyStatus.getFailed() == 0) {
					kgbPass++;
					passedTeams.add(ta);
				} else {
					kgbFail++;
					failedTeams.add(ta);
				}
			} else {
				kbgNotRun++;
				notRunTeams.add(ta);
			}
		}

		/*
		 * buffer.append("['KGB Passed TAs'," + kgbPass + "],"); buffer.append(
		 * "['KGB Failed TAs'," + kgbFail + "],"); buffer.append(
		 * "['Not Run TAs'," + kbgNotRun + "],"); buffer.append("]);");
		 */
		buffer.append("<tr id=TA><th>Tests Result</th><th colspan=3>TAL2</th></tr>");
		buffer.append("<tr id=pass><td>KGB Passed today</td><td colspan=3>" + passedTeams + "</td></tr>");
		buffer.append("<tr id=fail><td>KGB Failed today</td><td colspan=3>" + failedTeams + "</td></tr>");
		buffer.append("<tr id=noRun><td>KGB not run today</td><td colspan=3>" + notRunTeams + "</td></tr>");
		//buffer.append("<tr id=noRun><td>Other Teams include</td><td colspan=5>" + otherPackageSet + "</td></tr>");
		return buffer.toString();
	}

	public String getBarChartData() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append("\nvar barData = google.visualization.arrayToDataTable([['TA', 'TC Passed', 'TC failed', ],");
		Set<String> TAs = dailyStatusList.keySet();
		Date startDate = null;
		try {
			startDate = formatter.parse(nthDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		for (String ta : TAs) {
			DailyStatus dailyStatus = dailyStatusList.get(ta)!=null?dailyStatusList.get(ta).get(startDate):null;
			if (dailyStatus != null) {
				buffer.append("['" + ta + "', " + (dailyStatus.getTotal() - dailyStatus.getFailed()) + ", "
						+ dailyStatus.getFailed() + "],");
			} else {
				buffer.append("['" + ta + "',0,0],");
			}
		}
		buffer.append("]);");
		return buffer.toString();
	}

	public String getBarChartDataForPackageDelivery() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(
				"\nvar barData_pkg = google.visualization.arrayToDataTable([['TA', 'No. of Deliveries',{ role: 'annotation' }],");
		Set<String> TAs = dailyStatusList.keySet();
		StringBuffer otherBuffer = new StringBuffer("");
		for (String ta : TAs) {
			Map<Date, DailyStatus> dailyStatusMap = dailyStatusList.get(ta);
			Date startDate = null, endDate = null;
			try {
				startDate = formatter.parse(this.startDate);
				endDate = formatter.parse(this.endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			double noOfDeliveryAvg = 0;
			
			for (Date currentDate = startDate; currentDate.before(endDate)
					|| currentDate.equals(endDate) && currentDate.before(new Date()); currentDate
							.setDate(currentDate.getDate() + 1)) {
				if(dailyStatusMap!=null){
				DailyStatus dailyStatus = dailyStatusMap.get(currentDate);
				if (dailyStatus != null) {

					noOfDeliveryAvg += dailyStatus.getNoOfDelivery();
				}
				}
			}
			
			if(!ta.equals("OTHERS")){
			buffer.append("['" + ta + "'," + noOfDeliveryAvg + "," + noOfDeliveryAvg + "],");
			}else{
				otherBuffer.append("['" + ta + "'," + noOfDeliveryAvg + "," + noOfDeliveryAvg + "],");
					
			}
		}
		buffer.append(otherBuffer);
		buffer.append("]);");
		return buffer.toString();
	}

	public String getLineChartDataForTestCaseCount() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(
				"\nvar linedata = google.visualization.arrayToDataTable([['Day', 'Total Executed',{ role: 'annotation' }, 'Failed',{ role: 'annotation' }],");

		Date startDate = null, endDate = null, nthDate = null;
		try {
			startDate = formatter.parse(this.startDate);
			endDate = formatter.parse(this.endDate);
			nthDate = formatter.parse(this.nthDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int i = 1;
		Set<String> TAs = dailyStatusList.keySet();
		int lastNoOfTCExecuted = 0;
		int lastNoOfTCFailed = 0;

		for (Date currentDate = startDate; currentDate.before(endDate)
				|| currentDate.equals(endDate) && currentDate.before(new Date()); currentDate
						.setDate(currentDate.getDate() + 1), i++) {
			int noOfTCExecuted = 0;
			int noOfTCFailed = 0;
			for (String ta : TAs) {
				Map<Date, DailyStatus> dailyStatusMap = dailyStatusList.get(ta);
				
				DailyStatus dailyStatus = dailyStatusMap!=null?dailyStatusMap.get(currentDate):null;
				if (dailyStatus != null) {
					noOfTCExecuted += dailyStatus.getTotal();
					noOfTCFailed += dailyStatus.getFailed();
				}
			}
			if (currentDate.after(new Date())) {
				break;
			}
			lastNoOfTCExecuted = noOfTCExecuted;
			lastNoOfTCFailed = noOfTCFailed;
			buffer.append("['Day " + i + "'," + noOfTCExecuted + "," + noOfTCExecuted + "," + noOfTCFailed + ","
					+ noOfTCFailed + "],");
		}
		buffer.append("]);");
		return buffer.toString();
	}

	public String getKGBStatusinHTML() {
		StringBuffer html = new StringBuffer("");
		//html.append(String.format(header, (String) DataHandler.getAttribute("BUILD_TAG")));
		html.append(getStatusContentinHTML());
		html.append(getOthersInfo());
		//html.append(footer);
		return html.toString();
	}

	public String getFinalReportinHTML() {
		StringBuffer html = new StringBuffer("");
		html.append(String.format(finalReport, (String) DataHandler.getAttribute("BUILD_TAG")));
		return html.toString();
	}

	public String getOthersInfo() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append("\n<code>Other Teams include : "+otherPackageSet+"</code>");
		
		return buffer.toString();

	}

	public String getStatusContentinHTML() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append("\n<tr>");
		buffer.append("\n<td id=day colspan=1>Day</td>");
		Date startDate = null, endDate = null;
		try {
			startDate = formatter.parse(this.startDate);
			endDate = formatter.parse(this.endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat tableDateformatter = new SimpleDateFormat("dd-MM-yy");
		for (Date currentDate = startDate; currentDate.before(endDate)
				|| currentDate.equals(endDate) && currentDate.before(new Date()); currentDate
						.setDate(currentDate.getDate() + 1)) {
			buffer.append("<td id=blank colspan=3>" + tableDateformatter.format(currentDate) + "</td>");
		}
		buffer.append("\n</tr>");
		buffer.append("\n</tr>");
		buffer.append("\n<tr>");
		buffer.append("\n<td id=SprintDay colspan=1>Sprint Day</td>");
		try {
			startDate = formatter.parse(this.startDate);
			endDate = formatter.parse(this.endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int i = 1;
		for (Date currentDate = startDate; currentDate.before(endDate)
				|| currentDate.equals(endDate) && currentDate.before(new Date()); currentDate
						.setDate(currentDate.getDate() + 1)) {
			buffer.append("<td id=dayVal colspan=3>Day " + i + "</td>");
			if (currentDate.equals(nthDate_Date)) {
				dayth = i;
			}
			i++;

		}
		buffer.append("\n</tr>");

		buffer.append("\n<tr>");
		buffer.append("\n<td id=blank colspan=1>Catalog Released</td>");
		try {
			startDate = formatter.parse(this.startDate);
			endDate = formatter.parse(this.endDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		i = 0;
		for (Date currentDate = startDate; currentDate.before(endDate)
				|| currentDate.equals(endDate) && currentDate.before(new Date()); currentDate
						.setDate(currentDate.getDate() + 1)) {

			if (catalogReleasedStatus.get(currentDate) != null && catalogReleasedStatus.get(currentDate)) {
				buffer.append("<td id=Pass colspan=3>Yes</td>");
			} else {
				buffer.append("<td id=blank colspan=3>No</td>");
			}

		}
		buffer.append("\n</tr>");

		buffer.append("\n</tr>");
		buffer.append("\n<tr>");
		buffer.append("\n<td id=TA>TAL2</td>");
		try {
			startDate = formatter.parse(this.startDate);
			endDate = formatter.parse(this.endDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		i = 0;
		for (Date currentDate = startDate; currentDate.before(endDate)
				|| currentDate.equals(endDate) && currentDate.before(new Date()); currentDate
						.setDate(currentDate.getDate() + 1)) {

			buffer.append("<td id=blank colspan=3></td>");
			i++;

		}
		buffer.append("\n</tr>");

		buffer.append("\n</tr>");
		buffer.append("\n<tr>");
		buffer.append("\n<td id=TAVal></td>");
		try {
			startDate = formatter.parse(this.startDate);
			endDate = formatter.parse(this.endDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		for (Date currentDate = startDate; currentDate.before(endDate)
				|| currentDate.equals(endDate) && currentDate.before(new Date()); currentDate
						.setDate(currentDate.getDate() + 1)) {

			buffer.append("<td id=TAVal>F</td><td id=TAVal>E</td><td id=TAVal>DP</td>");

		}
		buffer.append("\n</tr>");

		Set<String> keys = dailyStatusList.keySet();
		StringBuffer otherBuffer = new StringBuffer("");
		for (String key : keys) {
			if(!key.equals("OTHERS")){
			buffer.append("\n<tr>");
			buffer.append("\n<td id=TAVal>" + key.trim() + "</td>");
			buffer.append(getCompleteDataInHTML(dailyStatusList.get(key.trim()), key.trim()));

			buffer.append("\n</tr>");
			}else {
				otherBuffer.append("\n<tr>");
				otherBuffer.append("\n<td id=TAVal>" + key.trim() + "</td>");
				otherBuffer.append(getCompleteDataInHTML(dailyStatusList.get(key.trim()), key.trim()));

				otherBuffer.append("\n</tr>");
			}
		}
		buffer.append(otherBuffer);
		buffer.append("\n</table><br>");

		return buffer.toString();
	}

	public String getCompleteDataInHTML(Map<Date, DailyStatus> dailyStatusMap, String TA) {
		StringBuffer buffer = new StringBuffer();

		Date startDate = null, endDate = null;
		try {
			startDate = formatter.parse(this.startDate);
			endDate = formatter.parse(this.endDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		double failedAvg = 0;
		double totalAvg = 0;
		double noOfDeliveryAvg = 0;
		int count = 0;

		StringBuffer avgBuffer = new StringBuffer("");
		StringBuffer dataBuffer = new StringBuffer("");
		int lastStatus = 0;
		for (Date currentDate = startDate; currentDate.before(endDate)
				|| currentDate.equals(endDate) && currentDate.before(new Date()); currentDate
						.setDate(currentDate.getDate() + 1)) {

			DailyStatus dailyStatus = dailyStatusMap!=null?dailyStatusMap.get(currentDate):null;
			if (dailyStatus != null) {

				failedAvg += dailyStatus.getFailed();
				totalAvg += dailyStatus.getTotal();
				noOfDeliveryAvg += dailyStatus.getNoOfDelivery();
				count++;
				dataBuffer.append(dailyStatus.toString());
				if (dailyStatus.getFailed() == 0) {
					lastStatus = 1;
				} else {
					lastStatus = 2;
				}
			} else {
				/*
				 * if(lastStatus==0 || currentDate.after(new Date())){
				 * dataBuffer.append("<td id=noRun colspan=3></td>"); }else
				 * if(lastStatus==1){ dataBuffer.append(
				 * "<td id=noRunPass colspan=3></td>"); }else if(lastStatus==2){
				 * dataBuffer.append("<td id=noRunFail colspan=3></td>"); }
				 */
				dataBuffer.append("<td id=noRun colspan=3></td>");
			}

		}

		if (count != 0) {
			failedAvg /= count;
			totalAvg /= count;
			noOfDeliveryAvg /= count;

			failedAvg = Math.round(failedAvg * 100) / 100;
			totalAvg = Math.round(totalAvg * 100) / 100;
			noOfDeliveryAvg = Math.round(noOfDeliveryAvg * 100) / 100;

		}

		// DailyStatus totalDailyStatus = new DailyStatus((int) failedAvg, (int)
		// totalAvg, (int) noOfDeliveryAvg);
		// totalDailyStatusList.put(TA, totalDailyStatus);

		avgBuffer.append("<td id=AvgVal>" + failedAvg + "</td><td id=AvgVal>" + totalAvg + "</td><td id=AvgVal>"
				+ noOfDeliveryAvg + "</td>");

		buffer.append(/* avgBuffer.toString() + */dataBuffer.toString());

		return buffer.toString();
	}

	public void addCatalogStatus() {

		String catalogStatusPath = (String) DataHandler.getAttribute("catalogStatusPath");
		try {
			FileReader fileReader = new FileReader(catalogStatusPath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				try {
					String[] data = line.split("-");
					if (data.length == 2) {
						data[1] = data[1].replace("  ", " ");
						String[] dateArray = data[1].split(" ");
						Date date = catalogFormatter.parse(dateArray[2] + "-" + dateArray[1] + "-" + dateArray[5]);
						if (data[0].trim().equals(sprint)) {
							System.out.println(date);
							catalogReleasedStatus.put(date, true);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(catalogReleasedStatus);
	}

	public void addJsonContent() {

		String content = getTestCaseResult(this.startDate, sprint, (String) DataHandler.getAttribute("ossrcProduct")/*,
				this.endDate*/);
		content = "[" + content + "]";

		List<ConfigObject> configObjects = JsonParser.parse(content);
		for (ConfigObject configObject : configObjects) {
			Set<String> dateSets = configObject.keySet();
			for (String dateString : dateSets) {
				System.out.println(dateString);
				Date date = null;
				try {
					date = formatter.parse(dateString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				System.out.println(date);

				Map<String, ConfigObject> dailyConfigObject = (Map<String, ConfigObject>) configObject.get(dateString);
				Set<String> packages = dailyConfigObject.keySet();
				for (String packageName : packages) {
					Map<String, Object> packageData = dailyConfigObject.get(packageName);
					System.out.println("-------------" + packageData);

					int failed = (int) Double.parseDouble("" + packageData.get("failed"));
					// int skipped =
					// (int)Double.parseDouble(""+packageData.get("skipped"));
					int passed = (int) Double.parseDouble("" + packageData.get("passed"));
					//String team = "" + packageData.get("team");
					String team = getTeam(packageName);

					if (team == null || team.trim().equals("")) {
						team = "OTHERS";
						otherPackageSet.add(packageName);
					}
					
					
					System.out.println("-------" + team);
					teamCache.put(packageName, team);
					Map<Date, DailyStatus> dayWiseStatus = dailyStatusList.get(team);
					if (dayWiseStatus == null) {
						dayWiseStatus = new HashedMap();
						dailyStatusList.put(team, dayWiseStatus);
					}
					DailyStatus dailyStatus = dayWiseStatus.get(date);
					if (dailyStatus == null) {
						dailyStatus = new DailyStatus(failed, passed + failed, 0);
						dayWiseStatus.put(date, dailyStatus);
					} else {
						dailyStatus.setFailed(failed + dailyStatus.getFailed());
						dailyStatus.setTotal(passed + failed + dailyStatus.getTotal());
					}

				}
				System.out.println("##########" + dailyStatusList);
			}
		}

	}

	public String readFile(String filePath) {

		String out = "";
		try {
			FileReader fileReader = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				out += line + "\n";
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}

	public void addDeliverData() {
		String deliveryFilePath = (String) DataHandler.getAttribute("deliveryStatusPath");
		SimpleDateFormat csvformatter = new SimpleDateFormat("dd/MM/yy");
		String regexPackages = "(E[\\S]+),(R[\\S]+),([0-9]+.[0-9]+.[0-9]+),([0-9]+/[0-9]+/[0-9]+):";

		String out = readFile(deliveryFilePath);
		System.out.println(out);

		Pattern pattern = Pattern.compile(regexPackages);
		Matcher m = pattern.matcher(out);
		while (m.find()) {
			String packageName = m.group(1);
			String shipment = m.group(3);
			String dateString = m.group(4);
			if (shipment.equals(sprint)) {
				System.out.println(packageName + "---" + shipment + "---" + dateString);
				String team = getTeam(packageName);
				Date date = null;
				try {
					date = csvformatter.parse(dateString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				System.out.println(packageName + "---" + shipment + "---" + date);

				Map<Date, DailyStatus> dayWiseStatus = dailyStatusList.get(team);
				if (dayWiseStatus == null) {
					dayWiseStatus = new HashedMap();
					dailyStatusList.put(team, dayWiseStatus);
				}
				DailyStatus dailyStatus = dayWiseStatus.get(date);
				if (dailyStatus == null) {
					dailyStatus = new DailyStatus(0, 0, 1);
					dayWiseStatus.put(date, dailyStatus);
				} else {
					dailyStatus.setNoOfDelivery(dailyStatus.getNoOfDelivery() + 1);
				}
			}
		}
		System.out.println(dailyStatusList);
	}

	public String getTestCaseResult(String startDate, String drop, String product/*, String endDate*/) {
		String out = null;
		String url = (String)DataHandler.getAttribute("cifwkUrl");
		try {
			out = cliCommandHelperHub.simpleExec(
					"/usr/sfw/bin/wget -q -O - --no-check-certificate \""+url+"/api/getTestCaseResult/?startDate="
							+ startDate + "&drop=" + drop + "&product=" + product /*+ "&endDate=" + endDate */+ "\"");
			System.out.println(out);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	

	public String getTeam(String packageName) {

		String team = teamCache.get(packageName);
		
		List<String> tas = Arrays.asList(TAList);
		
		if (team == null) {
			try {
				String out = cliCommandHelperHub.simpleExec(
						"/usr/sfw/bin/wget -q -O - --no-check-certificate \"https://cifwk-oss.lmera.ericsson.se/api/aam/label/artifact/lookup/"
								+ packageName + "/\"");
				System.out.print("out value: "+out);
				JsonParser parser = new JsonParser();
				List<ConfigObject> configObjects = parser.parse(out);
				ConfigObject configObject = configObjects.get(0);
				//ConfigObject configObject = null;
				//team = (String) configObject.get("team");
				team = (String) configObject.get("team");
				System.out.println("Team name :"+team);
				//added extra for COMINF packages.
				if(packageName.equals("ERICocs_CXC1731203") || packageName.equals("ERICsdse_CXC1731204") || packageName.equals("ERICocs_CXC1731203") ) {
					team = "COMINF";
				}
				System.out.println(team + "-------" + team.trim());
				if (team == null || team.trim().equals("")) {
					team = "OTHERS";
					otherPackageSet.add(packageName);
				}else if (!tas.contains(team)){
					team = "OTHERS";
					otherPackageSet.add(packageName);
				}
				teamCache.put(packageName, team);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return team;

	}

	public void prepareAndSendKGBReport() {

		addJsonContent();
		addDeliverData();
		addCatalogStatus();

		Date dateN = null;
		String nDayth = null;
		try {
			dateN = formatter.parse(nthDate);
			nDayth = catalogFormatter.format(dateN);
			System.out.println("----------------------" + nDayth);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			Date startDate = formatter.parse(this.startDate);
			Date endDate = formatter.parse(this.endDate);
			int i = 1;
			for (Date currentDate = startDate; currentDate.before(endDate)
					|| currentDate.equals(endDate) && currentDate.before(new Date()); currentDate
							.setDate(currentDate.getDate() + 1)) {
				if (currentDate.equals(nthDate_Date)) {
					dayth = i;
				}
				i++;

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		System.out.println("#################");
		System.out.println(piechart);
		System.out.println("#################");
		
		String pieChartCode = String.format(piechart,
				/* getPieChartData(), *//* getBarChartData(), nDayth, */
				getLineChartDataForTestCaseCount(), getBarChartDataForPackageDelivery(), getPieChartData(),getKGBStatusinHTML(), sprint,
				dayth);

		writeToFile(pieChartCode, "Chart.html");
		//writeToFile(getKGBStatusinHTML(), "kgbReport.html");
		writeToFile(getFinalReportinHTML(), "finalReport.html");

		System.out.println(getLineChartDataForTestCaseCount());

	}

	public static void writeToFile(String htmlReport, String fileName) {
		// TODO Auto-generated method stub
		try {

			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			fw.write(htmlReport);
			fw.close();

			System.out.println("Created a file : " + fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
