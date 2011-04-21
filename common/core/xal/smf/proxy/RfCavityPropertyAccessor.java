/*
 * Created on Mar 17, 2004
 */
package xal.smf.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xal.sim.scenario.Scenario;
import xal.smf.AcceleratorNode;
import xal.smf.impl.RfCavity;

/**
 * @author Craig McChesney
 */
public class RfCavityPropertyAccessor implements PropertyAccessor {	// Static Variables ========================================================
	
	// Constants ===============================================================	
	
	// Property Names	
	public static final String PROPERTY_PHASE = "PHASE";
	public static final String PROPERTY_AMPLITUDE = "AMPLITUDE";
	
	// Method Names	
	public static final String METHOD_LIVE_PHASE = "getCavPhaseAvg";	
	public static final String METHOD_DESIGN_PHASE = "getDfltCavPhase";	
	
	public static final String METHOD_LIVE_AMPLITUDE = "getCavAmpAvg";	
	public static final String METHOD_DESIGN_AMPLITUDE = "getDfltCavAmp";	

	// Class Variables =========================================================
	
	private static ArrayList propertyNames;	
	private static HashMap liveProxies;
	private static HashMap designProxies;
	
	
	// Class Initialization ===================================================
	
	static {
		propertyNames = new ArrayList();
		propertyNames.add(PROPERTY_PHASE);
		propertyNames.add(PROPERTY_AMPLITUDE);
		
		initLiveProxies();
		
		initDesignProxies();
	}
	
	private static void initLiveProxies() {
		liveProxies = new HashMap(propertyNames.size());
		liveProxies.put(PROPERTY_PHASE, 
			new PropertyProxy(RfCavity.class, METHOD_LIVE_PHASE));
		liveProxies.put(PROPERTY_AMPLITUDE, 
			new PropertyProxy(RfCavity.class, METHOD_LIVE_AMPLITUDE));
	}
	
	private static void initDesignProxies() {
		designProxies = new HashMap(propertyNames.size());
		designProxies.put(PROPERTY_PHASE, 
			new PropertyProxy(RfCavity.class, METHOD_DESIGN_PHASE));
		designProxies.put(PROPERTY_AMPLITUDE, 
			new PropertyProxy(RfCavity.class, METHOD_DESIGN_AMPLITUDE));
	}
	
	
	// PropertyAccessor Interface ==============================================

	public double doubleValueFor(AcceleratorNode node, String property, String mode) 
			throws ProxyException {
		PropertyProxy proxy = null;
		if (mode.equals(Scenario.SYNC_MODE_LIVE)) {
			proxy = (PropertyProxy) liveProxies.get(property);
		} else if (mode.equals(Scenario.SYNC_MODE_DESIGN)) {
			proxy = (PropertyProxy) designProxies.get(property);
				} else if (mode.equals(Scenario.SYNC_MODE_RF_DESIGN)) {
						proxy = (PropertyProxy) designProxies.get(property);
		} else {
			throw new IllegalArgumentException("Unknown mode: " + mode);
		}
		if (proxy == null)
			throw new IllegalArgumentException("Unknown property: " + property);
		return proxy.doubleValueFor(node);
	}
	
	
	// Accessing ===============================================================
	
	public List propertyNames() {
		return (List) propertyNames.clone();
	}

}
