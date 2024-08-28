package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.ericsson.ci.cloud.ossrc_cdb_setup.getters.EnvironmentSetUpGetter;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.DMCronValue;
import com.ericsson.cifwk.taf.annotations.Attachment;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;

@Operator(context = { Context.CLI })
public class CronOperatorCli implements CronOperator {
  private static final Host host = DataHandler.getHostByType(HostType.CIFWK);
  private static final CLICommandHelper cliCommandHelper = new CLICommandHelper(host, host.getUsers(UserType.CUSTOM).get(0));
  List<DMCronValue> cronlist = new ArrayList<DMCronValue>();
  private static final String EXIT_CODE = "EXIT_CODE:";

  public String getCronInfo() {
    String cronInfo=null;
    String cronListCommand=(String)DataHandler.getAttribute("cronListCommand");
    cronInfo=cliCommandHelper.simpleExec(cronListCommand);
    System.out.println(cronInfo);
    String filterShipment=(String)DataHandler.getAttribute("filterShipment");
    parseAllCronValues(cronInfo,filterShipment);
    String htmlReport=getCronReport(cronlist);
    writeToFile(htmlReport);
    //System.out.println(htmlReport);
    cliCommandHelper.disconnect();
    return cronInfo;
  }

  private void writeToFile(String htmlReport) {
    // TODO Auto-generated method stub
    try {
      //htmlReport="XXXX";
      File file = new File("cronReport.html");
      // if file doesnt exists, then create it
      if (!file.exists()) {
        file.createNewFile();
      }
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      fw.write(htmlReport);
      fw.close();
      System.out.println("HTML report generation done");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void parseAllCronValues(String cronInfo, String filter) {
    // TODO Auto-generated method stub
    System.out.println(cronInfo);
    String[] lines= cronInfo.split("\n");
    String cronComment=null;
    for (int i = 0; i < lines.length; i++) {
      lines[i]=lines[i].replaceAll("\r", "");
      lines[i]=lines[i].trim();
      if(lines[i].length()==0) {
        continue;
      }
      System.out.println("-------"+lines[i]);
      if(lines[i].startsWith("#") && lines[i].endsWith("#")) {
        cronComment=lines[i].trim();
      }else {
        if(!lines[i].contains("master_wrapper")) {
          continue;
        }
        DMCronValue dmCronValue = new DMCronValue(lines[i],cronComment);
        if(filter.contains("all")) {
          cronlist.add(dmCronValue);
        }else if(dmCronValue.getShipment().contains(filter)) {
          cronlist.add(dmCronValue);
        }
      }
    }
    for (Iterator iterator = cronlist.iterator(); iterator.hasNext();) {
      DMCronValue dmCronValue = (DMCronValue) iterator.next();
      System.out.println(dmCronValue);
    }
  }

  public String getfilteredCronInfo() {
    String filteredCronInfo=null;
    return filteredCronInfo;
  }

  @Attachment(type = "text/html", value = "Report showing synch status of the nodes present in the server")
    public String getCronReport(List<DMCronValue> cronlist) {
    StringBuffer buffer = new StringBuffer("<html>\n");
    buffer.append("<style media=screen type=text/css>\n"
                  +"#nodes {font-family: Arial, Verdana, sans-serif;  width: 100%;border-collapse: collapse;}\n"
                  +"#nodes td, #nodes th {font-size: .8em;border: 1px solid #04000C;padding: 3px 7px 2px 7px;}\n"
                  +"#nodes th {font-size: 1.1em;text-align: left;padding-top: 5px;padding-bottom: 4px;color: #ffffff;}\n"
                  +"#nodes tr.alt td {color: #FFFFFF;background-color: #EAF2D3;}\n"
                  +"#nodes tr{background: #FFFFFF;}\n"
                  +"#nodes tr:nth-child(odd){background: #FFFFFF;}\n"
                  +"#nodes tr:nth-child(even){background: #EAF2D3;}\n"
                  +"</style>\n");
    buffer.append("<h1 align=center>\n"
                  +"<font face=verdana color=#1E025C>Crontab Report for DM team</font></h1>\n"
                  +"<body>\n");
    buffer.append("<table align=center id=nodes>");
    for (DMCronValue dmCronValue : cronlist) {
      buffer.append(dmCronValue.getHTMLRowFormat());
    }
    buffer.append("</table></body></html>\n");
    return buffer.toString();
  }
}
