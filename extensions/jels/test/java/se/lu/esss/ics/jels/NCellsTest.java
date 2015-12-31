package se.lu.esss.ics.jels;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import se.lu.esss.ics.jels.model.elem.jels.JElsElementMapping;
import se.lu.esss.ics.jels.smf.ESSElementFactory;
import se.lu.esss.ics.jels.smf.impl.ESSRfCavity;
import se.lu.esss.ics.jels.smf.impl.ESSRfGap;
import xal.model.IElement;
import xal.smf.AcceleratorNode;
import xal.smf.AcceleratorSeq;
import xal.smf.attr.ApertureBucket;

@RunWith(Parameterized.class)
public class NCellsTest extends SingleElementTest {
	
	public NCellsTest(SingleElementTestData data) {
		super(data);
	}


	@Parameters(name = "NCells {index}: {0}")
	public static Collection<Object[]> tests() {
		final double frequency = 4.025e8, current = 0;
		
		List<Object []> tests = new ArrayList<>();
		
		// NCELLS m=0
		// NCELLS 0 3 0.5 5.27924e+06 -72.9826 31 0 0.493611 0.488812 12.9359 -14.4824 0.386525 0.664594 0.423349 0.350508 0.634734 0.628339 0.249724 0.639103 0.622128 0.25257
		// 0: basic test m=0
		tests.add(new Object[] {new SingleElementTestData() {{
			description = "basic test, m=0";
			probe = setupOpenXALProbe( 2.5e6, frequency, current); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(4.025e8, 0, 3, 0.5, 5.27924e+06, -72.9826, 31, 0, 
					0.493611, 0.488812, 12.9359, -14.4824, 
					0.386525, 0.664594, 0.423349, 0.350508, 0.634734, 0.628339, 0.249724, 0.639103, 0.622128, 0.25257);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{-1.251628e+02, -2.816564e+01, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{-4.892498e+02, -1.101003e+02, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -1.251628e+02, -2.816564e+01, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -4.892498e+02, -1.101003e+02, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +8.271148e+02, +1.599042e+02}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +6.136827e+03, +1.186419e+03}, 				 		
			};
			
			// TW correlation matrix
			TWGamma = 1.014228386; 
			TWCorrelationMatrix = new double[][] {
					{+2.417781e-08, +9.451023e-08, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+9.451023e-08, +3.694372e-07, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +3.069079e-08, +1.199687e-07, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.199687e-07, +4.689511e-07, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +2.999586e-06, +2.225560e-05}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +2.225560e-05, +1.651268e-04}	
			};			
		}}});
		
// 		System.out.println("NCELLS m=1");
		//  NCELLS 1 3 0.5 5.34709e+06 -55.7206 31 0 0.493611 0.488812 12.9604 -14.5077 0.393562 0.672107 0.409583 0.342918 0.645929 0.612576 0.257499 0.650186 0.606429 0.259876
		// 1: basic test m=1
		tests.add(new Object[] {new SingleElementTestData() {{
			description = "basic test, m=1";
			probe = setupOpenXALProbe( 2.5e6, frequency, current); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(4.025e8, 1, 3, 0.5, 5.34709e+06, -55.7206, 31, 0,
					0.493611, 0.488812, 12.9604, -14.5077,
					0.393562, 0.672107, 0.409583, 0.342918, 0.645929, 0.612576, 0.257499, 0.650186, 0.606429, 0.259876);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{+9.781907e+00, +1.608910e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+2.764530e+01, +4.587636e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +9.781907e+00, +1.608910e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +2.764530e+01, +4.587636e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, -1.945048e+01, -1.838470e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, -3.222670e+01, -3.066498e+00},	 		
			};
			
			// TW correlation matrix
			TWGamma = 1.016786270; 
			TWCorrelationMatrix = new double[][] {
					{+1.144407e-10, +3.244214e-10, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+3.244214e-10, +9.196951e-10, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.587170e-10, +4.494497e-10, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +4.494497e-10, +1.272745e-09, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +1.442445e-09, +2.390964e-09}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +2.390964e-09, +3.963208e-09},	
			};			
		}}});
		

