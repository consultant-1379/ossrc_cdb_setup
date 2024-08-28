package com.ericsson.ci.cloud.ossrc_cdb_setup.getters;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.Shell;

public class EnvironmentSetUpGetter {

    private static String configFiles;
    private static String hostName;
    private static String scriptPath;
    private static String jobName;

  private static String getConfigFiles() {
        if (configFiles == null)
            configFiles = DataHandler.getAttribute("CONFIG_FILES").toString();
        return configFiles;
  }

  private static String getJobName() {
        if (jobName == null)
          jobName = DataHandler.getAttribute("JOB_NAME").toString();
        return jobName;
  }

  private static String getHostName() {
        if (hostName == null){
          CLI cli = new CLI(DataHandler.getHostByName("gateway"));
          Shell sh = cli.executeCommand("hostname");
          hostName = sh.read();
          sh.disconnect();
          //            hostName = DataHandler.getHostByName("gateway").getIp();
        }return hostName;
  }

  private static String getScriptPath() {
    if (scriptPath == null) {
      scriptPath = DataHandler.getAttribute("scriptPath").toString();
    }
    return scriptPath;
  }

  public static String getInitialInstallCommand() {
    return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + " -g `hostname` -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f rollout_config";
  }

  //Command for ddp for solaris 10
  public static String getDdcDdpiSetupCommand() {
    return "python "+ getScriptPath() +"/CIinfra/setupDDC_DDP.py -j " + getJobName();
  }

  //Command for ddp for solaris 11
    public static String getDdcDdpiSol11SetupCommand() {
      return "" + getScriptPath() + "/bin/setupDDC.sh " + getJobName();
    }

  //Command for Cloud upgrade
  public static String getInitialUpgradeCommand() {
    return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + " -g `hostname` -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f upgrade_adm";
    }

  //Command for initialising MC's
    public static String getManageMCCommand() {
        return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + " -g `hostname` -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f manage_mcs_initial";
    }

  public static String getArneImportCommand() {
        return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f netsim_post_steps";
    }

  public static String getAddUserCommand() {
        return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + " -g `hostname` -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f create_users_config";
    }

  public static String getSimdepCommand() {
        return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f simdep_call";
    }

  //Command to disable password expiry
  public static String getDisablePasswordExpiryCommand() {
        return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f disable_password_expiry";
    }

  public static String getDisablePasswordLockoutCommand() {
        return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f disable_password_lockout";
    }

  public static String getDisablePasswordMustChangeCommand() {
        return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f disable_password_must_change";
    }

  public static String getRemovePasswordChangeHistoryCommand() {
        return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f remove_password_change_history";
    }

  public static String getReduceMinPasswordLengthCommand() {
        return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f reduce_min_password_length";
    }

  public static String getNetsimRollOutConfigCommand() {
    return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f  netsim_rollout_config";
  }

  public static String getNetsimRollOutPart1Command() {
    return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f  netsim_rollout_part1";
  }

  //Implementation for JIRA - CIS-43323

  public static String getRealNodeCDBAutoDeploymentECNCommand() {
    return "" + getScriptPath() + "/SOLARIS11/bin/master_ECN.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f  rollout_config";
  }

  public static String getRealNodeCDBAutoDeploymentEDNCommand() {
    return "" + getScriptPath() + "/SOLARIS11/bin/master_EDN.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f  rollout_config";
  }

  public static String getYoulabCDBAutoDeploymentCommand() {
    return "" + getScriptPath() + "/SOLARIS11/bin/master_youlab.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f  rollout_config";
  }

////Implementation for JIRA - CIS-49664
public static String getNetsimVMMemoryCommand() {
      return "free -m";
  }
////Implementation for JIRA - CIS-63304
public static String getNetsimRestart() {
	return "" + getScriptPath() + "/bin/master.sh -c " + getConfigFiles() + "  -g `hostname`  -o yes -l " + getScriptPath() + "/logs/web/CI_EXEC_OSSRC/ -f  restart_netsim";
}
}


