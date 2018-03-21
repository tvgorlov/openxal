/*
 * Copyright (C) 2018 European Spallation Source ERIC
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package xal.app.trajectorycorrection;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import xal.smf.Accelerator;
import xal.smf.AcceleratorSeq;
import xal.smf.impl.BPM;
import xal.smf.impl.HDipoleCorr;
import xal.smf.impl.VDipoleCorr;

/**
 * FXML Controller class
 *
 * @author nataliamilas
 */
public class CorrectionElementSelectionController implements Initializable {

    List<BPM> BPMSelection = new ArrayList<>();
    List<HDipoleCorr> HCSelection = new ArrayList<>();
    List<VDipoleCorr> VCSelection = new ArrayList<>();
    List<AcceleratorSeq> seqItem = new ArrayList<>();
    private final BooleanProperty loggedIn = new SimpleBooleanProperty();
    private final BooleanProperty selectionList = new SimpleBooleanProperty();
    @FXML
    private GridPane gridPaneBPM;
    @FXML
    private GridPane gridPaneHC;
    @FXML
    private GridPane gridPaneVC;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        gridPaneBPM.setAlignment(Pos.CENTER);
        gridPaneBPM.setHgap(10);
        gridPaneBPM.setVgap(10);
        gridPaneBPM.setPadding(new Insets(25, 25, 25, 25));

        gridPaneHC.setAlignment(Pos.CENTER);
        gridPaneHC.setHgap(10);
        gridPaneHC.setVgap(10);
        gridPaneHC.setPadding(new Insets(25, 25, 25, 25));

