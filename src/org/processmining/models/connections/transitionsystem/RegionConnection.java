package org.processmining.models.connections.transitionsystem;

import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystem;
import org.processmining.models.graphbased.directed.transitionsystem.regions.RegionSet;

public class RegionConnection extends AbstractConnection {

	public final static String TS = "TS";
	public final static String REGIONS = "Region";

	public RegionConnection(TransitionSystem ts, RegionSet rs) {
		super("Region of: " + ts.getLabel());
		super.put(TS, ts);
		super.put(REGIONS, rs);

	}

}
