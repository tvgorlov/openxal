package edu.stanford.lcls.modelmanager.dbmodel;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import xal.model.ModelException;
import xal.model.probe.traj.EnvelopeProbeState;
import xal.sim.scenario.Scenario;
import xal.smf.Accelerator;
import xal.smf.AcceleratorNode;
import xal.smf.impl.Electromagnet;
import xal.smf.impl.RfCavity;
import xal.tools.beam.Twiss;
import xal.tools.data.DataAdaptor;
import xal.tools.data.DataTable;
import xal.tools.data.GenericRecord;
import xal.tools.data.SortOrdering;
import xal.tools.messaging.MessageCenter;
import xal.tools.xml.XmlDataAdaptor;
import edu.stanford.lcls.modelmanager.view.ModelPlotData;
import edu.stanford.lcls.xal.model.RunModel;
import edu.stanford.lcls.xal.model.RunModelConfiguration;
import edu.stanford.slac.Message.Message;

/**
 * BrowserModel is the main document model.
 * 
 * @author 
 */
public class BrowserModel {


	final protected MessageCenter MESSAGE_CENTER;
	final protected BrowserModelListener EVENT_PROXY;
	final static public SimpleDateFormat machineModelDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	protected PersistentStore PERSISTENT_STORE;
	protected Connection _connection;
	protected boolean _hasConnected = false;
	protected String _user="MACHINE_MODEL";
	protected String _databaseURL;
	
	protected List<MachineModel> _allMachineModels;
	protected List<MachineModel> _fetchedMachineModels;
	protected List<MachineModel> _goldMachineModels;
	
	protected String _referenceMachineModelID;
	protected MachineModel _referenceMachineModel;
	protected MachineModelDetail[] _referenceMachineModelDetail;
	protected MachineModelDevice[] _referenceMachineModelDevice;
	
	protected String _selectedMachineModelID;
	protected MachineModel _selectedMachineModel;
	protected MachineModelDetail[] _selectedMachineModelDetail;
	protected MachineModelDevice[] _selectedMachineModelDevice;
	
	protected MachineModel _runMachineModel;
	protected MachineModelDetail[] _runMachineModelDetail;
	protected MachineModelDevice[] _runMachineModelDevice;
	
	protected MachineModel _goldMachineModel;
	protected MachineModelDetail[] _goldMachineModelDetail;
	
	private String plotFunctionID1;
	private String plotFunctionID2;
	
	protected RunModel rm;
	
	private final String autoRunID = "RUN";
	protected String modelMode;
	protected Accelerator acc;
	
	protected boolean stateReady = false;
	protected RunState runState = RunState.NONE;
	
	public static enum RunState {
		NONE, FETCHED_DATA, RUN
	}
	
	
	/**
	 * Constructor
	 */
	public BrowserModel(Accelerator acc) {
		this.acc = acc;
		rm = new RunModel(acc);
		modelMode = rm.getBeamlines().get(0);
		MESSAGE_CENTER = new MessageCenter("Browser Model");
		EVENT_PROXY = MESSAGE_CENTER.registerSource(this,
				BrowserModelListener.class);
	}
	
	public boolean getStateReady() {
		return stateReady;
	}

	/**
	 * Add a listener of model events from this model.
	 * 
	 * @param listener
	 *            the listener to add for receiving model events.
	 */
	public void addBrowserModelListener(final BrowserModelListener listener) {
		MESSAGE_CENTER.registerTarget(listener, this,
				BrowserModelListener.class);
	}

	/**
	 * Remove the listener from receiving model events from this model.
	 * 
	 * @param listener
	 *            the listener to remove from receiving model events.
	 */
	public void removeBrowserModelListener(final BrowserModelListener listener) {
		MESSAGE_CENTER.removeTarget(listener, this, BrowserModelListener.class);
	}

