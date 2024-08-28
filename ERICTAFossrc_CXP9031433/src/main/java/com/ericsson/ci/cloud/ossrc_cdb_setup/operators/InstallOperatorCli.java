package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

import groovy.util.ConfigObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.inject.Singleton;
import org.apache.log4j.Logger;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.PackageInfo;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.data.parser.JsonParser;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.CLITool;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.cifwk.taf.tools.cli.jsch.JSchCLIToolException;

@Singleton
@Operator(context = { Context.CLI })
public class InstallOperatorCli implements InstallOperator {
  private static final Host hostGateway = DataHandler.getHostByType(HostType.GATEWAY);
  private static final Host hostOssmaster = DataHandler.getHostByType(HostType.RC);
  private static final User rootUser = new User(hostOssmaster.getUser(UserType.ADMIN),hostOssmaster.getPass(UserType.ADMIN), UserType.ADMIN);
  private static final Host hostHub = DataHandler.getHostByType(HostType.CIFWK);
  private static final CLICommandHelper cliCommandHelperHub = new CLICommandHelper(hostHub, hostHub.getUsers(UserType.CUSTOM).get(0));
  private static final CLI cliGateway = new CLI(hostGateway, hostGateway.getUsers(UserType.ADMIN).get(0));
  private static CLI cliOssmaster = new CLI(hostOssmaster, hostOssmaster.getUsers(UserType.ADMIN).get(0));
  private static final CLICommandHelper cliCommandHelperGateway = new CLICommandHelper(hostGateway, hostGateway.getUsers(UserType.ADMIN).get(0));
  private static CLICommandHelper cliCommandHelperOssmaster = new CLICommandHelper(hostOssmaster, hostOssmaster.getUsers(UserType.ADMIN).get(0));
  private static final RemoteObjectHandler remoteObjectHandlerGateway = new RemoteObjectHandler(hostGateway, hostGateway.getUsers(UserType.ADMIN).get(0));
  private static RemoteObjectHandler remoteObjectHandlerOssmaster = new RemoteObjectHandler(hostOssmaster, rootUser);
  private static final Logger logger = Logger.getLogger(InstallOperatorCli.class);
  private static final String EXIT_CODE = "EXIT_CODE:";
  private static final String tempFolder = (String) DataHandler.getAttribute("tempFolder");
  private static final String tempPackageFolder = (String) DataHandler.getAttribute("tempPackageFolder");
  private static final String ossrcPackageInfoFile = (String) DataHandler.getAttribute("ossrcPackageInfoFile");
  private static final String simulationPackageInfoFile = (String) DataHandler.getAttribute("simulationPackageInfoFile");
  private static final String ossrcProduct = (String) DataHandler.getAttribute("ossrcProduct");
  private static final String ossrcDrop = (String) DataHandler.getAttribute("integrationDrop");
  private static final String simulationProduct = (String) DataHandler.getAttribute("simulationProduct");
  private static final String simulationDrop = (String) DataHandler.getAttribute("integrationDrop");
  private static final String userId = (String) DataHandler.getAttribute("userId");
  private static final String email = (String) DataHandler.getAttribute("email");
  private static final String shipmentName = (String) DataHandler.getAttribute("shipmentName");
  private static final String viewName = (String) DataHandler.getAttribute("viewName");
  private List<PackageInfo> packageInfos = new ArrayList<PackageInfo>();

