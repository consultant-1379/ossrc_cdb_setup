package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

import groovy.util.ConfigObject;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import org.apache.log4j.Logger;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.PackageInfo;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.data.parser.JsonParser;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.CLITool;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.Terminal;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchCLIToolException;

@Singleton
@Operator(context = { Context.CLI })
public class VNFOperatorCli implements VNFOperator {

  private static final Host hostGateway = DataHandler.getHostByType(HostType.GATEWAY);
  private static final Host hostVnflafservices = DataHandler.getHostByType(HostType.UNKNOWN);
  private static final CLI cliGateway = new CLI(hostGateway, hostGateway.getUsers(UserType.ADMIN).get(0));
  private static CLI cliVnflafservices = new CLI(hostVnflafservices, hostVnflafservices.getUsers(UserType.ADMIN).get(0));
  private static final CLICommandHelper cliCommandHelperGateway = new CLICommandHelper(hostGateway, hostGateway.getUsers(UserType.ADMIN).get(0));
  private static CLICommandHelper cliCommandHelperVnflafservices = new CLICommandHelper(hostVnflafservices, hostVnflafservices.getUsers(UserType.ADMIN).get(0));
  private static RemoteObjectHandler remoteObjectHandlerVnflafservices = new RemoteObjectHandler(hostVnflafservices, hostVnflafservices.getUsers(UserType.ADMIN).get(0));
  private static final Logger logger = Logger.getLogger(VNFOperatorCli.class);
  private static final String EXIT_CODE = "EXIT_CODE:";
  private static final String tempFolder = (String) DataHandler.getAttribute("tempFolder");
  private static final String tempPackageFolder = (String) DataHandler.getAttribute("tempPackageFolder");
  private static final String vnfProduct = (String) DataHandler.getAttribute("vnfProduct");
  private static final String vnfDrop = (String) DataHandler.getAttribute("vnfDrop");
  private List<PackageInfo> packageInfos = new ArrayList<PackageInfo>();

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

