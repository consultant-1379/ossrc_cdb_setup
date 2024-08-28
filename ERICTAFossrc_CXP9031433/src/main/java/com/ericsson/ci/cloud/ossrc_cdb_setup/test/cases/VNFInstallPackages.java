package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;

import java.util.List;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.ARNESetUpOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.EnvironmentSetUpOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.InstallOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.VNFOperator;

public class VNFInstallPackages extends TorTestCaseHelper implements TestCase {
	
	@Inject
	OperatorRegistry<VNFOperator> operatorRegistry;
	
	private VNFOperator getVNFOperator() {
		return operatorRegistry.provide(VNFOperator.class);
	}

    // This will install packages in VNF package for KGB
    @Context(context = { Context.CLI })
    @Test(priority = 1)
    public void installOrUpgradeVNFRPM() {
        assertTrue(getVNFOperator().initialFileStructureSetup());
    	assertTrue(getVNFOperator().packageDownload());
    	assertTrue(getVNFOperator().installOrUpgradeRPM());
    	assertTrue(getVNFOperator().cleanUpTempDir());	
    }
    
    
    @Context(context = { Context.CLI })
    @Test(priority = 1)
    public void installOrUpgradeVNFRPMsFromDrop() {
        assertTrue(getVNFOperator().initialFileStructureSetup());
        assertTrue(getVNFOperator().collectRPMsDataFromDrop());
    	assertTrue(getVNFOperator().packageDownload());
    	assertTrue(getVNFOperator().installOrUpgradeRPM());
    	assertTrue(getVNFOperator().cleanUpTempDir());	
    }

    
    
}