        gridPaneVC.setAlignment(Pos.CENTER);
        gridPaneVC.setHgap(10);
        gridPaneVC.setVgap(10);
        gridPaneVC.setPadding(new Insets(25, 25, 25, 25));

    }

    public BooleanProperty loggedInProperty() {
        return loggedIn;
    }

    public final boolean isLoggedIn() {
        return loggedInProperty().get();
    }

    public final void setLoggedIn(boolean loggedIn) {
        loggedInProperty().set(loggedIn);
    }

    public BooleanProperty changedSelectionListProperty() {
        return selectionList;
    }

    public final boolean isChangedSelectionList() {
        return changedSelectionListProperty().get();
    }

    public final void setChangedSelectionList(boolean selectionList) {
        changedSelectionListProperty().set(selectionList);
    }

    public boolean getChangedSelectionList() {
        return selectionList.getValue();
    }

    public List<BPM> getBPMSelectionList() {
        return BPMSelection;
    }

    public List<HDipoleCorr> getHCSelectionList() {
        return HCSelection;
    }

    public List<VDipoleCorr> getVCSelectionList() {
        return VCSelection;
    }

    public void populateElementGrid(Accelerator accl) {
        int col = 0;
        int row = 0;
        seqItem = accl.getSequences();
        List<BPM> bpm = new ArrayList<>();
        List<HDipoleCorr> HC = new ArrayList<>();
        List<VDipoleCorr> VC = new ArrayList<>();
        Text seqName;
        CheckBox checkBoxItem;

        seqItem.remove(accl.getSequence("LEBT"));
        seqItem.remove(accl.getSequence("RFQ"));

        //seqItem.forEach(Seq -> {   
        //    if(Seq.toString().equals("LEBT")){
        //        seqItem.remove(Seq);
        //    }
        //    if(Seq.toString().equals("RFQ")){
        //        seqItem.remove(Seq);
        //    }
        //});
        for (AcceleratorSeq Seq : seqItem) {
            seqName = new Text(Seq.toString());
            seqName.setFont(Font.font("System", FontWeight.BOLD, 16));
            GridPane.setConstraints(seqName, col, row);
            gridPaneBPM.add(seqName, col, row);
            seqName = new Text(Seq.toString());
            seqName.setFont(Font.font("System", FontWeight.BOLD, 16));
            GridPane.setConstraints(seqName, col, row);
            gridPaneHC.add(seqName, col, row);
            seqName = new Text(Seq.toString());
            seqName.setFont(Font.font("System", FontWeight.BOLD, 16));
            GridPane.setConstraints(seqName, col, row);
            gridPaneVC.add(seqName, col, row);
            row++;
            checkBoxItem = new CheckBox("ALL IN");
            GridPane.setConstraints(checkBoxItem, col, row);
            gridPaneBPM.add(checkBoxItem, col, row);
            checkBoxItem = new CheckBox("ALL IN");
            GridPane.setConstraints(checkBoxItem, col, row);
            gridPaneHC.add(checkBoxItem, col, row);
            checkBoxItem = new CheckBox("ALL IN");
            GridPane.setConstraints(checkBoxItem, col, row);
            gridPaneVC.add(checkBoxItem, col, row);
            row++;
            checkBoxItem = new CheckBox("ALL OUT");
            GridPane.setConstraints(checkBoxItem, col, row);
            gridPaneBPM.add(checkBoxItem, col, row);
            checkBoxItem = new CheckBox("ALL OUT");
            GridPane.setConstraints(checkBoxItem, col, row);
            gridPaneHC.add(checkBoxItem, col, row);
            checkBoxItem = new CheckBox("ALL OUT");
            GridPane.setConstraints(checkBoxItem, col, row);
            gridPaneVC.add(checkBoxItem, col, row);
            bpm = Seq.getAllNodesOfType("BPM");
            HC = Seq.getAllNodesOfType("DCH");
            VC = Seq.getAllNodesOfType("DCV");
            for (BPM item : bpm) {
                row++;
                checkBoxItem = new CheckBox(item.toString());
                if (!(item.getChannel("xAvg").isConnected() && item.getChannel("yAvg").isConnected())) {
                    checkBoxItem.setDisable(true);
                }
                GridPane.setConstraints(checkBoxItem, col, row);
                gridPaneBPM.add(checkBoxItem, col, row);
            }
            row = row - bpm.size();
            for (HDipoleCorr item : HC) {
                row++;
                checkBoxItem = new CheckBox(item.toString());
                if (!item.getChannel("fieldSet").isConnected()) {
                    checkBoxItem.setDisable(true);
                }
                GridPane.setConstraints(checkBoxItem, col, row);
                gridPaneHC.add(checkBoxItem, col, row);
            }
            row = row - HC.size();
            for (VDipoleCorr item : VC) {
                row++;
                checkBoxItem = new CheckBox(item.toString());
                if (!item.getChannel("fieldSet").isConnected()) {
                    checkBoxItem.setDisable(true);
                }
                GridPane.setConstraints(checkBoxItem, col, row);
                gridPaneVC.add(checkBoxItem, col, row);
            }
            col++;
            row = 0;
        }

        // Add listeners to all checkbox of the first row ("ALL IN" CheckBoxes)
        CheckBox selectedNode;
        for (int i = 0; i < seqItem.size(); i++) {
            selectedNode = (CheckBox) getNodeFromGridPane(gridPaneBPM, i, 1);
            final Node seqBPMAllIn = selectedNode;
            selectedNode.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    toggleGridAllIn(seqBPMAllIn, gridPaneBPM);
                }
            });
            selectedNode = (CheckBox) getNodeFromGridPane(gridPaneHC, i, 1);
            final Node seqHCAllIn = selectedNode;
            selectedNode.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    toggleGridAllIn(seqHCAllIn, gridPaneHC);
                }
            });
            selectedNode = (CheckBox) getNodeFromGridPane(gridPaneVC, i, 1);
            final Node seqVCAllIn = selectedNode;
            selectedNode.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    toggleGridAllIn(seqVCAllIn, gridPaneVC);
                }
            });

            // Add listeners to all checkbox of the first row ("ALL OUT" CheckBoxes)
            selectedNode = (CheckBox) getNodeFromGridPane(gridPaneBPM, i, 2);
            final Node seqBPMAllOut = selectedNode;
            selectedNode.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    toggleGridAllOut(seqBPMAllOut, gridPaneBPM);
                }
            });
            selectedNode = (CheckBox) getNodeFromGridPane(gridPaneHC, i, 2);
            final Node seqHCAllOut = selectedNode;
            selectedNode.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    toggleGridAllOut(seqHCAllOut, gridPaneHC);
                }
            });
            selectedNode = (CheckBox) getNodeFromGridPane(gridPaneVC, i, 2);
            final Node seqVCAllOut = selectedNode;
            selectedNode.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    toggleGridAllOut(seqVCAllOut, gridPaneVC);
                }
            });
        }

    }

    public void enterElementstoEdit(CorrectionBlock editBlock) {
        int seqIndex = 0;
        CheckBox selectedNode;
        ObservableList<BPM> bpm = FXCollections.observableArrayList();
        ObservableList<HDipoleCorr> HC = FXCollections.observableArrayList();
        ObservableList<VDipoleCorr> VC = FXCollections.observableArrayList();

        for (AcceleratorSeq Seq : seqItem) {
            bpm.addAll(Seq.getAllNodesOfType("BPM"));
            HC.addAll(Seq.getAllNodesOfType("DCH"));
            VC.addAll(Seq.getAllNodesOfType("DCV"));
            for (BPM bpmItem : editBlock.getBlockBPM()) {
                if (bpm.contains(bpmItem)) {
                    selectedNode = (CheckBox) getNodeFromGridPane(gridPaneBPM, seqIndex, bpm.indexOf(bpmItem) + 3);
                    selectedNode.setSelected(true);
                }
            }
            for (HDipoleCorr hcItem : editBlock.getBlockHC()) {
                if (HC.contains(hcItem)) {
                    selectedNode = (CheckBox) getNodeFromGridPane(gridPaneHC, seqIndex, HC.indexOf(hcItem) + 3);
                    selectedNode.setSelected(true);
                }
            }
            for (VDipoleCorr vcItem : editBlock.getBlockVC()) {
                if (VC.contains(vcItem)) {
                    selectedNode = (CheckBox) getNodeFromGridPane(gridPaneVC, seqIndex, VC.indexOf(vcItem) + 3);
                    selectedNode.setSelected(true);
                }
            }
            bpm.clear();
            HC.clear();
            VC.clear();
            seqIndex++;
        }

    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private void toggleGridAllIn(Node selectedNode, GridPane selectedGrid) {
        CheckBox checkNode;
        int index = (int) GridPane.getColumnIndex(selectedNode);
        switch (selectedGrid.getId()) {
            case "gridPaneBPM":
                List<BPM> bpm = seqItem.get(index).getAllNodesOfType("BPM");
                checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, 2);
                checkNode.setSelected(false);
                for (int j = 3; j < bpm.size() + 3; j++) {
                    checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, j);
                    checkNode.setSelected(true);
                }
                break;
            case "gridPaneHC":
                List<HDipoleCorr> HC = seqItem.get(index).getAllNodesOfType("DCH");
                checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, 2);
                checkNode.setSelected(false);
                for (int j = 3; j < HC.size() + 3; j++) {
                    checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, j);
                    checkNode.setSelected(true);
                }
                break;
            case "gridPaneVC":
                List<VDipoleCorr> VC = seqItem.get(index).getAllNodesOfType("DCV");
                checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, 2);
                checkNode.setSelected(false);
                for (int j = 3; j < VC.size() + 3; j++) {
                    checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, j);
                    checkNode.setSelected(true);
                }
                break;
        }

    }

    private void toggleGridAllOut(Node selectedNode, GridPane selectedGrid) {
        CheckBox checkNode;
        int index = (int) GridPane.getColumnIndex(selectedNode);
        switch (selectedGrid.getId()) {
            case "gridPaneBPM":
                List<BPM> bpm = seqItem.get(index).getAllNodesOfType("BPM");
                checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, 1);
                checkNode.setSelected(false);
                for (int j = 3; j < bpm.size() + 3; j++) {
                    checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, j);
                    checkNode.setSelected(false);
                }
                break;
            case "gridPaneHC":
                List<HDipoleCorr> HC = seqItem.get(index).getAllNodesOfType("DCH");
                checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, 1);
                checkNode.setSelected(false);
                for (int j = 3; j < HC.size() + 3; j++) {
                    checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, j);
                    checkNode.setSelected(false);
                }
                break;
            case "gridPaneVC":
                List<VDipoleCorr> VC = seqItem.get(index).getAllNodesOfType("DCV");
                checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, 1);
                checkNode.setSelected(false);
                for (int j = 3; j < VC.size() + 3; j++) {
                    checkNode = (CheckBox) getNodeFromGridPane(selectedGrid, index, j);
                    checkNode.setSelected(false);
                }
                break;
        }

    }

    @FXML
    private void handelButtonSaveSelection(ActionEvent event) {
        CheckBox selectedNode;
        BPMSelection.clear();
        HCSelection.clear();
        VCSelection.clear();
        for (int i = 0; i < seqItem.size(); i++) {
            List<BPM> bpm = seqItem.get(i).getAllNodesOfType("BPM");
            for (int j = 3; j < bpm.size() + 3; j++) {
                selectedNode = (CheckBox) getNodeFromGridPane(gridPaneBPM, i, j);
                if (selectedNode.isSelected()) {
                    BPMSelection.add(bpm.get(j - 3));
                }
            }
            List<HDipoleCorr> HC = seqItem.get(i).getAllNodesOfType("DCH");
            for (int j = 3; j < HC.size() + 3; j++) {
                selectedNode = (CheckBox) getNodeFromGridPane(gridPaneHC, i, j);
                if (selectedNode.isSelected()) {
                    HCSelection.add(HC.get(j - 3));
                }
            }
            List<VDipoleCorr> VC = seqItem.get(i).getAllNodesOfType("DCV");
            for (int j = 3; j < VC.size() + 3; j++) {
                selectedNode = (CheckBox) getNodeFromGridPane(gridPaneVC, i, j);
                if (selectedNode.isSelected()) {
                    VCSelection.add(VC.get(j - 3));
                }
            }
        }

        //sort the lists
        BPMSelection.sort((bpm1, bpm2) -> Double.compare(bpm1.getSDisplay(), bpm2.getSDisplay()));
        HCSelection.sort((hc1, hc2) -> Double.compare(hc1.getSDisplay(), hc2.getSDisplay()));
        VCSelection.sort((vc1, vc2) -> Double.compare(vc1.getSDisplay(), vc2.getSDisplay()));

        setChangedSelectionList(true);
        setLoggedIn(true);
    }

    @FXML
    private void handleButtonCancel(ActionEvent event) {
        setChangedSelectionList(false);
        setLoggedIn(true);
    }

}