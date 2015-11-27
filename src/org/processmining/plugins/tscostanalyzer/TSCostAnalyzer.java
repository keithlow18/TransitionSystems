package org.processmining.plugins.tscostanalyzer;

import java.util.Map.Entry;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.deckfour.xes.extension.std.XCostExtension;
import org.deckfour.xes.extension.std.XExtendedEvent;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.payload.event.EventPayloadTransitionSystem;
import org.processmining.plugins.transitionsystem.miner.util.TSEventCache;
import org.processmining.plugins.tsanalyzer.StatisticsAnnotationProperty;
//import org.processmining.plugins.tscostanalyzer.costextension.XCostExtension;

public class TSCostAnalyzer {

	/**
	 * The context of this miner.
	 */
	private final PluginContext context;

	/**
	 * the transition system to annotate
	 */
	private final EventPayloadTransitionSystem transitionSystem;

	/**
	 * eventCache is used for efficient access to events in XTrace
	 */
	private final TSEventCache eventCache;

	/**
	 * statistics for storing necessary time values
	 */
	private final CostStatistics statistics;

	/**
	 * the log to be used to annotate the transition system
	 */
	private final XLog log;

	/**
	 * transition system annotation
	 */
	private final CostTransitionSystemAnnotation annotation;

	public TSCostAnalyzer(final PluginContext context, final EventPayloadTransitionSystem ts, final XLog log) {
		super();
		this.context = context;
		transitionSystem = ts;
		this.log = log;
		eventCache = new TSEventCache();
		statistics = new CostStatistics();
		annotation = new CostTransitionSystemAnnotation();
	}

	/**
	 * This method creates the annotation for
	 * 
	 * @param log
	 * @return
	 */
	public CostTransitionSystemAnnotation annotate() {

		context.getProgress().setMinimum(0);
		context.getProgress().setMaximum(log.size());
		/**
		 * For every trace a tick on the progress bar, and an extra tick for the
		 * modification phase.
		 */
		context.getProgress().setCaption("Annotating transition system with cost");
		context.getProgress().setIndeterminate(false);

		// And now for the real thing.
		for (XTrace pi : log) {
			/**
			 * get the total cost of this instance (trace)
			 */
			//double instanceCost = getProcessInstanceCost(pi);
			//String costType = "PC";
			//double startingTraceCost = 0.00;
			double endTraceCost = getEndTraceCost(pi);
			double currentCost = 0.00;
			if (endTraceCost != -1) {
				/**
				 * process one instance (trace)
				 */
				for (int i = 0; i < pi.size(); i++) {
					currentCost = processEvent(pi, i, endTraceCost, currentCost);
				}
				/**
				 * increase the progress bar
				 */
				context.getProgress().inc();
			}
		}
		/**
		 * create annotations from collected statistics
		 */
		createAnnotationsFromStatistics();
		return annotation;
	}

