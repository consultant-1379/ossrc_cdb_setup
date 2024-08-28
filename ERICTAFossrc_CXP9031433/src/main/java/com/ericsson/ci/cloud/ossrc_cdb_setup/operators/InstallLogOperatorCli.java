package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Singleton;
import org.apache.log4j.Logger;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.InstallStep;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.PackageInfo;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.CLITool;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchCLIToolException;

@Singleton
@Operator(context = { Context.CLI })
public class InstallLogOperatorCli implements InstallLogOperator {
  private static final Host hostGateway = DataHandler.getHostByType(HostType.GATEWAY);
  private static final CLI cliGateway = new CLI(hostGateway, hostGateway.getUsers(UserType.ADMIN).get(0));
  private static final CLICommandHelper cliCommandHelperGateway = new CLICommandHelper(hostGateway, hostGateway.getUsers(UserType.ADMIN).get(0));
  private static final Logger logger = Logger.getLogger(InstallOperatorCli.class);
  private static final String EXIT_CODE = "EXIT_CODE:";
  String logPath=(String)DataHandler.getAttribute("logPath");
  String regexLogs="[\\s\\S]([a-zA-Z]+[\\s]+[0-9]{1,2} [:0-9]{5})[\\s\\S]([\\S]+.log)";
  //String regexStartTime="([\\S\\s]+Waiting for required process to complete \\((\\S+)_complete\\):\\sOK)+[\\s\\S]{1}\\[([a-zA-Z]{3}_[0-9]{1,2} [0-9:]+)\\]";
  String regexStartTime="\\[([a-zA-Z]{3}_[0-9]{1,2} [0-9:]+)\\] [A-Za-z]+:[\\s\\S]([\\w\\s]+)";

  private boolean runCommandOnGateway(String command, Long timeOut) {
    logger.info("Running " + command + " cmd");
    Shell runShell = cliGateway.executeCommand(command, "echo \""+ EXIT_CODE + "\"$?");
    String output = null;
    while (!runShell.isClosed() && timeOut > 0) {
      output = runShell.read();
      if (output.startsWith("EXIT_CODE:")) {
        break;
      }
      if (output.length() > 1 /* && !output.contains("% [") */) {
        logger.debug(output);
      }
      timeOut -= CLITool.DEFAULT_TIMEOUT_SEC;
    }
    String lastLine = null;
    if (output.startsWith("EXIT_CODE:")) {
      lastLine = output;
    } else {
      lastLine = runShell.read();
    }
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
    cliGateway.close();
    return result == 0;
  }

  //logPath=/export/scripts/CLOUD/logs/web/CI_EXEC_OSSRC/Jun_3_2016_16_57_12_atvts3016_rollout_config/logs/completed
  @Override
    public List<InstallStep> getInstallStepsInfo() {
    // TODO Auto-generated method stub
    List<InstallStep> installSteps= new ArrayList<InstallStep>();
    String out = cliCommandHelperGateway.execute("ls -lrt "+logPath);
    Pattern pattern = Pattern.compile(regexLogs);
    Matcher m = pattern.matcher(out);
    while (m.find())
    {
      //System.out.println("$" + m.group(1) + "$" );
      //System.out.println("$" + m.group(2) + "$" );
      String endTime=m.group(1);
      String name=m.group(2);
      InstallStep installStep = new InstallStep(name);
      installStep.setEndTime(endTime);
      installStep.setStartTime(getStartTime(logPath+"/"+name));
      installSteps.add(installStep);
    }
    cliCommandHelperGateway.disconnect();
    return installSteps;
  }

  public String getStartTime(String logName) {
    // TODO Auto-generated method stub
    String startTime=null;
    String out = cliCommandHelperGateway.execute("more "+logName+" | head -40");
    Pattern pattern = Pattern.compile(regexStartTime);
    Matcher m = pattern.matcher(out);
    while (m.find())
    {
      //System.out.println("$" + m.group(1) + "$" );
      //System.out.println("$" + m.group(2) + "$" );
      startTime=m.group(1);
      String log=m.group(2);
      System.out.println("------"+startTime+"-----------"+log);
      if(!log.contains("Waiting for required process to complete")){
        System.out.println("Real startTime------"+startTime+"-----------"+log);
        break;
      }
    }
    cliCommandHelperGateway.disconnect();
    return startTime;
  }
}