	/**
	 * Set the database connection to the one specified.
	 * 
	 * @param connection
	 *            the new database connection
	 * @throws ParseException
	 * @throws SQLException
	 */
	public void setDatabaseConnection(final Connection connection) throws SQLException,
			ParseException {
		_hasConnected = false;
		//_user = null;
		_databaseURL = null;
		_connection = connection;
		_hasConnected = true;
		_databaseURL = DataManager.url;
		final URL configurationURL = getClass()
				.getResource("configuration.xml");
		final DataAdaptor configurationAdaptor = XmlDataAdaptor.adaptorForUrl(
				configurationURL, false).childAdaptor("Configuration");
		final DataAdaptor persistentStoreAdaptor = configurationAdaptor
				.childAdaptor("persistentStore");
		PERSISTENT_STORE = new PersistentStore(persistentStoreAdaptor);
		
		EVENT_PROXY.modelStateChanged(this, BrowserModelListener.BrowserModelAction.CONNECTED);
		fetchAllMachineModel();
	}	

	public String getDataBaseURL() {
		return _databaseURL;
	}

	public String getConnectUser() {
		return _user;
	}

	public boolean hasConnected() {
		return _hasConnected;
	}


	/**
	 * Get the array of machine model that had been fetched.
	 */
	public List<MachineModel>getAllMachineModel() {
		return _allMachineModels;
	}

	public List<MachineModel> getFetchedMachineModel() {
		return _fetchedMachineModels;
	}

	public MachineModel getReferenceMachineModel() {
		return _referenceMachineModel;
	}

	public MachineModel getSelectedMachineModel() {
		return _selectedMachineModel;
	}

	public MachineModel getRunMachineModel() {
		return _runMachineModel;
	}
	
	public MachineModel getGoldMachineModel() {
		return _goldMachineModel;
	}

	public List<MachineModel> getGoldMachineModels() {
		return _goldMachineModels;
	}

	public MachineModelDetail[] getSelectedMachineModelDetail() {
		return _selectedMachineModelDetail;
	}

	public MachineModelDevice[] getSelectedMachineModelDevice() {
		return _selectedMachineModelDevice;
	}
	
	public MachineModelDetail[] getReferenceMachineModelDetail() {
		return _referenceMachineModelDetail;
	}
	
	public MachineModelDevice[] getReferenceMachineModelDevice() {
		return _referenceMachineModelDevice;
	}
	
	public MachineModelDevice[] getRunMachineModelDevice() {
		return _runMachineModelDevice;
	}
	
	public void setPlotFunctionID1(String plotFunctionID1) {
		this.plotFunctionID1 = plotFunctionID1;
	}

	public String getPlotFunctionID1() {
		return plotFunctionID1;
	}

	public RunModel getRunModel() {
		return rm;
	}

	public void setPlotFunctionID2(String plotFunctionID2) {
		this.plotFunctionID2 = plotFunctionID2;
	}

	public String getPlotFunctionID2() {
		return plotFunctionID2;
	}

	public String getModelMode() {
		return modelMode;
	}

	/** beamline selection */
	public void setModelMode(String _modelMode) throws SQLException {
		if (!modelMode.equals(_modelMode) && runState.equals(RunState.FETCHED_DATA)) resetRunData();
		modelMode = _modelMode;
		rm.setModelMode(modelMode);
		filterMachineModelInMode(_allMachineModels, _goldMachineModels, modelMode);
	}

	public boolean isGold() {
		return _referenceMachineModel != null ? _referenceMachineModel.getPropertyValue("GOLD").equals("PRESENT") : false;
	}
	
	/**
	 * Fetch the machine models
	 */

	public void fetchMachineModelInRange(final java.util.Date startTime,
			final java.util.Date endTime, final String modelMode)
			throws SQLException {
		List<MachineModel> _machineModel_tmp1 = PERSISTENT_STORE
				.fetchMachineModelsInRange(_connection, startTime, endTime);
		// If goldMachineModel isn't in, add it into the _machineModel
		
		int originalSize = _machineModel_tmp1.size();
		for (int j = 0; j < _goldMachineModels.size(); j++) {
			String goldMachineModelID = (String) _goldMachineModels.get(j)
					.getPropertyValue("ID");
			boolean isIncludeLastDesignMachineModel = false;
			for (int i = 0; i < originalSize; i++) {
				if (_machineModel_tmp1.get(i).getPropertyValue("ID").equals(
						goldMachineModelID))
					isIncludeLastDesignMachineModel = true;
			}
			if (!isIncludeLastDesignMachineModel) _machineModel_tmp1.add(_goldMachineModels.get(j));
		}
		_allMachineModels = _machineModel_tmp1;
		filterMachineModelInMode(_allMachineModels, _goldMachineModels,	modelMode);
	}

