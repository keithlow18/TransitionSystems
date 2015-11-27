package org.processmining.models.connections.transitionsystem;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystem;
import org.processmining.models.graphbased.directed.transitionsystem.regions.GeneralizedExitationRegions;

public class GeneralizedExitationRegionConnection extends AbstractConnection {

	public final static String TS = "TS";
	public final static String GERS = "Generalized Excitation Region";

	public GeneralizedExitationRegionConnection(TransitionSystem ts, GeneralizedExitationRegions r) {
		super("GERs of: " + ts.getLabel());
		super.put(TS, ts);
		super.put(GERS, r);

	}

}
