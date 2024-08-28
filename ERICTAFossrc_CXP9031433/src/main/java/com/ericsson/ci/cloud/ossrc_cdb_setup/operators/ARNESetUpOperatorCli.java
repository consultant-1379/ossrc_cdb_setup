package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.ericsson.ci.cloud.ossrc_cdb_setup.getters.EnvironmentSetUpGetter;
import com.ericsson.cifwk.taf.annotations.Attachment;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchCLIToolException;
import com.ericsson.oss.taf.hostconfigurator.HostGroup;

@Operator(context = { Context.CLI })
public class ARNESetUpOperatorCli implements ARNESetUpOperator {
  private static final Host host = DataHandler.getHostByType(HostType.RC);
  private static final CLICommandHelper cliCommandHelper = new CLICommandHelper(host, host.getUsers(UserType.ADMIN).get(0));
  private static final RemoteObjectHandler remoteObjectHandler = new RemoteObjectHandler(host, host.getUsers(UserType.ADMIN).get(0));
  private static final Host hostNetsim = DataHandler.getHostByType(HostType.NETSIM);
  private static final CLICommandHelper cliCommandHelperNetsim = new CLICommandHelper(hostNetsim, hostNetsim.getUsers().get(0));
  private static final Logger logger = Logger.getLogger(ARNESetUpOperatorCli.class);
  private static final String EXIT_CODE = "EXIT_CODE:";
  private int synch_count = 0;
  private int unsynch_count = 0;
  private int skippedNodes_count = 0;
  /* private boolean runCommand(String command, Long timeOut) {
        logger.info("Running " + command + " cmd");
        Shell runShell = cli.executeCommand(command, "echo \"" + EXIT_CODE + "\"$?");
        String output = null;
        while (!runShell.isClosed() && timeOut > 0) {
            output = runShell.read();
            if (output.length() > 1) {
                logger.debug(output);
            }
            timeOut -= CLITool.DEFAULT_TIMEOUT_SEC;
        }
        logger.info("Is Shell closed : " + runShell.isClosed());
        String lastLine = runShell.read();
        short result = -1;
        try {
            if (lastLine.length() < EXIT_CODE.length())
                lastLine = output;
            String exitCode = lastLine.split(EXIT_CODE)[1].trim();
            result = Short.valueOf(exitCode);
        } catch (JSchCLIToolException | IndexOutOfBoundsException e) {
            logger.error("Cannot get command result", e);
        }
        logger.info("Shell exit code is " + result);
        runShell.disconnect();
        return result == 0;
    }

    private boolean runCLICommand(String command, Long timeOut) {

        Shell runShell = cliCommandHelper.getShell();
        String output = null;
        logger.info("Running " + command + " cmd");
        runShell.writeln(command);
        do {
            output = runShell.read();
            if (output.length() > 1) {
                logger.debug(output);
            }
            timeOut -= CLITool.DEFAULT_TIMEOUT_SEC;
        } while (!runShell.isClosed() && timeOut > 0);
        logger.info("Is Shell closed : " + runShell.isClosed() + " Timeout left : " + timeOut);
        runShell.writeln("echo \"" + EXIT_CODE + "\"$?");
        String lastLine = runShell.read();
        short result = -1;
        try {
            if (lastLine.length() < EXIT_CODE.length())
                lastLine = output;
            String exitCode = lastLine.split(EXIT_CODE)[1].trim();
            result = Short.valueOf(exitCode);
        } catch (JSchCLIToolException | IndexOutOfBoundsException e) {
            logger.error("Cannot get command result", e);
        }
        logger.info("Shell exit code is " + result);
        runShell.disconnect();
        return result == 0;
    }*/

    @Override
    public boolean mafOffline() {
        // TODO Auto-generated method stub
        String mafOfflineCommand = (String) DataHandler.getAttribute("mafOfflineCommand");
        String smtoolProgress = (String) DataHandler.getAttribute("smtoolProgress");
        logger.info("mafOfflineCommand " + mafOfflineCommand);
        logger.info("smtoolProgress " + smtoolProgress);
        cliCommandHelper.execute(mafOfflineCommand);
        logger.info(cliCommandHelper.getStdOut());
        if (cliCommandHelper.getCommandExitValue() == 0) {
            cliCommandHelper.execute(smtoolProgress);
            logger.info(cliCommandHelper.getStdOut());
        }
        return cliCommandHelper.getCommandExitValue() == 0;
    }

