package com.ericsson.ci.cloud.ossrc_cdb_setup.test.cases;
import javax.inject.Inject;

import org.testng.annotations.Test;

import com.ericsson.ci.cloud.ossrc_cdb_setup.operators.PortProvider;
import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;

/**
 * 
 */

/**
 * @author judah
 *
 */
public class PortTest extends TorTestCaseHelper implements TestCase {

	//PortProvider portProvider = new PortProvider();
	@Inject
    OperatorRegistry<PortProvider> operator;
	
	private PortProvider getPort(){
		return operator.provide(PortProvider.class);
	}
	@Test
	@Context(context = { Context.CLI })
	public void verifyPort() {
		getPort().port();
	}
}
