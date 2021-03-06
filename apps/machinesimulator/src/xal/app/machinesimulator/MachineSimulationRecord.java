//
// MachineSimulationRecord.java
//
//
// Created by Tom Pelaia on 9/19/11
// Copyright 2011 Oak Ridge National Lab. All rights reserved.
//

package xal.app.machinesimulator;

import xal.model.probe.traj.ProbeState;
import xal.tools.beam.calc.SimResultsAdaptor;
import xal.tools.beam.PhaseVector;
import xal.tools.beam.Twiss;
import xal.tools.math.r3.R3;


/** MachineSimulation is the simulation result for a simulation on an accelerator sequence  */
public class MachineSimulationRecord  {
	/** the data adaptor label used for reading and writing this document */
	static public final String DATA_LABEL = "MachineSimulationRecord"; 
	
	/** probe state wrapped by this record */
	private final ProbeState<?> PROBE_STATE;

	/** twiss parameters */
	private final Twiss[] TWISS_PARAMETERS;

	/** betatron phase */
	private final R3 BETATRON_PHASE;
	
	/**phase vector*/
	private final PhaseVector PHASE_VECTOR;


	/** Constructor */
    public MachineSimulationRecord( final SimResultsAdaptor resultsAdaptor, final ProbeState<?> probeState ) {
		PROBE_STATE = probeState;		
		TWISS_PARAMETERS = resultsAdaptor.computeTwissParameters( probeState );
		BETATRON_PHASE = resultsAdaptor.computeBetatronPhase( probeState );
		PHASE_VECTOR = resultsAdaptor.computeFixedOrbit( probeState );
    }

	/** get the wrapped probe state */
	public ProbeState<?> getProbeState() {
		return PROBE_STATE;
	}


	/** Get the state's element ID */
	public String getElementID() {
		return PROBE_STATE.getElementId();
	}


	/** Get the state's beamline position */
	public double getPosition() {
		return PROBE_STATE.getPosition();
	}
	
	/**Get the position coordinates*/
	public R3 getPosCoordinates() {
		return PHASE_VECTOR.getPosition();
	}


	/** get the state's twiss parameters */
	public Twiss[] getTwissParameters() {
		return TWISS_PARAMETERS;
	}


	/** get the state's betatron phase */
	public R3 getBetatronPhase() {
		return BETATRON_PHASE;
	}
	
}
