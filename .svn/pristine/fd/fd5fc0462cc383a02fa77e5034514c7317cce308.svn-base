package org.processmining.models.graphbased.directed.transitionsystem;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.connections.transitionsystem.TransitionSystemConnection;

public class InitialAndAcceptStateSearcher {

	@Plugin(name = "Start and Accept State Searcher", returnTypes = { StartStateSet.class, AcceptStateSet.class }, returnLabels = {
			"States without input", "States without output" }, userAccessible = false, mostSignificantResult = 1, parameterLabels = "Transition System")
	public Object[] findAcceptingStates(PluginContext context, TransitionSystem ts) {
		AcceptStateSet accept = new AcceptStateSet();
		StartStateSet start = new StartStateSet();

		for (State s : ts.getNodes()) {
			if (ts.getOutEdges(s).isEmpty()) {
				// Add all states without outgoing arcs.
				accept.add(s.getIdentifier());
			}
			if (ts.getInEdges(s).isEmpty()) {
				// Add all states without incoming arcs.
				start.add(s.getIdentifier());
			}
		}

		context.getConnectionManager().addConnection(new TransitionSystemConnection(ts, start, accept));
		context.getFutureResult(0).setLabel("Initial States of " + ts.getLabel());
		context.getFutureResult(1).setLabel("Accepting States of " + ts.getLabel());

		return new Object[] { start, accept };
	}
}
