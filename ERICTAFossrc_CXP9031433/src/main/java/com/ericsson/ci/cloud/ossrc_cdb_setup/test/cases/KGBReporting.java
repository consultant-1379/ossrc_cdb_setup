package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;


import org.testng.annotations.Test;

import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.KGBStatusManager;
import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;

public class KGBReporting extends TorTestCaseHelper implements TestCase {

    @Test(priority = 1)
    public void prepareAndSendKGBReport() {
    	
    	KGBStatusManager kgbStatusManager = new KGBStatusManager();
    	
    	
    	
    	kgbStatusManager.prepareAndSendKGBReport();

    	
  	   	
    }
	

    
}