package xal.model.probe.traj;

import xal.tools.beam.CorrelationMatrix;
import xal.tools.beam.RelativisticParameterConverter;
import xal.tools.beam.PhaseVector;
import xal.tools.beam.PhaseMatrix;
import xal.tools.beam.Twiss;
import xal.tools.data.IDataAdaptor;

import xal.model.probe.EnvelopeProbe;
import xal.model.probe.Probe;
import xal.model.xml.ParsingException;



/**
 * Encapsulates the state of an EnvelopeProbe at a particular point in time.
 * 
 * @author Craig McChesney, Christopher K. Allen
 * @version $id:
 * 
 */
public class EnvelopeProbeState extends BunchProbeState implements IPhaseState {



    /*
     * Global Constants
     */

    /** element tag for envelope data */
    protected static final String ENVELOPE_LABEL = "envelope";

//    /** attribute tag for response matrix */
//    private static final String RESP_LABEL = "resp";
//    
//    /** attribute tag for perturbation matrix (current response) */
//    private static final String PERTURB_LABEL = "perturb";
//
    /** element tag for centroid data */
    protected static final String CENTROID_LABEL = "centroid";

    /** attribute tag for centroid value vector */
    private static final String VALUE_LABEL = "value";
    

    
    /** attribute tag for correlation matrix */
    private static final String CORR_LABEL = "correlation";
    
    protected static final String ALPHA_X_LABEL = "alphaX";
    protected static final String BETA_X_LABEL = "betaX";
    protected static final String EMIT_X_LABEL = "emitX";
    protected static final String ALPHA_Y_LABEL = "alphaY";
    protected static final String BETA_Y_LABEL = "betaY";
    protected static final String EMIT_Y_LABEL = "emitY";
    protected static final String ALPHA_Z_LABEL = "alphaZ";
    protected static final String BETA_Z_LABEL = "betaZ";
    protected static final String EMIT_Z_LABEL = "emitZ";
    

    /*
     * Local Attributes
     */
     
    /** current response matrix (Sako) */
    private PhaseMatrix         m_matPert;

    /** accumulated response matrix */
    private PhaseMatrix         m_matResp;
    
    
    /** accumulated response matrix (no space charge) */
    private PhaseMatrix         m_matRespNoSpaceCharge;

    /** envelope state */
    private CorrelationMatrix   m_matCorrel;

    
    
    /** 
     * the twiss parameters calculated from the transfer matrix 
     * (not calculated from the correlation matrix, except for
     * the initialization)
     * 
     * CKA NOTES:
     * - This attribute is redundant in the sense that all "Twiss parameter"
     * information is contained within the correlation matrix.  The correlation
     * matrix was intended as the primary attribute for an <code>EnvelopeProbe</code>.
     * 
     * - The dynamics of this attribute are computed from tranfer matrices,
     * however, with space charge the transfer matrices are computed using the
     * correlation matrix.  Thus, these parameters are inconsistent in the 
     * presence of space charge.
     * 
     * - I have made a separate Probe class, <code>TwissProbe</code> which has
     * Twiss parameters as its primary state.
     * 
     * - For all these reason I am deprecating this attribute
     * 
     * @deprecated
     */
    @Deprecated
    private Twiss [] twissParams;
    
    
    /** 
     * save Twiss parameters flag
     * 
     * CKA NOTES:
     * - As this attribute relates to the above attribes, I am 
     * deprecating it as well.
     * 
     * @deprecated
     */
    @Deprecated
    private boolean bolSaveTwiss = false;


    
    
    
    /*
     * Initialization
     */    


    /**
     * Default constructor.  Create a new, empty <code>EnvelopeProbeState<code> object.
     */    
    public EnvelopeProbeState() {
        this.m_matPert = PhaseMatrix.identity();
        this.m_matResp = PhaseMatrix.identity();
        this.m_matRespNoSpaceCharge = PhaseMatrix.identity();
    	this.m_matCorrel = new CorrelationMatrix();
    }
	
