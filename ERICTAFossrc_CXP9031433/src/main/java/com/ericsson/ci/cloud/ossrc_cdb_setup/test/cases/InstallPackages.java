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

public class InstallPackages extends TorTestCaseHelper implements TestCase {
	
	@Inject
	OperatorRegistry<EnvironmentSetUpOperator> operatorRegistry;
	@Inject
	OperatorRegistry<ARNESetUpOperator> ARNEOperatorRegistry;

	private ARNESetUpOperator getARNEOperator() {
		return ARNEOperatorRegistry.provide(ARNESetUpOperator.class);
	}

    private EnvironmentSetUpOperator getOperator() {
        return operatorRegistry.provide(EnvironmentSetUpOperator.class);
    }

    @Inject
    OperatorRegistry<InstallOperator> installOperatorRegistry;

    private InstallOperator getInstallOperator() {
        return installOperatorRegistry.provide(InstallOperator.class);
    }

    // This will install packages in OSSRC
    @Context(context = { Context.CLI })
    @Test(priority = 1)
    public void verifyAllPackageInstallation() {
        assertTrue(getInstallOperator().initialFileStructureSetup());
        assertTrue(getInstallOperator().packageDownload());
        assertTrue(getInstallOperator().installPackage());
        assertTrue(getInstallOperator().cleanUpTempDir());

    }

    @Context(context = { Context.CLI })
    @Test(dependsOnMethods = "verifyAllPackageInstallation")
    public void doPostInstallSteps() {
        assertTrue(getInstallOperator().postInstall());
    }
    
    @Context(context = { Context.CLI })
    @Test
    public void installOSSRCpkgFromPortal() {
        
            
        assertTrue(getInstallOperator().installLatestPackageForSimIntegration());
        assertTrue(getInstallOperator().initialFileStructureSetup());
        assertTrue(getInstallOperator().packageDownload());
        assertTrue(getInstallOperator().installPackages());
        assertTrue(getInstallOperator().generateScriptForOSSRCdelivery());
        assertTrue(getInstallOperator().cleanUpTempDir());
    }
    
    @Context(context = { Context.CLI })
    @Test
    public void deliverPkgToDrop() {
    	assertTrue(getInstallOperator().deliverSimulation());
    }
    
    @Context(context = { Context.CLI })
    @Test
    public void restartAllMCs() {
    	assertTrue(getInstallOperator().restartAllMCs());
    }
    
    @Context(context = { Context.CLI })
    @Test
    public void manageMC() {
    	assertTrue(getOperator().manageMC());
    }
    
    @Context(context = { Context.CLI })
    @Test
    public void rebootOssmaster() {
    	
    	assertTrue(getInstallOperator().rebootOssmaster());
    	
    }
    
    @Context(context = { Context.CLI })
    @Test
    public void verifyMOMUpgrade() {
    	
    	assertTrue(getInstallOperator().verifyMOMUpgrade());
    	
    }
    
    @TestId(id = "CIP-8129_Func_2")
    @Context(context = { Context.CLI })
    @Test(priority = 3)
    public void verifyNetworkPreparation() {

        setTestStep("Offline MAF");
        assertTrue(getARNEOperator().mafOffline());
        setTestStep("Offline IMS");
        assertTrue(getARNEOperator().imsOffline());
       
        boolean isPassed=getOperator().executeArneImport();
        
        setTestStep("Online MAF");
        assertTrue(getARNEOperator().mafOnline());
        setTestStep("Sleep for 2.5 min and start MAF synch");
        assertTrue(getARNEOperator().mafSynch());
        setTestStep("Online IMS");
        assertTrue(getARNEOperator().imsOnline());
        
        setTestStep("ARNE IMPORT");
        assertTrue("ARNE Import TC status",isPassed);

    }
    
    
    
    
    
    
}
