package org.processmining.plugins.tscostanalyzer;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;

public class CostStatistics {
	
	protected HashMap<State, StateStatistics> states;
	protected HashMap<Transition, TransitionStatistics> transitions;
	
	public CostStatistics()	{
		super();
		states = new HashMap<State, StateStatistics>();
		transitions = new HashMap<Transition, TransitionStatistics>();
	}
	
	public Iterable<Entry<State, StateStatistics>> getStates(){
		return states.entrySet();
	}
	
	public Iterable<Entry<Transition, TransitionStatistics>> getTransitions(){
		return transitions.entrySet();
	}

	public StateStatistics getStatistics(State state){
		StateStatistics statistics = states.get(state);
		if (statistics == null){
			statistics = new StateStatistics();
			states.put(state, statistics);
		}
		return statistics;
	}
	
	public TransitionStatistics getStatistics(Transition transition){
		TransitionStatistics statistics = transitions.get(transition);
		if (statistics == null){
			statistics = new TransitionStatistics();
			transitions.put(transition, statistics);
		}
		return statistics;
	}
	
	class StateStatistics{
		
		private final DescriptiveStatistics current;
	    private final DescriptiveStatistics remaining;
		private final DescriptiveStatistics consumed;
		
		public StateStatistics() {
			super();
			current = new DescriptiveStatistics();
			remaining = new  DescriptiveStatistics();
			consumed = new  DescriptiveStatistics();
		}	

		public DescriptiveStatistics getCurrent(){
			return current;
		}
		
		public DescriptiveStatistics getRemaining(){
			return remaining;
		}
		
		public DescriptiveStatistics getConsumed(){
			return consumed;
		}
	}
	
	class TransitionStatistics{
		private DescriptiveStatistics amount;
		
		public TransitionStatistics() {
			super();
			amount = new DescriptiveStatistics();
		}
		
		public DescriptiveStatistics getAmount(){
			return amount;
		}
	}
}
