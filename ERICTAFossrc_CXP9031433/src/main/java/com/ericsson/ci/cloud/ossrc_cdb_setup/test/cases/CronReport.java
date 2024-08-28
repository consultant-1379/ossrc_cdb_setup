package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.CronOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.InstallOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.DMCronValue;

public class CronReport extends TorTestCaseHelper implements TestCase {

    @Inject
    OperatorRegistry<CronOperator> cronOperatorRegistry;

    private CronOperator getCronOperator() {
        return cronOperatorRegistry.provide(CronOperator.class);
    }

    // This will install packages in OSSRC
    @Context(context = { Context.CLI })
    @Test(priority = 1)
    public void showCronReport() {
    	
  	getCronOperator().getCronInfo();
       	
    }

    
}
