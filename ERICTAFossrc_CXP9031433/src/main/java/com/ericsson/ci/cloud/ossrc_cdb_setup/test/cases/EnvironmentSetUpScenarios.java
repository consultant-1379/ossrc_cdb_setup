package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.api.ScenarioListener;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.ci.cloud.ossrc_cdb_setup.test.steps.InstallationTestSteps;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;

public class EnvironmentSetUpScenarios extends TorTestCaseHelper implements TestCase {

    @Inject
    InstallationTestSteps steps;

    @Context(context = { Context.CLI })
    @Test
    public void verifyEnvironmentInstallation() {
        TestStepFlow installationFlow;

        installationFlow = flow("CDB Setup")
                .addTestStep(annotatedMethod(steps, "install"))
                //.addTestStep(annotatedMethod(steps, "Ddc_Ddpi_Setup"))
                .addTestStep(annotatedMethod(steps, "arneImport"))
                .addTestStep(annotatedMethod(steps, "prepareUsers"))
                .addTestStep(annotatedMethod(steps, "disablePasswordExpiry"))
                .addTestStep(annotatedMethod(steps, "disablePasswordLockout"))
                .addTestStep(annotatedMethod(steps, "disablePasswordMustChange"))
                .addTestStep(annotatedMethod(steps, "removePasswordChangeHistory")).build();

        TestScenario scenario = scenario("CDB Environment Setup").addFlow(installationFlow).build();

        ScenarioListener listener = new LoggingScenarioListener();
        TestScenarioRunner runner = runner().withListener(listener).build();
        runner.start(scenario);
    }

    @Context(context = { Context.CLI })
    @Test
    public void verifyRNCDBEnvironmentInstallation() {
        TestStepFlow installationFlow;

        installationFlow = flow("RNCDB Setup")
                .addTestStep(annotatedMethod(steps, "install"))
                .addTestStep(annotatedMethod(steps, "prepareUser"))
                .addTestStep(annotatedMethod(steps, "disablePasswordExpiry"))
                .addTestStep(annotatedMethod(steps, "disablePasswordLockout"))
                .addTestStep(annotatedMethod(steps, "disablePasswordMustChange"))
                .addTestStep(annotatedMethod(steps, "removePasswordChangeHistory")).build();

        TestScenario scenario = scenario("RNCDB Environment Setup").addFlow(installationFlow).build();

        ScenarioListener listener = new LoggingScenarioListener();
        TestScenarioRunner runner = runner().withListener(listener).build();
        runner.start(scenario);
    }

/*    @Context(context = { Context.CLI })
    @Test
    public void verifyInstallationWithDdcDdpiSetup() {
        TestStepFlow installationFlow;

        installationFlow = flow("CDB Setup")
                .addTestStep(annotatedMethod(steps, "install"))
                .addTestStep(annotatedMethod(steps, "Ddc_Ddpi_Setup"))
                .addTestStep(annotatedMethod(steps, "arneImport"))
                .addTestStep(annotatedMethod(steps, "prepareUsers"))
                .addTestStep(annotatedMethod(steps, "disablePasswordExpiry"))
                .addTestStep(annotatedMethod(steps, "disablePasswordLockout"))
                .addTestStep(annotatedMethod(steps, "disablePasswordMustChange"))
                .addTestStep(annotatedMethod(steps, "removePasswordChangeHistory")).build();

        TestScenario scenario = scenario("CDB Environment and DdcDdpi Setup").addFlow(installationFlow).build();

        ScenarioListener listener = new LoggingScenarioListener();
        TestScenarioRunner runner = runner().withListener(listener).build();
        runner.start(scenario);
    } */
}