	/**
	 * This method processes one event from a trace by collecting all necessary
	 * statistical time (i.e., elapsed, remaining, etc. times) data.
	 * 
	 * @param pi
	 *            the trace to which the event belongs
	 * @param i
	 *            the index of the event to be processed
	 * @param instanceCost
	 *            the overall cost of this trace
	 * @param currentCost
	 *            the current cost
	 * @return the current cost so far of the trace  
	 * 			         
	 */
	private double processEvent(final XTrace pi, final int i, final double endTraceCost, double currentCost) {
		/**
		 * Get the cost of the event we are processing
		 * event.getAttributes().values();
						logCurrency = ((XAttributeLiteral) attribute).getValue();
		 */
		Double activityCost = getEventCost(pi, i);
		/*
		if() {
			XEvent event = (XEvent) pi.getAttributes();
		}
		//String costType = getCostType(pi, i);
		System.out.println("CT2-" + activityCost.toString());
		*/
		System.out.println("CT2-" + pi.get(1).getAttributes().values().toString());
		if (activityCost != null)
		{
			currentCost = currentCost + activityCost;
			/**
			 * Get the transition that corresponds to this event. Also get the
			 * source and the target for this transition.
			 */
			Transition transition = transitionSystem.getTransition(pi, i);
			if (transition != null) {
				State source = transition.getSource();
				State target = transition.getTarget();
				
				/**
				 * create statistics for the transition, source and target
				 */
				CostStatistics.TransitionStatistics transitionStatistics = statistics.getStatistics(transition);
				CostStatistics.StateStatistics sourceStatistics = statistics.getStatistics(source);
				CostStatistics.StateStatistics targetStatistics = statistics.getStatistics(target);

				/**
				 * annotate the source only for the first event in the trace
				 */
				if (i == 0) {
					sourceStatistics.getRemaining().addValue(endTraceCost - activityCost);
					sourceStatistics.getConsumed().addValue(0.0);
					sourceStatistics.getCurrent().addValue(0.0);
				}

				/**
				 * annotate target with elapsed and remaining time
				 */
				targetStatistics.getConsumed().addValue(currentCost);
				targetStatistics.getRemaining().addValue(endTraceCost - currentCost);
				targetStatistics.getCurrent().addValue(activityCost);

				/**
				 * annotate the transition with cost
				 */
				if (i == 0)
				{
					transitionStatistics.getAmount().addValue(0.0);
				}
				else
				{
					transitionStatistics.getAmount().addValue(activityCost);
				}
			}
		}
		return currentCost;
}
	/*
	private void printValues(String name, DescriptiveStatistics st){
		System.out.println(name + "\n" + st);
	}
	*/
	/**
	 * Goes through all gathered statistics for each state and transition and
	 * creates annotations from the statistics.
	 * 
	 * @return the annotation of the transition system
	 */
	private void createAnnotationsFromStatistics() {
		/**
		 * create annotation for each state
		 */
		for (Entry<State, CostStatistics.StateStatistics> entry : statistics.getStates()) {
			//printValues("consumed",entry.getValue().getConsumed() );
			//printValues("remainian",entry.getValue().getRemaining() );
			//printValues("current",entry.getValue().getCurrent() );
			CostStateAnnotation stateAnnotation = getStateAnnotation(entry.getKey());
			annotateState(stateAnnotation, entry.getValue());
		}
		/**
		 * create annotation for each transition
		 */
		for (Entry<Transition, CostStatistics.TransitionStatistics> entry : statistics.getTransitions()) {
			CostTransitionAnnotation transitionAnnotation = getTransitionAnnotation(entry.getKey());
			annotateTransition(transitionAnnotation, entry.getValue());
		}
	}

	/**
	 * Create state annotation from its statistics (i.e., elapsed, remaining and
	 * soujourn time).
	 * 
	 * @param state
	 *            to be annotated
	 * @param statistics
	 *            collected for this state
	 */
	private void annotateState(CostStateAnnotation stateAnnotation, CostStatistics.StateStatistics statistics) {
		annotateStatisticsProperty(stateAnnotation.getCurrent(), statistics.getCurrent());
		annotateStatisticsProperty(stateAnnotation.getRemaining(), statistics.getRemaining());
		annotateStatisticsProperty(stateAnnotation.getConsumed(), statistics.getConsumed());
	}

	/**
	 * Create transition annotation from its statistics (i.e., duration time).
	 * 
	 * @param transitionAnnotation
	 * @param statistics
	 */
	private void annotateTransition(CostTransitionAnnotation transitionAnnotation, CostStatistics.TransitionStatistics statistics) {
		annotateStatisticsProperty(transitionAnnotation.getDuration(), statistics.getAmount());
	}

	/**
	 * Creates annotation form one statistics property (i.e, average, standard
	 * deviation, variance, etc.)
	 * 
	 * @param prop
	 *            annotation to be created
	 * @param stat
	 *            statistics with time values
	 */
	private void annotateStatisticsProperty(StatisticsAnnotationProperty prop, DescriptiveStatistics stat) {
		prop.setValue(new Double(stat.getMean()));
		prop.setAverage(stat.getMean());
		prop.setStandardDeviation(stat.getStandardDeviation());
		prop.setMin(stat.getMin());
		prop.setMax(stat.getMax());
		prop.setSum(stat.getSum());
		prop.setVariance(stat.getVariance());
		prop.setFrequencey(stat.getN());
		prop.setMedian(stat.getPercentile(50));
	}

