package com.ericsson.ci.cloud.ossrc_cdb_setup.test.steps;

import org.testng.annotations.Test;

import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.ARNESetUpOperatorCli;
import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.EnvironmentSetUpOperatorCli;
import com.ericsson.cifwk.taf.annotations.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static org.testng.Assert.*;

@Singleton
public class InstallationTestSteps {

    @Inject
    private EnvironmentSetUpOperatorCli operator;

    @Inject
    private ARNESetUpOperatorCli ARNEOperator;

    @TestStep(id = "install")
    public void verifyEnvironmentInstallation() {
        assertTrue(operator.executeInitialInstall());
    }

    @TestStep(id = "Offline_MAF")
    public void offlineMAF() {
        assertTrue(ARNEOperator.mafOffline());
    }

    @TestStep(id = "Offline_IMS")
    public void offlineIMS() {
        assertTrue(ARNEOperator.imsOffline());
    }

    @TestStep(id = "Online_MAF")
    public void onlineMAF() {
        assertTrue(ARNEOperator.mafOnline());
    }

    @TestStep(id = "Online_IMS")
    public void onlineIMS() {
        assertTrue(ARNEOperator.imsOnline());
    }

    @TestStep(id = "MAF_Synch")
    public void mafSynch() {
        assertTrue(ARNEOperator.mafSynch());
    }

    @TestStep(id = "arneImport")
    public void verifyNetworkPreparation() {

        offlineMAF();
        offlineIMS();
        assertTrue(operator.executeArneImport());
        onlineMAF();
        mafSynch();
        onlineIMS();

    }

    @TestStep(id = "prepareUsers")
    public void verifyPrepareUsers() {
        assertTrue(operator.prepareUsers());
    }

    @TestStep(id = "disablePasswordExpiry")
    public void verifydisablePasswordExpiry() {
        assertTrue(operator.disablePasswordExpiry());
    }

    @TestStep(id = "disablePasswordLockout")
    public void verifyDisablePasswordLockout() {
        assertTrue(operator.disablePasswordLockout());
    }

    @TestStep(id = "disablePasswordMustChange")
    public void verifyDisablePasswordMustChange() {
        assertTrue(operator.disablePasswordMustChange());
    }

    /**
     * This method is not used in any flow as of now.
     */
    @TestStep(id = "reduceMinPasswordLength")
    public void verifyReduceMinPasswordLength() {
        assertTrue(operator.reduceMinPasswordLength());
    }

    @TestStep(id = "removePasswordChangeHistory")
    public void verifyRemovePasswordChangeHistory() {
        assertTrue(operator.removePasswordChangeHistory());
    }

}
