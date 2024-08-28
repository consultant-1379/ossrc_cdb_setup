package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

public interface InstallOperator {

    boolean initialFileStructureSetup();

    boolean packageDownload();

    boolean installPackage();
    
    boolean installPackages();
    
    boolean restartAllMCs();
    
    boolean rebootOssmaster();
    
    boolean cleanUpTempDir();

    boolean postInstall();
    
    boolean installLatestPackageForSimIntegration();
    
    boolean generateScriptForOSSRCdelivery();
    
    boolean verifyMOMUpgrade();
    
    boolean deliverSimulation();
    
    void saveDeliveryContentToFile(String simulationPackages);    	

}
