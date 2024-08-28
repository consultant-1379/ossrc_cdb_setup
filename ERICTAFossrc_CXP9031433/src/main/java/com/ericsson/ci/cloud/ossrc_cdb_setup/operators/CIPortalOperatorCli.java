package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.Drop;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.Package;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.data.parser.JsonParser;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import groovy.util.ConfigObject;

@Operator(context = { Context.CLI })
public class CIPortalOperatorCli implements CIPortalOperator {
  private static final Host hostGateway = DataHandler.getHostByType(HostType.GATEWAY);
  private static final CLICommandHelper cliCommandHelperGateway = new CLICommandHelper(hostGateway, hostGateway.getUsers(UserType.ADMIN).get(0));
  private static final Logger logger = Logger.getLogger(CIPortalOperatorCli.class);

  @Override
    public List<String> getDropList(String product) {
    // TODO Auto-generated method stub
    List<String> dropsVersionList = null;
    String out = cliCommandHelperGateway.simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/dropsInProduct/.json/?products="+ product + "&pretty=true\"");
    out = out.replace(product + ":", "");
    out = "[" + out + "]";
    logger.debug(out);
    JsonParser parser = new JsonParser();
    List<ConfigObject> configObjects = parser.parse(out);
    logger.debug(configObjects.size());
    ConfigObject dropObject = configObjects.get(0);
    logger.info(dropObject.get("Drops"));
    dropsVersionList = (List<String>) dropObject.get("Drops");
    //dropsVersionList.add("17.0.3");
    cliCommandHelperGateway.disconnect();
    return dropsVersionList;
  }

  @Override
    public void populateDrop(Drop drop) {
    // TODO Auto-generated method stub
    try {
      String out = cliCommandHelperGateway.simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/getDropContents/?drop=" + drop+ "&product=OSS-RC&pretty=true\"");
      JsonParser parser = new JsonParser();
      List<ConfigObject> configObjects = parser.parse(out);
      for (ConfigObject configObject : configObjects) {
        drop.addPackageList(new Package(configObject));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    finally{
      cliCommandHelperGateway.disconnect();
    }
  }

  public List<Drop> getDrops(Drop startDrop,Drop endDrop) {
    // TODO Auto-generated method stub
    //startDrop=new Drop("14.0.1");
    //endDrop=new Drop("17.0.3");
    List<Drop> drops= new ArrayList<Drop>();
    String ossrcProduct=(String)DataHandler.getAttribute("ossrcProduct");
    List<String> dropsVersionList=getDropList(ossrcProduct);
    for (String dropsVersion : dropsVersionList) {
      Drop drop = new Drop(dropsVersion);
      if(startDrop.compareTo(drop)<=0 && endDrop.compareTo(drop) >=0 ){
        drops.add(drop);
        populateDrop(drop);
      }
    }
    Collections.sort(drops);
    logger.info("Drops :"+drops);
    return drops;
  }

  public void SynchDrop(Drop dropTosynch,List<Drop> drops) {
    System.out.println("Synching for :"+dropTosynch);
    List<Package> packagesToDeliver= new ArrayList<Package>();
    System.out.println("List of Packages that needs to be checked out");
    for (Package package1 : dropTosynch.packages) {
      if(!package1.getDeliveryDrop().equals(dropTosynch.version)){
        System.out.println(package1);
        packagesToDeliver.add(package1);
      }
    }
    List<Package> packagesToUpdate= new ArrayList<Package>();
    for (Package package1 : packagesToDeliver) {
      boolean isDeliverRequired=false;
      Drop curentPackageDrop =  new Drop(package1.getDeliveryDrop());
      Package LatestPackage=package1;
      for (Drop drop : drops) {
        if(drop.equals(dropTosynch)) continue;
        if(curentPackageDrop.compareTo(drop)<=0 || curentPackageDrop.toString().contains(dropTosynch.toString()+".")){
          int tempPackageIndex=drop.packages.indexOf(package1);
          if(tempPackageIndex==-1) continue;
          Package tempPackage=drop.packages.get(tempPackageIndex);
          if(!tempPackage.getVersion().equals(package1.getVersion())){
            System.out.println(tempPackage.getVersion()+"--------"+package1.getVersion());
            System.out.println(tempPackage);
            if(LatestPackage.compareTo(tempPackage)<0){
              LatestPackage=tempPackage;
              System.out.println(tempPackage);
            }
          }
        }
      }
      if(LatestPackage.compareTo(package1)!=0){
        packagesToUpdate.add(LatestPackage);
      }
    }
    logger.debug("Packages to be updated");
    for (Package package1 : packagesToUpdate) {
      logger.debug(package1);
    }
    logger.info(packagesToUpdate.size()+"will get synched for"+dropTosynch);
    String deliverToDropCommand=(String)DataHandler.getAttribute("deliverToDropCommand");
    for (Package package1 : packagesToUpdate) {
      String deliverToDrop = String.format(deliverToDropCommand, package1.getName(), package1.getVersion(), dropTosynch, package1.getPlatform(),package1.getType(),"jagadish.sethi@wipro.com");
      /*String out = cliCommandHelperGateway.simpleExec("wget -q -O - --no-check-certificate --post-data=\"packageName="
      + package1.getName() + "&version=" + package1.getVersion() + "&drop=" + dropTosynch
      + "&product=OSS-RC&platform=" + package1.getPlatform() + "&type=" + package1.getType()
      + "&email=jagadish.sethi@wipro.com\" https://cifwk-oss.lmera.ericsson.se/deliverToDrop/ \"");*/
      logger.info(deliverToDrop);
      String out = cliCommandHelperGateway.simpleExec(deliverToDrop);
      logger.info(out);
      cliCommandHelperGateway.disconnect();
      //break;
    }
  }

  public void SynchDrops() {
    //startDrop=14.0.1
    //endDrop=17.0.3
    String startDropString=(String)DataHandler.getAttribute("startDrop");
    String endDropString=(String)DataHandler.getAttribute("endDrop");
    Drop startDrop=new Drop(startDropString);
    Drop endDrop=new Drop(endDropString);
    List<Drop> drops=getDrops(startDrop,endDrop);
    //DropSynchList=17.0.3
    Object object=DataHandler.getAttribute("DropSynchList");
        List<String> DropSynchList=null;
        if(object instanceof List){
          DropSynchList=(List)object;
        }else {
          DropSynchList= new ArrayList<String>();
          DropSynchList.add((String)object);
        }
    for (String dropTosynch : DropSynchList) {
      for (Drop maxDrop : drops) {
        if(maxDrop.toString().trim().equals(dropTosynch.trim())){
          SynchDrop(maxDrop, drops);
          //break;
        }
      }
    }
    //Drop maxDrop=drops.get(drops.size()-1);
    //SynchDrop(maxDrop, drops);
  }
}
