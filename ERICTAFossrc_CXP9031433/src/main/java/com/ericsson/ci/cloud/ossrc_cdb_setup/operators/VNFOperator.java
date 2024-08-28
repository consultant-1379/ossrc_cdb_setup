package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

public interface VNFOperator {

    boolean initialFileStructureSetup();

    boolean packageDownload();

    boolean installOrUpgradeRPM();
    
    boolean cleanUpTempDir();
    
    boolean collectRPMsDataFromDrop(); 
   

}
