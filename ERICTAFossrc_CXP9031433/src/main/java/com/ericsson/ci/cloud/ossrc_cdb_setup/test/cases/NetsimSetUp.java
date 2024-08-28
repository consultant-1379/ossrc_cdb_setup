package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.ARNESetUpOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.EnvironmentSetUpOperator;

public class NetsimSetUp extends TorTestCaseHelper implements TestCase {

    @Inject
    OperatorRegistry<EnvironmentSetUpOperator> operatorRegistry;

    @Inject
    OperatorRegistry<ARNESetUpOperator> ARNEOperatorRegistry;

    private EnvironmentSetUpOperator getOperator() {
        return operatorRegistry.provide(EnvironmentSetUpOperator.class);
    }

    private ARNESetUpOperator getARNEOperator() {
        return ARNEOperatorRegistry.provide(ARNESetUpOperator.class);
    }

    @Context(context = { Context.CLI })
    @Test(priority = 1)
    public void verifyNetsimRollOutConfig() {
        assertTrue(getOperator().executeNetsimRollOutConfig());
    }

    @Context(context = { Context.CLI })
    @Test(priority = 2)
    public void verifyNetworkPreparation() {

        setTestStep("Offline MAF");
        assertTrue(getARNEOperator().mafOffline());
        //setTestStep("Offline IMS");
        //assertTrue(getARNEOperator().imsOffline());
        setTestStep("ARNE IMPORT");
        assertTrue(getOperator().executeArneImport());
        setTestStep("Online MAF");
        assertTrue(getARNEOperator().mafOnline());

        setTestStep("Sleep for 2.5 min and start MAF synch");
        assertTrue(getARNEOperator().mafSynch());
        //setTestStep("Online IMS");
        //assertTrue(getARNEOperator().imsOnline());
    }

    @Context(context = { Context.CLI })
    @Test(priority = 5)
    public void showNodeSynchStatus() {

        setTestStep("Show Node Synch Status");
        getARNEOperator().getNodeSynchStatus(10);
        assertTrue(true);

    }
}
