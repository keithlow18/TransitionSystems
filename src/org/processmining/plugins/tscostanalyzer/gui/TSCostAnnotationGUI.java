/*
 * Created on July. 02, 2007
 * 
 * Author: Minseok Song (c) 2006 Technische Universiteit Eindhoven, Minseok Song
 * all rights reserved
 * 
 * LICENSE WARNING: This code has been created within the realm of an STW
 * project. The question of under which license to publish this code, or whether
 * to have it published openly at all, is still unclear. Before this code can be
 * released in any form, be it binary or source code, this issue has to be
 * clarified with the STW. Please do not add this file to any build or source
 * export transferred to anybody outside the TM.IS group.
 */

package org.processmining.plugins.tscostanalyzer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.GraphSelectionModel;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.payload.PayloadTransitionSystem;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.elements.ProMGraphEdge;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
import org.processmining.plugins.tsanalyzer.StatisticsAnnotationProperty;
import org.processmining.plugins.tsanalyzer.gui.GUIPropertyInteger;
import org.processmining.plugins.tsanalyzer.gui.GUIPropertyListEnumeration;
import org.processmining.plugins.tsanalyzer.gui.GuiNotificationTarget;
import org.processmining.plugins.tscostanalyzer.CostStateAnnotation;
import org.processmining.plugins.tscostanalyzer.CostTransitionAnnotation;
import org.processmining.plugins.tscostanalyzer.CostTransitionSystemAnnotation;

import com.fluxicon.slickerbox.components.AutoFocusButton;

public class TSCostAnnotationGUI extends JPanel implements GuiNotificationTarget{

	private static final long serialVersionUID = -3036511492888928964L;

	public static String INTER = "inter";
	public static String OVER = "overall";
	
	private static final Color blueColor = new Color(56,189,255);;
	private static final Color yellowColor = new Color(250,250,157);
	private static final Color redColor = new Color(255,102,120);

	// Performance objects
	private JPanel splitPane;
	private JPanel menuPanel = new JPanel();
	private JSplitPane chartPanel;
	protected GUIPropertyInteger min = new GUIPropertyInteger("min", 60, 0, 100);
	protected GUIPropertyInteger max = new GUIPropertyInteger("max", 80, 0, 100);

	protected GUIPropertyListEnumeration colorBySort;
	
	private  CostStatisticsAnnotationPanel table;

	private PayloadTransitionSystem<?> system;
	private CostTransitionSystemAnnotation annotation;
	private ViewSpecificAttributeMap  map;
	
	private final ProMJGraphPanel graphVisPanel;
	
	//private PluginContext context;
	

	public TSCostAnnotationGUI(final PluginContext context, PayloadTransitionSystem<?> system, CostTransitionSystemAnnotation annotation/*, JGraphVisualizationPanel panel*/) {
		super();
		this.system = system;
		this.annotation = annotation;
		this.map = new ViewSpecificAttributeMap();
		//this.context = context;
		graphVisPanel = ProMJGraphVisualizer.instance().visualizeGraph(context, system, map);
		
		this.setLayout(new BorderLayout());
		this.removeAll();
		initGraphMenu();
		buildMainMenuGui();
		updateGUI();	
	}

