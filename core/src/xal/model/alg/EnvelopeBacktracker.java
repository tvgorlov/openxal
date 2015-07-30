/*
 * EnvelopeBacktracker.java
 *
 *  Created on February 4, 2009
 *  Modified:
 *
 */
 
package xal.model.alg;

import xal.model.IElement;
import xal.model.IProbe;
import xal.model.ModelException;
import xal.model.elem.ChargeExchangeFoil;
import xal.model.elem.IdealRfGap;
import xal.model.probe.EnvelopeProbe;
import xal.model.probe.traj.EnvelopeProbeState;
import xal.tools.beam.CovarianceMatrix;
import xal.tools.beam.PhaseMap;
import xal.tools.beam.PhaseMatrix;


/**
 * 
 * <h1>Tracking algorithm for <code>EnvelopeProbe</code> objects.</h1>  
 * 
 * <p>
 * This tracker object is based
 * The <code>EnvelopeProbe</code>'s
 * state, which is a <code>CovarianceMatrix</code> object, is advanced using the linear
 * dynamics portion of any beamline element (<code>IElement</code> exposing object) transfer
 * map.  The linear portion is represented as a matrix, thus, the state advance is accomplished
 * with a transpose conjugation with this matrix.
 * </p>
 * <p>
 * The effects of space charge are also included in the dynamics calculations.  Space charge
 * effects are also represented with a matrix transpose conjugation, however, the matrix is
 * computed using the values of the probe's correlation matrix.  The result is a nonlinear
 * effect.  The space charge forces are computed using a linear fit to the fields generated by
 * an ellipsoidal charge distribution with the same statistics described in the probe's 
 * correlation matrix.  The linear fit is weighted by the beam distribution itself, so it is 
 * more accurate in regions of higher charged density.  For a complete description see the reference
 * below.
 * </P
 * <p>
 * <strong>NOTES</strong>: (CKA)
 * <br/>
 * &middot;  This class has been un-deprecated.  Currently refactoring the 
 * hierarchy structure of the Algorithm base to simplify implementation of
 * implementation is not supported yet. Considering a modification
 *              of the Tracker class

 * </p>
 *
 * @see xal.tools.beam.em.BeamEllipsoid
 * @see xal.model.alg.EnvelopeTracker 
 * @see <a href="http://lib-www.lanl.gov/cgi-bin/getfile?00796950.pdf">Theory and Technique
 *      of Beam Envelope Simulation</a>
 *
 * @version 1.0
 * 
 * @author Christopher K. Allen
 * @since Feb 4, 2009
 * 
 */
public class EnvelopeBacktracker extends EnvelopeTrackerBase {

    /*
     *  Global Constants
     */
    

    
    /** string type identifier for algorithm */
    public static final String      s_strTypeId = EnvelopeTracker.class.getName();
    
    /** current algorithm version */
    public static final int         s_intVersion = 4;
    
    /** probe type recognized by this algorithm */
    public static final Class<EnvelopeProbe>       s_clsProbeType = EnvelopeProbe.class;
    

    
    /*
     * Initialization
     */
    
    /**
     * <p>
     * Create a new, uninitialized <code>EnvelopeBacktracker()</code>
     * algorithm object.  This is the default constructor to be used
     * when creating objects of this type.
     * </p>
     */
    public EnvelopeBacktracker() {
        super(s_strTypeId, s_intVersion, s_clsProbeType);
    }

    /**
     *  <p>
     *  This method is a protected constructor meant only for building
     *  child classes.
     *  </p>
     *
     *  @param      strType         string type identifier of algorithm
     *  @param      intVersion      version of algorithm
     *  @param      clsProbeType    class object for probe handled by this algorithm.
     */
    protected EnvelopeBacktracker(String strType, int intVersion,
            Class<? extends IProbe> clsProbeType) {
        super(strType, intVersion, clsProbeType);
    }
    
