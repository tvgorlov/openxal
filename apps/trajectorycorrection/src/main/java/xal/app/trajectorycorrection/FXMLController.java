/*
 * FXMLControler.java
 *
 * Created by Natalia Milas on 07.07.2017
 *
 * Copyright (c) 2017 European Spallation Source ERIC
 * Tunavägen 20
 * Lund, Sweden
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package xal.app.trajectorycorrection;

/**
 * Main Controller Application
 * @author nataliamilas
 * 06-2017
 */


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import xal.ca.ConnectionException;
import xal.ca.GetException;
import xal.ca.PutException;

public class FXMLController implements Initializable {
   
    //Creates the Accelerator
    public xal.smf.Accelerator accl = xal.smf.data.XMLDataManager.acceleratorWithPath("/Users/nataliamilas/projects/openxal/site/optics/design/main.xal");
    //public xal.smf.Accelerator accl = xal.smf.data.XMLDataManager.loadDefaultAccelerator();
    public TrajectoryArray DisplayTraj = new TrajectoryArray();//Trajectory to be displayed on the plot
    public List<CorrectionBlock> CorrectionElements = new ArrayList<>(); //List of defined Blocks
    public List<CorrectionBlock> CorrectionElementsSelected = new ArrayList<>(); //List selected blocks (can be used in correction)
    public CorrectionMatrix CorrectTraj;//Stores values for the trajectory correction part (1-to-1 correction)
    public List<CorrectionSVD> CorrectSVDMatrix;//Stores values for the trajectory correction part (SVD correction)
 
    //Create list of ref traj to be display on the table
    private final ObservableList<RefTrajectoryFile> refTrajData=FXCollections.observableArrayList(
                    FXCollections.observableArrayList(
                            new RefTrajectoryFile("Zero","/Users/nataliamilas/NetBeansProjects/TrajectoryCorrection/ZeroTrajectory.csv",true),
                            new RefTrajectoryFile("Golden","/Users/nataliamilas/NetBeansProjects/TrajectoryCorrection/GoldenTrajectory.csv",false)
                    ));
    
    private boolean progressSVD = false;
    
    //Contex Menu for the reference trajectory table
    @FXML
    private MenuItem contexMenuSave;
    @FXML
    private MenuItem contexMenuLoad;
    @FXML
    private MenuItem contexMenuShow;

    @FXML
    private MenuItem exitMenu;
    @FXML
    private ToggleGroup group1;
    @FXML
    private ToggleGroup groupSVD;
    @FXML
    private TableView<RefTrajectoryFile> tableView;
    @FXML
    private TableColumn<RefTrajectoryFile,Boolean> selectColumn;
    @FXML
    private TableColumn<RefTrajectoryFile,String> nameColumn;
    @FXML
    private TableColumn<RefTrajectoryFile,String> fileColumn;
    @FXML
    private RadioButton radioButtonHor;
    @FXML
    private RadioButton radioButtonVer;
    @FXML
    private RadioButton radioButtonHorVer;
    @FXML
    private Button buttonPairBPMCorrector;
    @FXML
    private Button buttonMeasureResponse1to1;
    @FXML
    private Button buttonCorrect1to1;
    @FXML
    private TextField textFieldCorrFactor1to1;
    @FXML
    private RadioButton radioButtonHorSVD;
    @FXML
    private RadioButton radioButtonVerSVD;
    @FXML
    private RadioButton radioButtonHorVerSVD;
    @FXML
    private Button buttonMeasureResponseSVD;
    @FXML
    private Button buttonShowSingularValues;
    @FXML
    private TextField textFieldCorrFactorSVD;
    @FXML
    private TextField textFieldSingularValCut;
    @FXML
    private Button buttonCorrectSVD;
    @FXML
    private Button buttonCalcCorrectSVD;
    @FXML
    private TextArea textArea1to1;
    @FXML
    private TextArea textAreaSVDBPM;
    @FXML
    private TextArea textAreaSVDDialog;
    @FXML
    private ProgressBar progressBarCorrection;
    @FXML
    private Label labelProgressCorrection;
   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Set elements not visible at start
        labelProgressCorrection.setVisible(false);
        progressBarCorrection.setVisible(false);
        buttonCorrect1to1.setDisable(true); 
        buttonMeasureResponse1to1.setDisable(true);
        