	public void initGraphMenu() {
		table = new  CostStatisticsAnnotationPanel();
		splitPane = new JPanel(new BorderLayout());
		chartPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		initColorBySort();
		menuPanel.add(colorBySort.getPropertyPanel());
		menuPanel.add(min.getPropertyPanel());
		menuPanel.add(max.getPropertyPanel());

		JButton updateButton = new AutoFocusButton("Update");
		updateButton.setOpaque(false);
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateGUI();
			}
		});
		menuPanel.add(updateButton);
		splitPane.add(menuPanel,BorderLayout.NORTH);
		adjustScale();
	}
	
	private double getOverallTransitionMax(){
		double overallEdgeMax = 0.0;
         for (CostTransitionAnnotation tran: this.annotation.getAllTransitionAnnotations()){
			
			StatisticsAnnotationProperty prop = tran.getDuration();
			if (overallEdgeMax < getData(prop))
				overallEdgeMax = getData(prop);
		}
        return overallEdgeMax;
	}
	
	private double getOverallStateMax(){
		double overallMax = 0.0;
		for (CostStateAnnotation state: this.annotation.getAllStateAnnotations()){
			
			StatisticsAnnotationProperty prop = getMap(state);
			if (overallMax < getData(prop))
				overallMax = getData(prop);
		}

		return overallMax;
	}

	public void buildMainMenuGui() {
		this.removeAll();
		this.add(splitPane, BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
	}

	public void updateGUI() {
		adjustScale();
		splitPane.remove(chartPanel);
		
		//JGraphVisualizationPanel graphVisPanel = ProMJGraphVisualizer.getVisualizationPanel(system, map,context.getProgress());
		double scale = graphVisPanel.getScale();
		graphVisPanel.setScale(0.5 * scale);
		graphVisPanel.setScale(scale);
		
		chartPanel.setLeftComponent(graphVisPanel);
		chartPanel.setRightComponent(table);	
		int divider = chartPanel.getDividerLocation();
		chartPanel.validate();
		
		splitPane.add(chartPanel,BorderLayout.CENTER);
		
		GraphSelectionModel model = graphVisPanel.getGraph().getSelectionModel();
		model.setSelectionMode(GraphSelectionModel.SINGLE_GRAPH_SELECTION);
		model.addGraphSelectionListener(new GraphSelectionListener() {
			
				public void valueChanged(GraphSelectionEvent evt) {
				for (Object cell : evt.getCells()) {
					if (evt.isAddedCell(cell)) {
						if (cell instanceof ProMGraphCell) {							
							DirectedGraphNode node = ((ProMGraphCell) cell).getNode();
							if (node instanceof State){
								State state = (State) node;
								CostStateAnnotation stateAnnotation = annotation.getStateAnnotation(state);
								table.showStateAnnotation(stateAnnotation);
							}							
						} else {
							if (cell instanceof ProMGraphEdge) {
								DirectedGraphEdge<?,?> edge = ((ProMGraphEdge) cell).getEdge();
								if (edge instanceof Transition){
									Transition transition = (Transition) edge;
									CostTransitionAnnotation transitionAnnotation = annotation.getTransitionAnnotation(transition);
									table.showTransitionAnnotation(transitionAnnotation);
								}
							}
						}
					}
				}

			}
		});
		validate();
		chartPanel.setDividerLocation(divider);
	}

	protected void initColorBySort() {
		ArrayList<String> colorByList = new ArrayList<String>();
		for (String property: CostStateAnnotation.getNamesOfProperties()){
			colorByList.add(property);
		}
	
		colorBySort = new GUIPropertyListEnumeration("Color By:", "", colorByList, this, 150);
	}

	protected double getData(StatisticsAnnotationProperty stat) {
		return stat.getValue().doubleValue();
	}

	private StatisticsAnnotationProperty getMap(CostStateAnnotation sa) {
		
		return (StatisticsAnnotationProperty) sa.getProperty(colorBySort.getValue().toString());
	}

	private void adjustScale() {
		double overallStateMax = getOverallStateMax();
		double overallEdgeMax = getOverallTransitionMax();
			
		int minValue = min.getValue();
		int maxValue = max.getValue();
		
		double stateMinValue = overallStateMax * ( minValue / 100.0);
		double stateMaxValue = overallStateMax * ( maxValue / 100.0);
		
		double transMinValue = overallEdgeMax * (minValue / 100.0);
		double transMaxValue = overallEdgeMax * (maxValue / 100.0);
		
		for (State state : system.getNodes()) {
			String old = "<html><p align=\"center\">"+state.getAttributeMap().get(AttributeMap.LABEL).toString();
			String value = "unknown";
			CostStateAnnotation san = annotation.getStateAnnotation(state);
			if (san != null){
			
			StatisticsAnnotationProperty  property = getMap(san);
			
			NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US); 

			value =  (property != null)? "["+(n.format(getData(property))).toString()+"]":"unknown";
				
				map.putViewSpecific(state, AttributeMap.AUTOSIZE, true);
				if (getData(getMap(san)) >= 0.0) {
					double tempValue = getData(getMap(san));	
					map.putViewSpecific(state, AttributeMap.FILLCOLOR, getColor(tempValue, stateMinValue, stateMaxValue));
				}
			}
			map.putViewSpecific(state, AttributeMap.LABEL, old + "<br>"+value+"</p></html>");
		}
		
		for (Transition trans : system.getEdges()) {
				CostTransitionAnnotation trann =  annotation.getTransitionAnnotation(trans);
				String value = "unknown";
				if (trann != null) {
					double tempValue = getData(trann.getDuration());
					map.putViewSpecific(trans, AttributeMap.EDGECOLOR, getColor(tempValue, transMinValue, transMaxValue));				
			}
				map.putViewSpecific(trans, AttributeMap.TOOLTIP,  "interval = " + value);
				}
	}
	
	private Color getColor(double value, double min, double max){
		Color color = redColor;
		if (value < min) {
			color =  blueColor;
		} else if (value < max) {
			color = yellowColor;
		}	
		return color;
	}
	
	public HashSet<Transition> getAllEdgesTo(State state) {
		HashSet<Transition> s = new HashSet<Transition>();
		for (Transition t: system.getEdges()) {
			if (isInPath(t, state))
				s.add(t);
		}
		return s;
	}

	public boolean isInPath(State v1, State v2, HashSet<State> vs) {
		Iterator<Transition> it = getOutEdges(v1).iterator();
		while (it.hasNext()) {
			Transition e = it.next();
			if (e.getTarget() == v1)
				continue;
			if (vs.contains(e.getTarget()))
				return false;
			if (e.getTarget() == v2) {
				return true;
			} else {
				vs.add(v1);
				if (getOutEdges(e.getTarget()) != null && getOutEdges(e.getTarget()).size() > 0)
					if (isInPath(e.getTarget(), v2, vs))
						return true;
			}
		}
		return false;
	}

	public boolean isInPath(Transition e1, State v2) {
		if (e1.getTarget() == v2) {
			return true;
		} else {
			if (e1.getSource() != e1.getTarget() && getOutEdges(e1.getTarget()) != null
					&& getOutEdges(e1.getTarget()).size() > 0) {
				HashSet<State> vs = new HashSet<State>();
				return isInPath(e1.getTarget(), v2, vs);
			}
		}
		return false;
	}
	
	private Collection<Transition> getOutEdges(State s){
		ArrayList<Transition> result = new ArrayList<Transition>();
		for (Transition t: system.getEdges()){
			if (t.getSource() == s){
				result.add(t);
			}
		}
		return result;
	}
	
	
}