    /**
     * Copy constructor for EnvelopeBackTracker
     *
     * @param       sourceTracker   Tracker that is being copied
     */
    public EnvelopeBacktracker(EnvelopeBacktracker sourceTracker) {
        super( sourceTracker );
    }
    
    /**
     * Create a deep copy of EnvelopeBackTracker
     */
    @Override
    public EnvelopeBacktracker copy() {
        return new EnvelopeBacktracker( this );
    }


    
      /** 
     * <h2>Implementation of Abstract Tracker#doPropagation(IProbe, IElement)</h2>
     *
     * <p>
     * This method is essentially the same implementation as the method
     * <code>EnvelopeTracker#doPropagation()</code>, only here the probe object 
     * is back-propagated.  The method calls <code>Tracker.retractProbe()</code>
     * rather than <code>Tracker.advanceProbe()</code>, and the implemented
     * method <code>EnvelopeBacktracker.retractState()</code> rather than
     * <code>EnvelopeTracker.advanceState</code>.
     * </p>
     * 
     * @author Christopher K. Allen
     * @since Feb 9, 2009
     *
     * @see xal.model.alg.Tracker#propagate(xal.model.IProbe, xal.model.IElement)
     * @see xal.model.alg.EnvelopeTracker#doPropagation(IProbe, IElement)
     */
    @Override
    public void doPropagation(IProbe probe, IElement elem)
            throws ModelException 
    {
        
        //sako
        double elemPos = this.getElemPosition();
        double elemLen = elem.getLength();
        double propLen = elemLen - elemPos;
        
        if (propLen < 0) {
            System.err.println("doPropagation, elemPos, elemLen = "+elemPos+" "+elemLen);
            return;
        }

        // Determine the number of integration steps and the step size 
        int     cntSteps;   // number of steps through element
        double  dblStep;    // step size through element
        
        if(this.getUseSpacecharge())
            cntSteps = (int) Math.max(Math.ceil(propLen / getStepSize()), 1);
        else 
            cntSteps = 1;
        
        dblStep = elem.getLength() / cntSteps;
//        dblStep = propLen / cntSteps;
        
        for (int i=0 ; i<cntSteps ; i++) {
            this.retractState(probe, elem, dblStep);
            this.retractProbe(probe, elem, dblStep);
        }
        
    }
    /** 
     * <h2>Back-propagates the Defining State of the Probe Object</h2>
     *
     * <p>
     * This method uses the same basic algorithm as in 
     * <code>EnvelopeTracker#advanceState()</code>, only the probe object is
     * back-propagated.  The method utilizes all the space charge mechanisms 
     * of the base class <code>EnvelopeTracker</code>.
     * </p>
     *
     * @param  ifcElem     interface to the beam element
     * @param  ifcProbe    interface to the probe
     * @param  dblLen      length of element subsection to retract 
     *
     * @throws ModelException     bad element transfer matrix/corrupt probe state
     * 
     * @author Christopher K. Allen
     * @since Feb 9, 2009
     *
     * @see gov.sns.xal.model.alg.EnvelopeTracker#advanceState(gov.sns.xal.model.IProbe, gov.sns.xal.model.IElement, double)
     * 
     */
    protected void retractState(IProbe ifcProbe, IElement ifcElem, double dblLen)
            throws ModelException {
        
        // Identify probe
        EnvelopeProbe   probe = (EnvelopeProbe)ifcProbe;
        
        // Get initial conditions of probe
//        R3                  vecPhs0  = probe.getBetatronPhase();
//        Twiss[]             twiss0   = probe.getCovariance().computeTwiss();
        PhaseMatrix         matResp0 = probe.getResponseMatrix();
        PhaseMatrix         matTau0  = probe.getCovariance();

        // Remove the emittance growth
        if (this.getEmittanceGrowth())   
            matTau0 = this.removeEmittanceGrowth(probe, ifcElem, matTau0);
        
        // Compute the transfer matrix
        //def PhaseMatrix matPhi = compTransferMatrix(dblLen, probe, ifcElem);
        PhaseMatrix matPhi = compTransferMatrix(dblLen, probe, ifcElem);
        
        // Advance the probe states 
        PhaseMatrix matResp1 = matPhi.times( matResp0 );
        PhaseMatrix matTau1  = matTau0.conjugateTrans( matPhi );

        // Save the new state variables in the probe
        probe.setResponseMatrix(matResp1);
        probe.setCurrentResponseMatrix(matPhi);
        probe.setCovariance(new CovarianceMatrix(matTau1));
//        probe.advanceTwiss(matPhi, ifcElem.energyGain(probe, dblLen) );
        
        // phase update:
//        Twiss []    twiss1  = probe.getCovariance().computeTwiss();
//        R3          vecPhs1 = vecPhs0.plus( matPhi.compPhaseAdvance(twiss0, twiss1) );
//        probe.setBetatronPhase(vecPhs1);
        
        /** sako 
         * treatment of ChargeExchangeFoil
         **/
        treatChargeExchange(probe, ifcElem);
    }


