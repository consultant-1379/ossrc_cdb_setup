package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

import javax.inject.Singleton;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.HostType;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.utils.cluster.FreePortFinder;
import com.ericsson.cifwk.taf.utils.cluster.GatewayUtility;

/**
 * 
 */

/**
 * @author judah
 * 
 */
@Singleton
@Operator(context = { Context.CLI })
public class PortProvider {

	private static Host gateway;
	private static final Logger logger = Logger.getLogger(PortProvider.class);

	private static Host getGateway() {
		if (gateway == null) {
			gateway = DataHandler.getHostByType(HostType.GATEWAY);
		}
		return gateway;
	}

	public static final int DEFAULT_START_PORT = 10_000;
	private static int lastFreePort = DEFAULT_START_PORT;
	private static final String NAT_MS_IP = "192.168.0.5";

	private static boolean requiresNat(final Host host) {
		return getGateway() != null
				&& host.getIp().equalsIgnoreCase(getGateway().getIp())
				&& !GatewayUtility.isInPrivateNetwork();
	}

	private synchronized static int getNextFreePort(final Host host) {
		if (requiresNat(host)) {
			String gwRootPass = getGateway().getUsers(UserType.ADMIN).get(0)
					.getPassword();
			lastFreePort = FreePortFinder.findFreePort(lastFreePort,
					getGateway().getIp(), gwRootPass, NAT_MS_IP);
			logger.info("Last Free Port" + lastFreePort);
		} else {
			lastFreePort = FreePortFinder.findFreePort(host.getIp(),
					lastFreePort);
			logger.info("in else block Last Free Port" + lastFreePort);
		}
		return lastFreePort++;
	}

	public int port() {
		getNextFreePort(getGateway());
		return lastFreePort;
	}
}
