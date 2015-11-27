package org.processmining.plugins.tscostanalyzer.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.plugins.tsanalyzer.annotation.Annotation;
import org.processmining.plugins.tscostanalyzer.CostStateAnnotation;
import org.processmining.plugins.tscostanalyzer.CostTransitionAnnotation;

public class CostStatisticsAnnotationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final CostStatisticsAnnotationTable table;

	public CostStatisticsAnnotationPanel() {
		super(new BorderLayout());
		table = new CostStatisticsAnnotationTable();
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void showStateAnnotation(CostStateAnnotation annotation) {
		if (annotation != null) {
			setBorder(BorderFactory.createTitledBorder(getString(annotation.getState())));
			showAnnotation(annotation);
		} else {
			setBorder(BorderFactory.createTitledBorder("no annotations"));
			table.clear();
			validate();
		}
	}

	public void showTransitionAnnotation(CostTransitionAnnotation annotation) {
		if (annotation != null) {
			setBorder(BorderFactory.createTitledBorder(getString(annotation.getTransition())));
			showAnnotation(annotation);
		} else {
			setBorder(BorderFactory.createTitledBorder("no annotations"));
			table.clear();
			validate();
		}
	}

	private void showAnnotation(Annotation annotation) {
		table.setAnnotation(annotation);
		validate();
	}

	private String getString(State state) {
		String result = "state ";
		if (state != null) {
			result += state.getLabel();
		} else {
			result += "unknown";
		}
		return result;
	}

	private String getString(Transition transition) {
		String result = "transition ";
		if (transition != null) {
			String source = getString(transition.getSource());
			String target = getString(transition.getTarget());
			if ((source != null) && (target != null)) {
				String label = transition.getLabel();
				if (label == null) {
					label = "unknown";
				}
				result = "[" + source + "]  " + label + "  [" + target + "]";

			}
		} else {
			result += "unknown";
		}
		return result;
	}
}