    /**
     * <h2>Compute Transfer Matrix Including Space Charge</h2>
     * 
     * <p>
     * Computes the back-propagating transfer matrix over the incremental 
     * distance <code>dblLen</code> 
     * for the beamline modeling element <code>ifcElem</code>, and for the given 
     * <code>probe</code>.  We include space charge and emittance growth effects 
     * if specified.
     * </p>
     * 
     * <p>
     * <strong>NOTE</strong>: (CKA)
     * <br/>
     * &middot; If space charge is included, the space charge matrix is computed for length
     * <code>dblLen</code>, but at a half-step location behind the current probe
     * position.  This method is the same technique used by Trace3D.  The space charge
     * matrix is then pre- and post- multiplied by the element transfer matrix for
     * a half-step before and after the mid-step position, respectively.  
     * <br/>
     * &middot; I do not
     * know if this (leap-frog) technique buys us much more accuracy then full 
     * stepping.
     * </p> 
     *  
     * @param dblLen    incremental path length
     * @param probe     beam probe under simulation
     * @param ifcElem   beamline element propagating probe
     * 
     * @return      transfer matrix for given element
     * 
     * @throws ModelException   bubbles up from IElement#transferMap()
     * 
     * @see EnvelopeTracker#compScheffMatrix(double, EnvelopeProbe, PhaseMatrix)
     * @see EnvelopeTracker#transferEmitGrowth(EnvelopeProbe, IElement, PhaseMatrix)
     * @see EnvelopeTracker#modTransferMatrixForDisplError(double, double, double, PhaseMatrix)
     */
    private PhaseMatrix compTransferMatrix(double dblLen, EnvelopeProbe probe, IElement ifcElem)
        throws ModelException
    {
        
        // Returned value
        PhaseMatrix matPhi;     // transfer matrix including all effects
    
        
        // Check for exceptional circumstance and modify transfer matrix accordingly
        if (ifcElem instanceof IdealRfGap) {
            IdealRfGap elemRfGap = (IdealRfGap)ifcElem;
            double dW = elemRfGap.energyGain(probe, dblLen);
            double W  = probe.getKineticEnergy();
            probe.setKineticEnergy(W-dW);
            PhaseMatrix matPhiI = elemRfGap.transferMap(probe, dblLen).getFirstOrder();
            
            if (this.getEmittanceGrowth()) {
                double      dphi     = this.effPhaseSpread(probe, elemRfGap);
                
                matPhiI = super.modTransferMatrixForEmitGrowth(dphi, matPhiI);
            }
            matPhi = matPhiI.inverse();
            probe.setKineticEnergy(W);

            return matPhi;
        }

            
        if (dblLen==0.0)    {
            matPhi = ifcElem.transferMap(probe, dblLen).getFirstOrder();

            return matPhi;
        }
         
        // Check for easy case of no space charge
        if (!this.getUseSpacecharge())    {
            matPhi = ifcElem.transferMap(probe, dblLen).getFirstOrder();
            
        // we must treat space charge
        } else {
            

            // Store the current probe state (for rollback)
            EnvelopeProbeState state0 = probe.cloneCurrentProbeState();
        	//ProbeState  state0 = probe.createProbeState();


            // Get half-step back-propagation matrix at current probe location
            //  NOTE: invert by computing for negative propagation length
            PhaseMap    mapElem0 = ifcElem.transferMap(probe, -dblLen/2.0); 
            PhaseMatrix matPhi0  = mapElem0.getFirstOrder();

            // Get the RMS envelopes at probe location
            CovarianceMatrix covTau0  = probe.getCovariance();    // covariance matrix at entrance


            // Move probe back a half step for position-dependent transfer maps
            double            pos     = probe.getPosition() - dblLen/2.0;
            PhaseMatrix       matTau1 = covTau0.conjugateTrans(matPhi0);
            CovarianceMatrix covTau1 = new CovarianceMatrix(matTau1);

            probe.setPosition(pos);
            probe.setCovariance(covTau1);


            // space charge transfer matrix
            //  NOTE: invert by computing for negative propagation length
            PhaseMatrix matPhiSc = this.compScheffMatrix(-dblLen, probe, ifcElem);   


            // Compute half-step transfer matrix at new probe location
            PhaseMap    mapElem1 = ifcElem.transferMap(probe, -dblLen/2.0);
            PhaseMatrix matPhi1  = mapElem1.getFirstOrder();

            // Restore original probe state
            probe.applyState(state0);
            
            
            // Compute the full transfer matrix for the distance dblLen
            matPhi   = matPhi1.times( matPhiSc.times(matPhi0) );
        }
        
        return matPhi;
    }

