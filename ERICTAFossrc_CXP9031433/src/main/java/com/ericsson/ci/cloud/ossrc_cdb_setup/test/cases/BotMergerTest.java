package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.BotSuiteOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.EnvironmentSetUpOperator;
import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;

public class BotMergerTest extends TorTestCaseHelper implements TestCase{

	@Inject
    OperatorRegistry<BotSuiteOperator> operatorRegistry;

    private BotSuiteOperator getOperator() {
        return operatorRegistry.provide(BotSuiteOperator.class);
    }
    
    @TestId(id = "CIP-9163_Func_1")
    @Context(context = { Context.CLI })
    @Test(priority = 1)
    public void verifyDdcDdpiSetup() {
        assertTrue(getOperator().mergeToJar());
    }
}
