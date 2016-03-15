package edu.stanford.slac.util.zplot.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import edu.stanford.slac.util.zplot.ZPlot;
import edu.stanford.slac.util.zplot.ZPlotEvent;
import edu.stanford.slac.util.zplot.ZPlotFeatherRenderer;
import edu.stanford.slac.util.zplot.ZPlotListener;
import edu.stanford.slac.util.zplot.ZPlotPanel;
import edu.stanford.slac.util.zplot.ZPlot.RendererType;
import edu.stanford.slac.util.zplot.cartoon.model.CartoonDevice;
import edu.stanford.slac.util.zplot.cartoon.model.widget.BPMWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.CavityWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.DQuadWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.DipoleWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.FQuadWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.LossMonitorWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.MarkerWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.ProfileMonitorWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.SolenoidWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.UndulatorWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.XCollimatorWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.XCorWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.YCollimatorWidget;
import edu.stanford.slac.util.zplot.cartoon.model.widget.YCorWidget;
import edu.stanford.slac.util.zplot.model.Beamline;
import edu.stanford.slac.util.zplot.model.Device;
import edu.stanford.slac.util.zplot.model.Obstruction;
import edu.stanford.slac.util.zplot.model.WidgetsRepository;

public class SimpleExampleApplication {

	// Example

	private static double getRandomZ() {
		return Math.random() * 1000;
	}

	private static double getRandomLength() {
		return Math.random() * 3;
	}

	private static double getRandomValue() {
		return Math.random() * 5000;
	}

	private static int getRandomStatus() {
		return (int) Math.round(Math.random());
	}

	private static double getRandomBeamlineLength() {
		return Math.random() * 200 + 1;
	}

