package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.CronOperator;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.CDBInstallReport;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.KGBStatusManager;
import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;

public class CDBInstallReporting extends TorTestCaseHelper implements TestCase {

    @Test(priority = 1)
    public void prepareAndSendCDBTimelineReport() {
    	
    	CDBInstallReport cdbInstallReport = new CDBInstallReport();
    	
    	
    	
    	cdbInstallReport.prepareAndSendCDBReport();

    	
  	   	
    }

    
}