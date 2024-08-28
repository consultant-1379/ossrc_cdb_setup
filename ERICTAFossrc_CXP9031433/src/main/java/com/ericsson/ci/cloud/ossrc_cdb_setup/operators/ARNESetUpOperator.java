package com.ericsson.ci.cloud.ossrc_cdb_setup.operators;

public interface ARNESetUpOperator {

	boolean mafOffline();

	boolean mafOnline();

	boolean mafSynch();

	boolean getNodeSynchStatus(int min);

	boolean imsOffline();

	boolean imsOnline();

	boolean deleteSimulationZips();

}