    @Override
    public boolean mafOnline() {
        // TODO Auto-generated method stub
        String mafTraceStop = (String) DataHandler.getAttribute("mafTraceStop");
        String mafTraceFileLength = (String) DataHandler.getAttribute("mafTraceFileLength");
        String mafStartAdjust = (String) DataHandler.getAttribute("mafStartAdjust");
        String onrmCsStartAdjust = (String) DataHandler.getAttribute("onrmCsStartAdjust");
        String mafOnlineCommand = (String) DataHandler.getAttribute("mafOnlineCommand");
        String smtoolProgress = (String) DataHandler.getAttribute("smtoolProgress");
        logger.info("mafTraceStop " + mafTraceStop);
        logger.info("mafTraceFileLength " + mafTraceFileLength);
        logger.info("mafStartAdjust " + mafStartAdjust);
        logger.info("onrmCsStartAdjust " + onrmCsStartAdjust);
        logger.info("mafOnlineCommand " + mafOnlineCommand);
        logger.info("smtoolProgress " + smtoolProgress);
        logger.info(cliCommandHelper.execute(mafTraceStop));
        logger.info(cliCommandHelper.execute(mafTraceFileLength));
        logger.info(cliCommandHelper.execute(mafStartAdjust));
        logger.info(cliCommandHelper.execute(onrmCsStartAdjust));
        cliCommandHelper.execute(mafOnlineCommand);
        logger.info(cliCommandHelper.getStdOut());
        if (cliCommandHelper.getCommandExitValue() == 0) {
            cliCommandHelper.execute(smtoolProgress);
            logger.info(cliCommandHelper.getStdOut());
        }
        return cliCommandHelper.getCommandExitValue() == 0;
    }

