package org.processmining.plugins.tscostanalyzer;

import java.util.ArrayList;
import java.util.Collection;

import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.plugins.tsanalyzer.StatisticsAnnotationProperty;
import org.processmining.plugins.tsanalyzer.annotation.StateAnnotation;

public class CostStateAnnotation extends StateAnnotation{
	private static final String CURRENT = "current";
	private static final String CONSUMED = "consumed";
	private static final String REMAINING = "remaining";
	
	public CostStateAnnotation(State state) {
		super(state);
		
		/* TODO use switch/case...
		if (costtype = something)
			allocate the appropriate stat annotate prop
			
			addProperty(CURRENT, costType, new StatisticsAnnotationProperty());
			===========================
			get all the costType strings
			amend the costtype strings to the timing
			add property like normal
		
		*/
		
		addProperty(CURRENT, new StatisticsAnnotationProperty());
		addProperty(CONSUMED, new StatisticsAnnotationProperty());
		addProperty(REMAINING, new StatisticsAnnotationProperty());
		//TODO-create a function that detects the cost type, and instantiate a class whenever there is a new CT
		//create multiple cost types for each property
	}
	
	private StatisticsAnnotationProperty getCostAnnotationProperty(String name){
		return (StatisticsAnnotationProperty) getProperty(name);
	}
	
	public StatisticsAnnotationProperty getCurrent(){
		return getCostAnnotationProperty(CURRENT);
	}
	
	public StatisticsAnnotationProperty getConsumed(){
		return getCostAnnotationProperty(CONSUMED);
	}
	
	public StatisticsAnnotationProperty getRemaining(){
		return getCostAnnotationProperty(REMAINING);
	}	
	
	public static Iterable<String> getNamesOfProperties(){
		Collection<String> temp = new ArrayList<String>();
		temp.add(CURRENT);
		temp.add(CONSUMED);
		temp.add(REMAINING);
		return temp;
	}
}