	public void fetchAllMachineModel() throws SQLException, ParseException {
		_allMachineModels = PERSISTENT_STORE.fetchAllMachineModels(_connection);
		_goldMachineModels = fetchGoldMachineModel(_allMachineModels);
		filterMachineModelInMode(_allMachineModels, _goldMachineModels,
				modelMode);
	}

	/**
	 * get the last design machine model
	 */
	public List<MachineModel> fetchGoldMachineModel(List<MachineModel> allMachineModel)
			throws ParseException, SQLException {
		List<MachineModel> goldMachineModels = new ArrayList<MachineModel>();
		for (int i = 0; i < allMachineModel.size(); i++) {
			if (allMachineModel.get(i).getPropertyValue("GOLD").equals("PRESENT"))
				goldMachineModels.add(allMachineModel.get(i));
		}
		boolean containDesignGold;
		for (String modelMode : rm.getBeamlines()) {
			containDesignGold = false;
			for (int j = 0; j < goldMachineModels.size(); j++) {
				if (goldMachineModels.get(j).getPropertyValue("MODEL_MODES_ID")
						.equals(String.valueOf(modelMode))
						&& goldMachineModels.get(j).getPropertyValue(
								"RUN_SOURCE_CHK").equals("DESIGN")) {
					containDesignGold = true;
					break;
				}
			}
			// If this mode doesn't contain gold model, add the last design to
			// gold.
			if (!containDesignGold) {
				MachineModel lastDesignMachineModel = null;
				for (int j = 0; j < allMachineModel.size(); j++) {
					if (!allMachineModel.get(j).getPropertyValue("ID").equals(
							autoRunID)
							&& allMachineModel.get(j).getPropertyValue(
									"RUN_SOURCE_CHK").equals("DESIGN")
							&& allMachineModel.get(j)
									.getPropertyValue("MODEL_MODES_ID") != null
							&& allMachineModel.get(j).getPropertyValue(
									"MODEL_MODES_ID").equals(
									String.valueOf(modelMode))) {
						if (lastDesignMachineModel == null)
							lastDesignMachineModel = allMachineModel.get(j);
						else {
							java.util.Date d1 = machineModelDateFormat
									.parse(allMachineModel.get(j).getPropertyValue(
											"RUN_ELEMENT_DATE").toString());
							java.util.Date d2 = machineModelDateFormat
									.parse(lastDesignMachineModel
											.getPropertyValue(
													"RUN_ELEMENT_DATE")
											.toString());
							if (d1.after(d2))
								lastDesignMachineModel = allMachineModel.get(j);
						}
					}
				}
				if (lastDesignMachineModel != null)
					goldMachineModels.add(lastDesignMachineModel);
			}
		}
		return goldMachineModels;
	}

	public MachineModel getGoldMachineModel(List<MachineModel> goldMachineModels, String modelMode, String runSource) throws SQLException {
		MachineModel goldModel = null;
		for (int i = 0; i < goldMachineModels.size(); i++) {
			if (goldMachineModels.get(i).getPropertyValue("MODEL_MODES_ID").equals(modelMode)
					&& goldMachineModels.get(i).getPropertyValue("RUN_SOURCE_CHK").equals(runSource))
				goldModel = goldMachineModels.get(i);
		}
		if (goldModel == null && runSource.equals("EXTANT")) {
			for (int i = 0; i < goldMachineModels.size(); i++) {
				if (goldMachineModels.get(i).getPropertyValue("MODEL_MODES_ID").equals(modelMode)
						&& goldMachineModels.get(i).getPropertyValue("RUN_SOURCE_CHK").equals("DESIGN"))
					goldModel = goldMachineModels.get(i);
			}
		}
		return goldModel;
	}