    /**
     * Initializing Constructor.  Create a new <code>EnvelopeProbeState</code> object and
     * initialize it to the state of the probe argument.
     * 
     * @param probe     <code>EnvelopeProbe</code> containing initializing state information
     */
    @SuppressWarnings("deprecation")
    public EnvelopeProbeState(EnvelopeProbe probe) {
        super(probe);
        this.setCorrelation(probe.getCorrelation());
        this.setResponseMatrix(probe.getResponseMatrix());
        this.setResponseMatrixNoSpaceCharge(probe.getResponseMatrixNoSpaceCharge());
        this.setPerturbationMatrix(probe.getCurrentResponseMatrix());
        //obsolete this.setTwiss(probe.getTwiss());
        this.setTwiss(probe.getCorrelation().computeTwiss());
        this.setSaveTwissFlag(probe.getSaveTwissFlag());
	//sako

    }
    
    
    /**
     * Changes the behavior of the save state methods.
     * By setting this flag to <code>true</code> the Twiss
     * parameter attributes will be saved <b>instead</b> of
     * the correlation matrix.  The default behavior for this class
     * is to save the correlation matrix.
     * 
     * CKA Notes:
     * - This is clearly a kluge; use this method with caution.
     * It is provided to maintain backward compatibility.
     * 
     * @param   bolSaveTwiss    behavior of save state methods 
     * 
     * @see EnvelopeProbeState#addPropertiesTo(IDataAdaptor)
     * 
     * @deprecated
     */
    @Deprecated
    public void setSaveTwissFlag(boolean bolSaveTwiss)    {
        this.bolSaveTwiss = bolSaveTwiss;
    }
    
    /**
     * Set the first-order response matrix of the current element slice
     * 
     * @param matPerturb   first-order response matrix in homogeneous coordinates
     */
    public void setPerturbationMatrix(PhaseMatrix matPerturb)  {
        this.m_matPert = matPerturb;
    }

    /**
     * Set the first-order response matrix accumulated by the Envelope since its initial
     * state.  Note that this response includes the effects of space charge.
     * 
     * @param matResp   first-order response matrix in homogeneous coordinates
     */
    public void setResponseMatrix(PhaseMatrix matResp)  {
        this.m_matResp = matResp;
    }

    /**
     * Set the first-order response matrix accumulated by the Envelope since its initial
     * state.  Note that this response does not include the effects of space charge.
     * 
     * @param matResp   first-order response matrix in homogeneous coordinates
     */
    public void setResponseMatrixNoSpaceCharge(PhaseMatrix matResp)  {
        this.m_matRespNoSpaceCharge = matResp;
    }

    /**
     *  Set the correlation matrix for this probe (7x7 matrix in homogeneous coordinates).
     *
     *  @param  matTau    new phase space covariance matrix of this probe
     *
     *  @see xal.tools.beam.CorrelationMatrix
     */
    public void setCorrelation(CorrelationMatrix matTau) {
        m_matCorrel = matTau;
    }

    /** 
     * Set the twiss parameters for the probe 
     * 
     * @param twiss new 3 dimensional array of Twiss objects (horizontal, vertical and longitudinal)
     * 
     * @see xal.tools.beam.Twiss
     * @see EnvelopeProbeState#getTwiss()
     * 
     * @deprecated
     */
    @Deprecated
    public void setTwiss(Twiss [] twiss) {
        twissParams = twiss;
    }
    
    
    
    /*
     * Attribute Queries
     */
     	
	


    /**
     * Get the first-order response matrix accumulated by the Envelope since its initial
     * state.  Note that this response includes the effects of space charge.
     * 
     * @return  first-order response matrix in homogeneous coordinates
     */
    public PhaseMatrix getResponseMatrix()  {
        return this.m_matResp;
    }
    
    /**
     * Get the first-order response matrix accumulated by the Envelope since its initial
     * state.  Note that this response does not include the effects of space charge.
     * 
     * @return  first-order response matrix in homogeneous coordinates
     */
    public PhaseMatrix getResponseMatrixNoSpaceCharge()  {
        return this.m_matRespNoSpaceCharge;
    }
    
    /**
     * Get the first-order response matrix of current element slice
     * 
     * @return  first-order response matrix in homogeneous coordinates
     */
    public PhaseMatrix getPerturbationMatrix()  {
        return this.m_matPert;
    }
    
