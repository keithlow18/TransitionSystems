package org.processmining.models.graphbased.directed.transitionsystem.regions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystem;

public class RegionImpl extends LinkedHashSet<State> implements Region {

	private static final long serialVersionUID = 6861407982355641952L;

	private final Set<Object> entering = new LinkedHashSet<Object>();
	private final Set<Object> exiting = new LinkedHashSet<Object>();
	private final Set<Object> internal = new LinkedHashSet<Object>();
	private final Set<Object> external = new LinkedHashSet<Object>();

	private boolean dis_ent_ex;

	private boolean dis_ent_int;

	private boolean dis_ent_ext;

	private boolean dis_ex_ext;

	private boolean dis_ex_int;

	public Set<Object> getEntering() {
		return entering;
	}

	public Set<Object> getExiting() {
		return exiting;
	}

	public Set<Object> getInternal() {
		return internal;
	}

	public Set<Object> getExternal() {
		return external;
	}

	public boolean isValidRegion() {
		return (isDis_ent_ex() && isDis_ent_int() && isDis_ent_ext() && isDis_ex_int() && isDis_ex_ext());

	}

	public void initialize(TransitionSystem ts) {
		// Check if toExpand is a region.
		// First, find the getEntering(), exiting and notcrossing identifiers
		for (Transition t : ts.getEdges()) {
			boolean sc = contains(t.getSource());
			boolean tc = contains(t.getTarget());
			if (!sc && tc) {
				// getEntering()
				getEntering().add(t.getIdentifier());
			} else if (sc && !tc) {
				// exiting
				getExiting().add(t.getIdentifier());
			} else if (sc && tc) {
				// not crossing and internal
				getInternal().add(t.getIdentifier());
			} else {
				// not crossing
				getExternal().add(t.getIdentifier());
			}
		}

		// now check for mutual exclusion
		dis_ent_ex = Collections.disjoint(getEntering(), getExiting());

		dis_ent_int = Collections.disjoint(getEntering(), getInternal());
		dis_ent_ext = Collections.disjoint(getEntering(), getExternal());

		dis_ex_int = Collections.disjoint(getExiting(), getInternal());
		dis_ex_ext = Collections.disjoint(getExiting(), getExternal());

	}

	public boolean isDis_ent_ex() {
		return dis_ent_ex;
	}

	public boolean isDis_ent_int() {
		return dis_ent_int;
	}

	public boolean isDis_ent_ext() {
		return dis_ent_ext;
	}

	public boolean isDis_ex_int() {
		return dis_ex_int;
	}

	public boolean isDis_ex_ext() {
		return dis_ex_ext;
	}
}
