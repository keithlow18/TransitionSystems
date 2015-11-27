package org.processmining.models.graphbased.directed.transitionsystem.regions;

import java.util.Set;

import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystem;

public interface Region extends Set<State> {

	public Set<Object> getEntering();

	public Set<Object> getExiting();

	public Set<Object> getInternal();

	public Set<Object> getExternal();

	public boolean isValidRegion();

	public void initialize(TransitionSystem ts);

}