    /** 
     *  Returns the correlation matrix of this state in homogeneous 
     *  phase space coordinates.  This is the primary state attribute
     *  for <code>EnvelopeProbe</code> objects.
     * 
     * @return  7x7 matrix <zz^T> in homogeneous coordinates
     */
    public CorrelationMatrix getCorrelationMatrix()   {
        return m_matCorrel;
    }
    
    
    /** 
     * Returns the (independent attribute) array of Twiss parameters for this 
     * state for all three planes.
     * 
     * CKA NOTES:
     * - This attribute is redundant in the sense that all "Twiss parameter"
     * information is contained within the correlation matrix.  The correlation
     * matrix was intended as the primary attribute of an <code>EnvelopeProbe</code>.
     * 
     * - The dynamics of this attribute are computed from tranfer matrices,
     * however, with space charge the transfer matrices are computed using the
     * correlation matrix.  Thus these parameters are inconsistent in the 
     * presence of space charge.
     * 
     * - I have made a separate Probe class, <code>TwissProbe</code> which has
     * Twiss parameters as its primary state.
     * 
     * - For all these reason I am deprecating this method
     * 
     * @return array(twiss-H, twiss-V, twiss-L)
     * 
     * @deprecated redundant state variable
     */
    @Deprecated
    public Twiss[] getTwiss() { 
        return this.twissParams;
    }
    
    /**
     * Return the save Twiss parameters flag.  If this flag is set then
     * only the Twiss parameters are saved to a <code>IDataAdaptor</code>
     * object.
     * 
     * NOTES: 
     * This can be dangerous as we have the 
     * potential to loss a lot of information.  In particular,
     * if the probe has pasted through a bend or a steering
     * magnet, the Twiss parameters do not contain enough information
     * to restart the probe. 
     * 
     * @return Twiss parameter save flag
     * 
     * @see Probe#save(IDataAdaptor)
     * @see Probe#applyState(ProbeState)
     * 
     * @deprecated  associated with the redundant state variable <code>twissParams</code>
     */
    @Deprecated
    public boolean getSaveTwissFlag()   {
        return this.bolSaveTwiss;
    }
    
    
    

    /*
     * Computed Properties
     */

    
    /**
     *  Convenience Method: Returns the covariance matrix of this state in 
     *  homogeneous phase space coordinates.  This value is computed directly 
     *  from the correlation matrix.
     * 
     * @return  &lt;<b>zz</b><sup><i>T</i></sup>&gt; - &lt;<b>z</b>&gt;&lt;<b>z</b>&gt;<sup><i>T</i></sup>
     * 
     * @see xal.tools.beam.CorrelationMatrix#computeCovariance()
     */
    public  CorrelationMatrix phaseCovariance()   {
        return getCorrelationMatrix().computeCovariance();
    }
    
    /**
     *  Convenience Method:  Returns the rms emittances for this state as
     *  determined by the <b>correlation matrix</b>.  This value is computed
     *  directly from the correlation matrix and is independent of the
     *  <code>twissParams</code> local attribute.
     * 
     * @return array (&epsilon;<sub>x</sub>,&epsilon;<sub>y</sub>,&epsilon;<sub>z</sub>) of rms emittances
     */
    public double[] rmsEmittances() {
        return getCorrelationMatrix().computeRmsEmittances();
    }
    
    /**
     * Return the twiss parameters for this state calculated from the 
     * correlation matrix.
     * 
     * CKA Notes:
     * - Use this method with caution.  The returned information is incomplete,
     * it is taken only from the three 2x2 diagonal blocks of the correlation
     * matrix and, therefore, does not contain the full state of the beam.  In
     * general, you cannot restart the beam with the returned parameters, for
     * example, in the case of bends, offsets, dipoles, etc.
     *
     * @return  twiss parameters computed from diagonal blocks of the correlation matrix
     */
    public Twiss[] twissParameters() {
        return getCorrelationMatrix().computeTwiss();
    }
    
    
//    /**
//     * get the array of twiss objects for this state for all three planes
//     * @deprecated This method does not provide correct Twiss info with any dipole bend presented.  Should use getTwiss() from EnvelopeProbe.
//     * @return array(twiss-H, twiss-V, twiss-L
//     */
//    public Twiss[] getTwiss() {
//        return twissParameters();
//    }
    
    
    /*
     * CKA - Why do we have three methods that return exactly the same thing?
     * 
     * This is a dangerous situation.
     */ 