	/**
	 * Returns the annotation object for the given state. If the annotation
	 * object for the state does not exist, a new annotation object is created
	 * for the state and returned.
	 * 
	 * @param state
	 *            for which to find the annotation
	 * @return time annotation for the state
	 */
	private CostStateAnnotation getStateAnnotation(State state) {
		CostStateAnnotation stateAnnotation = annotation.getStateAnnotation(state);
		if (stateAnnotation == null) {
			stateAnnotation = new CostStateAnnotation(state);
			annotation.addStateAnnotation(stateAnnotation);
		}
		return stateAnnotation;
	}

	/**
	 * Returns the annotation object for the given transition. If the annotation
	 * object for the transition does not exist, a new annotation object is
	 * created for the transition and returned.
	 * 
	 * @param transition
	 *            for which to find the annotation object
	 * @return time annotation for the transition object
	 */
	private CostTransitionAnnotation getTransitionAnnotation(Transition transition) {
		CostTransitionAnnotation transitionAnnotation = annotation.getTransitionAnnotation(transition);
		if (transitionAnnotation == null) {
			transitionAnnotation = new CostTransitionAnnotation(transition);
			annotation.addTransitionAnnotation(transitionAnnotation);
		}
		return transitionAnnotation;
	}
	
	/**
	 * Gets the total cost of a trace.
	 * 
	 * @param pi
	 *            the trace
	 * @return the cost of the trace
	 */
	/*
	private double getProcessInstanceCost(XTrace pi) {
		try {
			//if (attribute.getKey().startsWith("cost:currency"))
			//	strLogCurrency = ((XAttributeLiteral) attribute).getValue();
			XAttribute attribute = pi.getAttributes().get("cost:amount");
			//XAttribute attribute = pi.getAttributes().get(key);
			attribute.getKey().startsWith("org:resource");
			System.out.println("attribute" + attribute.toString());
			
			if (attribute != null)
			{   
				String costString = ((XAttributeLiteral) attribute).getValue();
			    double instanceCost = Double.parseDouble(costString);
			    System.out.println("cost:amount" + instanceCost);
				return instanceCost;
			
			}
			
			}
		catch (Exception ce) {
			//System.out.println(ce.getMessage());
		}
		//return -1;
		return 0;
	}
	*/
	
	/**
	 * Converts the i-the element of a trace into XExtendedEvent.
	 * 
	 * @param pi
	 *            the trace
	 * @return it-th element of the trace as XExtendedEvent
	 */
	@SuppressWarnings("unused")
	private XExtendedEvent getExtendedEvent(XTrace trace, int index) {
		return XExtendedEvent.wrap(eventCache.get(trace, index));
	}
	
/*	private long getEventCost(XTrace pi, int index) {
		try {
			
			XEvent event = pi.get(index);
			XAttribute attribute = event.getAttributes().get("cost:amount");
			if (attribute != null)
			{  
				
				String costString = ((XAttributeLiteral) attribute).getValue();
			    long eventCost = Long.parseLong(costString);
			    System.out.println("cost:amount" + eventCost);
			
					return eventCost;
				
			}
			}
		catch (Exception ce) {
			System.out.println(ce.toString());
		}
		return 0;
	}
	*/
	private Double getEventCost(XTrace pi, int index) {
		try {
			XEvent event = pi.get(index);
			Double costamount = XCostExtension.instance().extractTotal(event);
			//Double costamount = XCostExtension.instance().extractCostAmount(event);
			if (costamount != null)
			{   
				return costamount.doubleValue();
			}
		}
		catch (Exception ce) {
			//System.out.println(ce.toString());
			return 0.00;
		}
		return 0.00;
	}
	/*
	private String getCostType(XTrace pi, int index) {
		try {
			XEvent event = pi.get(index);
			//extractTypes(event);
			//String costType = XCostExtension.instance().extractCostType(event);
			//String costType = XCostExtension.instance().extractType("cost:type");
			
			if (costType != "")
			{   
				//System.out.println("CT-" + costType.toString());
				return costType;
			}
		}
		catch (Exception ce) {
			//System.out.println(ce.toString());
			return "";
		}
		return "";
	}
	*/
	private Double getEndTraceCost (XTrace pi) {
		double totalEndTraceCost = 0.00;
		
		for (int i = 0; i < pi.size(); i++) {
			double currentCost = 0;
			
			currentCost = processEvent(pi, i, 0, 0);
			
			totalEndTraceCost += currentCost;
		}
		//System.out.println("totalEndTraceCost-" + totalEndTraceCost);
		return totalEndTraceCost;
	}
	
	
	
}
