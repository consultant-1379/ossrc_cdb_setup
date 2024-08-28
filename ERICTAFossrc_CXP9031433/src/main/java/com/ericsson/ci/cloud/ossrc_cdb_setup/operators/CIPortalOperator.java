package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

import java.util.List;

import com.ericsson.ci.cloud.ossrc_cdb_setup.test.data.Drop;

public interface CIPortalOperator {

	List<String> getDropList(String product);
	void populateDrop(Drop drop);
	void SynchDrops();

}
