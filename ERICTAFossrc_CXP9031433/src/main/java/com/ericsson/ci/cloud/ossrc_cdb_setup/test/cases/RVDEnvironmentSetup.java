package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.ARNESetUpOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.EnvironmentSetUpOperator;
import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;

public class RVDEnvironmentSetup extends TorTestCaseHelper implements TestCase {

  @Inject
    OperatorRegistry<EnvironmentSetUpOperator> operatorRegistry;

  private EnvironmentSetUpOperator getOperator() {
        return operatorRegistry.provide(EnvironmentSetUpOperator.class);
    }

  @Context(context = { Context.CLI })
    @Test
    public void verifyRVDEnvironmentSetupForECN() {
    assertTrue(getOperator().executeRealNodeCDBAutoDeploymentECN());
    }

  @Context(context = { Context.CLI })
    @Test
    public void verifyRVDEnvironmentSetupForEDN() {
    assertTrue(getOperator().executeRealNodeCDBAutoDeploymentEDN());
    }

  @Context(context = { Context.CLI })
    @Test
    public void verifyRVDEnvironmentSetupForYoulab() {
    assertTrue(getOperator().executeYoulabCDBAutoDeployment());
    }

}
