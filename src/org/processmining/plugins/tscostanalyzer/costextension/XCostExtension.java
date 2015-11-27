/*
 
 */

package org.processmining.plugins.tscostanalyzer.costextension;

import java.net.URI;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;

/**
 * This extension defines the Cost perspective on event logs.
 * It makes it possible to assign to each event different costs associated with the event.
 * 
 * @author Moe Wynn (m.wynn@qut.edu.au)
 *
 */
@SuppressWarnings("serial")
public class XCostExtension extends XExtension {
	
	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI.create("http://www.yawlfoundation.org/yawlschema/xes/cost.xesext");
	/**
	 * Key for the cost total attribute.
	 * COSTTOTAL attribute prototype.
	 */
	public static final String KEY_COSTTOTAL = "cost:total";
	public static final String KEY_COSTAMOUNT = "cost:amount";
	public static final String KEY_COSTTYPE = "cost:type";
	public static final String KEY_COSTDRIVER = "cost:driver";
	public static final String KEY_COSTCURRENCY = "cost:currency";
	
	public static XAttributeContinuous ATTR_COSTTOTAL, ATTR_COSTAMOUNT;
	public static XAttributeLiteral ATTR_COSTTYPE, ATTR_COSTDRIVER, ATTR_COSTCURRENCY;

	private static XCostExtension singleton = new XCostExtension();
	
	/**
	 * Provides access to the singleton instance of this extension.
	 * @return The Cost extension singleton.
	 */
	public static XCostExtension instance() {
		return singleton;
	}
	
	/**
	 * Creates a new instance (hidden constructor).
	 */
	private XCostExtension() {
		super("Cost", "cost", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		ATTR_COSTTOTAL  = factory.createAttributeContinuous(KEY_COSTTOTAL, 0.0, this);
		this.eventAttributes.add((XAttribute)ATTR_COSTTOTAL.clone());
		ATTR_COSTAMOUNT  = factory.createAttributeContinuous(KEY_COSTAMOUNT, 0.0, this);
		this.eventAttributes.add((XAttribute)ATTR_COSTAMOUNT.clone());
		ATTR_COSTTYPE  = factory.createAttributeLiteral(KEY_COSTTYPE, "", this);
		this.eventAttributes.add((XAttribute)ATTR_COSTTYPE.clone());
		ATTR_COSTDRIVER  = factory.createAttributeLiteral(KEY_COSTDRIVER, "", this);
		this.eventAttributes.add((XAttribute)ATTR_COSTDRIVER.clone());
		ATTR_COSTCURRENCY  = factory.createAttributeLiteral(KEY_COSTCURRENCY, "", this);
		this.eventAttributes.add((XAttribute)ATTR_COSTCURRENCY.clone());
		// register mapping aliases
		XGlobalAttributeNameMap.instance().registerMapping(XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_COSTTOTAL, "CostTotal");
		XGlobalAttributeNameMap.instance().registerMapping(XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_COSTAMOUNT, "CostAmount");
		XGlobalAttributeNameMap.instance().registerMapping(XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_COSTTYPE, "CostType");
		XGlobalAttributeNameMap.instance().registerMapping(XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_COSTDRIVER, "CostDriver");
		XGlobalAttributeNameMap.instance().registerMapping(XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_COSTCURRENCY, "CostCurrency");
	
	}
	
	/**
	 * Extracts from a given event the cost amount.
	 * 
	 * @param event Event to be queried.
	 * @return The cost of this event, as a Double
	 * (may be <code>null</code> if not defined).
	 */
	public Double extractCostAmount(XEvent event) {
		String eventCost = event.getAttributes().get(KEY_COSTTOTAL).toString();

		if(eventCost == null) {
			return null;
		} else {
			return Double.parseDouble(eventCost);
		}
	}
	
	public String extractCostType(XEvent event) {
		String costType = event.getAttributes().values().toString();
//.get(KEY_COSTTYPE)
		if(costType == null) {
			return null;
		} else {
			return costType;
		}
	}
		
	/**
	 * Assigns to a given event its cost.
	 * 
	 * @param event Event to be modified.
	 * @param cost Cost as a double.
	 */
	public void assignCost(XEvent event, double cost) {
		XAttributeContinuous attr = (XAttributeContinuous)ATTR_COSTTOTAL.clone();
		attr.setValue(cost);
		event.getAttributes().put(KEY_COSTTOTAL, attr);
	}

}
