package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.EnvironmentSetUpOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.TestRetry;

public class DdcDdpiSetup extends TorTestCaseHelper implements TestCase {

	    @Inject
	    OperatorRegistry<EnvironmentSetUpOperator> operatorRegistry;

	    private EnvironmentSetUpOperator getOperator() {
	        return operatorRegistry.provide(EnvironmentSetUpOperator.class);
	    }

	    @TestId(id = "CIP-9163_Func_1")
	    @Context(context = { Context.CLI })
	    @Test(priority = 1)
	    public void verifyDdcDdpiSetup() {
	        assertTrue(getOperator().executeDdcDdpiSetup());
	    }

	    @TestId(id = "CIP-9163_Func_2")
	    @Context(context = { Context.CLI })
	    @Test(priority = 1)
	    public void verifyDdcDdpiSol11Setup() {
	        assertTrue(getOperator().executeDdcDdpiSol11Setup());
	    }

	}