    /** 
     *  Convenience Method: Return the phase space coordinates of the centroid 
     *  in homogeneous coordinates.  This value is taken from the correlation
     *  matrix.
     *
     *  @return         &lt;z&gt; = (&lt;x&gt;, &lt;xp&gt;, &lt;y&gt;, &lt;yp&gt;, &lt;z&gt;, &lt;zp&gt;, 1)^T
     *  
     *  @see    xal.tools.beam.CorrelationMatrix#getMean()
     */
    public PhaseVector phaseMean()  {
        return getCorrelationMatrix().getMean();
    }
    
    /** 
     *  <p>
     *  Returns homogeneous phase space coordinates of the particle.  The units
     *  are meters and radians.
     *  </p>
     *  <p>
     *  <h4>CKA NOTE:</h4>
     *  This method simply returns the value of EnvelopeProbeState#phaseMean()
     *  </p>
     *
     *  @return     vector (x,x',y,y',z,z',1) of phase space coordinates
     *  
     *  @see    EnvelopeProbeState#phaseMean()
     */
    public PhaseVector phaseCoordinates() {
        return phaseMean();
    }
    
    /**
     * <p>
     * Get the fixed orbit about which betatron oscillations occur.
     * </p>
     * <p>
     * <h4>CKA NOTE:</h4>
     *  This method simply returns the value of EnvelopeProbeState#phaseMean()
     * </p>
     *
     * @return the fixed orbit vector (x,x',y,y',z,z',1)
     *  
     * @see    EnvelopeProbeState#phaseMean()
     */
    public PhaseVector getFixedOrbit() {
        return phaseMean();
    }
    