	public void setAccelerator(Accelerator accelerator) {
		acc = accelerator;
		rm = new RunModel(acc);
		modelMode = rm.getBeamlines().get(0);
		EVENT_PROXY.modelStateChanged(this, BrowserModelListener.BrowserModelAction.ACC_LOAD);
		try {
			setModelMode(modelMode);
		} catch (SQLException e) {
			Message.error("SQL connection error!");
		}
	}
	
	/**
	 * Update the accelerator from the model manager objects.
	 */
	public void updateAccelerator() {
		updateTables();
		if (_selectedMachineModelDevice == null) {
			return;
		}
		for (MachineModelDevice dev : _selectedMachineModelDevice) {
		    //getting parameter info
		    final String nodeId = (String)dev.getPropertyValue("ELEMENT_NAME"); 
		    final AcceleratorNode node = acc.getNodeWithId(nodeId);
			final Object prop = dev.getPropertyValue("DEVICE_PROPERTY");
			final double val = Double.parseDouble((String) dev.getPropertyValue("DEVICE_VALUE"));
			//checking for parameter type
			switch((String) prop){
			case "B":          
                ((Electromagnet)node).setDfltField(val);
                break;
			case "P":              
                ((RfCavity)node).setDfltCavPhase(val);
                break;
			case "A":              
                ((RfCavity)node).setDfltCavAmp(val);
                break;
			case "APRX":
			    node.getAper().setAperX(val);
			    break;
			case "MISX":
			    node.getAlign().setX(val);
			    break;
			case "MISY":
			    node.getAlign().setY(val);
			    break;
			case "MISZ":
			    node.getAlign().setZ(val);
			    break;
			case "ROTX":
			    node.getAlign().setPitch(val);
			    break;
			case "ROTY":
			    node.getAlign().setYaw(val);
			    break;
			case "ROTZ":
			    node.getAlign().setRoll(val);
			    break;
			case "ENBL":
			    node.setStatus((val == 1));
			    break;
			}
		}
	}

	/**
	 * Updates the tables in accelerator's edit context with values from the probe. 
	 */
	private void updateTables() {
		EnvelopeProbeState probeState = rm.getProbe().cloneCurrentProbeState();
		final String beamline = (probeState.getElementId()!=null && !probeState.getElementId().isEmpty()) ? 
				probeState.getElementId() : acc.getSequences().get(0).getId();
				
		DataTable twissTable = acc.editContext().getTable("twiss");
		for (GenericRecord r : twissTable.getRecords(new SortOrdering())) {
			if (beamline.equals(r.stringValueForKey("name"))) twissTable.remove(r);
		}
		Twiss[] twiss = probeState.getCovarianceMatrix().computeTwiss();
		for (int i = 0; i < 3; i++) {
			String axis = new String[] {"x","y","z"}[i];				
			GenericRecord record = new GenericRecord(twissTable);
			record.setValueForKey(beamline, "name");
			record.setValueForKey(axis, "coordinate");
			record.setValueForKey(twiss[i].getAlpha(), "alpha");
			record.setValueForKey(twiss[i].getBeta(), "beta");
			record.setValueForKey(twiss[i].getEmittance(), "emittance");
			twissTable.add(record);
		}
		
		DataTable locationTable = acc.editContext().getTable("location");
		for (GenericRecord r : locationTable.getRecords(new SortOrdering())) {
			if (beamline.equals(r.stringValueForKey("name"))) locationTable.remove(r);
		}
		GenericRecord record = new GenericRecord(locationTable);
		record.setValueForKey(beamline, "name");
		record.setValueForKey(probeState.getKineticEnergy(), "W");
		record.setValueForKey("PROTON", "species");
		locationTable.add(record);

		DataTable beamTable = acc.editContext().getTable("beam");
		GenericRecord beamrecord = beamTable.getRecords(new SortOrdering()).get(0);
		beamrecord.setValueForKey(probeState.getBeamCurrent(), "current");
		beamrecord.setValueForKey(probeState.getBunchFrequency(), "bunchFreq");
	}

