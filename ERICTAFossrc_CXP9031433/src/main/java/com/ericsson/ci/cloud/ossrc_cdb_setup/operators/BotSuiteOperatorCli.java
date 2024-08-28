package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;

public class BotSuiteOperatorCli implements BotSuiteOperator {

	private static final Host ossmasterhost = DataHandler.getHostByType(HostType.RC);
	private static final CLICommandHelper cliCommandHelper = new CLICommandHelper(ossmasterhost, ossmasterhost.getUsers(UserType.ADMIN).get(0));
	
	@Override
	public boolean mergeToJar() {
		// TODO Auto-generated method stub
		if(cliCommandHelper != null) {
		cliCommandHelper.execute("jar uf /home/ossrcdm/.m2/repository/com/ericsson/oss/hck/ERICTAFepchck_CXP9031153/1.0.397/ERICTAFepchck_CXP9031153-1.0.397.jar osgi.xml");
		return true;
		}
		return false;
	}

}