    /**
     * <p>
     * Save the state values particular to <code>EnvelopeProbeState</code> objects
     * to the data sink.  In particular we save only the data in the 2x2 diagonal
     * blocks of the correlation matrix, and as Twiss parameters.
     * </p>
     * <p>
     * <h4>CKA NOTE:</h4>
     * - <strong>Be careful</strong> when using this method!  It is here as a convenient.  It
     * saves the <code>EnvelopeProbeState</code> information in the save format as
     * the load()/save() methods do, but you cannot restore an <code>EnvelopeProbe</code>
     * object from these data in general.
     * </p>
     * 
     *  @param  daSink   data sink represented by <code>IDataAdaptor</code> interface
     */
    public void saveStateAsTwiss(IDataAdaptor daSink) {
    	IDataAdaptor stateNode = daSink.createChild(STATE_LABEL);
        stateNode.setValue(TYPE_LABEL, getClass().getName());
        stateNode.setValue("id", this.getElementId());
        
        super.addPropertiesTo(stateNode);
        
        
        IDataAdaptor envNode = stateNode.createChild(EnvelopeProbeState.ENVELOPE_LABEL);
        //sako this is bad for dispersion (2008/07/07) envNode.setValue(EnvelopeProbeState.RESP_LABEL, this.getResponseMatrix().toString());
        //sako this is unnecessary  (2008/07/07) envNode.setValue(EnvelopeProbeState.PERTURB_LABEL, this.getPerturbationMatrix().toString());
        
        Twiss[]   arrTwiss = this.twissParameters();
        
        envNode.setValue(EnvelopeProbeState.ALPHA_X_LABEL, arrTwiss[0].getAlpha());
        envNode.setValue(EnvelopeProbeState.BETA_X_LABEL, arrTwiss[0].getBeta());
        envNode.setValue(EnvelopeProbeState.EMIT_X_LABEL, arrTwiss[0].getEmittance());
        envNode.setValue(EnvelopeProbeState.ALPHA_Y_LABEL, arrTwiss[1].getAlpha());
        envNode.setValue(EnvelopeProbeState.BETA_Y_LABEL, arrTwiss[1].getBeta());
        envNode.setValue(EnvelopeProbeState.EMIT_Y_LABEL, arrTwiss[1].getEmittance());
        envNode.setValue(EnvelopeProbeState.ALPHA_Z_LABEL, arrTwiss[2].getAlpha());
        envNode.setValue(EnvelopeProbeState.BETA_Z_LABEL, arrTwiss[2].getBeta());
        envNode.setValue(EnvelopeProbeState.EMIT_Z_LABEL, arrTwiss[2].getEmittance());           
    }
    
    

    
    /*
     * Support Methods
     */ 
    
    
    /**
     * Save the state values particular to <code>EnvelopeProbeState</code> objects
     * to the data sink.
     * 
     *  @param  container   data sink represented by <code>IDataAdaptor</code> interface
     */
    @Override
    protected void addPropertiesTo(IDataAdaptor container) {
        super.addPropertiesTo(container);
        
        IDataAdaptor envNode = container.createChild(EnvelopeProbeState.ENVELOPE_LABEL);
        //sako this is bad for dispersion (2008/07/07) envNode.setValue(EnvelopeProbeState.RESP_LABEL, this.getResponseMatrix().toString());
//      sako this is unnecessary  (2008/07/07) envNode.setValue(EnvelopeProbeState.PERTURB_LABEL, this.getPerturbationMatrix().toString());
        
        if (!bolSaveTwiss)
            envNode.setValue(EnvelopeProbeState.CORR_LABEL, this.getCorrelationMatrix().toString());
        else {
            //sako
//            this.setTwiss(this.twissParameters());
            
        	Twiss[] twiss = this.twissParameters();
            envNode.setValue(EnvelopeProbeState.ALPHA_X_LABEL, twiss[0].getAlpha());
            envNode.setValue(EnvelopeProbeState.BETA_X_LABEL, twiss[0].getBeta());
            envNode.setValue(EnvelopeProbeState.EMIT_X_LABEL, twiss[0].getEmittance());
            envNode.setValue(EnvelopeProbeState.ALPHA_Y_LABEL, twiss[1].getAlpha());
            envNode.setValue(EnvelopeProbeState.BETA_Y_LABEL, twiss[1].getBeta());
            envNode.setValue(EnvelopeProbeState.EMIT_Y_LABEL, twiss[1].getEmittance());
            envNode.setValue(EnvelopeProbeState.ALPHA_Z_LABEL, twiss[2].getAlpha());
            envNode.setValue(EnvelopeProbeState.BETA_Z_LABEL, twiss[2].getBeta());
            envNode.setValue(EnvelopeProbeState.EMIT_Z_LABEL, twiss[2].getEmittance());           
            
            //sako
            envNode.setValue(EnvelopeProbeState.CORR_LABEL, this.getCorrelationMatrix().toString());
            
        }
    }
        