	/**
	 * filter machine models
	 */
	public void filterMachineModelInMode(List<MachineModel> allMachineModels,
			List<MachineModel> goldMachineModels, String modelMode)
			throws SQLException {
		// add run if run is not null

		List<MachineModel> fetchedMachineModels;
		if (modelMode == null || RunModel.DEFAULT_BEAMLINE_TEXT.equals(modelMode)) {
			fetchedMachineModels = new ArrayList<>(allMachineModels);
		} else {
			fetchedMachineModels = new ArrayList<>();
			
			for (int i = 0; i < allMachineModels.size(); i++) {
				MachineModel machineModel = allMachineModels.get(i);
				if (machineModel.getPropertyValue("MODEL_MODES_ID") != null) {
					if (modelMode.equals((String)machineModel.getPropertyValue("MODEL_MODES_ID"))) {
						fetchedMachineModels.add(machineModel);						
					}
				}
			}
		}
		Collections.sort(fetchedMachineModels, new Sort(Sort.DOWM));

		_fetchedMachineModels = fetchedMachineModels;
		_goldMachineModel = getGoldMachineModel(goldMachineModels, rm.getModelMode(), "DESIGN");
		setGoldModel(_goldMachineModel);
		_referenceMachineModel = getGoldMachineModel(goldMachineModels,	rm.getModelMode(), "EXTANT");
		_selectedMachineModel = null;
		_selectedMachineModelDetail = null;
		_selectedMachineModelDevice = null;
		setReferenceModel(_referenceMachineModel);
		ModelPlotData.clearRange();
		
		stateReady = true;
		EVENT_PROXY.modelStateChanged(this, BrowserModelListener.BrowserModelAction.FETCHED);
	}

	public void setSelectedMachineModel(MachineModel referenceMachineModel, MachineModel selectedMachineModel) throws SQLException {
		if (_selectedMachineModel != null && selectedMachineModel == null) {
			_selectedMachineModel = null;
			_selectedMachineModelDetail = null;
			_selectedMachineModelDevice = null;
		} else if (_selectedMachineModel != selectedMachineModel) {
			setSelectedModel(selectedMachineModel);
		} else if (_referenceMachineModel != referenceMachineModel && _selectedMachineModel == null) {
			setReferenceModel(referenceMachineModel);
		} else if (_referenceMachineModel != referenceMachineModel && _selectedMachineModel != null) {
			setReferenceModel(referenceMachineModel);
			setSelectedModel(_selectedMachineModel);
		}
		EVENT_PROXY.modelStateChanged(this, BrowserModelListener.BrowserModelAction.MODEL_SELECTED);
	}

	public void setGoldModel(MachineModel goldMachineModel)
			throws SQLException {
		_goldMachineModel = goldMachineModel;
		if (goldMachineModel != null)
			_goldMachineModelDetail = PERSISTENT_STORE.fetchMachineModelDetails(acc, _connection,
					Long.valueOf((String) _goldMachineModel.getPropertyValue("ID")));
	}

	public void setReferenceModel(MachineModel referenceMachineModel) throws SQLException {
		_referenceMachineModel = referenceMachineModel;
		if (referenceMachineModel == null) return;
		if (referenceMachineModel.getPropertyValue("ID").toString().equals(
				autoRunID)) {
			_referenceMachineModelDetail = _runMachineModelDetail;
			_referenceMachineModelDevice = _runMachineModelDevice;
		} else {
			_referenceMachineModelDetail = PERSISTENT_STORE.fetchMachineModelDetails(acc, _connection,
					Long.valueOf((String) _referenceMachineModel.getPropertyValue("ID")));
			_referenceMachineModelDevice = PERSISTENT_STORE.fetchMachineModelDevices(_connection, 
							Long.valueOf((String) _referenceMachineModel.getPropertyValue("ID")));
		}
		_referenceMachineModelDetail = DataManager.calculateBmag(
				_referenceMachineModelDetail, _goldMachineModelDetail);

	}