    @Override
    public boolean mafSynch() {
        // TODO Auto-generated method stub
        String mafSynchCommand = (String) DataHandler.getAttribute("mafSynchCommand");
        String segMasterService = (String) DataHandler.getAttribute("segMasterService");
        String mafColdRestart = (String) DataHandler.getAttribute("mafColdRestart");
        String removeStartAdjust = (String) DataHandler.getAttribute("removeStartAdjust");
        String mafTraceStop = (String) DataHandler.getAttribute("mafTraceStop");
        logger.info("mafSynchCommand " + mafSynchCommand);
        logger.info("segMasterService " + segMasterService);
        logger.info("mafColdRestart " + mafColdRestart);
        logger.info("removeStartAdjust " + removeStartAdjust);
        logger.info("mafTraceStop " + mafTraceStop);
        logger.info("Sleep for 2.5 minutes");
        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cliCommandHelper.DEFAULT_COMMAND_TIMEOUT_VALUE = 14400;
        logger.info(cliCommandHelper.execute(mafSynchCommand));
        cliCommandHelper.DEFAULT_COMMAND_TIMEOUT_VALUE = 7200;
        logger.info(cliCommandHelper.execute(segMasterService));
        logger.info(cliCommandHelper.getStdOut());
        int nodes = Character.getNumericValue(cliCommandHelper.getStdOut().replaceAll(" ", "").charAt(0));
        logger.info("StdOut after remove spaces" +nodes);
        if (nodes == 0)
        {
          logger.info("Retrying once again");
          logger.info(cliCommandHelper.execute(mafColdRestart));
          try {
                Thread.sleep(150000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
          cliCommandHelper.DEFAULT_COMMAND_TIMEOUT_VALUE = 14400;
          logger.info(cliCommandHelper.execute(mafSynchCommand));
          cliCommandHelper.DEFAULT_COMMAND_TIMEOUT_VALUE = 7200;
        }else {
          logger.info(cliCommandHelper.execute(removeStartAdjust));
        }
        logger.info(cliCommandHelper.execute(mafTraceStop));
        return cliCommandHelper.getCommandExitValue() == 0;
    }

    public boolean getNodeSynchStatus(int min) {
        // TODO Auto-generated method stub
      String sleepTime_NodeSynchCheck=(String)DataHandler.getAttribute("sleepTime_NodeSynchCheck");
      int sleepTime=min;
      if (!sleepTime_NodeSynchCheck.equals("-1")) {
        try {
          sleepTime = Integer.parseInt(sleepTime_NodeSynchCheck);
        } catch (Exception e) {
          // TODO: handle exception
        }
      }
      logger.info("Sleep for "+sleepTime+" minutes");
        try {
            Thread.sleep(60000*sleepTime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        remoteObjectHandler.copyLocalFileToRemote("listNodesSynchStatus.sh", "/tmp/listNodesSynchStatus.sh");
        logger.info(cliCommandHelper.execute("chmod 777 /tmp/listNodesSynchStatus.sh"));
        cliCommandHelper.DEFAULT_COMMAND_TIMEOUT_VALUE = 14400;
        cliCommandHelper.execute("/tmp/listNodesSynchStatus.sh");
        nodeSynchStatusReport(cliCommandHelper.getStdOut());
        cliCommandHelper.disconnect();
        return (synch_count!=0 && unsynch_count==0);
    }

    @Attachment(type = "text/html", value = "Report showing synch status of the nodes present in the server")
    public String nodeSynchStatusReport(String result) {
        logger.info(result);
        Object object=DataHandler.getAttribute("skippedNodes");
        List<String> skippednodeList=null;
        if(object instanceof List){
          skippednodeList=(List)object;
        }else {
          skippednodeList= new ArrayList<String>();
          skippednodeList.add((String)object);
        }
        StringBuffer buffer = new StringBuffer("<html>\n");
        buffer.append("<style media=screen type=text/css>" + "#nodes {" + "font-family: Arial, Verdana, sans-serif;" + "  width: 100%;"
                + "border-collapse: collapse;" + "}" + "#nodes td, #nodes th {" + "font-size: 1em;" + "border: 1px solid #04000C;"
                + "padding: 3px 7px 2px 7px;" + "}" + "#nodes th {" + "font-size: 1.1em;" + "text-align: left;" + "padding-top: 5px;" + "padding-bottom: 4px;"
                + "background-color: #1E025C;" + "color: #ffffff;" + "}" + "#nodes tr.alt td {" + "color: #000000;" + "background-color: #EAF2D3;" + "}"
                + "</style>");
        buffer.append("<h1 align=center> Node Synch Status Report</h1>\n");
        buffer.append("<body><table align=center id=nodes>\n");
        StringBuffer synched = new StringBuffer("");
        StringBuffer unsynched = new StringBuffer("");
        StringBuffer skipped = new StringBuffer("");
        synch_count = 0;
        unsynch_count = 0;
        String[] rows = result.split("\n");
        try {
            for (int i = 1; i < rows.length; i++) {
              String[] data = rows[i].split("[ ]+");
              if (data.length > 3) {
                boolean isskippedNode = false;
                for (String nodeType : skippednodeList) {
                  if (data[1].toLowerCase().contains(nodeType.toLowerCase())) {
                    skipped.append("<tr bgcolor=yellow><td>" + data[1]
                                   + "</td><td>" + data[2]
                                   + "</td><td><font color=red>" + data[3]
                                   + "</font></td></tr>\n");
                    skippedNodes_count++;
                    isskippedNode = true;
                    break;
                  }
                }
                if (!isskippedNode) {
                  if (data[3].trim().toLowerCase().equals("synched")) {
                    synched.append("<tr><td>" + data[1] + "</td><td>"
                                   + data[2] + "</td><td><font color=green>"
                                   + data[3] + "</font></td></tr>\n");
                    synch_count++;
                  } else if (data[3].trim().toLowerCase().equals("unsynched")) {
                    unsynched.append("<tr><td>" + data[1] + "</td><td>"
                                     + data[2] + "</td><td><font color=red>"
                                     + data[3] + "</font></td></tr>\n");
                    unsynch_count++;
                  }
                }
              }
            }
        }catch(Exception e) {
            logger.info("Exception occurred while parsing", e);
        }
        buffer.append("<tr><th align=center colspan=3>Statistics</th></tr>");
        buffer.append("<tr><td align=left colspan=2>Total no. of synched nodes</td><td align=center>" + synch_count + "</td></tr>");
        buffer.append("<tr><td align=left colspan=2>Total no. of unsynched nodes</td><td align=center>" + unsynch_count + "</td></tr>");
        buffer.append("<tr><td align=left colspan=2>Total no. of nodes skipped for synch check</td><td align=center>" + skippedNodes_count + "</td></tr>");
        buffer.append("<tr><th>Node Name</th><th>Node Version</th><th>Synch Status</th></tr>\n");
        buffer.append(synched);
        buffer.append(unsynched);
        buffer.append(skipped);
        buffer.append("</table></body></html>\n");
        return buffer.toString();
    }

  @Override
      public boolean imsOffline() {
        // TODO Auto-generated method stub
        String imsOfflineCommand = (String) DataHandler.getAttribute("imsOfflineCommand");
        String smtoolProgress = (String) DataHandler.getAttribute("smtoolProgress");
        logger.info("imsOfflineCommand " + imsOfflineCommand);
        logger.info("smtoolProgress " + smtoolProgress);
        cliCommandHelper.execute(imsOfflineCommand);
        logger.info(cliCommandHelper.getStdOut());
        if (cliCommandHelper.getCommandExitValue() == 0) {
            cliCommandHelper.execute(smtoolProgress);
            logger.info(cliCommandHelper.getStdOut());
        }
        return cliCommandHelper.getCommandExitValue() == 0;
    }

  @Override
      public boolean imsOnline() {
        // TODO Auto-generated method stub
        String imsOnlineCommand = (String) DataHandler.getAttribute("imsOnlineCommand");
        String smtoolProgress = (String) DataHandler.getAttribute("smtoolProgress");
        logger.info("imsOnlineCommand " + imsOnlineCommand);
        logger.info("smtoolProgress " + smtoolProgress);
        cliCommandHelper.execute(imsOnlineCommand);
        logger.info(cliCommandHelper.getStdOut());
        if (cliCommandHelper.getCommandExitValue() == 0) {
            cliCommandHelper.execute(smtoolProgress);
            logger.info(cliCommandHelper.getStdOut());
        }
        return cliCommandHelper.getCommandExitValue() == 0;
    }

  @Override
    public boolean deleteSimulationZips() {
    // TODO Auto-generated method stub
    cliCommandHelperNetsim.DEFAULT_COMMAND_TIMEOUT_VALUE = 7200;
    String simZipDeleteCommand=(String)DataHandler.getAttribute("simZipDeleteCommand");
    try {
      logger.info("Going to delete simulation zip from simdir");
      logger.info(cliCommandHelperNetsim.execute(simZipDeleteCommand));
      logger.info("simulation zip files are deleted at netsim vm");
    } catch (Exception e) {
      logger.error("simulation zip files are deleted at netsim vm");
      logger.error(e);
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
 }

