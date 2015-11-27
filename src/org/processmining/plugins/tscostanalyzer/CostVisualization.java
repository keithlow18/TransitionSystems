package org.processmining.plugins.tscostanalyzer;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.transitionsystem.payload.PayloadTransitionSystem;
import org.processmining.plugins.tsanalyzer.annotation.TransitionSystemAnnotationConnection;
import org.processmining.plugins.tscostanalyzer.gui.TSCostAnnotationGUI;

public class CostVisualization {

	@Plugin(name = "Transition system cost annotation visualization", returnLabels = { "Visualized Transition System Cost Annotation" }, returnTypes = { JComponent.class }, parameterLabels = { "Transition System Cost Annotation to visualize" }, userAccessible = false)
	@Visualizer
	public static JComponent costVisualize(PluginContext context, CostTransitionSystemAnnotation tsa) {

		/**
		 * 1. Tries to get connected transition weights from the framework.
		 */
		ConnectionManager cm = context.getConnectionManager();
		try {
			CostTransitionSystemAnnotationConnection tsac = cm.getFirstConnection(CostTransitionSystemAnnotationConnection.class, context, tsa);
        
			PayloadTransitionSystem<?> ts = tsac.getObjectWithRole(TransitionSystemAnnotationConnection.TS);
			if (ts != null){
				return new TSCostAnnotationGUI(context,ts,tsa);
			} else {
				return new JLabel("FAILED: transition system is no longer available!");
			}
		} catch (ConnectionCannotBeObtained e) {

		}		
		/**
		 * Returns the visualization.
		 */
		return new JLabel("FAILED");
	}
}