    /**
     * Recover the state values particular to <code>EnvelopeProbeState</code> objects 
     * from the data source.
     *
     *  @param  container   data source represented by a <code>IDataAdaptor</code> interface
     * 
     *  @exception ParsingException     state information in data source is malformatted
     */
    @Override
    protected void readPropertiesFrom(IDataAdaptor container) 
        throws ParsingException 
    {
        super.readPropertiesFrom(container);
        
        IDataAdaptor envNode = container.childAdaptor(EnvelopeProbeState.ENVELOPE_LABEL);
        if (envNode == null)
            throw new ParsingException("EnvelopeProbeState#readPropertiesFrom(): no child element = " + ENVELOPE_LABEL);
        
        if (envNode.hasAttribute(EnvelopeProbeState.ALPHA_X_LABEL)) {
            Twiss[] twiss = new Twiss[3];
            twiss[0] = new Twiss(envNode.doubleValue(EnvelopeProbeState.ALPHA_X_LABEL), 
                    envNode.doubleValue(EnvelopeProbeState.BETA_X_LABEL),
                    envNode.doubleValue(EnvelopeProbeState.EMIT_X_LABEL));
            twiss[1] = new Twiss(envNode.doubleValue(EnvelopeProbeState.ALPHA_Y_LABEL), 
                    envNode.doubleValue(EnvelopeProbeState.BETA_Y_LABEL),
                    envNode.doubleValue(EnvelopeProbeState.EMIT_Y_LABEL));
            twiss[2] = new Twiss(envNode.doubleValue(EnvelopeProbeState.ALPHA_Z_LABEL), 
                    envNode.doubleValue(EnvelopeProbeState.BETA_Z_LABEL),
                    envNode.doubleValue(EnvelopeProbeState.EMIT_Z_LABEL));
            this.setTwiss(twiss);
            IDataAdaptor parNode = container.childAdaptor(EnvelopeProbeState.CENTROID_LABEL);
            if (parNode == null) {
                this.setCorrelation(CorrelationMatrix.buildCorrelation(twiss[0], twiss[1], twiss[2]));
//              throw new ParsingException("EnvelopeProbeState#readPropertiesFrom(): no child element = " + EnvelopeProbeState.PARTICLE_LABEL);
            } else {
//                if (parNode.hasAttribute(EnvelopeProbeState.X_LABEL)) {
//                    PhaseVector phaseV = new PhaseVector();
//                    phaseV.setx(parNode.doubleValue(EnvelopeProbeState.X_LABEL));
//                    phaseV.setxp(parNode.doubleValue(EnvelopeProbeState.XP_LABEL));
//                    phaseV.sety(parNode.doubleValue(EnvelopeProbeState.Y_LABEL));
//                    phaseV.setyp(parNode.doubleValue(EnvelopeProbeState.YP_LABEL));
//                    phaseV.setz(parNode.doubleValue(EnvelopeProbeState.Z_LABEL));
//                    phaseV.setzp(parNode.doubleValue(EnvelopeProbeState.ZP_LABEL));
//                
//                    this.setCorrelation(CorrelationMatrix.buildCorrelation(twiss[0], twiss[1], twiss[2], phaseV));
//                }
                if (parNode.hasAttribute(EnvelopeProbeState.VALUE_LABEL))   {
                    String      strCent = parNode.stringValue(EnvelopeProbeState.VALUE_LABEL);
                    PhaseVector vecCent = new PhaseVector(strCent);
                    
                    this.setCorrelation(CorrelationMatrix.buildCorrelation(twiss[0], twiss[1], twiss[2], vecCent));
                }
                
            }
            
        } else if (envNode.hasAttribute(EnvelopeProbeState.CORR_LABEL))   {
            CorrelationMatrix matChi = new CorrelationMatrix(envNode.stringValue(EnvelopeProbeState.CORR_LABEL));
            this.setCorrelation(matChi);
            // initialize the state twiss parameters from the correlation matrix
            this.setTwiss(matChi.computeTwiss());
        }
        
        /* sako 2008/07/07
        if (envNode.hasAttribute(EnvelopeProbeState.RESP_LABEL)) {
            PhaseMatrix matResp = new PhaseMatrix(envNode.stringValue(EnvelopeProbeState.RESP_LABEL));
            this.setResponseMatrix(matResp);
        }
        */
        
        /* sako 2008/07/07
        // TODO - treatment for matCurResp?  CKA - Added 2006/11
        if (envNode.hasAttribute(EnvelopeProbeState.PERTURB_LABEL)) {
            PhaseMatrix matPert = new PhaseMatrix(envNode.stringValue(EnvelopeProbeState.PERTURB_LABEL));
            this.setPerturbationMatrix(matPert);
        }
        */
    }
    
    
//=====================================================================================
//
//  A much more practical implemetation is to have a parameter specifying 
//  phase coordinate rather than six separate functions.
//
    