    /**
     * <h2>Remove Emittance Growth Through an RF Gap</h2>
     * <p>
     * Method to modify the covariance matrix when simulating emittance
     * growth through RF accelerating gaps.  (The method only 
     * considers the  case of propagation 
     * through an <code>IdealRfGap</code> element).  If the <code>IElement</code>
     * argument is any other type of element, nothing is done.
     * The argument <code>matTau</code> is the covariance matrix after the
     * usual propagation through the <code>elem</code> element.
     * </p> 
     * <p>
     * Note that this method is essentially the complement of the method
     * {@link EnvelopeTracker#addEmittanceGrowth(EnvelopeProbe, IElement, PhaseMatrix)}.
     * Whereas <code>addEmittanceGrowth()</code> augments the momentum
     * elements of <b>&sigma;</b>, this method reduces them by the same amount.
     * Specifically, let <i>x</i> be either transverse phase space variable.  
     * The emittance growth effect is achieved
     * by first multiplying the element &lt;x'|x&gt; of the RF gap transfer
     * matrix <b>&Phi;</b> by the factor 
     * <i>F<sub>t</sub></i>(&Delta;<i>&phi;</i>)
     * returned by method 
     * {@link EnvelopeTrackerBase#compTransFourierTransform(double)} 
     * (see {@link EnvelopeTrackerBase#modTransferMatrixForEmitGrowth(double, PhaseMatrix)}).
     * Currently this action is done in 
     * {@link #compTransferMatrix(double, EnvelopeProbe, IElement)}.
     * Once the  covariance matrix <b>&tau;</b> is back-propagated by the 
     * modified transfer 
     * matrix <b>&Phi;</b>, the moment &lt;<i>x'</i><sup>2</sup>&gt; is 
     * reduced by the result of this function.
     * </p>
     * <p>
     * The discussion below is taken directly from 
     * {@link EnvelopeTracker#addEmittanceGrowth(EnvelopeProbe, IElement, PhaseMatrix)}.
     * It is applicable here if the emittance is reduced by 
     * &Delta;&lt;<i>x'<sub>f</sub></i><sup>2</sup>&gt; rather than increased 
     * by it.
     * </p>
     * <p>
     * The before gap and after gap transverse RMS divergence angles, 
     * <i>x'<sub>i</sub></i> and 
     * <i>x'<sub>f</sub></i>, respectively, 
     * are related by the following formula:
     * <br/>
     * <br/>
     * &nbsp; &lt;<i>x'<sub>f</sub></i><sup>2</sup>&gt; = 
     *         &Delta;&lt;<i>x'<sub>f</sub></i><sup>2</sup>&gt; +
     *         &lt;<i>x'<sub>i</sub></i><sup>2</sup>&gt;
     * <br/>
     * <br/>
     * where &Delta;&lt;<i>x'<sub>f</sub></i><sup>2</sup>&gt;
     * is the emittance growth factor given by
     * <br/>
     * <br/>
     * &nbsp;  &Delta;&lt;<i>x'<sub>f</sub></i><sup>2</sup>&gt; &equiv; 
     *        <i>k<sub>t</sub></i><sup>2</sup>
     *        <i>G<sub>t</sub></i>(<i>&phi;<sub>s</sub></i>,&Delta;<i>&phi;</i>)
     *        &lt;<i>x<sub>i</sub></i></i><sup>2</sup>&gt;.
     * <br/>
     * <br/>
     * where 
     * <i>G<sub>t</sub></i>(<i>&phi;<sub>s</sub></i>,&Delta;<i>&phi;</i>)
     * is the transverse 3-dimensional emittance growth function,
     * and <i>x<sub>i</sub></i> represents the 
     * before-gap position for <em>either</em>
     * transverse phase plane.  The action of this method is described
     * by the original equation.
     * </p>
     * <p>
     * The resulting action on the before gap and after gap transverse RMS emittances, 
     * <i>&epsilon;<sub>t,i</sub></i> and 
     * <i>&epsilon;<sub>t,f</sub></i>, respectively, 
     * is now described by the following formula:
     * <br/>
     * <br/>
     * &nbsp; <i>&epsilon;<sub>t,f</sub></i><sup>2</sup> = 
     *        <i>&eta;</i><sup>2</sup><i>&epsilon;<sub>t,i</sub></i><sup>2</sup> +
     *        &Delta;<i>&epsilon;<sub>t,f</sub></i><sup>2</sup>
     * <br/>
     * <br/>
     * where <i>&eta;</i> is the momentum compaction due to acceleration
     * <br/>
     * <br/>
     *  <i>&eta;</i> &equiv; 
     *    <i>&beta;<sub>i</sub>&gamma;<sub>i</sub></i>/<i>&beta;<sub>f</sub>&gamma;<sub>f</sub></i>
     * <br/>
     * <br/>
     * and &Delta;<i>&epsilon;<sub>t,f</sub></i> is the emittance increase term 
     * <br/>
     * <br/>
     * &nbsp;  &Delta;<i>&epsilon;<sub>t,f</sub></i><sup>2</sup> &equiv; 
     *        &Delta;&lt;<i>x'<sub>f</sub></i><sup>2</sup>&gt;
     *        &lt;<i>x<sub>f</sub></i></i><sup>2</sup>&gt;<sup>2</sup>.
     * <br/>
     * <br/>
     * There are analogous formulas for the before and after gap
     * longitudinal plane emittances 
     * <i>&epsilon;<sub>z,i</sub></i> and 
     * <i>&epsilon;<sub>z,f</sub></i>, respectively, with
     * <i>G<sub>t</sub></i>(<i>&phi;<sub>s</sub></i>,&Delta;<i>&phi;</i>)
     * replaced by 
     * <i>G<sub>z</sub></i>(<i>&phi;<sub>s</sub></i>,&Delta;<i>&phi;</i>) 
     * and <i>x</i><sub>(<i>f,i</i>)</sub> replaced by 
     * <i>z</i><sub>(<i>f,i</i>)</sub>.  
     * </p> 
     * <p>
     * <strong>NOTES</strong>: CKA
     * <br/>
     * &middot; Since we are modeling the RF gap as a thin lens, only the 
     * momentum (divergance angle) is modified, &lt;<i>x</i><sup>2</sup>&gt;,
     * &lt;<i>y</i><sup>2</sup>&gt;, and &lt;<i>z</i><sup>2</sup>&gt; remain
     * unaffected.  Thus, &lt;<i>x<sub>f</sub></i><sup>2</sup>&gt;
     * = &lt;<i>x<sub>i</sub></i><sup>2</sup>&gt; and
     * &lt;<i>z<sub>f</sub></i><sup>2</sup>&gt;
     * = &lt;<i>z<sub>i</sub></i><sup>2</sup>&gt; and may be computed
     * as such in the above.
     * <br/>
     * &middot; The &lt;<i>x'</i><sup>2</sup>&gt; element is modified by the formula
     * <br/>
     * <br/>
     * &nbsp; &lt;<i>x'</i><sup>2</sup>&gt; = &lt;<i>x'</i><sup>2</sup>&gt; + <i>c<sub>eg</sub></i>&lt;<i>x</i><sup>2</sup>&gt;
     * <br/>
     * <br/>
     * where <i>c<sub>eg</sub></i> is the emittance growth coefficent.  There are similar 
     * equations for the other phase planes.  The emittance growth coefficents are computed
     * in the base class <code>EnvelopeTrackerBase</code> by the methods 
     * <code>emitGrowthCoefTrans(EnvelopeProbe, IdealRfGap)</code> and 
     * <code>emitGrowthCoefLong(EnvelopeProbe, IdealRfGap)</code>.
     * </p>  
     * <p>
     * <strong>NOTES</strong>: (H. SAKO)
     * <br/>
     *  &middot; Increase emittance using same (nonlinear) procedure on the second
     *  moments as in Trace3D. 
     * </p>
     * 
     * @param   iElem        <code>IElement</code> element for exceptional processing
     * @param   probe       <code>IProbe</code> object associated with correlation matrix
     * @param   matTau      correlation matrix after (normal) propagation thru <code>elem</code>
     * 
     * @return  covariance matrix of <code>probe</code> after adjusting for emittance growth
     * 
     * @throws  ModelException  unknown/unsupported emittance growth model, or
     *                          unknown/unsupported phase plane
     * 
     * @see #compTransferMatrix(double, EnvelopeProbe, IElement)
     * @see EnvelopeTrackerBase#compTransFourierTransform(double)
     * @see EnvelopeTrackerBase#compLongFourierTransform(double)
     * @see EnvelopeTracker#addEmittanceGrowth(EnvelopeProbe, IElement, PhaseMatrix)
     * 
     * @author  Hiroyuki Sako
     * @author  Christopher K. Allen
     */
    private PhaseMatrix   removeEmittanceGrowth(EnvelopeProbe probe, IElement iElem, PhaseMatrix matTau) 
        throws ModelException
    {
        
        // Check for RF Gap
        if (!(iElem instanceof IdealRfGap))
            return matTau;
        
        if (!this.getEmittanceGrowth())
            return matTau;
    
        
        // Get the synchronous phase and compute the phase spread 
        IdealRfGap  elemRfGap = (IdealRfGap)iElem;
        
        double  W     = probe.getKineticEnergy();
        double  dW    = elemRfGap.energyGain(probe);
        probe.setKineticEnergy(W - dW);
        
        double  phi_s = elemRfGap.getPhase();
        double  dphi  = this.effPhaseSpread(probe, elemRfGap);
    
        
        // Compute the divergence angle increment coefficients 
        //  (emittance growth coefficients)
        double  dxp_2;      // transverse divergence angle augmentation factor
        double  dzp_2;      // longitudinal divergence angle augmentation factor

        //        if (this.getEmitGrowthModel() == EmitGrowthModel.TRACE3D) {
        //            
        //            dxp_2 = this.emitGrowthCoefTrans(probe, elemRfGap);
        //            dzp_2 = this.emitGrowthCoefLong(probe, elemRfGap);
        //    
        //        } else {

        double  Gt    = this.compEmitGrowthFunction(PhasePlane.TRANSVERSE, phi_s, dphi);
        double  kt    = elemRfGap.compTransFocusing(probe);
        dxp_2 = kt*kt*Gt;

        double  Gz    = this.compEmitGrowthFunction(PhasePlane.LONGITUDINAL, phi_s, dphi);
        double  kz    = elemRfGap.compLongFocusing(probe);
        //            double  gf    = elemRfGap.gammaFinal(probe);
        //            double  gf_2  = gf*gf;
        //            dzp_2 = kz*kz*Gz/(gf_2*gf_2);
        //            dzp_2 = gf_2*gf_2*kz*kz*Gz;
        dzp_2 = kz*kz*Gz;

        //        }

        probe.setKineticEnergy(W);

        // Compute new correlation matrix
        //      Transverse planes
        double x_2    = matTau.getElem(0,0);
        double xp_2   = matTau.getElem(1,1);
        double xp_2eg = xp_2 - dxp_2*x_2; 
        matTau.setElem(1,1,xp_2eg);
        
        double y_2    = matTau.getElem(2,2);
        double yp_2   = matTau.getElem(3,3);
        double yp_2eg = yp_2 - dxp_2*y_2; 
        matTau.setElem(3,3,yp_2eg);
        
        //      Longitudinal plane
        double z_2    = matTau.getElem(4,4);
        double zp_2   = matTau.getElem(5,5);
        double zp_2eg = zp_2 - dzp_2*z_2; 
        matTau.setElem(5,5,zp_2eg);
    
        return matTau;
    }

//    protected PhaseMatrix modTransferMatrixForEmitReduction(double dphi, PhaseMatrix   matPhi) 
//    throws ModelException
//{
//    
//    if (!this.getEmittanceGrowthFlag())
//        return matPhi;
//
//    // Compute auxiliary parameters
//    double  Ft;     // transverse plane Fourier transform
//    double  Fz;     // longitudinal plane Fourier transform
//
//    Ft = this.compTransFourierTransform(dphi);
//    Fz = this.compLongFourierTransform(dphi);
//    
//    // Modify the transfer matrix
//    double  fl;     // thin-lens focal-length element of tranfer matrix
//
//    fl = matPhi.getElem(PhaseIndexHom.Xp, PhaseIndexHom.X);
//    matPhi.setElem(PhaseIndexHom.Xp, PhaseIndexHom.X, fl/Ft);
//
//    fl = matPhi.getElem(PhaseIndexHom.Yp, PhaseIndexHom.Y);
//    matPhi.setElem(PhaseIndexHom.Yp, PhaseIndexHom.Y, fl/Ft);
//
//    fl = matPhi.getElem(PhaseIndexHom.Zp, PhaseIndexHom.Z);
//    matPhi.setElem(PhaseIndexHom.Zp, PhaseIndexHom.Z, fl/Fz);
//
//    return matPhi;
//}

    /**
     * <p>
     * Test for a <code>ChargeExchangeFoil<code> element.
     * If found, the probe represent an H<sup>+</sup> beam, the electrons 
     * are added and the beam becomes H<sup>-</sup>.
     * </p>
     * <p>
     * The opposite of 
     * {@link EnvelopeTracker#treatChargeExchange(EnvelopeProbe, IElement)}
     * </p>
     * 
     * @param probe    Propagating beam
     * @param ifcElem  Element to tested for <code>ChargeExchangeFoil</code> type
     * 
     * @author Hiroyuki Sako
     * 
     * @see gov.sns.xal.model.elem.ChargeExchangeFoil
     * @see EnvelopeTracker#treatChargeExchange(EnvelopeProbe, IElement)
     */
    private void treatChargeExchange(EnvelopeProbe probe, IElement ifcElem) {
    	if (ifcElem instanceof ChargeExchangeFoil) {
    		double q = probe.getSpeciesCharge();
    		if (q>0) {
    			System.out.println("charge exchanged at "+ifcElem.getId()+" from " + q + " to "+ (-q));
    			probe.setSpeciesCharge(-q);
    		}
    	}
    }
    
}