	public void setSelectedModel(MachineModel selectedMachineModel) throws SQLException {
		if (selectedMachineModel == null)
			return;
		if (_selectedMachineModel == null
				|| !_selectedMachineModel.getPropertyValue("ID").equals(
						selectedMachineModel.getPropertyValue("ID"))
				|| selectedMachineModel.getPropertyValue("ID")
						.equals(autoRunID)) {
			_selectedMachineModel = selectedMachineModel;

			if (selectedMachineModel.getPropertyValue("ID").equals(autoRunID)) {
				_selectedMachineModelDetail = _runMachineModelDetail;
				_selectedMachineModelDevice = _runMachineModelDevice;
			} else {
				_selectedMachineModelDetail = PERSISTENT_STORE.fetchMachineModelDetails(acc, _connection,
						Long.valueOf((String) selectedMachineModel.getPropertyValue("ID")));
				_selectedMachineModelDevice = PERSISTENT_STORE.fetchMachineModelDevices(_connection, 
						Long.valueOf((String) selectedMachineModel.getPropertyValue("ID")));
				// Correct some models' ZPOS
				String startElementName = _selectedMachineModelDetail[0]
						.getPropertyValue("ELEMENT_NAME").toString();
				Double startElementZPos = Double
						.valueOf(_selectedMachineModelDetail[0]
								.getPropertyValue("ZPOS").toString());
				if (_referenceMachineModel != null) {
					for (int i = 0; i < _referenceMachineModelDetail.length; i++) {
						if (_referenceMachineModelDetail[i].getPropertyValue(
								"ELEMENT_NAME").toString().equals(startElementName)) {
							Double startElementRealZPos = Double
									.valueOf(_referenceMachineModelDetail[i]
											.getPropertyValue("ZPOS").toString());
							if (!startElementRealZPos.equals(startElementZPos)) {
								Double offSet = startElementRealZPos

										- startElementZPos;
								for (int j = 0; j < _selectedMachineModelDetail.length; j++) {
									Double newElementZPos = Double
											.valueOf(_selectedMachineModelDetail[j]
													.getPropertyValue("ZPOS")
													.toString())
											+ offSet;
									_selectedMachineModelDetail[j]
											.setPropertyValue("ZPOS",
													newElementZPos.toString());
								}
							}
							break;
						}
					}
				}
			}
		}
		if (_selectedMachineModelDetail != null)
			_selectedMachineModelDetail = DataManager.calculateBmag(_selectedMachineModelDetail, _goldMachineModelDetail);
	}
	
	public void runModel(RunModelConfiguration config) throws SQLException, ModelException {
		rm.run(config);
		
		Scenario scenario = rm.getScenario();
		MachineModel _runMachineModel = DataManager.getRunMachineModel(config.getRunModelMethod(), rm.getModelMode()); // add runMachineMode to _fetchedMachineModels
		replaceRunModel(_runMachineModel, 
				DataManager.getRunMachineModeDetail(config.getRunModelMethod(), scenario), 
				DataManager.getRunMachineModeDevice(scenario));
		
		createRunModelComment(_runMachineModel, _runMachineModelDetail, rm.getModelMode());

		
		setSelectedModel(_runMachineModel);
		runState = RunState.RUN;
		EVENT_PROXY.modelStateChanged(this, BrowserModelListener.BrowserModelAction.MODEL_RUN);
	}

	public void createRunModelComment(MachineModel runMachineModel, MachineModelDetail[] runMachineModelDetail, String modelMode) {
		runMachineModel.setPropertyValue("COMMENTS", "Run "
				+ runMachineModel.getPropertyValue("RUN_SOURCE_CHK")
				+ " Model on "
				+ modelMode
				+ " beam line. And the Energy is "
				+ runMachineModelDetail[runMachineModelDetail.length - 1]
						.getPropertyValue("E") + " GeV.");
	}

