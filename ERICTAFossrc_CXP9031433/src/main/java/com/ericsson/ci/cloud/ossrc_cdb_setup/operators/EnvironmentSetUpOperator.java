package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

public interface EnvironmentSetUpOperator {

  boolean executeInitialInstall();

  boolean executeDdcDdpiSetup();

  boolean executeDdcDdpiSol11Setup();

  boolean executeInitialUpgrade();

  boolean manageMC();

  boolean executeArneImport();

  boolean prepareUsers();

  boolean executeSimdep();

  boolean disablePasswordExpiry();

  boolean disablePasswordLockout();

  boolean disablePasswordMustChange();

  boolean removePasswordChangeHistory();

  boolean reduceMinPasswordLength();

  boolean executeNetsimRollOutConfig();

  boolean executeNetsimRollOutPart1();

  //Implementation for JIRA - CIS-43323
  boolean executeRealNodeCDBAutoDeploymentECN();

  boolean executeRealNodeCDBAutoDeploymentEDN();

  boolean executeYoulabCDBAutoDeployment();

  void updateCrontabForCPUMonitoring();
  
////Implementation for JIRA - CIS-49664
  boolean executeNetsimVMMemory();
  
////Implementation for JIRA - CIS-63304
  boolean executeNetsimRestart();
  
//Implementation for JIRA - CIS-64665//
  boolean sleepTimeafterUpgarde();

}