    /**
     * Convenience function for returning the x plane chromatic dispersion as defined by
     * D.C. Carey in "The Optics of Charged Particle Beams".
     * 
     * NOTE:
     * We convert to the conventional definition of dispersion dx/(dp/p) by dividing
     * the (x|z') element of the first-order response matrix by relativistic gamma 
     * squared. 
     * 
     * @return  x plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see  Reference text D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionX()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrix().getElem(PhaseMatrix.IND_X, PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);
	//return d;//be carefull. Previous J-PARC vesion def is this.
    } 
	
    /**
     * Convenience function for returning the y plane chromatic dispersion as defined by
     * D.C. Carey in "The Optics of Charged Particle Beams".
     * 
     * NOTE:
     * We convert to the conventional definition of dispersion dy/(dp/p) by dividing
     * the (y|z') element of the first-order response matrix by relativistic gamma 
     * squared. 
     * 
     * @return  y plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionY()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrix().getElem(PhaseMatrix.IND_Y, PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);
        //return d;
    }
    
       
//sako, 20 dec 2004, add other dispersion functions
    /**
     * Convenience function for returning the x' plane chromatic dispersion as defined by
     * D.C. Carey in "The Optics of Charged Particle Beams".
     * 
     * NOTE:
     * We convert to the conventional definition of dispersion dx'/(dp/p) by dividing
     * the (x'|z') element of the first-order response matrix by relativistic gamma 
     * squared. 
     * 
     * @return  x' plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionXP()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrix().getElem(PhaseMatrix.IND_XP, PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);
        //return d;
    } 
	
    /**
     * Convenience function for returning the y' plane chromatic dispersion as defined by
     * D.C. Carey in "The Optics of Charged Particle Beams".
     * 
     * NOTE:
     * We convert to the conventional definition of dispersion dy'/(dp/p) by dividing
     * the (y'|z') element of the first-order response matrix by relativistic gamma 
     * squared. 
     * 
     * @return  y' plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionYP()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrix().getElem(PhaseMatrix.IND_YP, PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);
        //return d;
    }
    
    
    /**
     * Convenience function for returning the z plane chromatic dispersion as defined by
     * D.C. Carey in "The Optics of Charged Particle Beams".
     * 
     * NOTE:
     * We convert to the conventional definition of dispersion dz/(dp/p) by dividing
     * the (z|z') element of the first-order response matrix by relativistic gamma 
     * squared. 
     * 
     * @return  z plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionZ()  {
        //double  W  = this.getKineticEnergy();
        //double  Er = this.getSpeciesRestEnergy(); 
        //double  gamma = ParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrix().getElem(PhaseMatrix.IND_Z, PhaseMatrix.IND_ZP);
        
        //return d/(gamma*gamma);
        return d;
    } 
	
    /**
     * Convenience function for returning the z' plane chromatic dispersion as defined by
     * D.C. Carey in "The Optics of Charged Particle Beams".
     * 
     * NOTE:
     * We convert to the conventional definition of dispersion dzp/(dp/p) by dividing
     * the (z'|z') element of the first-order response matrix by relativistic gamma 
     * squared. 
     * 
     * @return  z' plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionZP()  {
        //double  W  = this.getKineticEnergy();
        //uble  Er = this.getSpeciesRestEnergy(); 
        //uble  gamma = ParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrix().getElem(PhaseMatrix.IND_ZP, PhaseMatrix.IND_ZP);
        
        //return d/(gamma*gamma);
        return d;
    }
    
    
    /** setter for dispersion, Sako, 16 Mar 06 */
    public void setChromDispersionX(double d)  {
        //double  W  = this.getKineticEnergy();
        //double  Er = this.getSpeciesRestEnergy(); 
        //double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        this.getResponseMatrix().setElem(PhaseMatrix.IND_X, PhaseMatrix.IND_ZP, d);
    } 
	
    /** setter for dispersion, Sako, 16 Mar 06 */
    public void setChromDispersionXP(double d)  {
        //double  W  = this.getKineticEnergy();
        //double  Er = this.getSpeciesRestEnergy(); 
        //double  gamma = ParameterConverter.computeGammaFromEnergies(W, Er);
        this.getResponseMatrix().setElem(PhaseMatrix.IND_XP, PhaseMatrix.IND_ZP, d);
    } 