	public void replaceRunModel(MachineModel runMachineModel, MachineModelDetail[] runMachineModelDetail, MachineModelDevice[] runMachineModelDevice) {
		removeRunModel();
		_allMachineModels.add(runMachineModel);
		_fetchedMachineModels.add(runMachineModel);
		
		_runMachineModel = runMachineModel;
		_runMachineModelDetail = runMachineModelDetail;
		// switch fetch Machine model
		_runMachineModelDevice = runMachineModelDevice;
		
		Collections.sort(_allMachineModels, new Sort(Sort.DOWM));
		Collections.sort(_fetchedMachineModels, new Sort(Sort.DOWM));
	}

	public void updateRunModelWithUploadID(String uploadID) {
		//add upload machine model to all machine models.
		_runMachineModel.setPropertyValue("ID", uploadID);
		_runMachineModel.setPropertyValue("GOLD", "");
		
		// refresh the run id
		if (_selectedMachineModel == _runMachineModel || _referenceMachineModel == _runMachineModel) {
			for (int i = 0; i < _selectedMachineModelDetail.length; i++) {
				_runMachineModelDetail[i].setPropertyValue("RUNS_ID", uploadID);
			}
		}
		
		_runMachineModel = null;
		_runMachineModelDetail = null;
		_runMachineModelDevice = null;
		
		EVENT_PROXY.modelStateChanged(this, BrowserModelListener.BrowserModelAction.MODEL_SAVED);
	}
	
	public void removeRunModel()
	{
		if (_runMachineModel == null) return;
		
		if (_selectedMachineModel == _runMachineModel) {
			_selectedMachineModel = null;
			_selectedMachineModelDetail = null;
			_selectedMachineModelDevice = null;
		}
		
		if (_referenceMachineModel == _runMachineModel) {
			_referenceMachineModel = null;
			_referenceMachineModelDetail = null;
			_referenceMachineModelDevice = null;
		}
			
		_fetchedMachineModels.remove(_runMachineModel);
		_allMachineModels.remove(_runMachineModel);
			
		_runMachineModel = null;
		_runMachineModelDetail = null;
		_runMachineModelDevice = null;		
	}

	public void exportToXML(final JFrame parent) {
		DataManager.exportToXML(parent, _runMachineModel, rm.getScenario());
	}

	public String uploadToDatabase(final JFrame parent) {
		String runID = DataManager.newUploadToDatabase(parent, this, _runMachineModel,
				_runMachineModelDetail, _runMachineModelDevice);
		if (runID != null) {
			// automatically update the table after a model is uploaded
			updateRunModelWithUploadID(runID);
			runState = RunState.NONE;
		}
		return runID;
	}

	public void exportDetailData(JFrame parent) {
		if (_selectedMachineModelDetail == null)
			DataManager.exportDetailData(parent, _referenceMachineModelDetail);
		else
			DataManager.exportDetailData(parent, _selectedMachineModelDetail);
	}

	public void makeGold(String comment) throws SQLException {
		DataManager.makeGold(comment, _selectedMachineModel);
	}
	
	public void closeDBConnection() {
		try {
			_connection.close();
		} catch (SQLException e) {
			Message.error("Unable to close connection to database. "+
					e.getMessage());
		}
	}

	public void fetchRunData(RunModelConfiguration config) throws SQLException, ModelException {
		Scenario scenario = rm.getScenario();
		config.initialize(scenario);
		
		MachineModel _runMachineModel = DataManager.getRunMachineModel(config.getRunModelMethod(), modelMode); // add runMachineMode to _fetchedMachineModels
		_runMachineModel.setPropertyValue("COMMENTS", "Fetched machine parameters.");
		replaceRunModel(_runMachineModel, null, DataManager.getRunMachineModeDevice(scenario));
		setSelectedModel(_runMachineModel);
		
		runState = RunState.FETCHED_DATA;
		EVENT_PROXY.modelStateChanged(this, BrowserModelListener.BrowserModelAction.RUN_DATA_FETCHED);
	}
	
	public RunState getRunState()
	{
		return runState;
	}

	public void resetRunData() {
		removeRunModel();
		
		runState = RunState.NONE;
		EVENT_PROXY.modelStateChanged(this, BrowserModelListener.BrowserModelAction.RUN_DATA_RESET);
	}

}