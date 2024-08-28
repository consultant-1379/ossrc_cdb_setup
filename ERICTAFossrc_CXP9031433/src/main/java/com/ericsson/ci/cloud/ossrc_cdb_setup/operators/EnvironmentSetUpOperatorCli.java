package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;


import org.apache.log4j.Logger;

import com.ericsson.ci.cloud.ossrc_cdb_setup.getters.EnvironmentSetUpGetter;
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
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchCLIToolException;

@Operator(context = { Context.CLI })
public class EnvironmentSetUpOperatorCli implements EnvironmentSetUpOperator {

  private static final CLI cli = new CLI(DataHandler.getHostByName("gateway"));
  private static final CLI cliNETSIM = new CLI(DataHandler.getHostByName("netsim"));
  //private static final Host hostNETSim = DataHandler.getHostByType(HostType.NETSIM);
  //private static final CLI cliNETSIM = new CLI(hostNETSim, hostNETSim.getUsers(UserType.ADMIN).get(0));
  private static final Logger logger = Logger.getLogger(EnvironmentSetUpOperatorCli.class);
  private static final String EXIT_CODE = "EXIT_CODE:";
  private static final Host ossmasterhost = DataHandler.getHostByType(HostType.RC);
  private static final CLICommandHelper cliCommandHelper = new CLICommandHelper(ossmasterhost, ossmasterhost.getUsers(UserType.ADMIN).get(0));
  //private static final Host hostNetsim = DataHandler.getHostByType(HostType.NETSIM);
  //private static final CLI cliNETSIM = new CLI(hostNetsim, hostNetsim.getUsers().get(0));
  private static final RemoteObjectHandler remoteObjectHandler = new RemoteObjectHandler(ossmasterhost, ossmasterhost.getUsers(UserType.ADMIN).get(0));

  private boolean runCommand(String command, Long timeOut) {
    logger.info("Running "+ command +" cmd");
    Shell runShell = cli.executeCommand(command, "echo \"" + EXIT_CODE + "\"$?");
    String output = null;
    while (!runShell.isClosed() && timeOut > 0) {
      output = runShell.read();
      if (output.length() > 1){
        logger.debug(output);
      }
      timeOut -= CLITool.DEFAULT_TIMEOUT_SEC;
    }
    String lastLine = runShell.read();
    short result = -1;
    logger.info("lastline:"+lastLine.length());
    logger.info("EXIT_CODE:"+EXIT_CODE.length());
    logger.info("output:"+output);
    try {
      if (lastLine.length() < EXIT_CODE.length()){
        logger.info("inside if");
        lastLine = output;
      }
      logger.info(lastLine.split(EXIT_CODE)[1].trim());
      String exitCode = lastLine.split(EXIT_CODE)[1].trim();
      logger.info("result:"+Short.valueOf(exitCode));
      result = Short.valueOf(exitCode);
    } catch (JSchCLIToolException | IndexOutOfBoundsException e) {
      logger.error("Cannot get command result", e);
    }
    logger.info("Shell exit code is " + result);
    runShell.disconnect();
    cli.close();
    return result == 0;
  }

