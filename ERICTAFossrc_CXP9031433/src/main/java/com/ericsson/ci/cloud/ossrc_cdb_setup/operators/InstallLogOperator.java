package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

import java.util.List;

import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.InstallStep;

public interface InstallLogOperator {

	List<InstallStep> getInstallStepsInfo();

}
