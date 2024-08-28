package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.ARNESetUpOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.EnvironmentSetUpOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.EnvironmentSetUpOperatorCli;


public class EnvironmentUpgradeAndSetUp extends TorTestCaseHelper implements TestCase {

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

    @Context(context = { Context.CLI })
    @Test(priority = 1)
    public void verifyEnvironmentUpgrade() {
    	assertTrue(getOperator().executeInitialUpgrade());
    }
    
    @Context(context = { Context.CLI })
    @Test(priority = 2, dependsOnMethods = { "verifyEnvironmentUpgrade" })
    public void verifyMCInitialisation() {
    	assertTrue(getOperator().manageMC());
    }
    
    @Context(context = { Context.CLI })
    @Test(priority = 3, dependsOnMethods = { "verifyMCInitialisation" })
    public void verifyNetworkPreparation() {
    	assertTrue(getOperator().executeNetsimRollOutConfig());
    	assertTrue(getOperator().sleepTimeafterUpgarde());
    	
    }
    
    @Context(context = { Context.CLI })
    @Test(priority = 5)
    public void showNodeSynchStatus() {

        setTestStep("Show Node Synch Status");
        getARNEOperator().getNodeSynchStatus(10);
        assertTrue(true);

    }
    
    @Context(context = { Context.CLI })
    @Test(priority = 6)
    public void executeNetsimRollOutPart1() {
    	
    	setTestStep("Delete Simulation zip files present at simdir");
    	getARNEOperator().deleteSimulationZips();
        setTestStep("executeNetsimRollOutPart1");
        assertTrue(getOperator().executeNetsimRollOutPart1());
        
        }

    
    @Context(context = { Context.CLI })
    @Test(priority = 7)
    public void executeSimdep() {

        setTestStep("executeSimdep");
        assertTrue(getOperator().executeSimdep());
     }

}