  private boolean runCommandOnNETSim(String command, Long timeOut) {
    logger.info("Running " + command + " cmd");
    Shell runShell = cliNETSIM.executeCommand(command, "echo \"" + EXIT_CODE + "\"$?");
    String output = null;
    while (!runShell.isClosed() && timeOut > 0) {
      output = runShell.read();
      if (output.startsWith("EXIT_CODE:")) {
        break;
      }
      if (output.length() > 1) {
        logger.info(output);
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
    cliNETSIM.close();
    return result == 0;
  }

  @Override
    public boolean executeInitialInstall() {
    try{
      runCommand("sudo yum --nogpgcheck localinstall -y /export/scripts/CLOUD/CIinfra/paramiko/python-crypto-2.0.1-22.el6.x86_64.rpm",200L);
      runCommand("sudo yum --nogpgcheck localinstall -y /export/scripts/CLOUD/CIinfra/paramiko/python-paramiko-1.7.5-2.1.el6.noarch.rpm",200L);
    }catch(Exception e){
      e.printStackTrace();
    }
    boolean isInstalled=runCommand(EnvironmentSetUpGetter.getInitialInstallCommand(), 43200L);
    //updateCrontabForCPUMonitoring();
    return isInstalled;
  }
  //DCC/DPPI Setup for Solaris 10

  @Override
    public boolean executeDdcDdpiSetup() {
    // TODO Auto-generated method stub
    return runCommand(EnvironmentSetUpGetter.getDdcDdpiSetupCommand(), 14400L);
  }
  //DCC/DPPI Setup for Solaris 11

  @Override
    public boolean executeDdcDdpiSol11Setup() {
    // TODO Auto-generated method stub
    return runCommand(EnvironmentSetUpGetter.getDdcDdpiSol11SetupCommand(), 14400L);
  }

  //New method added for Cloud Upgrade call

  @Override
    public boolean executeInitialUpgrade() {
    return runCommand(EnvironmentSetUpGetter.getInitialUpgradeCommand(), 43200L);
  }

  @Override
    public boolean executeArneImport() {
    return runCommand(EnvironmentSetUpGetter.getArneImportCommand(), 14400L);
  }

  @Override
    public boolean executeSimdep() {
    return runCommand(EnvironmentSetUpGetter.getSimdepCommand(), 14400L);
  }

  @Override
    public boolean prepareUsers() {
    return runCommand(EnvironmentSetUpGetter.getAddUserCommand(), 3600L);
  }

  @Override
    public boolean manageMC() {
    return runCommand(EnvironmentSetUpGetter.getManageMCCommand(), 32400L);
  }

  @Override
    public boolean disablePasswordExpiry() {
    return runCommand(EnvironmentSetUpGetter.getDisablePasswordExpiryCommand(), 3600L);
  }

  @Override
    public boolean disablePasswordLockout() {
    return runCommand(EnvironmentSetUpGetter.getDisablePasswordLockoutCommand(), 3600L);
  }

  @Override
    public boolean disablePasswordMustChange() {
    return runCommand(EnvironmentSetUpGetter.getDisablePasswordMustChangeCommand(), 3600L);
  }

  @Override
    public boolean removePasswordChangeHistory() {
    return runCommand(EnvironmentSetUpGetter.getRemovePasswordChangeHistoryCommand(), 3600L);
  }

  @Override
    public boolean reduceMinPasswordLength() {
    return runCommand(EnvironmentSetUpGetter.getReduceMinPasswordLengthCommand(), 3600L);
  }

  @Override
    public boolean executeNetsimRollOutConfig() {
    return runCommand(EnvironmentSetUpGetter.getNetsimRollOutConfigCommand(), 34400L);
  }
  public boolean sleepTimeafterUpgarde(){
	  logger.info("Sleep for 40 minutes");
      try {
          Thread.sleep(2400000);
      } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          return false;
      }
      return true;
      }

  @Override
    public boolean executeNetsimRollOutPart1() {
    // TODO Auto-generated method stub
    return runCommand(EnvironmentSetUpGetter.getNetsimRollOutPart1Command(), 34400L);
  }

  //Implementation for JIRA - CIS-43323
  @Override
  public boolean executeRealNodeCDBAutoDeploymentECN() {
  // TODO Auto-generated method stub
  return runCommand(EnvironmentSetUpGetter.getRealNodeCDBAutoDeploymentECNCommand(), 34400L);
}
  @Override
  public boolean executeRealNodeCDBAutoDeploymentEDN() {
  // TODO Auto-generated method stub
  return runCommand(EnvironmentSetUpGetter.getRealNodeCDBAutoDeploymentEDNCommand(), 34400L);
}
  @Override
  public boolean executeYoulabCDBAutoDeployment() {
  // TODO Auto-generated method stub
  return runCommand(EnvironmentSetUpGetter.getYoulabCDBAutoDeploymentCommand(), 34400L);
}

////Implementation for JIRA - CIS-49664
  @Override
  public boolean executeNetsimVMMemory() {
  return runCommandOnNETSim(EnvironmentSetUpGetter.getNetsimVMMemoryCommand(), 14400L);
}
////Implementation for JIRA - CIS-63304
@Override
public boolean executeNetsimRestart() {
return runCommand(EnvironmentSetUpGetter.getNetsimRestart(), 14400L);
}
  public void updateCrontabForCPUMonitoring() {
    // TODO Auto-generated method stub
    try {
      logger.info("Updating Crontab entry for CPU monitoring");
      remoteObjectHandler.copyLocalFileToRemote("updateCron.sh", "/tmp/updateCron.sh");
      logger.info(cliCommandHelper.execute("chmod 777 /tmp/updateCron.sh"));
      cliCommandHelper.execute("/tmp/updateCron.sh");
      logger.info(cliCommandHelper.execute("crontab -l"));
    }catch(Exception e) {
      logger.error("Exception occurred while updating Crontab file", e);
    }
    finally{
      cliCommandHelper.disconnect();
    }
 }
   
}