  private boolean runCommandOnGateway(String command, Long timeOut) {
    logger.info("Running " + command + " cmd");
    Shell runShell = cliGateway.executeCommand(command, "echo \"" + EXIT_CODE + "\"$?");
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

  private boolean runCommandOnOssmaster(String command, Long timeOut) {
    logger.info("Running " + command + " cmd");
    Shell runShell = cliOssmaster.executeCommand(command, "echo \"" + EXIT_CODE + "\"$?");
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
    cliOssmaster.close();
    return result == 0;
  }

  @Override
    public boolean initialFileStructureSetup() {
    // TODO Auto-generated method stub
    cliCommandHelperGateway.DEFAULT_COMMAND_TIMEOUT_VALUE = 600;
    try {
      logger.info("Going to create " + tempFolder + " gateway");
      logger.info(cliCommandHelperGateway.execute("mkdir -p " + tempFolder));
      logger.info("Going to create " + tempFolder + " on master");
      logger.info(cliCommandHelperOssmaster.execute("mkdir -p " + tempFolder));
      logger.info("" + tempFolder + " is created on master as well as gateway");
      logger.info(cliCommandHelperGateway.execute("mkdir -p " + tempPackageFolder));
      logger.info(cliCommandHelperOssmaster.execute("mkdir -p " + tempPackageFolder));
      logger.info("" + tempPackageFolder + " is created on master as well as gateway");
      boolean isCopied = remoteObjectHandlerGateway .copyLocalFileToRemote("packageDownload.sh", "" + tempFolder + "/packageDownload.sh");
      if (isCopied) {
        logger.info("packageDownload.sh file is copied to " + tempFolder + "/packageDownload.sh");
      } else {
        logger.error("packageDownload.sh file was not copied to " + tempFolder + "/packageDownload.sh. Please check server connection");
        return false;
      }
      logger.info(cliCommandHelperGateway.execute("chmod 777 " + tempFolder + "/packageDownload.sh"));
      isCopied = remoteObjectHandlerOssmaster.copyLocalFileToRemote("installPackage.sh", "" + tempFolder + "/installPackage.sh");
      if (isCopied) {
        logger.info("installPackage.sh file is copied to " + tempFolder + "/installPackage.sh");
      } else {
        logger.error("installPackage.sh file was not copied to " + tempFolder + "/installPackage.sh. Please check server connection");
        return false;
      }
      logger.info(cliCommandHelperOssmaster.execute("chmod 777 " + tempFolder + "/installPackage.sh"));
      isCopied = remoteObjectHandlerOssmaster.copyLocalFileToRemote("installPackages.sh", "" + tempFolder + "/installPackages.sh");
      if (isCopied) {
        logger.info("installPackages.sh file is copied to " + tempFolder + "/installPackages.sh");
      } else {
        logger.error("installPackages.sh file was not copied to " + tempFolder + "/installPackages.sh. Please check server connection");
        return false;
      }
      logger.info(cliCommandHelperOssmaster.execute("chmod 777 " + tempFolder + "/installPackages.sh"));
    } catch (Exception e) {
      logger.error(e);
      return false;
    }
    finally{
      cliCommandHelperOssmaster.disconnect();
      cliCommandHelperGateway.disconnect();
    }
    return true;
  }

  @Override
    public boolean packageDownload() {
    // TODO Auto-generated method stub
    boolean isAllPackageDownloaded = true;
    try {
      String deployPackage = (String) DataHandler.getAttribute("deployPackage");
      String[] separatePackages = deployPackage.split("\\|\\|");
      for (String packageWithVerrsion : separatePackages) {
        String[] parsePackageInfo = packageWithVerrsion.split("::");
        PackageInfo packageInfo = new PackageInfo(parsePackageInfo[0], parsePackageInfo[1]);
        if (parsePackageInfo[1].equalsIgnoreCase("latest")) {
          String getLatestVersion = cliCommandHelperGateway.simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/dmt/getLatestPackageObj/?package=" + parsePackageInfo[0]+ "&version="+ parsePackageInfo[1] + "\"");
          String latestVersion = getLatestVersion.split("::")[0];
          String groupId = getLatestVersion.split("::")[1].trim();
          packageInfo.setVersion(latestVersion);
          packageInfo.setGroupId(groupId);
        } else {
          String getLatestVersion = cliCommandHelperGateway.simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/dmt/getLatestPackageObj/?package=" + parsePackageInfo[0] + "&version=" + parsePackageInfo[1] + "\"");
          String latestVersion = getLatestVersion.split("::")[0];
          String groupId = getLatestVersion.split("::")[1].trim();
          packageInfo.setVersion(latestVersion);
          packageInfo.setGroupId(groupId);
        }
        packageInfos.add(packageInfo);
      }
      for (PackageInfo packageInfo : packageInfos) {
        logger.info("Downloading package :" + packageInfo);
        boolean isDowloaded = runCommandOnGateway("" + tempFolder + "/packageDownload.sh " + packageInfo + " " + tempPackageFolder, 36000L);
        String size = cliCommandHelperGateway.simpleExec("du -h " + tempPackageFolder + "/" + packageInfo.getPackageName() + ".pkg");
        logger.info(size);
        size.trim();
        size = size.split("\t")[0];
        size.trim();
        logger.debug(size);
        if (isDowloaded && !size.equals("0")) {
          logger.info("Package :" + packageInfo + " downloading completed");
        } else {
          logger.error("Package :" + packageInfo + " downloading failed. Please check the server connections");
          isAllPackageDownloaded = false;
        }
      }
    } catch (Exception e) {
      logger.error(e);
      isAllPackageDownloaded = false;
      return true;
    }
    cliCommandHelperGateway.disconnect();
    return isAllPackageDownloaded;
  }

  public boolean installPackage() {
    // TODO Auto-generated method stub
    try {
      for (PackageInfo packageInfo : packageInfos) {
        logger.info("Installing package :" + packageInfo);
        runCommandOnOssmaster("" + tempFolder + "/installPackage.sh "+ tempPackageFolder + "/" + packageInfo.getPackageName() + ".pkg", 3600L);
        logger.info("Package installed");
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e);
      return false;
    }
    return true;
  }

  public boolean installPackages() {
    // TODO Auto-generated method stub
    try {
      logger.info("Installing packages ");
      runCommandOnOssmaster("" + tempFolder + "/installPackages.sh " + tempPackageFolder, 36000L);
      logger.info("Packages installed");
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
      logger.info("Going to delete " + tempFolder+ " on master as well as gateway");
      logger.info(cliCommandHelperGateway.execute("rm -rf " + tempFolder));
      logger.info(cliCommandHelperOssmaster.execute("rm -rf "+ tempFolder));
      logger.info("" + tempFolder+ " is deleted on master as well as gateway");
    } catch (Exception e) {
      logger.error("" + tempFolder+ " is not deleted on master as well as gateway.");
      logger.error(e);
      e.printStackTrace();
      return true;
    }
    finally{
      cliCommandHelperOssmaster.disconnect();
      cliCommandHelperGateway.disconnect();
    }
    return true;
  }

  public boolean postInstall() {
    // TODO Auto-generated method stub
    String allCommands = (String) DataHandler.getAttribute("Commands");
    boolean commandExitStatus = true;
    if (allCommands != null && !allCommands.equals("")) {
      String[] commands = allCommands.split(";");
      for (String command : commands) {
        commandExitStatus = commandExitStatus && (runCommandOnOssmaster(command, 3600L));
        if (!commandExitStatus) {
          return commandExitStatus;
        }
      }
      return commandExitStatus;
    } else {
      return true;
    }
  }

  public boolean installLatestPackageForSimIntegration() {
    try {
      String deployPackages = "";
      String ossrcPackages = "";
      String simulationPackages = "";
      String integrationProduct = (String) DataHandler.getAttribute("integrationProduct");
      String integrationDrop = (String) DataHandler.getAttribute("integrationDrop");
      logger.info("Fetching all details from Sim-Integration portal");
      String out = cliCommandHelperGateway.simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/getDropContents/?drop="+ integrationDrop + "&product="	+ integrationProduct + "&pretty=true\"");
      logger.info(out);
      JsonParser parser = new JsonParser();
      List<ConfigObject> configObjects = parser.parse(out);
      logger.info("Listing out OSSRC & simulation packages");
      for (ConfigObject configObject : configObjects) {
        logger.info(configObject.get("name"));
        logger.info(configObject.get("version"));
        if (configObject.get("type").equals("pkg")) {
          deployPackages += "" + configObject.get("name") + "::" + configObject.get("version") + "||";
          ossrcPackages += "#" + configObject.get("name") + "::"+ configObject.get("version") + "::" + configObject.get("group");
        } else if (configObject.get("type").equals("zip")) {
          simulationPackages += "#" + configObject.get("name") + "::"+ configObject.get("version") + "::"+ configObject.get("group");
        }
      }
      if (!deployPackages.trim().equals("")) {
        deployPackages = deployPackages.substring(0,deployPackages.length() - 2);
      }
      logger.info("deployPackages >> " + deployPackages);
      logger.info("ossrcPackages >> " + ossrcPackages);
      logger.info("simulationPackages >> " + simulationPackages);
      DataHandler.setAttribute("deployPackage", deployPackages);
      // "/tmp/ossrcPackageInfo.txt"
      FileWriter fw1 = new FileWriter(new File(ossrcPackageInfoFile));
      fw1.write(ossrcPackages + "\n");
      fw1.close();
      logger.info(ossrcPackageInfoFile + " created with value"+ ossrcPackages);
      FileWriter fw2 = new FileWriter(new File(simulationPackageInfoFile));
      fw2.write(simulationPackages + "\n");
      fw2.close();
      logger.info(simulationPackageInfoFile + " created with value"+ simulationPackages);
    } catch (Exception e) {
      logger.error("Exception inside installLatestPackageForSimIntegration method",e);
      return false;
    }
    finally{
      cliCommandHelperGateway.disconnect();
    }
    return true;
  }

  public boolean generateScriptForOSSRCdelivery() {
    // TODO Auto-generated method stub
    try {
      // Read data
      FileInputStream fis1 = new FileInputStream(new File(ossrcPackageInfoFile));
      // Construct BufferedReader from InputStreamReader
      BufferedReader br1 = new BufferedReader(new InputStreamReader(fis1));
      String ossrcPackages = br1.readLine();
      logger.info("OSSRC package list :" + ossrcPackages);
      br1.close();
      ossrcPackages = filterOutDeliveredOne(ossrcProduct, ossrcDrop, "pkg", ossrcPackages);
      logger.info("OSSRC package list to deliver :" + ossrcPackages);
      // Generate deliver Script
      String deliverPackageScriptFile = (String) DataHandler.getAttribute("deliverPackageScriptFile");
      FileWriter fw = new FileWriter(new File(deliverPackageScriptFile));
      // /tmp/packageDeliveryScript/
      fw.write((String) DataHandler.getAttribute("bashHeader") + "\n");
      fw.write((String) DataHandler.getAttribute("deletePackageDirectory") + "\n");
      fw.write((String) DataHandler.getAttribute("createPackageDirectory") + "\n");
      String[] pkgDetails = ossrcPackages.split("#");
      for (int i = 0; i < pkgDetails.length; i++) {
        if (!pkgDetails[i].trim().equals("")) {
          String[] pkgInfo = pkgDetails[i].split("::");
          String name = pkgInfo[0];
          String version = pkgInfo[1];
          String group = pkgInfo[2];
          String cxpnumber = name.split("_")[1];
          String downloadPackageCommand = String.format((String) DataHandler.getAttribute("downloadCommand"), name, group, name, version, "pkg");
          logger.info(downloadPackageCommand);
          // logger.info(DataHandler.getAttribute("downloadCommand1"));
          fw.write(downloadPackageCommand + "\n");
          fw.write("echo \"Package download completed : " + name + ".pkg of version " + version + "("+ getRstateFromVersion(version) + ") \"" + "\n");
          String fetchContainerCommand = String.format((String) DataHandler.getAttribute("fetchContainerCommand"), name.split("_")[0], viewName);
          logger.info(fetchContainerCommand);
          fw.write(fetchContainerCommand + "\n");
          logger.info(DataHandler.getAttribute("fetchContainerCommand1"));
          String readContainerValueCommand = (String) DataHandler.getAttribute("readContainerValueCommand");
          logger.info(readContainerValueCommand);
          fw.write(readContainerValueCommand + "\n");
          String deliverPackageToVobCommand = String.format((String) DataHandler.getAttribute("deliverPackageToVobCommand"),email, shipmentName, cxpnumber, userId,name, viewName);
          logger.info(deliverPackageToVobCommand);
          // logger.info(DataHandler
          // .getAttribute("deliverPackageToVobCommand1"));
          fw.write(deliverPackageToVobCommand + "\n");
          fw.write("echo \"Package delivery completed : " + name + ".pkg of version " + version + "("+ getRstateFromVersion(version) + ") \"" + "\n");
        }
      }
      fw.close();
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
      logger.error(e);
      return false;
    }
    return true;
  }

  public boolean deliverSimulation() {
    // TODO Auto-generated method stub
    try {
      FileInputStream fis2 = new FileInputStream(new File(simulationPackageInfoFile));
      BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
      String simulationPackages = br2.readLine();
      logger.info("Simulation package list :" + simulationPackages);
      br2.close();
      // create directory and temporary folder
      cliCommandHelperGateway.DEFAULT_COMMAND_TIMEOUT_VALUE = 72000;
      logger.info("Going to create " + tempFolder + " on gateway");
      logger.info(cliCommandHelperGateway.execute("mkdir -p "+ tempFolder));
      logger.info("" + tempFolder + " is created on gateway");
      boolean isCopied = remoteObjectHandlerGateway.copyLocalFileToRemote("deliverMultiplePackagesToDrop.sh","" + tempFolder+ "/deliverMultiplePackagesToDrop.sh");
      if (isCopied) {
        logger.info("deliverMultiplePackagesToDrop.sh file is copied to "+ tempFolder + "/deliverMultiplePackagesToDrop.sh");
      } else {
        logger.error("deliverMultiplePackagesToDrop.sh file was not copied to "+ tempFolder+ "/deliverMultiplePackagesToDrop.sh. Please check server connection");
        return false;
      }
      logger.info(cliCommandHelperGateway.execute("chmod 777 "+ tempFolder + "/deliverMultiplePackagesToDrop.sh"));
      // Deliver the Simulation package list.
      simulationPackages = filterOutDeliveredOne(simulationProduct,simulationDrop, "zip", simulationPackages);
      logger.info("list of Simulation package need to be delivered"+ simulationPackages);
      if (!simulationPackages.trim().equals("")) {
        boolean isDeliveredToSimulationdrops = runCommandOnGateway(""+ tempFolder + "/deliverMultiplePackagesToDrop.sh -a '"+ simulationPackages.trim() + "' -P "
                                                                   + simulationProduct + " -i " + simulationDrop+ " -t zip -u " + userId + " -e " + email + " -o 3",12000L);
        logger.info("Is the listed has delivered to OSSRC drops "+ isDeliveredToSimulationdrops);
      } else {
        logger.info("All simulation present at integration product is already delivered to corresponding drop of SIMNET/NETSIM product");
        // return false;
      }
      logger.info("Veifying and Saving the delivery content to a file");
      saveDeliveryContentToFile(simulationPackages);
    } catch (Exception e) {
      // TODO: handle exception
      logger.error(e);
      return false;
    }
    finally{
      cliCommandHelperGateway.disconnect();
    }
    return true;
  }

  public void saveDeliveryContentToFile(String simulationPackages) {
    // //export/scripts/CLOUD/bin/getNetsimVerifiedPatchList.pl -v=R28C
    // cliCommandHelperGateway.execute("/export/scripts/CLOUD/bin/getNetsimVerifiedPatchList.pl -v=R28C");
    // String patchList = cliCommandHelperGateway.getStdOut();
    // "/export/scripts/CLOUD/logs/web/CI_EXEC_OSSRC/netsim.simnet"
    String netsimDeliveryFilePath = (String) DataHandler.getAttribute("netsimDeliveryFilePath");
    Set<String> oldSimulationSet = new HashSet<String>();
    Set<String> oldPatchSet = new HashSet<String>();
    try {
      try (BufferedReader br = new BufferedReader(new FileReader(new File(netsimDeliveryFilePath)))) {
        String line;
        boolean isReadingSimulations = false;
        boolean isReadingPatches = false;
        while ((line = br.readLine()) != null) {
          // process the line.
          logger.info(line);
          if (line.contains("Simulation details")) {
            isReadingSimulations = true;
            isReadingPatches = false;
            continue;
          } else if (line.contains("Netsim Patch details")) {
            isReadingSimulations = false;
            isReadingPatches = true;
            continue;
          }
          if (!line.trim().equals("")) {
            if (isReadingSimulations) {
              oldSimulationSet.add(line.trim());
            } else if (isReadingPatches) {
              oldPatchSet.add(line.trim());
            }
          }
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    logger.info("Simulations already delivered :: " + oldSimulationSet);
    logger.info("Patches already delivered :: " + oldPatchSet);
    logger.info("###############################################");
    // Getting current patchSet and simulationSet
    Set<String> currentSimulationSet = new HashSet<String>();
    Set<String> currentPatchSet = new HashSet<String>();
    // This line has to be commented out
    // simulationPackages =
    // "#LTE16B-V3x3-FTDG2-FDD-LTE15_LTE::1.0.2::com.ericsson.oss.common#LTE16A-V12x3-FTDG2-TDD-LTE12_LTE::1.0.3::com.ericsson.oss.common#LTE16A-V12x3-FTDG2-FDD-LTE11_LTE::1.0.3::com.ericsson.oss.common#LTE15B-V9x3-FTDG2-TDD-LTE14_LTE::1.0.3::com.ericsson.oss.common#LTE15B-V9x3-FTDG2-FDD-LTE13_LTE::1.0.3::com.ericsson.oss.common";
    String[] simulations = simulationPackages.split("#");
    for (String simulation : simulations) {
      if (!simulation.trim().equals("")) {
        currentSimulationSet.add(simulation.trim());
      }
    }
    String getNetsimVerifiedPatchCommand = (String) DataHandler.getAttribute("getNetsimVerifiedPatchCommand");
    cliCommandHelperGateway.execute(getNetsimVerifiedPatchCommand);
    String netsimVerifiedPatches = cliCommandHelperGateway.getStdOut();
    logger.info("$" + netsimVerifiedPatches + "$");
    String[] patches = netsimVerifiedPatches.split("\n");
    for (String patch : patches) {
      if (patch.contains("Patches")) {
        currentPatchSet.add(patch.trim());
      }
    }
    logger.info("Simulations delivered now :: " + currentSimulationSet);
    logger.info("Patches delivered till now :: " + currentPatchSet);
    // Calculating the delta or difference
    currentPatchSet.removeAll(oldPatchSet);
    currentSimulationSet.removeAll(oldSimulationSet);
    if (currentPatchSet.size() > 0) {
      logger.info("Following patches are newly delivered :: "+ currentPatchSet);
    }
    if (currentSimulationSet.size() > 0) {
      logger.info("Following patches are newly delivered :: "+ currentSimulationSet);
    }
    // adding delta patch list to the final delivered patchSet
    if (currentPatchSet.size() > 0 || currentSimulationSet.size() > 0) {
      oldPatchSet.addAll(currentPatchSet);
      oldSimulationSet.addAll(currentSimulationSet);
      try {
        FileWriter writer = new FileWriter(new File(netsimDeliveryFilePath));
        Date date = new Date();
        logger.info("Updated on :" + date);
        writer.write("Updated on :" + date + "\n");
        logger.info("#########Simulation details#############");
        writer.write("#########Simulation details#############\n");
        for (Iterator iterator = oldSimulationSet.iterator(); iterator.hasNext();) {
          String simulation = (String) iterator.next();
          if (!simulation.trim().equals("")) {
            writer.write(simulation + "\n");
          }
        }
        logger.info("#########Netsim Patch details############");
        writer.write("#########Netsim Patch details############\n");
        for (Iterator iterator = oldPatchSet.iterator(); iterator.hasNext();) {
          String patch = (String) iterator.next();
          if (patch.contains("Patches")) {
            writer.write(patch + "\n");
          }
        }
        writer.flush();
        writer.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // Creating final touch file representing new netsim delivery to
      // proj/ossrcdm/isNetsimDelivered/netsim.simnet
      logger.info("Creating final touch file representing new netsim delivery to proj/ossrcdm/isNetsimDelivered/netsim.simnet");
      cliCommandHelperHub.execute("touch /proj/ossrcdm/isNetsimDelivered/netsim.simnet");
    }
    cliCommandHelperHub.disconnect();
    cliCommandHelperGateway.disconnect();
  }

  public String filterOutDeliveredOne(String product, String drop,String type, String values) {
    try {
      String deployPackages = "";
      String ossrcPackages = "";
      String simulationPackages = "";
      logger.info("Fetching all details from Sim-Integration portal");
      String out = cliCommandHelperGateway.simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/getDropContents/?drop="+ drop + "&product=" + product + "&pretty=true\"");
      logger.info(out);
      JsonParser parser = new JsonParser();
      List<ConfigObject> configObjects = parser.parse(out);
      logger.info("Listing out packages of type : " + type);
      for (ConfigObject configObject : configObjects) {
        if (configObject.get("type").equals(type)) {
          logger.info(configObject.get("name"));
          logger.info(configObject.get("version"));
          String temp = "#" + configObject.get("name") + "::"
            + configObject.get("version") + "::"
            + configObject.get("group");
          if (values.contains(temp)) {
            values = values.replace(temp, "");
            logger.info(configObject.get("name") + "  "
                        + configObject.get("version")
                        + " is already delivered at drop=" + drop
                        + " of product=" + product + "");
          }
        }
      }
    } catch (Exception e) {
      logger.error(e);
    }
    finally{
      cliCommandHelperGateway.disconnect();
    }
    return values;
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

 /*
  * public List<ConfigObject> getOSSRCDeliveryList( String
  * simulationIntegrationProduct, String ossrcProduct, String drop, String
  * type, String values) {
  * List<ConfigObject> configObjectsForDelivery = null; try {
  * logger.info("Fetching all details from Sim-Integration portal");
  * String OSSRC_out = cliCommandHelperGateway .simpleExec(
  * "curl --request GET \"https://cifwk-oss.lmera.ericsson.se/getDropContents/?drop="
  * + drop + "&product=" + ossrcProduct + "&pretty=true\"");
  * logger.info(OSSRC_out);
  * String integration_out = cliCommandHelperGateway .simpleExec(
  * "curl --request GET \"https://cifwk-oss.lmera.ericsson.se/getDropContents/?drop="
  * + drop + "&product=" + simulationIntegrationProduct + "&pretty=true\"");
  * logger.info(integration_out);
  * JsonParser parser = new JsonParser(); List<ConfigObject> configObjects =
  * parser.parse(OSSRC_out); List<ConfigObject> integrationConfigObjects =
  * parser .parse(integration_out);
  * String deployPackages = ""; String ossrcPackages = ""; String
  * simulationPackages = ""; configObjectsForDelivery = new
  * ArrayList<ConfigObject>();
  * logger.info("Listing out OSSRC packages"); for (ConfigObject configObject
  * : configObjects) { if (configObject.get("type").equals(type)) { String
  * temp = "#" + configObject.get("name") + "::" +
  * configObject.get("version") + "::" + configObject.get("group");
  * logger.info(temp); if (values.contains(temp)) { values =
  * values.replace(temp, ""); logger.info(configObject.get("name") + "  " +
  * configObject.get("version") + " is already delivered"); } } }
  * logger.info("Values ----" + values);
  * for (ConfigObject configObject : integrationConfigObjects) { if
  * (configObject.get("type").equals(type)) { String temp = "#" +
  * configObject.get("name") + "::" + configObject.get("version") + "::" +
  * configObject.get("group"); logger.info(temp); if (values.contains(temp))
  * { values = values.replace(temp, ""); logger.info(configObject.get("name")
  * + "  " + configObject.get("version") + " is already delivered");
  * configObjectsForDelivery.add(configObject); } } }
  * for (ConfigObject configObject : configObjectsForDelivery) {
  * logger.info(configObject.get("deliveryDrop"));
  * logger.info(configObject.get("group"));
  * logger.info(configObject.get("mediaCategory"));
  * logger.info(configObject.get("mediaPath"));
  * logger.info(configObject.get("name"));
  * logger.info(configObject.get("number"));
  * logger.info(configObject.get("platform"));
  * logger.info(configObject.get("type"));
  * logger.info(configObject.get("url"));
  * logger.info(configObject.get("version"));
  * String name = (String) configObject.get("name"); String pkgName =
  * name.split("_")[0];
  * logger.info("Package name :" + pkgName);
  * }
  * } catch (Exception e) { logger.error(e);
  * } return configObjectsForDelivery; }
  * public boolean deliverOSSRCAndSimulation_test() { // TODO Auto-generated
  * method stub try { // Read data FileInputStream fis1 = new
  * FileInputStream(new File( "C:/Users/ekuijah/ossrcPackageInfo.txt")); //
  * Construct BufferedReader from InputStreamReader BufferedReader br1 = new
  * BufferedReader(new InputStreamReader(fis1)); String ossrcPackages =
  * br1.readLine(); logger.info("OSSRC package list :" + ossrcPackages);
  * br1.close(); System.out.println(ossrcPackages);
  *
  * // Deliver the OSSRC packages 1st
  * String ossrcProduct = (String) DataHandler .getAttribute("ossrcProduct");
  * String ossrcDrop = (String) DataHandler .getAttribute("integrationDrop");
  * String simulationProduct = (String) DataHandler
  * .getAttribute("integrationProduct");
  * String userId = (String) DataHandler.getAttribute("userId"); String email
  * = (String) DataHandler.getAttribute("email"); String shipmentName =
  * (String) DataHandler .getAttribute("shipmentName"); String viewName =
  * (String) DataHandler.getAttribute("viewName");
  * List<ConfigObject> configObjects = getOSSRCDeliveryList(
  * simulationProduct, ossrcProduct, ossrcDrop, "pkg", ossrcPackages);
  * FileWriter fw1 = new FileWriter(new File(
  * "C:/Users/ekuijah/deliverPackage.sh"));
  * // /tmp/packageDeliveryScript/ fw1.write((String)
  * DataHandler.getAttribute("bashHeader") + "\n"); fw1.write((String)
  * DataHandler .getAttribute("deletePackageDirectory") + "\n");
  * fw1.write((String) DataHandler .getAttribute("createPackageDirectory") +
  * "\n");
  * for (ConfigObject configObject : configObjects) { String deliveryDrop =
  * (String) configObject.get("deliveryDrop"); String group = (String)
  * configObject.get("group"); String mediaCategory = (String) configObject
  * .get("mediaCategory"); String mediaPath = (String)
  * configObject.get("mediaPath"); String name = (String)
  * configObject.get("name"); String cxpnumber = (String)
  * configObject.get("number"); String platform = (String)
  * configObject.get("platform"); String type = (String)
  * configObject.get("type"); String url = (String) configObject.get("url");
  * String version = (String) configObject.get("version");
  * String downloadPackageCommand = String.format( (String)
  * DataHandler.getAttribute("downloadCommand"), name, group, name, version,
  * "pkg");
  *
  * logger.info(downloadPackageCommand);
  * logger.info(DataHandler.getAttribute("downloadCommand1"));
  *
  * fw1.write(downloadPackageCommand + "\n");
  * fw1.write("echo \"Package download completed : " + name +
  * ".pkg of version " + version + "(" + getRstateFromVersion(version) +
  * ") \"" + "\n");
  *
  * String fetchContainerCommand = String.format( (String) DataHandler
  * .getAttribute("fetchContainerCommand"), name .split("_")[0], viewName);
  *
  * logger.info(fetchContainerCommand); fw1.write(fetchContainerCommand +
  * "\n");
  *
  * logger.info(DataHandler.getAttribute("fetchContainerCommand1"));
  *
  * String readContainerValueCommand = (String) DataHandler
  * .getAttribute("readContainerValueCommand");
  * logger.info(readContainerValueCommand);
  * fw1.write(readContainerValueCommand + "\n");
  *
  * String deliverPackageToVobCommand = String.format( (String) DataHandler
  * .getAttribute("deliverPackageToVobCommand"), email, shipmentName,
  * cxpnumber, userId, name, viewName);
  *
  * logger.info(deliverPackageToVobCommand); logger.info(DataHandler
  * .getAttribute("deliverPackageToVobCommand1"));
  * fw1.write(deliverPackageToVobCommand + "\n");
  * fw1.write("echo \"Package delivery completed : " + name +
  * ".pkg of version " + version + "(" + getRstateFromVersion(version) +
  * ") \"" + "\n");
  *
  * } fw1.close();
  *
  * } catch (Exception e) { // TODO: handle exception logger.error(e); return
  * false; }
  *
  * return true; }
  */

  public boolean restartAllMCs() {
    String restartAll = (String) DataHandler.getAttribute("restartAll");
    String smtoolProgress = (String) DataHandler.getAttribute("smtoolProgress");
    logger.info("mafOnlineCommand " + restartAll);
    logger.info("smtoolProgress " + smtoolProgress);
    cliCommandHelperOssmaster.execute(restartAll);
    logger.info(cliCommandHelperOssmaster.getStdOut());
    if (cliCommandHelperOssmaster.getCommandExitValue() == 0) {
      try {
        logger.info("Sleeping for 30 minutes");
        Thread.sleep(1800000);
        runCommandOnOssmaster(smtoolProgress, 1200L);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return false;
      }
      runCommandOnOssmaster(smtoolProgress, 1200L);
      logger.info(cliCommandHelperOssmaster.getStdOut());
    }
    return cliCommandHelperOssmaster.getCommandExitValue() == 0;
  }

  public boolean rebootOssmaster() {
    // TODO Auto-generated method stub
    cliCommandHelperGateway.DEFAULT_COMMAND_TIMEOUT_VALUE = 7200;
    try {
      logger.info("Going to create " + tempFolder+ " on master as well as gateway");
      logger.info(cliCommandHelperGateway.execute("mkdir -p "+ tempFolder));
      logger.info(cliCommandHelperOssmaster.execute("mkdir -p "+ tempFolder));
      logger.info("" + tempFolder + " is created on master as well as gateway");
      boolean isCopied = remoteObjectHandlerGateway.copyLocalFileToRemote("doReboot.sh", "" + tempFolder + "/doReboot.sh");
      if (isCopied) {
        logger.info("doReboot.sh file is copied to " + tempFolder + "/doReboot.sh");
      } else {
        logger.error("doReboot.sh file was not copied to " + tempFolder+ "/doReboot.sh. Please check server connection");
        return false;
      }
      logger.info(cliCommandHelperGateway.execute("chmod 777 "+ tempFolder + "/doReboot.sh"));
      isCopied = remoteObjectHandlerOssmaster.copyLocalFileToRemote("smtoolCheck.sh", "" + tempFolder + "/smtoolCheck.sh");
      if (isCopied) {
        logger.info("smtoolCheck.sh file is copied to " + tempFolder + "/smtoolCheck.sh");
      } else {
        logger.error("smtoolCheck.sh file was not copied to "+ tempFolder + "/smtoolCheck.sh. Please check server connection");
        return false;
      }
      logger.info(cliCommandHelperOssmaster.execute("chmod 777 "+ tempFolder + "/smtoolCheck.sh"));
      boolean isRebooted = runCommandOnGateway("" + tempFolder + "/doReboot.sh 192.168.0.5", 3600L);
      if (isRebooted) {
        logger.info("Ossmaster successfully rebooted");
      } else {
        logger.info("Ossmaster didn't reboot properly . please check the server connection");
      }
      cliOssmaster = new CLI(hostOssmaster, hostOssmaster.getUsers(UserType.ADMIN).get(0));
      cliCommandHelperOssmaster = new CLICommandHelper(hostOssmaster,hostOssmaster.getUsers(UserType.ADMIN).get(0));
      remoteObjectHandlerOssmaster = new RemoteObjectHandler(hostOssmaster, hostOssmaster.getUsers(UserType.ADMIN).get(0));
      boolean isSmtoolUp = false;
      int attempt = 1;
      logger.info("Waiting until Oss is online\n");
      while (attempt <= 360) {
        logger.info(" Attempt number " + attempt + " of 360 to check Oss online status \n");
        cliCommandHelperOssmaster.execute("hagrp -display Oss -sys ossmaster | grep ONLINE >  /dev/null 2>&1");
        if (cliCommandHelperOssmaster.getCommandExitValue() != 0) {
          logger.info(" Oss is not online yet, waiting for 5 seconds before trying again\n");
          Thread.sleep(5000);
        } else {
          logger.info("Oss is online now\n");
          cliCommandHelperOssmaster.execute("hastatus -sum");
          logger.info(cliCommandHelperOssmaster.getStdOut(5));
          isSmtoolUp = true;
          break;
        }
        attempt++;
      }
  /*
   * System.out.println(cliCommandHelperOssmaster.getCommandExitValue()
   * ); boolean isSmtoolUp = runCommandOnOssmaster("sh " + tempFolder
   * + "/smtoolCheck.sh", 3600L);
   *
   * if(isSmtoolUp) { logger.info("Smtool is up now"); }else {
   * logger.info("Smtool still not up . please check the server"); }
   */
      String smtoolProgress = (String) DataHandler.getAttribute("smtoolProgress");
      logger.info("Sleeping for 30 minutes");
      Thread.sleep(600000);
      runCommandOnOssmaster(smtoolProgress, 1200L);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    finally{
      cliOssmaster.close();
      cliCommandHelperOssmaster.disconnect();
      cliCommandHelperGateway.disconnect();
    }
    return true;
  }

  public boolean verifyMOMUpgrade() {
    // TODO Auto-generated method stub
    cliCommandHelperGateway.DEFAULT_COMMAND_TIMEOUT_VALUE = 7200;
    try {
      boolean ismomUpgraded = false;
      String momUpgradeFile = (String) DataHandler.getAttribute("momUpgradeFile");
      ismomUpgraded = remoteObjectHandlerOssmaster.remoteFileExists(momUpgradeFile);
      if (!ismomUpgraded) {
        logger.info(momUpgradeFile+ " file is not present. So MOM package was not part of this upgrade step "+ "and skipping verifyMOMUpgrade TC");
        return true;
      }
      int attempt = 1;
      logger.info("Waiting until MOM Upgrade successful\n");
      while (attempt <= 60) {
        logger.info(" Attempt number " + attempt + " of 60 to check Oss online status \n");
        cliCommandHelperOssmaster.simpleExec("more " + momUpgradeFile);
        if (cliCommandHelperOssmaster.getStdOut().trim().equals("0")) {
          logger.info("MOM Upgraded successfully\n");
          logger.info(cliCommandHelperOssmaster.getStdOut(5));
          ismomUpgraded = true;
          break;
        } else if (cliCommandHelperOssmaster.getStdOut().trim().equals("1")) {
          logger.info("MOM Upgrade failed\n");
          logger.info(cliCommandHelperOssmaster.getStdOut(5));
          ismomUpgraded = false;
          break;
        } else {
          logger.info("MOM Upgrade is still running, waiting for 1 min before trying again\n");
          Thread.sleep(60000);
        }
        attempt++;
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    finally{
      cliCommandHelperOssmaster.disconnect();
      cliCommandHelperGateway.disconnect();
    }
    return true;
  }
}