// 		System.out.println("NCELLS m=2");
		// NCELLS 2 3 0.5 5.34709e+06 -55.7206 31 0 0.493611 0.488812 12.9604 -14.5077 0.393562 0.672107 0.409583 0.342918 0.645929 0.612576 0.257499 0.650186 0.606429 0.259876
		// 2: basic test m=2
		tests.add(new Object[] {new SingleElementTestData() {{
			description = "basic test, m=2";
			probe = setupOpenXALProbe( 2.5e6, frequency, current); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(4.025e8, 2, 3, 0.5, 5.34709e+06, -55.7206, 31, 0,
					0.493611, 0.488812, 12.9604, -14.5077,
					0.393562, 0.672107, 0.409583, 0.342918, 0.645929, 0.612576, 0.257499, 0.650186, 0.606429, 0.259876);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{+4.158248e+01, +7.331508e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+1.039245e+02, +1.833476e+01, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +4.158248e+01, +7.331508e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.039245e+02, +1.833476e+01, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +2.384991e+00, +2.217700e-01}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +6.462182e+01, +6.210999e+00},	 		
			};
			
			// TW correlation matrix
			TWGamma = 1.011418698; 
			TWCorrelationMatrix = new double[][] {
					{+2.173306e-09, +5.432880e-09, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+5.432880e-09, +1.358124e-08, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +2.961684e-09, +7.403073e-09, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +7.403073e-09, +1.850484e-08, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +2.164218e-11, +5.876521e-10}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +5.876521e-10, +1.595673e-08},	
			};			
		}}});
		
		
		// NCELLS 0 3 0.5 5.27924e+06 -72.9826 31 0 0.493611 0.488812 12.9359 -14.4824 0 1 0 0 1 0 0 1 0 0
		//System.out.println("NCELLS no TTF m=0");
		// 3: no TTF, m=0
		tests.add(new Object[] {new SingleElementTestData() {{
			description = "no TTF, m=0";
			probe = setupOpenXALProbe( 2.5e6, frequency, current); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(4.025e8, 0, 3, 0.5, 5.27924e+06, -72.9826, 31, 0, 
					0.493611, 0.488812, 12.9359, -14.4824, 
					0,1,0,0,1,0,0,1,0,0);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{-1.735870e+03, -4.086639e+02, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{-8.865846e+03, -2.087226e+03, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -1.735870e+03, -4.086639e+02, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -8.865846e+03, -2.087226e+03, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, -6.122390e+02, -1.129077e+02}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, -3.024516e+03, -5.577793e+02},	 				 		
			};
			
			// TW correlation matrix
			TWGamma = 1.000303097; 
			TWCorrelationMatrix = new double[][] {
					{+4.857058e-06, +2.480712e-05, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+2.480712e-05, +1.267008e-04, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +6.076266e-06, +3.103415e-05, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +3.103415e-05, +1.585049e-04, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +1.622443e-06, +8.015025e-06}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +8.015025e-06, +3.959499e-05},
			};			
		}}});
	
		// NCELLS 1 3 0.5 5.27924e+06 -72.9826 31 0 0.493611 0.488812 12.9359 -14.4824 0 1 0 0 1 0 0 1 0 0