    /** setter for dispersion, Sako, 16 Mar 06 */
    public void setChromDispersionY(double d)  {
        //double  W  = this.getKineticEnergy();
        //uble  Er = this.getSpeciesRestEnergy(); 
        //uble  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        this.getResponseMatrix().setElem(PhaseMatrix.IND_Y, PhaseMatrix.IND_ZP, d);
    }
    /** setter for dispersion, Sako, 16 Mar 06 */
    public void setChromDispersionYP(double d)  {
        //double  W  = this.getKineticEnergy();
        //uble  Er = this.getSpeciesRestEnergy(); 
        //uble  gamma = ParameterConverter.computeGammaFromEnergies(W, Er);
        this.getResponseMatrix().setElem(PhaseMatrix.IND_YP, PhaseMatrix.IND_ZP, d);
    }
    /** setter for dispersion, Sako, 16 Mar 06 */
    public void setChromDispersionZ(double d)  {
        //double  W  = this.getKineticEnergy();
        //double  Er = this.getSpeciesRestEnergy(); 
        //double  gamma = ParameterConverter.computeGammaFromEnergies(W, Er);
        this.getResponseMatrix().setElem(PhaseMatrix.IND_Z, PhaseMatrix.IND_ZP, d);
    } 
    /** setter for dispersion, Sako, 16 Mar 06 */
    public void setChromDispersionZP(double d)  {
        //double  W  = this.getKineticEnergy();
        //uble  Er = this.getSpeciesRestEnergy(); 
        //uble  gamma = ParameterConverter.computeGammaFromEnergies(W, Er);
        this.getResponseMatrix().setElem(PhaseMatrix.IND_ZP, PhaseMatrix.IND_ZP, d);
    }
    
    
    
    
    /**
     * dispersion x without space charge
     * 
     * @return  x plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionXNoSpaceCharge()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrixNoSpaceCharge().getElem(PhaseMatrix.IND_X, PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);
    } 
	
    /**
     *     * dispersion y without space charge
     * 
     * @return  y plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionYNoSpaceCharge()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrixNoSpaceCharge().getElem(PhaseMatrix.IND_Y, PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);
    }
    
    /**
     * dispersion x' without space charge
      * 
     * @return  x' plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionXPNoSpaceCharge()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrixNoSpaceCharge().getElem(PhaseMatrix.IND_XP, PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);
    } 
	
    /**
     * dispersion y' without space charge
     * 
     * @return  y' plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionYPNoSpaceCharge()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrixNoSpaceCharge().getElem(PhaseMatrix.IND_YP, PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);
    }
    
    
    
    
    
    /**
     * dispersion x with space charge
 
     * 
     * @return  x plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see Ohkawa, Ikegami, NUM A 576 (2007) 274
      */
    public double getChromDispersionXSpaceCharge()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getCorrelationMatrix().getElem(PhaseMatrix.IND_X, PhaseMatrix.IND_ZP)
        / this.getCorrelationMatrix().getElem(PhaseMatrix.IND_ZP,PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);//Is gamma necessary?
    } 
	
    /**
     *     * dispersion y with space charge
     * 
     * @return  y plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionYSpaceCharge()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrixNoSpaceCharge().getElem(PhaseMatrix.IND_Y, PhaseMatrix.IND_ZP)
                / this.getCorrelationMatrix().getElem(PhaseMatrix.IND_ZP,PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);
    }
    
    /**
     * dispersion x' with space charge
      * 
     * @return  x' plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionXPSpaceCharge()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrixNoSpaceCharge().getElem(PhaseMatrix.IND_XP, PhaseMatrix.IND_ZP)
                / this.getCorrelationMatrix().getElem(PhaseMatrix.IND_ZP,PhaseMatrix.IND_ZP);
        
        return d/(gamma*gamma);
    } 
	
    /**
     * dispersion y' with space charge
     * 
     * @return  y' plane chromatic dispersion in <b>meters/radian</b>
     * 
     * @see D.C. Carey, "The Optics of Charged Particle Beams"
     */
    public double getChromDispersionYPSpaceCharge()  {
        double  W  = this.getKineticEnergy();
        double  Er = this.getSpeciesRestEnergy(); 
        double  gamma = RelativisticParameterConverter.computeGammaFromEnergies(W, Er);
        double  d     = this.getResponseMatrixNoSpaceCharge().getElem(PhaseMatrix.IND_YP, PhaseMatrix.IND_ZP)
                / this.getCorrelationMatrix().getElem(PhaseMatrix.IND_ZP,PhaseMatrix.IND_ZP);
        return d/(gamma*gamma);
    }
    
  
    
  
    
    /*
     * Debugging
     */
     
     
     
    /**
     * Write out state information to a string.
     * 
     * @return     text version of internal state data
     */
    @Override
    public String toString() {
        return super.toString() + " correlation: " + getCorrelationMatrix().toString();
    }   
    
    
    
    
}