	/**
	 * My Method (please, document me!)
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// construct devices
		final int NR_DEVICES = 40;
		Device[] devices = new Device[NR_DEVICES];
		// -----------------------------------------------------------------
		for (int i = 0; i < NR_DEVICES; i++) {
			double z = getRandomZ();
			double value = getRandomValue();
			int status = getRandomStatus();
			devices[i] = new Device("bpm" + i, z, value, WidgetsRepository
					.getBPMWidget(status));
		}

		// sort by z
		Arrays.sort(devices);
		final Device[] bpms = new Device[devices.length];
		System.arraycopy(devices, 0, bpms, 0, devices.length);
		// -----------------------------------------------------------------
		for (int i = 0; i < NR_DEVICES / 2; i++) {
			devices[i] = new Device(bpms[i].getName(), getRandomZ(),
					getRandomValue(), WidgetsRepository
							.getLargeDispersionBPMWidget(getRandomStatus()));

		}

		final Device[] largeDispBPMs = new Device[devices.length / 2];
		System.arraycopy(devices, 0, largeDispBPMs, 0, devices.length / 2);
		// -----------------------------------------------------------------

		for (int i = 0; i < NR_DEVICES; i++) {
			double z = getRandomZ();
			double value = 0;
			int status = getRandomStatus();

			devices[i] = new Device("marker" + i, z, value, WidgetsRepository
					.getMarkerWidget(status));
		}

		// sort by z
		Arrays.sort(devices);
		final Device[] markers = new Device[devices.length];
		System.arraycopy(devices, 0, markers, 0, devices.length);
		// -----------------------------------------------------------------
		for (int i = 0; i < NR_DEVICES; i++) {
			double z = getRandomZ();
			// double value = getRandomValue();
			int status = getRandomStatus();
			devices[i] = new Device("magnet" + i, z, 0.0, WidgetsRepository
					.getAlarmedXCORWidget(status));

		}
		// sort by z
		Arrays.sort(devices);
		final Device[] xmagnets = new Device[devices.length];
		System.arraycopy(devices, 0, xmagnets, 0, devices.length);
		// -----------------------------------------------------------------
		for (int i = 0; i < NR_DEVICES; i++) {
			double z = getRandomZ();
			// double value = getRandomValue();
			int status = getRandomStatus();
			devices[i] = new Device("magnet" + i, z, 0.0, WidgetsRepository
					.getAlarmedYCORWidget(status));

		}
		// sort by z
		Arrays.sort(devices);
		final Device[] ymagnets = new Device[devices.length];
		System.arraycopy(devices, 0, ymagnets, 0, devices.length);
		// -----------------------------------------------------------------
		for (int i = 0; i < NR_DEVICES; i++) {
			double z = getRandomZ();
			double value = getRandomValue();
			int status = getRandomStatus();
			if (i % 5 != 0) {
				devices[i] = new Device("toroid" + i, z, value,
						WidgetsRepository.getToroidWidget(status));
			} else {
				devices[i] = new Device("marker" + i, z, value,
						WidgetsRepository.getMarkerWidget(status));
			}
		}
		// sort by z
		Arrays.sort(devices);
		final Device[] toroids = new Device[devices.length];
		System.arraycopy(devices, 0, toroids, 0, devices.length);
		// -----------------------------------------------------------------
		for (int i = 0; i < NR_DEVICES; i++) {
			double z = getRandomZ();
			int status = getRandomStatus();
			devices[i] = new Device("stopper" + i, z, 0, WidgetsRepository
					.getStopperWidget(status));

		}
		// sort by z
		Arrays.sort(devices);
		final Device[] stoppers = new Device[devices.length];
		System.arraycopy(devices, 0, stoppers, 0, devices.length);
		// -----------------------------------------------------------------

		for (int i = 0; i < NR_DEVICES; i++) {
			double z = getRandomZ();
			int status = getRandomStatus();
			devices[i] = new Obstruction("pms" + i, z, WidgetsRepository
					.getProfMonScreenWidget(status));

		}
		// sort by z
		Arrays.sort(devices);
		final Device[] obstructions = new Device[devices.length];
		System.arraycopy(devices, 0, obstructions, 0, devices.length);
		// -----------------------------------------------------------------
		// Beamline cartoon devices
		final int NR_CARTOON_DEVICE_TYPES = 16;
		for (int i = 0; i < NR_DEVICES; i++) {
			double z = getRandomZ();
			double length = getRandomLength();
			int m = NR_CARTOON_DEVICE_TYPES + 1;
			if (i % m-- == 0) {
				devices[i] = new CartoonDevice("bpm" + i, z, new BPMWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("cavity" + i, z, length,
						new CavityWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("dipole" + i, z, length,
						new DipoleWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("dquad" + i, z, length,
						new DQuadWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("fquad" + i, z, length,
						new FQuadWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("lossmonitor" + i, z,
						new LossMonitorWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("marker" + i, z,
						new MarkerWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("profmon" + i, z,
						new ProfileMonitorWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("solenoid" + i, z, length,
						new SolenoidWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("toroid" + i, z, length,
						new UndulatorWidget());//new ToroidWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("undulator" + i, z, length,
						new UndulatorWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("wirescanner" + i, z,
						new UndulatorWidget());//new WireScannerWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("xcollimator" + i, z,
						new XCollimatorWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("ycollimator" + i, z,
						new YCollimatorWidget());
			} else if (i % m-- == 0) {
				devices[i] = new CartoonDevice("xcor" + i, z, new XCorWidget());
			} else {
				devices[i] = new CartoonDevice("ycor" + i, z, new YCorWidget());
			}
		}

		// sort by z
		Arrays.sort(devices);
		final CartoonDevice[] cartoonDevices = new CartoonDevice[devices.length];
		System.arraycopy(devices, 0, cartoonDevices, 0, devices.length);
		// -----------------------------------------------------------------
		// beamlines
		final Beamline[] beamlines = new Beamline[10];
		double startZ = 0;
		double length = 0;
		for (int i = 0; i < beamlines.length; i++) {
			length = getRandomBeamlineLength();
			beamlines[i] = new Beamline("bl" + i, startZ, startZ + length);
			startZ = startZ + length;
		}

		// create z plot listener
		final ZPlotListener zPlotListener = new ZPlotListener() {

			public void tooltipShown(ZPlotEvent event) {
				// TODO Auto-generated method stub

			}

			public void zoomCompleted(ZPlotEvent event) {
				// TODO Auto-generated method stub

			}

		};

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				JFrame f = new JFrame("Test");

				JPanel p = new JPanel();

				JButton b = new JButton("Update");

				f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				f.setPreferredSize(new Dimension(1024, 768));

				final int X_PLOT_INDEX = 0;
				final int Y_PLOT_INDEX = 1;

				// create z plot
				ZPlot zPlot = new ZPlot(2);

				// zPlot.labelDevices(bpms, beamlines);
				zPlot.setGap(70);

				XYPlot xPlot = zPlot.getSubplot(X_PLOT_INDEX);

				ValueAxis xPlotDomainAxis = new NumberAxis();
				xPlotDomainAxis.setTickLabelsVisible(true);
				
				zPlot.setSubplotDomainAxis(X_PLOT_INDEX, xPlotDomainAxis);

				zPlot.setDevices(xPlot, bpms, 0, RendererType.FEATHER);
				zPlot.setDevices(xPlot, largeDispBPMs, 1, RendererType.FEATHER);
				zPlot.setDevices(xPlot, markers, 2, RendererType.FEATHER);
				{
					ZPlotFeatherRenderer renderer = (ZPlotFeatherRenderer) xPlot
							.getRenderer(1);
					renderer.setBaseShapesVisible(true);

					renderer = (ZPlotFeatherRenderer) xPlot.getRenderer(2);
					renderer.setBaseShapesVisible(true);
				}

				xPlot.getRangeAxis().setLabel("x");
				xPlot.getRangeAxis().setRange(-10, 1000);

				XYPlot yPlot = zPlot.getSubplot(Y_PLOT_INDEX);
				zPlot.setDevices(yPlot, toroids, 0, RendererType.LINE);
				zPlot.setDevices(yPlot, stoppers, 1, RendererType.LINE);
				{
					XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) yPlot
							.getRenderer(1);
					renderer.setBaseLinesVisible(false);
				}
				zPlot.setDevices(yPlot, obstructions, 2, RendererType.LINE);

				yPlot.getRangeAxis().setRange(0, 5000);

				// -->
				// add in alarmed magnets
				zPlot.setDevices(xPlot, xmagnets, -1, RendererType.LINE);
				{
					XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xPlot
							.getRenderer(xPlot.getDatasetCount() - 1);
					renderer.setBaseLinesVisible(false);
				}

				zPlot.setDevices(yPlot, ymagnets, -1, RendererType.LINE);
				{
					XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) yPlot
							.getRenderer(yPlot.getDatasetCount() - 1);
					renderer.setBaseLinesVisible(false);
					renderer.setBaseShapesFilled(false);
				}

				//zPlot.setCartoonDevices(cartoonDevices);

				// label subplots
				zPlot.setSubplotLabel(X_PLOT_INDEX, "X Plot");

				// create plot panel
				final ZPlotPanel zPlotPanel = new ZPlotPanel(p, zPlot);
				zPlotPanel.addZPlotListener(zPlotListener);

				b.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						zPlotPanel.repaint();
					}

				});

				f.setLayout(new BorderLayout());
				f.add(p, BorderLayout.CENTER);
				f.add(b, BorderLayout.SOUTH);

				f.pack();
				// does repaint
				f.setVisible(true);
			}

		});
	}
}