        //Fill the initial BPMList (first sequece by desfault)
        List<xal.smf.AcceleratorSeq> seqItem = accl.getSequences();
        
        //Defines the MEBT correction block
        CorrectionBlock correctionMEBT = new CorrectionBlock();
        correctionMEBT.setBlockBPM(seqItem.get(0).getAllNodesOfType("BPM"));
        correctionMEBT.setBlockHC(seqItem.get(0).getAllNodesOfType("DCH"));
        correctionMEBT.setBlockVC(seqItem.get(0).getAllNodesOfType("DCV"));
        correctionMEBT.setBlockName("blockMEBT");
        CorrectionElements.add(correctionMEBT);
        
        //Defines the DTL correction block
        correctionMEBT = new CorrectionBlock();
        correctionMEBT.setBlockBPM(seqItem.get(1).getAllNodesOfType("BPM"));
        correctionMEBT.setBlockHC(seqItem.get(1).getAllNodesOfType("DCH"));
        correctionMEBT.setBlockVC(seqItem.get(1).getAllNodesOfType("DCV"));
        correctionMEBT.setBlockName("blockDTL");
        CorrectionElements.add(correctionMEBT);
        
        //Defines the SPK correction block
        correctionMEBT = new CorrectionBlock();
        correctionMEBT.setBlockBPM(seqItem.get(2).getAllNodesOfType("BPM"));
        correctionMEBT.setBlockHC(seqItem.get(2).getAllNodesOfType("DCH"));
        correctionMEBT.setBlockVC(seqItem.get(2).getAllNodesOfType("DCV"));
        correctionMEBT.setBlockName("blockSPK");
        CorrectionElements.add(correctionMEBT);
                
