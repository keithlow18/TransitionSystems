package org.processmining.plugins.tscostanalyzer;

import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.plugins.tsanalyzer.StatisticsAnnotationProperty;
import org.processmining.plugins.tsanalyzer.annotation.TransitionAnnotation;

public class CostTransitionAnnotation extends TransitionAnnotation {
	private static final String AMOUNT = "amount";
	
	public CostTransitionAnnotation(Transition transition) {
		super(transition);
		addProperty(AMOUNT, new StatisticsAnnotationProperty());
	}
	
	public StatisticsAnnotationProperty getDuration(){
		return (StatisticsAnnotationProperty) getProperty(AMOUNT);
	}
}