  private boolean runCommandOnVnflafservices(String command, Long timeOut) {
    logger.info("Running " + command + " cmd");
    Shell runShell = cliVnflafservices.executeCommand(command, "echo \""+ EXIT_CODE + "\"$?");
    String output = null;
    while (!runShell.isClosed() && timeOut > 0) {
      output = runShell.read();
      if (output.startsWith("EXIT_CODE:")) {
        break;
      }
      if (output.length() > 1) {
        if (output.contains("#") && !output.contains("100")) {
          logger.debug(output);
        } else {
          logger.info(output);
        }
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
      logger.info("$"+lastLine+"$");
      String exitCode = lastLine.split(EXIT_CODE)[1].trim();
      result = Short.valueOf(exitCode);
    } catch (JSchCLIToolException | IndexOutOfBoundsException e) {
      logger.error("Cannot get command result", e);
    }
    logger.info("Shell exit code is " + result);
    runShell.disconnect();
    cliVnflafservices.close();
    return result == 0;
  }

  @Override
    public boolean initialFileStructureSetup() {
    // TODO Auto-generated method stub
    cliCommandHelperGateway.DEFAULT_COMMAND_TIMEOUT_VALUE = 7200;
    try {
      logger.info("Going to create " + tempFolder	+ " on master as well as gateway");
      logger.info(cliCommandHelperGateway.execute("mkdir -p "	+ tempFolder));
      logger.info(cliCommandHelperVnflafservices.execute("mkdir -p " + tempFolder));
      logger.info("" + tempFolder	+ " is created on vnflafservices as well as gateway");
      logger.info(cliCommandHelperVnflafservices.execute("mkdir -p " + tempPackageFolder));
      logger.info("" + tempPackageFolder + " is created on vnflafservices");
      boolean isCopied = remoteObjectHandlerVnflafservices.copyLocalFileToRemote("rpmDownload.sh", "" + tempFolder + "/rpmDownload.sh");
      if (isCopied) {
        logger.info("rpmDownload.sh file is copied to " + tempFolder + "/rpmDownload.sh");
      } else {
        logger.error("rpmDownload.sh file was not copied to " + tempFolder + "/rpmDownload.sh. Please check server connection");
        return false;
      }
      logger.info(cliCommandHelperVnflafservices.execute("chmod 777 "	+ tempFolder + "/rpmDownload.sh"));
      isCopied = remoteObjectHandlerVnflafservices.copyLocalFileToRemote("upgradeRPM.sh", "" + tempFolder+ "/upgradeRPM.sh");
      if (isCopied) {
        logger.info("upgradeRPM.sh file is copied to " + tempFolder	+ "/upgradeRPM.sh");
      } else {
        logger.error("upgradeRPM.sh file was not copied to " + tempFolder + "/upgradeRPM.sh. Please check server connection");
        return false;
      }
      logger.info(cliCommandHelperVnflafservices.execute("chmod 777 " + tempFolder + "/upgradeRPM.sh"));
    } catch (Exception e) {
      logger.error(e);
      return false;
    }
    finally{
      cliCommandHelperVnflafservices.disconnect();
      cliCommandHelperGateway.disconnect();
    }
    return true;
  }

  @Override
    public boolean packageDownload() {
    // TODO Auto-generated method stub
    boolean isAllPackageDownloaded=true;
    try {
      String deployPackage = (String) DataHandler.getAttribute("deployPackage");
      String[] separatePackages = deployPackage.split("\\|\\|");
      String vnfPackages="";
      for (String packageWithVerrsion : separatePackages) {
        String[] parsePackageInfo = packageWithVerrsion.split("::");
        PackageInfo packageInfo = new PackageInfo(parsePackageInfo[0], parsePackageInfo[1]);
        if (parsePackageInfo[1].equalsIgnoreCase("latest")) {
          String getLatestVersion = cliCommandHelperGateway
            .simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/dmt/getLatestPackageObj/?package="
                        + parsePackageInfo[0]
                        + "&version="
                        + parsePackageInfo[1] + "\"");
          String latestVersion = getLatestVersion.split("::")[0];
          String groupId = getLatestVersion.split("::")[1].trim();
          packageInfo.setVersion(latestVersion);
          packageInfo.setGroupId(groupId);
        } else {
          String getLatestVersion = cliCommandHelperGateway
            .simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/dmt/getLatestPackageObj/?package="
                        + parsePackageInfo[0]
                        + "&version="
                        + parsePackageInfo[1] + "\"");
          String latestVersion = getLatestVersion.split("::")[0];
          String groupId = getLatestVersion.split("::")[1].trim();
          packageInfo.setVersion(latestVersion);
          packageInfo.setGroupId(groupId);
        }
        vnfPackages += "#" + packageInfo.getPackageName() + "::"
          + packageInfo.getVersion() + "::"
          + packageInfo.getGroupId();
        packageInfos.add(packageInfo);
      }
      logger.info("GAVs_success >> "+vnfPackages);
      cliCommandHelperGateway.execute("touch "+tempFolder+"/GAVs_success.txt");
      cliCommandHelperGateway.execute("echo '"+vnfPackages+"' | "+tempFolder+"/GAVs_success.txt");
      for (PackageInfo packageInfo : packageInfos) {
        logger.info("Downloading rpm :" + packageInfo);
        boolean isDowloaded = false;
        int count=0;
        while (!isDowloaded) {
          count++;
          logger.info("trying to download package. Attempt "+count+" of 5");
          isDowloaded = runCommandOnVnflafservices("" + tempFolder+ "/rpmDownload.sh " + packageInfo + " " + tempPackageFolder, 46000L);
          if(count==5) {
            break;
          }
        }
        String size = cliCommandHelperVnflafservices.simpleExec("du -h "+ tempPackageFolder + "/" + packageInfo.getPackageName()+ ".rpm");
        logger.info(size);
        size.trim();
        size = size.split("\t")[0];
        size.trim();
        logger.debug(size);
        if (isDowloaded && !size.equals("0")) {
          logger.info("Package :" + packageInfo + " downloading completed");
        } else {
          logger.error("Package :" + packageInfo + " downloading failed. Please check the server connections");
          isAllPackageDownloaded=false;
        }
      }
    } catch (Exception e) {
      logger.error(e);
      isAllPackageDownloaded=false;
      //return true;
    }
    finally{
      cliCommandHelperVnflafservices.disconnect();
      cliCommandHelperGateway.disconnect();
    }
    return isAllPackageDownloaded;
  }

  public boolean installOrUpgradeRPM() {
    // TODO Auto-generated method stub
    try {
      for (PackageInfo packageInfo : packageInfos) {
        logger.info("Installing package :" + packageInfo);
        runCommandOnVnflafservices( "" + tempFolder + "/upgradeRPM.sh " + tempPackageFolder + "/" + packageInfo.getPackageName() + ".rpm", 4600L);
        logger.info("Package installed");
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e);
      return false;
    }
    return true;
  }

  public boolean cleanUpTempDir() {
    // TODO Auto-generated method stub
    cliCommandHelperGateway.DEFAULT_COMMAND_TIMEOUT_VALUE = 7200;
    try {
      logger.info("Going to delete " + tempFolder + " on master as well as gateway");
      logger.info(cliCommandHelperGateway.execute("rm -rf " + tempFolder));
      logger.info(cliCommandHelperVnflafservices.execute("rm -rf " + tempFolder));
      logger.info("" + tempFolder + " is deleted on master as well as gateway");
    } catch (Exception e) {
      logger.error("" + tempFolder + " is not deleted on master as well as gateway.");
      logger.error(e);
      e.printStackTrace();
      return true;
    }
    finally{
      cliCommandHelperVnflafservices.disconnect();
      cliCommandHelperGateway.disconnect();
    }
    return true;
  }

  public boolean collectRPMsDataFromDrop() {
    try {
      String deployPackages = "";
      String vnfPackages = "";
      String integrationProduct = (String) DataHandler.getAttribute("vnfProduct");
      String integrationDrop = (String) DataHandler.getAttribute("vnfDrop");
      logger.info("Fetching all details from VNF drop");
      String out = cliCommandHelperGateway
        .simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/getDropContents/?drop="
                    + integrationDrop
                    + "&product="
                    + integrationProduct + "&pretty=true\"");
      logger.info(out);
      JsonParser parser = new JsonParser();
      List<ConfigObject> configObjects = parser.parse(out);
      logger.info("Listing out VNF packages");
      for (ConfigObject configObject : configObjects) {
        logger.info(configObject.get("name"));
        logger.info(configObject.get("version"));
        if (configObject.get("type").equals("rpm")) {
          deployPackages += "" + configObject.get("name") + "::"+ configObject.get("version") + "||";
          vnfPackages += "#" + configObject.get("name") + "::"+ configObject.get("version") + "::"+ configObject.get("group");
        }
      }
      if (!deployPackages.trim().equals("")) {
        deployPackages = deployPackages.substring(0, deployPackages.length() - 2);
      }
      logger.info("deployPackages >> " + deployPackages);
      logger.info("vnfPackages >> " + vnfPackages);
      DataHandler.setAttribute("deployPackage", deployPackages);
    } catch (Exception e) {
      logger.error("Exception inside installRPMsFromDrop method",e);
      return false;
    }
finally{
cliCommandHelperGateway.disconnect();
    }
return true;
}

  public String getRstateFromVersion(String version) {
    String rstate = "R";
    char c = 65;
    String[] array = version.split("\\.");
    if (array.length == 3) {
      rstate += array[0];
      c += Integer.parseInt(array[1]);
      rstate += c;
      if (Integer.parseInt(array[2]) > 9) {
        rstate += array[2];
      } else {
        rstate += "0" + array[2];
      }
    } else if (array.length == 4) {
      rstate += array[0];
      c += Integer.parseInt(array[1]);
      rstate += c;
      if (Integer.parseInt(array[2]) > 9) {
        rstate += array[2];
      } else {
        rstate += "0" + array[2];
      }
      rstate += "_EC";
      if (Integer.parseInt(array[3]) > 9) {
        rstate += array[3];
      } else {
        rstate += "0" + array[3];
      }
    }
    return rstate;
  }
}