//		   NCELLS 1 3 0.5 5.27924e+06 -72.9826 31 0 0.493611 0.488812 12.9359 -14.4824 0 1 0 0 1 0 0 1 0 0
		//System.out.println("NCELLS no TTF m=1");	
		// 4: no TTF, m=1
		tests.add(new Object[] {new SingleElementTestData() {{
			description = "no TTF, m=1";
			probe = setupOpenXALProbe( 2.5e6, frequency, current); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(4.025e8, 1, 3, 0.5, 5.27924e+06, -72.9826, 31, 0, 
					0.493611, 0.488812, 12.9359, -14.4824, 
					0,1,0,0,1,0,0,1,0,0);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{-2.469811e-01, +1.206558e-01, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{-8.205329e+00, -2.699117e-01, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -2.469811e-01, +1.206558e-01, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -8.205329e+00, -2.699117e-01, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +3.321139e+01, +2.165450e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +4.969652e+02, +3.243518e+01},
			};
			
			// TW correlation matrix
			TWGamma = 1.002351947; 
			TWCorrelationMatrix = new double[][] {
					{+1.891455e-13, +5.588942e-13, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+5.588942e-13, +5.034769e-11, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.299134e-13, +1.187813e-12, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.187813e-12, +8.128660e-11, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +4.042244e-09, +6.048951e-08}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +6.048951e-08, +9.051856e-07}, 		
			};			
			TMerrTolerance = 2;  //TODO check if there's a problem with TW - gamma is weird
			CMerrTolerance = 1;
		}}});
		
		// NCELLS 2 3 0.5 5.27924e+06 -72.9826 31 0 0.493611 0.488812 12.9359 -14.4824 0 1 0 0 1 0 0 1 0 0
		// System.out.println("NCELLS no TTF m=2");
		// 5: no TTF, m=2
		tests.add(new Object[] {new SingleElementTestData() {{
			description = "no TTF, m=2";
			probe = setupOpenXALProbe( 2.5e6, frequency, current); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(4.025e8, 2, 3, 0.5, 5.27924e+06, -72.9826, 31, 0, 
					0.493611, 0.488812, 12.9359, -14.4824, 
					0,1,0,0,1,0,0,1,0,0);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{-1.323586e+01, -1.982918e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{-6.859146e+02, -1.028191e+02, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -1.323586e+01, -1.982918e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -6.859146e+02, -1.028191e+02, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +5.565648e+02, +4.473234e+01}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +3.890135e+03, +3.126602e+02},
			};
			
			// TW correlation matrix
			TWGamma = 1.004183576; 
			TWCorrelationMatrix = new double[][] {
					{+1.971330e-10, +1.021774e-08, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+1.021774e-08, +5.296029e-07, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +2.793855e-10, +1.448011e-08, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.448011e-08, +7.504809e-07, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +1.158552e-06, +8.097754e-06}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +8.097754e-06, +5.659964e-05},	
			};			
		}}});
	
		// NCELLS 0 3 0.5 5.27924e+06 -72.9826 31 0 0.493611 0.488812 12.9359 -14.4824 0 1 0 0 1 0 0 1 0 0
		//System.out.println("NCELLS no TTF m=0 spacecharge I=30mA");
		// 6: spacecharge no TTF, m=0
		tests.add(new Object[] {new SingleElementTestData() {{
			description = "space charge no TTF, m=0";
			probe = setupOpenXALProbe2( 2.5e6, frequency, 30e-3); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(4.025e8, 0, 3, 0.5, 5.27924e+06, -72.9826, 31, 0, 
					0.493611, 0.488812, 12.9359, -14.4824, 
					0,1,0,0,1,0,0,1,0,0);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{-1.735870e+03, -4.086639e+02, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{-8.865846e+03, -2.087226e+03, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -1.735870e+03, -4.086639e+02, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -8.865846e+03, -2.087226e+03, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, -6.122390e+02, -1.129077e+02}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, -3.024516e+03, -5.577793e+02}, 
			};
			
			// TW correlation matrix
			TWGamma = 1.000303097; 
			TWCorrelationMatrix = new double[][] {
					{+6.334602e+00, +3.235359e+01, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+3.235359e+01, +1.652440e+02, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +7.764918e+00, +3.965884e+01, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +3.965884e+01, +2.025550e+02, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +1.794976e+00, +8.867365e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +8.867365e+00, +4.380568e+01}, 
			};			
			CMerrTolerance = 1e-4;
		}}});
		
		// NCELLS 0 3 0.5 5.27924e+06 -72.9826 31 0 0.493611 0.488812 12.9359 -14.4824 0 1 0 0 1 0 0 1 0 0
		//System.out.println("NCELLS no TTF m=0 spacecharge I=30mA E=200MeV");
		// 7: spacecharge no TTF, m=0, E=200MeV		
		tests.add(new Object[] {new SingleElementTestData() {{
			description = "space charge no TTF, m=0";
			probe = setupOpenXALProbe2( 200e6, frequency, 30e-3); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(4.025e8, 0, 3, 0.5, 5.27924e+06, -72.9826, 31, 0, 
					0.493611, 0.488812, 12.9359, -14.4824, 
					0,1,0,0,1,0,0,1,0,0);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{+1.052581e+00, +1.136137e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+6.062891e-02, +1.024003e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.052581e+00, +1.136137e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +6.062891e-02, +1.024003e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +9.047307e-01, +7.417816e-01}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, -1.770513e-01, +9.700690e-01},  
			};
			
			// TW correlation matrix
			TWGamma = 1.209700645; 
			TWCorrelationMatrix = new double[][] {
					{+2.479708e-06, +2.195419e-06, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+2.195419e-06, +1.982029e-06, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +2.027902e-06, +1.667244e-06, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.667244e-06, +1.417256e-06, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +1.353820e-06, +1.246704e-06}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +1.246704e-06, +1.277632e-06},  
			};			
			CMerrTolerance = 1e-3;
		}}});
	
		// NCELLS m=0, spacecharge I=30mA, E=200MeV
		// NCELLS 0 3 0.5 5.27924e+06 -72.9826 31 0 0.493611 0.488812 12.9359 -14.4824 0.386525 0.664594 0.423349 0.350508 0.634734 0.628339 0.249724 0.639103 0.622128 0.25257
		// 8: spacecharge, m=0, E=200MeV
		tests.add(new Object[] {new SingleElementTestData() {{
			description = "space charge, m=0";
			probe = setupOpenXALProbe2( 200e6, frequency, 30e-3); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(4.025e8, 0, 3, 0.5, 5.27924e+06, -72.9826, 31, 0, 
					0.493611, 0.488812, 12.9359, -14.4824, 
					0.386525, 0.664594, 0.423349, 0.350508, 0.634734, 0.628339, 0.249724, 0.639103, 0.622128, 0.25257);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{+1.039008e+00, +1.132766e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+4.406705e-02, +1.016869e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.039008e+00, +1.132766e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +4.406705e-02, +1.016869e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +9.320184e-01, +7.463337e-01}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, -1.290993e-01, +9.766603e-01},  		
			};
			
			// TW correlation matrix
			TWGamma = 1.210602499; 
			TWCorrelationMatrix = new double[][] {
					{+2.461872e-06, +2.170665e-06, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+2.170665e-06, +1.952307e-06, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +2.010118e-06, +1.644789e-06, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.644789e-06, +1.392577e-06, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +1.389399e-06, +1.289996e-06}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +1.289996e-06, +1.323364e-06}, 
			};			
			CMerrTolerance = 1e-3;
		}}});
		
		
		// NCELLS m=0, spacecharge I=30mA, E=3MeV, bunch freq=352.21MHz, RF freq=704.42MHz
		// NCELLS 0 3 0.5 5.27924e+06 -72.9826 31 0 0.493611 0.488812 12.9359 -14.4824 0.386525 0.664594 0.423349 0.350508 0.634734 0.628339 0.249724 0.639103 0.622128 0.25257
		// 9: spacecharge, m=0, E=3MeV
		tests.add(new Object[] {new SingleElementTestData() {{
			description = "space charge, m=0";
			probe = setupOpenXALProbe2( 3e6, 352.21e6, 30e-3); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(704.42e6, 0, 3, 0.5, 5.27924e+06, -72.9826, 31, 0, 
					0.493611, 0.488812, 12.9359, -14.4824, 
					0.386525, 0.664594, 0.423349, 0.350508, 0.634734, 0.628339, 0.249724, 0.639103, 0.622128, 0.25257);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{-1.276103e+02, -1.719167e+01, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{-2.252699e+03, -3.034929e+02, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -1.276103e+02, -1.719167e+01, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -2.252699e+03, -3.034929e+02, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +1.318713e+03, +1.501224e+02}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +9.914092e+03, +1.128622e+03}, 
			};
			
			// TW correlation matrix
			TWGamma = 1.011699131; 
			TWCorrelationMatrix = new double[][] {
					{+1.802048e-02, +3.181184e-01, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+3.181184e-01, +5.615796e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +2.552146e-02, +4.505330e-01, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +4.505330e-01, +7.953306e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +6.507757e+00, +4.892536e+01}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +4.892536e+01, +3.678212e+02}, 
			};
			TMerrTolerance = 1e-3;
			CMerrTolerance = 2e-2;
		}}});
		
		/*tests.add(new Object[] {new SingleElementTestData() {{
			description = "basic test, m=0";
			probe = setupOpenXALProbe( 2.5e6, frequency, 30e-3); 
			elementMapping = JElsElementMapping.getInstance();
			sequence = ncells(4.025e8, 0, 3, 0.5, 5.27924e+06, -72.9826, 31, 0, 
					0.493611, 0.488812, 12.9359, -14.4824, 
					0.386525, 0.664594, 0.423349, 0.350508, 0.634734, 0.628339, 0.249724, 0.639103, 0.622128, 0.25257, .1, .2, 0, 0, 0, 0);
			
			// TW transfer matrix
			TWTransferMatrix = new double[][]{
					{-1.251628e+02, -2.816564e+01, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{-4.892498e+02, -1.101003e+02, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -1.251628e+02, -2.816564e+01, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, -4.892498e+02, -1.101003e+02, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +8.271148e+02, +1.599042e+02}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +6.136827e+03, +1.186419e+03}, 				 		
			};
			
			// TW correlation matrix
			TWGamma = 1.014228386; 
			TWCorrelationMatrix = new double[][] {
					{+2.417781e-08, +9.451023e-08, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+9.451023e-08, +3.694372e-07, +0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +3.069079e-08, +1.199687e-07, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +1.199687e-07, +4.689511e-07, +0.000000e+00, +0.000000e+00}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +2.999586e-06, +2.225560e-05}, 
					{+0.000000e+00, +0.000000e+00, +0.000000e+00, +0.000000e+00, +2.225560e-05, +1.651268e-04}	
			};	
			TWMean = new double[] {	 12.3902, 47.9196, 24.7805, 95.8393, -0.203508, 7.79045 };
		}}});*/
		return tests;
	}
	
	public static AcceleratorSeq ncells(double frequency, int m, int n, double betag, double E0T, double Phis, double R, double p,
			double kE0Ti, double kE0To, double dzi, double dzo, 
			double betas, double Ts, double kTs, double k2Ts, double Ti, double kTi, double k2Ti, double To, double kTo, double k2To)
	{
		return ncells(frequency, m, n, betag, E0T, Phis, R, p,	kE0Ti, kE0To, dzi, dzo,	betas, Ts, kTs, k2Ts, Ti, kTi, k2Ti, To, kTo, k2To, 0, 0, 0, 0, 0, 0);
		
	}
	
	public static AcceleratorSeq ncells(double frequency, int m, int n, double betag, double E0T, double Phis, double R, double p,
			double kE0Ti, double kE0To, double dzi, double dzo, 
			double betas, double Ts, double kTs, double k2Ts, double Ti, double kTi, double k2Ti, double To, double kTo, double k2To,
			double dx, double dy, double dz, double fx, double fy, double fz)
	{
		AcceleratorSeq sequence = new AcceleratorSeq("GapTest");
		AcceleratorNode[] nodes = new AcceleratorNode[n];
		
		double lambda = IElement.LightSpeed/frequency;
		double Lc0,Lc,Lcn;
		double amp0,ampn;
		double pos0, posn;
		
		amp0 = (1+kE0Ti)*(Ti/Ts);		
		ampn = (1+kE0To)*(To/Ts);
		if (m==0) {
			Lc = Lc0 = Lcn = betag * lambda;
			pos0 = 0.5*Lc0 + dzi*1e-3;
			posn = Lc0 + (n-2)*Lc + 0.5*Lcn + dzo*1e-3;			
		} else if (m==1) {
			Lc = Lc0 = Lcn = 0.5 * betag * lambda;
			pos0 = 0.5*Lc0 + dzi*1e-3;
			posn = Lc0 + (n-2)*Lc + 0.5*Lcn + dzo*1e-3;
		} else { //m==2
			Lc0 = Lcn = 0.75 * betag * lambda;
			Lc = betag * lambda;			
			pos0 = 0.25 * betag * lambda + dzi*1e-3;
			posn = Lc0 + (n-2)*Lc + 0.5 * betag * lambda + dzo*1e-3;
		}
						
		// setup		
		nodes[0] = ESSElementFactory.createESSRfGap("g0", true, amp0, new ApertureBucket(), Lc0, pos0);
				
		for (int i = 1; i<n-1; i++) {
			nodes[i] = ESSElementFactory.createESSRfGap("g"+i, false, 1, new ApertureBucket(), Lc, Lc0 + (i-0.5)*Lc);
		}
		
		ESSRfGap lastgap = ESSElementFactory.createESSRfGap("g"+(n-1), false, ampn, new ApertureBucket(), Lcn, posn);
		lastgap.getRfGap().setEndCell(1);

		nodes[n-1] = lastgap;
		
		ESSRfCavity cavity = ESSElementFactory.createESSRfCavity("c", Lc0+(n-2)*Lc+Lcn, nodes, Phis, E0T*1e-6, frequency*1e-6, 0);

		cavity.getAlign().setX(dx*1e-3);
		cavity.getAlign().setY(dy*1e-3);
		cavity.getAlign().setZ(dz*1e-3);
		cavity.getAlign().setPitch(fx*Math.PI/180.);
		cavity.getAlign().setYaw(fy*Math.PI/180.);
		cavity.getAlign().setRoll(fz*Math.PI/180.);

		// TTF		
		if (betas == 0.0) {
			cavity.getRfField().setTTF_startCoefs(new double[] {});
			cavity.getRfField().setTTFCoefs(new double[] {});
			cavity.getRfField().setTTF_endCoefs(new double[] {});
		} else {
			cavity.getRfField().setTTF_startCoefs(new double[] {betas, Ti, kTi, k2Ti});
			cavity.getRfField().setTTFCoefs(new double[] {betas, Ts, kTs, k2Ts});
			cavity.getRfField().setTTF_endCoefs(new double[] {betas, To, kTo, k2To});			
		}		
		if (m==1) {
			cavity.getRfField().setStructureMode(1);
		}
		
		sequence.addNode(cavity);
		sequence.setLength(Lc0+(n-2)*Lc+Lcn);
		
		return sequence;
	}	
}