        //Configure table of reference trajectories
        selectColumn.setEditable(true);
        nameColumn.setCellValueFactory(new PropertyValueFactory<RefTrajectoryFile,String>("name"));
        fileColumn.setCellValueFactory(new PropertyValueFactory<RefTrajectoryFile,String>("fileName"));
        //selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setCellValueFactory(new PropertyValueFactory<RefTrajectoryFile,Boolean>("select"));
        //create listeners for the Checkboxes
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
            //Callback routine that toggles the checkbox in the table and read the base reference trajectory
            @Override
            public ObservableValue<Boolean> call(Integer param) {

                if(refTrajData.get(param).isSelected()){
                    for(int i=0; i<refTrajData.size(); i++){
                        if(i != param){
                            refTrajData.get(i).setSelected(false);
                        }
                    }
                    //read new reference trajectory file
                    try {
                        DisplayTraj.readReferenceTrajectoryFromFile(accl, accl.getAllNodesOfType("BPM"), (String) refTrajData.get(param).getFileName());
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } 
                if(refTrajData.get(param).isSelected()==false){
                    int isChecked = 0;
                    for(int i=0; i<refTrajData.size(); i++){
                        if(refTrajData.get(i).isSelected()){
                            isChecked++;
                        }
                    }
                    if(isChecked ==0){
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("You have to select one reference trajecotry file. If you want to see the real trajectory please select the Zero file.");

                        alert.show();
                        refTrajData.get(param).setSelected(true);
                    }
                }
                
                return refTrajData.get(param).selectProperty();
            }
        }));
        //load zero and golden orbit
        tableView.setItems(refTrajData);               
        
    }

       
    //handles table context menu for saving a new reference
    @FXML
    public void handleContextMenuSave(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Trajectory File");

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CVS files (*.cvs)", "*.cvs");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            refTrajData.add(new RefTrajectoryFile(selectedFile.getName(),selectedFile.getAbsolutePath(),false));
            //Save Trajecotry of the whole machine
            DisplayTraj.saveTrajectory(accl,selectedFile.getAbsolutePath());
        }
    }
    
    //handles table context menu for loading a new reference orbit
    @FXML
    public void handleContextMenuLoad(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Trajectory File");

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            refTrajData.add(new RefTrajectoryFile(selectedFile.getName(),selectedFile.getAbsolutePath(),false));

        }
    }

    //handles table context menu for deleting a entry (doesn;t allow deleting the zero orbit)
    @FXML
    public void handleContextMenuDelete(ActionEvent event) {
        //check first which entry is selected
        for(int i=1; i<refTrajData.size(); i++){
            if (refTrajData.get(i).isSelected()){
                refTrajData.remove(i);
                refTrajData.get(0).setSelected(true);
            }
        }        
        
    }
                  
    @FXML
    private void handleMenuExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleContextMenuShow(ActionEvent event) throws IOException{
        Stage stage; 
        Parent root;
        URL    url  = null;
        String sceneFile = "/fxml/PopUpPlot.fxml";
        try
        {
            stage = new Stage();
            url  = getClass().getResource(sceneFile);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(sceneFile));
            root = loader.load();
            root.getStylesheets().add("/styles/Styles.css");
            stage.setScene(new Scene(root));
            stage.setTitle("View Reference Trajectory");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tableView.getScene().getWindow());
            PopUpPlotController loginController = loader.getController();
            loginController.loggedInProperty().addListener((obs, wasLoggedIn, isNowLoggedIn) -> {
                if (isNowLoggedIn) {
                    stage.close();
                }
            });
            //setup a BPM list to show
            final List<xal.smf.impl.BPM> BPMList = new ArrayList<>();
            if(CorrectionElementsSelected.size()>0){
                CorrectionElementsSelected.forEach(item -> BPMList.addAll(item.getBlockBPM()));
            } else {
                BPMList.addAll(accl.getAllNodesOfType("BPM"));
            }
            try {
                DisplayTraj.readBPMListTrajectory(BPMList);
            } catch (ConnectionException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (GetException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            //read new reference trajectory file
            refTrajData.forEach(item -> {
                if(item.isSelected()){
                    try {
                        DisplayTraj.readReferenceTrajectoryFromFile(accl, BPMList, (String) item.getFileName());
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            loginController.updatePlot(DisplayTraj);
            stage.showAndWait();
        }
        catch ( IOException ex )
        {
            System.out.println( "Exception on FXMLLoader.load()" );
            System.out.println( "  * url: " + url );
            System.out.println( "  * " + ex );
            System.out.println( "    ----------------------------------------\n" );
            throw ex;
        }
        
    }  
    
    @FXML
    private void handlePairBPMCorrector(ActionEvent event) {
        List<xal.smf.impl.BPM> BPMList = new ArrayList<>();
        List<xal.smf.impl.HDipoleCorr> HCList = new ArrayList<>();
        List<xal.smf.impl.VDipoleCorr> VCList = new ArrayList<>();
                
        for(CorrectionBlock item: CorrectionElementsSelected){ 
             BPMList.addAll(item.getBlockBPM());
             HCList.addAll(item.getBlockHC());
             VCList.addAll(item.getBlockVC());
        }
        
        // Create pairs of BPMs and Corrector for the 1-to-1 correction scheme
        CorrectTraj = new CorrectionMatrix();
        try {
            CorrectTraj.getPairs(accl,BPMList,HCList,VCList);
        } catch (ConnectionException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GetException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //print pairs to text area
        textArea1to1.clear();
        textArea1to1.setText("Horizontal pairs: \n");
        for(xal.smf.impl.BPM item : BPMList){
            for(xal.smf.impl.BPM bpm : CorrectTraj.HC.keySet()){
                if(item == bpm){
                    textArea1to1.setText(textArea1to1.getText()+bpm.toString()+" : "+CorrectTraj.HC.get(bpm).toString()+"\n");   
                }
            }
        }
        textArea1to1.setText(textArea1to1.getText()+"Vertical pairs: \n");
        for(xal.smf.impl.BPM item : BPMList){
            for(xal.smf.impl.BPM bpm : CorrectTraj.VC.keySet()){
                if(item == bpm){
                    textArea1to1.setText(textArea1to1.getText()+bpm.toString()+" : "+CorrectTraj.VC.get(bpm).toString()+"\n");     
                }
            }
        }

        buttonMeasureResponse1to1.setDisable(false);
        
    }
    
    @FXML
    private void handleMeasureResponse1to1(ActionEvent event) {
        
        TextInputDialog dialog = new TextInputDialog("0.002");
        dialog.setTitle("Set Field Variation");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Corrector strength (T.m) step:");
        Optional<String> result = dialog.showAndWait();
        double resultval=0.002;
        if (result.isPresent()){
            resultval = Double.parseDouble(result.get());
            if ( resultval<= 0.0 || resultval > 0.01){
               resultval=0.002;
            }   
        }    
 
        final double Dk = resultval;
        Task<Void> task;
        task = new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
                
                int progress = 0;
                int total = CorrectTraj.HC.size() + CorrectTraj.VC.size();
                //make horizontal calibration for each BPM
                for(xal.smf.impl.BPM bpm : CorrectTraj.HC.keySet()){
                    CorrectTraj.getHCalibration(bpm, Dk);
                    //update progressbar
                    progress++;
                    updateProgress(progress,total);
                    System.out.print("Measure: "+bpm.toString()+" and "+CorrectTraj.HC.get(bpm).toString()+"\n");
                }
                
                //make vertical calibration for each BPM
                for(xal.smf.impl.BPM bpm : CorrectTraj.VC.keySet()){
                    CorrectTraj.getVCalibration(bpm, Dk);
                    //update progressbar
                    progress++;
                    updateProgress(progress,total);
                    System.out.print("Measure: "+bpm.toString()+" and "+CorrectTraj.VC.get(bpm).toString()+"\n");
                }
                
                //Enable CORRECT button
                buttonCorrect1to1.setDisable(false); 
                
                //when the scan finishes set the label and progress bar to zero
                labelProgressCorrection.setVisible(false);
                progressBarCorrection.setVisible(false);
                updateProgress(0,total);
                
                return null;
            };
            
        };
 
        Thread calibrate = new Thread(task);
        calibrate.setDaemon(true); // thread will not prevent application shutdown  
        if (result.isPresent()){
            labelProgressCorrection.setVisible(true);
            progressBarCorrection.setVisible(true);
            labelProgressCorrection.setText("Aquiring BPM responses");
            progressBarCorrection.progressProperty().bind(task.progressProperty());
            calibrate.start();
        }
        
    }
    
    @FXML
    private void handleButtonCorrect1to1(ActionEvent event) {
        
        Task<Void> task;
        task = new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
               
                double DeltaK = 0.0;
                double val = 0.0;
                double correctFactor = Double.parseDouble(textFieldCorrFactor1to1.getText())/100;
                int total = 0;
                int step = 0;
                
                if (radioButtonHor.isSelected()){
                    total = CorrectTraj.HC.size();
                } else if (radioButtonVer.isSelected()){
                    total = CorrectTraj.VC.size();
                } else if (radioButtonHorVer.isSelected()){
                    total = CorrectTraj.VC.size()+CorrectTraj.HC.size();
                }
                
                List<xal.smf.impl.BPM> BPMList = new ArrayList<>();
                for(CorrectionBlock item: CorrectionElementsSelected){ 
                    BPMList.addAll(item.getBlockBPM());
                }
                                
                //correct trajectory
                for(xal.smf.impl.BPM item : BPMList){
                    if(radioButtonHor.isSelected() || radioButtonHorVer.isSelected()){
                        if(CorrectTraj.HC.containsKey(item)){
                            val = CorrectTraj.HC.get(item).getField();                        
                            DeltaK = correctFactor*CorrectTraj.calcHCorrection(item,DisplayTraj.XRef.get(item));
                            val = val + DeltaK;
                            CorrectTraj.HC.get(item).setField(val);
                            Thread.sleep(2000);
                            step++;
                            updateProgress(step,total);
                        }
                    }
                    if(radioButtonHorVer.isSelected() || radioButtonVer.isSelected()){
                        if(CorrectTraj.VC.containsKey(item)){
                            val = CorrectTraj.VC.get(item).getField();                        
                            DeltaK = correctFactor*CorrectTraj.calcVCorrection(item,DisplayTraj.YRef.get(item));
                            val = val + DeltaK;
                            CorrectTraj.VC.get(item).setField(val);
                            Thread.sleep(2000);
                            step++;
                            updateProgress(step,total);
                        }
                    }
                }
                
                //when the scan finishes set the label and progress bar to zero
                labelProgressCorrection.setVisible(false);
                progressBarCorrection.setVisible(false);
                updateProgress(0,total);
                
                return null;
            };
            
        };
 
        labelProgressCorrection.setVisible(true);
        progressBarCorrection.setVisible(true);
        labelProgressCorrection.setText("Correcting Trajectory");
        progressBarCorrection.progressProperty().bind(task.progressProperty());
        Thread correct = new Thread(task);
        correct.setDaemon(true); // thread will not prevent application shutdown  
        correct.start();
        
    }
    
    
    @FXML
    private void handleMeasureResponseSVD(ActionEvent event) {
        
        TextInputDialog dialog = new TextInputDialog("0.002");
        dialog.setTitle("Set Field Variation");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Corrector strength (T.m) step:");
        Optional<String> result = dialog.showAndWait();
        double resultval=0.002;
        if (result.isPresent()){
            resultval = Double.parseDouble(result.get());
            if ( resultval<= 0.0 || resultval > 0.01){
               resultval=0.002;
            }   
        }    
 
        final double Dk = resultval;
        
        //print knobs
        textAreaSVDDialog.clear();
        
        
        CorrectSVDMatrix = new ArrayList<>();
        int i =0;
        for(CorrectionBlock item: CorrectionElementsSelected){ 
            CorrectSVDMatrix.add(new CorrectionSVD()); 
            CorrectSVDMatrix.get(i).defineKnobs(accl, item.getBlockBPM(),item.getBlockHC(),item.getBlockVC());
            textAreaSVDDialog.setText(textAreaSVDDialog.getText()+item.getBlockName()+"\n");
            textAreaSVDDialog.setText(textAreaSVDDialog.getText()+"Horizontal Correctors: \n");
            for(xal.smf.impl.HDipoleCorr hcorr : CorrectSVDMatrix.get(i).HC){
                textAreaSVDDialog.setText(textAreaSVDDialog.getText()+hcorr.toString()+"\n");     
            }
            textAreaSVDDialog.setText(textAreaSVDDialog.getText()+"Vertical correctors: \n");
            for(xal.smf.impl.VDipoleCorr vcorr : CorrectSVDMatrix.get(i).VC){
                textAreaSVDDialog.setText(textAreaSVDDialog.getText()+vcorr.toString()+"\n");     
            }
            i++;
        }
        
        Task<Void> task;
        task = new Task<Void>() {
            
            @Override
            protected Void call() throws Exception {
                
                progressSVD = true;
                int progress = 0;
                
                for(CorrectionSVD item: CorrectSVDMatrix){ 
                    item.measureTRMHorizontal(Dk);
                    progress++;
                    updateProgress(progress,CorrectSVDMatrix.size()*2);
                    item.measureTRMVertical(Dk);
                    progress++;
                    updateProgress(progress,CorrectSVDMatrix.size()*2);
                }
                
                //Enable SVD calculation button
                buttonCalcCorrectSVD.setDisable(false); 
                
                //when the scan finishes set the label and progress bar to zero
                labelProgressCorrection.setVisible(false);
                progressBarCorrection.setVisible(false);
                updateProgress(0,CorrectSVDMatrix.size()*2);
                
                progressSVD = false;
                
                return null;
            };
            
        };
 
        Thread calibrate = new Thread(task);
        calibrate.setDaemon(true); // thread will not prevent application shutdown  
        progressBarCorrection.progressProperty().bind(task.progressProperty());
        if (result.isPresent()){
            labelProgressCorrection.setVisible(true);
            progressBarCorrection.setVisible(true);
            labelProgressCorrection.setText("Measuring response matrix...");
            calibrate.start();
        }
        
    }
    
    @FXML
    private void handleCalculateCorrectionSVD(ActionEvent event) throws IOException {
        
        double[] kickH;
        double[] kickV;
        double[] singVal;
        double svCut;
        double correctionFactor;
        int i = 0;
        
        svCut = Double.parseDouble(textFieldSingularValCut.getText());       
        
        textAreaSVDDialog.clear();
        
        for(CorrectionSVD matrix: CorrectSVDMatrix){
            //reads a new trajectory
            try {
                DisplayTraj.readBPMListTrajectory(matrix.BPM);
            } catch (ConnectionException | GetException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            kickH = matrix.calculateHCorrection(DisplayTraj, svCut);
            kickV = matrix.calculateVCorrection(DisplayTraj, svCut);

            textAreaSVDDialog.setText(textAreaSVDDialog.getText()+"Horizontal Corrector strenghts \n");
            i = 0;
            for(xal.smf.impl.HDipoleCorr hcorr: matrix.HC){
                textAreaSVDDialog.setText(textAreaSVDDialog.getText()+hcorr.toString()+" : "+kickH[i]+"\n");
                i++;
            }
            textAreaSVDDialog.setText(textAreaSVDDialog.getText()+"Vertical Corrector strenghts \n");
            i = 0;
            for(xal.smf.impl.VDipoleCorr vcorr: matrix.VC){
                textAreaSVDDialog.setText(textAreaSVDDialog.getText()+vcorr.toString()+" : "+kickV[i]+"\n");
                i++;
            }

            singVal = matrix.getSigularValuesH();
            textAreaSVDDialog.setText(textAreaSVDDialog.getText()+"Horizontal Singular Values \n");
            for(int j = 0; j<singVal.length; j++){
                textAreaSVDDialog.setText(textAreaSVDDialog.getText()+singVal[j]+"\n");
            }
            singVal = matrix.getSigularValuesV();
            textAreaSVDDialog.setText(textAreaSVDDialog.getText()+"Vertical Singular Values \n");
            for(int j = 0; j<singVal.length; j++){
                textAreaSVDDialog.setText(textAreaSVDDialog.getText()+singVal[j]+"\n");
            }
        }
        
        //Enable SVD correction button
        buttonCorrectSVD.setDisable(false);
        
    }
    
    @FXML
    private void handleCorrectSVD(ActionEvent event) {
        
        double[] kickH;
        double[] kickV;
        double svCut;
        double correctionFactor;
        List<xal.smf.impl.HDipoleCorr> HC = new ArrayList<>();
        List<xal.smf.impl.VDipoleCorr> VC = new ArrayList<>();
        int i = 0;
        int j = 0;
        
        svCut = Double.parseDouble(textFieldSingularValCut.getText());
        correctionFactor = Double.parseDouble(textFieldCorrFactorSVD.getText())/100;
        
        for(CorrectionSVD matrix: CorrectSVDMatrix){
            //reads a new trajectory
            try {
                DisplayTraj.readBPMListTrajectory(matrix.BPM);
            } catch (ConnectionException | GetException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            kickH = matrix.calculateHCorrection(DisplayTraj, svCut);
            kickV = matrix.calculateVCorrection(DisplayTraj, svCut);

        
            double aux = 0;
            i = 0;
            if(radioButtonHorSVD.isSelected() || radioButtonHorVerSVD.isSelected()){
                for(xal.smf.impl.HDipoleCorr hcorr: matrix.HC){
                    try {
                        aux = hcorr.getField();
                    } catch (ConnectionException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (GetException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    aux = aux + correctionFactor*kickH[i];
                    i++;
                    try {
                        hcorr.setField(aux);
                    } catch (ConnectionException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (PutException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            j = 0;
            if(radioButtonVerSVD.isSelected() || radioButtonHorVerSVD.isSelected()){
                for(xal.smf.impl.VDipoleCorr vcorr: matrix.VC){
                    try {
                        aux = vcorr.getField();
                    } catch (ConnectionException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (GetException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    aux = aux + correctionFactor*kickV[j];
                    j++;
                    try {
                        vcorr.setField(aux);
                    } catch (ConnectionException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (PutException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
    }

    @FXML
    private void handleDefineCorrectionBlocks(ActionEvent event) {
        
        Stage stage; 
        Parent root;
        URL    url  = null;
        String sceneFile = "/fxml/CorrectionBlockSelection.fxml";
        try
        {
            stage = new Stage();
            url  = getClass().getResource(sceneFile);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(sceneFile));
            root = loader.load();
            root.getStylesheets().add("/styles/Styles.css");
            stage.setScene(new Scene(root));
            stage.setTitle("Add/Edit Correction Blocks");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tableView.getScene().getWindow());
            CorrectionBlockSelectionController loginController = loader.getController();
            loginController.loggedInProperty().addListener((obs, wasLoggedIn, isNowLoggedIn) -> {
                if (isNowLoggedIn) {   
                    if(loginController.getChangedSelectionList()){
                        CorrectionElements.clear();
                        CorrectionElements.addAll(loginController.getDefinedBlocks());
                        CorrectionElementsSelected.clear();
                        CorrectionElementsSelected.addAll(loginController.getSelectedBlocks());
                        textArea1to1.clear();
                        textAreaSVDBPM.clear();
                        textAreaSVDDialog.clear();
                        for(CorrectionBlock item: CorrectionElementsSelected){ 
                                //print in the text area
                                textAreaSVDBPM.setText(textAreaSVDBPM.getText()+item.getBlockName()+" :\n");
                                List<xal.smf.impl.BPM> BPMList = item.getBlockBPM();
                                for(int i=0; i<BPMList.size();i++){
                                    textArea1to1.setText(textArea1to1.getText()+BPMList.get(i).toString()+"\n");
                                    textAreaSVDBPM.setText(textAreaSVDBPM.getText()+BPMList.get(i).toString()+"\n");
                                }
                                textAreaSVDDialog.setText(textAreaSVDDialog.getText()+item.getBlockName()+"\n");
                                textAreaSVDDialog.setText(textAreaSVDDialog.getText()+"Horizontal Correctors: \n");
                                for(xal.smf.impl.HDipoleCorr hcorr : item.getBlockHC()){
                                    textAreaSVDDialog.setText(textAreaSVDDialog.getText()+hcorr.toString()+"\n");     
                                }
                                textAreaSVDDialog.setText(textAreaSVDDialog.getText()+"Vertical correctors: \n");
                                for(xal.smf.impl.VDipoleCorr vcorr : item.getBlockVC()){
                                    textAreaSVDDialog.setText(textAreaSVDDialog.getText()+vcorr.toString()+"\n");     
                                }
                        }
                        buttonMeasureResponse1to1.setDisable(true);
                        buttonShowSingularValues.setDisable(true);
                        buttonCalcCorrectSVD.setDisable(true);
                        buttonCorrectSVD.setDisable(true);
                        buttonCorrect1to1.setDisable(true);
                    }
                    stage.close();
                }
            });
            loginController.initiateElements(accl,CorrectionElements,CorrectionElementsSelected);
            stage.showAndWait();
        }
        catch ( IOException ex )
        {
            System.out.println( "Exception on FXMLLoader.load()" );
            System.out.println( "  * url: " + url );
            System.out.println( "  * " + ex );
            System.out.println( "    ----------------------------------------\n" );
            try {
                throw ex;
            } catch (IOException ex1) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
    }

  
}
