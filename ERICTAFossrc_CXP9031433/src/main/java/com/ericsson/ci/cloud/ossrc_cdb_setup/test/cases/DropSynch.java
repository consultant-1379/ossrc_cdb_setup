package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.data.parser.JsonParser;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.cifwk.taf.handlers.RemoteFileHandler;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;

import groovy.util.ConfigObject;

import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.CIPortalOperatorCli;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.CronOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.InstallOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.DMCronValue;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.Drop;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.Package;

public class DropSynch extends TorTestCaseHelper implements TestCase {

	
	/*@Context(context = { Context.CLI })
    @Test(priority = 1)
    public void SynchReport() {
    	String out = null;
    	
    	out = cliCommandHelperGateway
				.simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/dropsInProduct/.json/?products=OSS-RC&pretty=true\"");
    	
    	out=out.replace("OSS-RC:", "");
    	out="["+out+"]";
    	
    	System.out.println(out);
    	
    	JsonParser parser = new JsonParser();
		List<ConfigObject> configObjects = parser.parse(out);

		System.out.println(configObjects.size());
		
		ConfigObject dropObject=configObjects.get(0);
		
       	System.out.println(dropObject.get("Drops"));
       	
       	List<String> dropsVersionList=(List<String>)dropObject.get("Drops");
       	dropsVersionList.add("17.0.3");
       
       	
       	List<Drop> drops= new ArrayList<Drop>();
       	
       	//Drop startDrop=new Drop("16.2.6");
       	Drop startDrop=new Drop("14.0.1");
       	Drop endDrop=new Drop("17.0.3");
       	
		for (String dropsVersion : dropsVersionList) {
			Drop drop = new Drop(dropsVersion);
			if(startDrop.compareTo(drop)<=0 && endDrop.compareTo(drop) >=0 ){
			drops.add(drop);
			
			try {
				out = cliCommandHelperGateway
						.simpleExec("curl --request GET \"https://cifwk-oss.lmera.ericsson.se/getDropContents/?drop="
								+ drop + "&product=OSS-RC&pretty=true\"");

				//System.out.println(out);

				parser = new JsonParser();
				configObjects = parser.parse(out);

				for (ConfigObject configObject : configObjects) {

					drop.addPackageList(new Package(configObject));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			}

		}
       	
      
		Collections.sort(drops);
       	
		
		for (Drop drop : drops) {
			System.out.println(drop);
		}
		
		Drop maxDrop=drops.get(drops.size()-1);
		System.out.println("Synching for :"+maxDrop);
		List<Package> packagesToDeliver= new ArrayList<Package>();
		System.out.println("List of Packages that needs to be checked out");
		for (Package package1 : maxDrop.packages) {
			if(!package1.getDeliveryDrop().equals(maxDrop.version)){
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
				if(drop.equals(maxDrop)) continue;
				
				if(curentPackageDrop.compareTo(drop)<=0){
					//System.out.println(package1+"We need to check package in :"+drop);
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
		
		System.out.println("Packages to be updated");
		for (Package package1 : packagesToUpdate) {
			System.out.println(package1);
		}
		
		System.out.println(packagesToUpdate.size()+"will get synched for"+maxDrop);
		
		String deliverToDropCommand=(String)DataHandler.getAttribute("deliverToDropCommand");
		
		for (Package package1 : packagesToUpdate) {

			out = cliCommandHelperGateway.simpleExec("wget -q -O - --no-check-certificate --post-data=\"packageName="
					+ package1.getName() + "&version=" + package1.getVersion() + "&drop=" + maxDrop
					+ "&product=OSS-RC&platform=" + package1.getPlatform() + "&type=" + package1.getType()
					+ "&email=jagadish.sethi@wipro.com\" https://cifwk-oss.lmera.ericsson.se/deliverToDrop/ || echo \"wget deliver to OSS-RC Drop failed\"");

			System.out.println(out);
			break;
		}
		
	
    }*/
	
	@Context(context = { Context.CLI })
    @Test(priority = 1)
    public void SynchDrops() {
		CIPortalOperatorCli cli = new CIPortalOperatorCli();
		cli.SynchDrops();
	}

    
}


