/*
 * MORTAR - MOlecule fRagmenTAtion fRamework
 * Copyright (C) 2023  Felix Baensch, Jonas Schaub (felix.baensch@w-hs.de, jonas.schaub@uni-jena.de)
 *
 * Source code is available at <https://github.com/FelixBaensch/MORTAR>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unijena.cheminf.mortar.gui.util;

import de.unijena.cheminf.mortar.gui.views.FragmentsDataTableView;
import de.unijena.cheminf.mortar.gui.views.IDataTableView;
import de.unijena.cheminf.mortar.gui.views.ItemizationDataTableView;
import de.unijena.cheminf.mortar.message.Message;
import de.unijena.cheminf.mortar.model.data.FragmentDataModel;
import de.unijena.cheminf.mortar.model.data.MoleculeDataModel;
import de.unijena.cheminf.mortar.model.depict.DepictionUtil;
import de.unijena.cheminf.mortar.model.settings.SettingsContainer;
import de.unijena.cheminf.mortar.model.util.CollectionUtil;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * GUI utility
 *
 * @author Jonas Schaub, Felix Baensch
 * @version 1.0.0.0
 */
public class GuiUtil {
    //<editor-fold defaultstate="collapsed" desc="Public static final class constants">
    /**
     * Logger of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(GuiUtil.class.getName());
    //</editor-fold>
    //
    //<editor-fold desc="public static methods" defaultstate="collapsed">
    /**
     * Creates and shows an alert with arbitrary alert type.
     *
     * @param anAlertType - pre-built alert type of the alert message that the Alert class can use to pre-populate
     *                    various properties, chosen of an enumeration containing the available
     * @param aTitle Title of the alert message
     * @param aHeaderText Header of the alert message
     * @param aContentText Text that the alert message contains
     */
    public static void guiMessageAlert(Alert.AlertType anAlertType, String aTitle, String aHeaderText, String aContentText){
        Alert tmpAlert = new Alert(anAlertType);
        tmpAlert.setTitle(aTitle);
        tmpAlert.setHeaderText(aHeaderText);
        tmpAlert.setContentText(aContentText);
        //tmpAlert.setResizable(true);
        tmpAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        tmpAlert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        tmpAlert.showAndWait();
    }
    //
    /**
     * Creates and shows an alert with arbitrary alert type and the given hyperlink in the content section.
     *
     * @param anAlertType - pre-built alert type of the alert message that the Alert class can use to pre-populate
     *                    various properties, chosen of an enumeration containing the available
     * @param aTitle Title of the alert message
     * @param aHeaderText Header of the alert message
     * @param aHyperlink Hyperlink that the alert message contains
     */
    public static void guiMessageAlertWithHyperlink(Alert.AlertType anAlertType, String aTitle, String aHeaderText, Hyperlink aHyperlink){
        Alert tmpAlert = new Alert(anAlertType);
        tmpAlert.setTitle(aTitle);
        tmpAlert.setHeaderText(aHeaderText);
        tmpAlert.getDialogPane().setContent(aHyperlink);
        //tmpAlert.setResizable(true);
        tmpAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        tmpAlert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        tmpAlert.showAndWait();
    }
    //
    /**
     * Creates and shows confirmation type alert and returns the button selected by user as ButtonType.
     * Two buttons are possible - ButtonType.OK and ButtonType.CANCEL.
     *
     * @param aTitle Title of the confirmation alert
     * @param aHeaderText Header of the confirmation alert
     * @param aContentText Text that the confirmation alert contains
     * @return ButtonType selected by user - ButtonType.OK or ButtonType.CANCEL
     */
    public static ButtonType guiConfirmationAlert(String aTitle, String aHeaderText, String aContentText){
        Alert tmpAlert = new Alert(Alert.AlertType.CONFIRMATION);
        //tmpAlert.setResizable(true);
        tmpAlert.setTitle(aTitle);
        tmpAlert.setHeaderText(aHeaderText);
        tmpAlert.setContentText(aContentText);
        tmpAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        tmpAlert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        return tmpAlert.showAndWait().orElse(ButtonType.CANCEL);
    }
    //
    /**
     * Creates and shows an alert dialog to report an exception that occurred. The stack trace of the exception is also
     * given.
     *
     * @param aTitle title of the alert dialog
     * @param aHeaderText header of the alert dialog
     * @param aContentText Text of the alert dialog
     * @param anException exception to report, may be null
     */
    public static void guiExceptionAlert(String aTitle, String aHeaderText, String aContentText, Exception anException){
        String tmpExceptionString;
        if(Objects.isNull(anException)){
            tmpExceptionString = "Exception is null.";
        } else {
            StringWriter tmpStringWriter = new StringWriter();
            PrintWriter tmpPrintWriter = new PrintWriter(tmpStringWriter);
            anException.printStackTrace(tmpPrintWriter);
            tmpExceptionString = tmpStringWriter.toString();
        }
        GuiUtil.guiExpandableAlert(aTitle, aHeaderText, aContentText, Message.get("Error.ExceptionAlert.Label"), tmpExceptionString);
    }
    //
    /**
     * Creates and shows an alert explicit for exceptions, which contains the stack trace of the given exception in
     * an expandable pane.
     *
     * @param aTitle Title of the exception alert
     * @param aHeaderText Header of the exception alert
     * @param aContentText Text that the exception alert contains
     * @param aLabelText Text to show above expandable area
     * @param anExpandableString Text to show in expandable area
     */
    public static void guiExpandableAlert(String aTitle, String aHeaderText, String aContentText, String aLabelText, String anExpandableString){
        try{
            Alert tmpAlert = new Alert(Alert.AlertType.ERROR);
            tmpAlert.setTitle(aTitle);
            tmpAlert.setHeaderText(aHeaderText);
            tmpAlert.setContentText(aContentText);
            Label tmpLabel = new Label(aLabelText);
            TextArea tmpExpandableTextArea = new TextArea(anExpandableString);
            tmpExpandableTextArea.setEditable(false);
            tmpExpandableTextArea.setWrapText(true);
            tmpExpandableTextArea.setMaxWidth(Double.MAX_VALUE);
            tmpExpandableTextArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(tmpExpandableTextArea, Priority.ALWAYS);
            GridPane.setHgrow(tmpExpandableTextArea, Priority.ALWAYS);
            GridPane tmpGridPane = new GridPane();
            tmpGridPane.setMaxWidth(Double.MAX_VALUE);
            tmpGridPane.add(tmpLabel, 0, 0);
            tmpGridPane.add(tmpExpandableTextArea, 0, 1);
            //Add expandable text to the dialog/alert pane
            tmpAlert.getDialogPane().setExpandableContent(tmpGridPane);
            //Show and wait alert
            tmpAlert.showAndWait();
        }catch(Exception aNewThrownException){
            guiMessageAlert(Alert.AlertType.ERROR, Message.get("Error.ExceptionAlert.Title"), Message.get("Error.ExceptionAlert.Header"), aNewThrownException.toString());
            LOGGER.log(Level.SEVERE, aNewThrownException.toString(), aNewThrownException);
        }
    }
    //
    /**
     * Sorts the items of the TableView over all pages of the pagination and adds
     *
     * @param anEvent SortEvent {@literal <}TableView {@literal >}
     * @param tmpPagination Pagination
     * @param tmpRowsPerPage int
     */
    public static void sortTableViewGlobally(SortEvent<TableView> anEvent, Pagination tmpPagination, int tmpRowsPerPage){
        if(anEvent == null || anEvent.getSource().getSortOrder().size() == 0)
            return;
        String tmpSortProp = ((PropertyValueFactory)((TableColumn) anEvent.getSource().getSortOrder().get(0)).cellValueFactoryProperty().getValue()).getProperty().toString();
        TableColumn.SortType tmpSortType = ((TableColumn) anEvent.getSource().getSortOrder().get(0)).getSortType();
        CollectionUtil.sortGivenFragmentListByPropertyAndSortType(((IDataTableView)anEvent.getSource()).getItemsList(), tmpSortProp, tmpSortType.toString());
        int fromIndex = tmpPagination.getCurrentPageIndex() * tmpRowsPerPage;
        int toIndex = Math.min(fromIndex + tmpRowsPerPage, ((IDataTableView)anEvent.getSource()).getItemsList().size());
        anEvent.getSource().getItems().clear();
        anEvent.getSource().getItems().addAll(((IDataTableView)anEvent.getSource()).getItemsList().subList(fromIndex,toIndex));
    }
    //
    /**
     * Binds height and width property of the child control to the parent pane properties
     *
     * @param aParentPane Pane
     * @param aChildControl Control
     */
    public static void guiBindControlSizeToParentPane(Pane aParentPane, Control aChildControl){
        aChildControl.prefHeightProperty().bind(aParentPane.heightProperty());
        aChildControl.prefWidthProperty().bind(aParentPane.widthProperty());
    }
    //
    /**
     * Returns an input pattern for integer values. "-" may be the first sign, the first number may not be 0.
     *
     * @return GUI input pattern for integer values
     */
    public static Pattern getIntegerPattern(){
        return Pattern.compile("-?(([1-9][0-9]*)|0)?");
    }
    //
    /**
     * Returns an input pattern for positive integer values, including 0.
     *
     * @return GUI input pattern for positive integer values
     */
    public static Pattern getPositiveIntegerInclZeroPattern(){
        return Pattern.compile("[0-9]*");
    }
    //
    /**
     * Returns an input pattern for double values. "-" may be the first sign, the first number may not be 0.
     *
     * @return GUI input pattern for double values
     */
    public static Pattern getDoublePattern(){
        return Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
    }
    //
    /**
     * Returns an input filter for integer values. "-" may be the first sign, the first number may not be 0.
     *
     * @return GUI input filter for integer values
     */
    public static UnaryOperator<TextFormatter.Change> getIntegerFilter(){
        return c ->{
            String tmpText = c.getControlNewText();
            if(GuiUtil.getIntegerPattern().matcher(tmpText).matches()) {
                return c;
            } else {
                return null;
            }
        };
    }
    //
    /**
     *
     * Method that creates an Integer filter to prevent the entry of unwanted
     * characters such as Strings or special characters and also 0 for first entry.
     *
     * @return GUI input filter for positive integer values
     */
    public static UnaryOperator<TextFormatter.Change> getPositiveIntegerWithoutZeroFilter() {
        return c -> {
            String tmpText = c.getControlNewText();
            if (tmpText.equals("0")) {
                return null;
            }
            if (GuiUtil.getPositiveIntegerInclZeroPattern().matcher(tmpText).matches()) {
                return c;
            }
            return null;
        };
    }
    //
    /**
     * Returns an input filter for double values. "-" may be the first sign, the first number may not be 0.
     *
     * @return GUI input filter for double values
     */
    public static UnaryOperator<TextFormatter.Change> getDoubleFilter(){
        return c ->{
          String text = c.getControlNewText();
          if(getDoublePattern().matcher(text).matches()) {
              return c;
          } else {
              return null;
          }
        };
    }
    //
    /**
     * Returns a String {@literal <->} Integer converter that mostly relies on the given toString() and fromString() methods
     * but additionally turns empty strings, "-", ".", and "-." into 0.
     *
     * @return String-Integer converter
     */
    public static StringConverter<Integer> getStringToIntegerConverter(){
        return new StringConverter<Integer>() {
            @Override
            public String toString(Integer anObject) {
                return anObject.toString();
            }
            @Override
            public Integer fromString(String aString) {
                if(aString.isEmpty() || "-".equals(aString) || ".".equals(aString) || "-.".equals(aString) || "0.".equals(aString)){
                    return 0;
                } else{
                    return Integer.valueOf(aString);
                }
            }
        };
    }
    //
    /**
     * Returns a String {@literal <->} Double converter that mostly relies on the given toString() and fromString() methods
     * but additionally turns empty strings, "-", ".", and "-." into 0.0.
     *
     * @return String-Double converter
     */
    public static StringConverter<Double> getStringToDoubleConverter(){
        return new StringConverter<Double>() {
            @Override
            public String toString(Double anObject) {
                return anObject.toString();
            }
            @Override
            public Double fromString(String aString) {
                if(aString.isEmpty() || "-".equals(aString) || ".".equals(aString) || "-.".equals(aString)){
                    return 0.0;
                }
                else {
                    return Double.valueOf(aString);
                }
            }
        };
    }
    //
    /**
     * Copies content of selected cell to system clipboard
     *
     * @param aTableView TableView to copy from
     */
    public static void copySelectedTableViewCellsToClipboard(TableView<?> aTableView){
        for(TablePosition tmpPos :aTableView.getSelectionModel().getSelectedCells()){
            int tmpRowIndex = tmpPos.getRow();
            int tmpColIndex = tmpPos.getColumn();
            int tmpFragmentColIndexItemsTab = 2;
            Object tmpCell;
            if(aTableView.getClass() == ItemizationDataTableView.class && tmpColIndex > tmpFragmentColIndexItemsTab -1){
                tmpCell = aTableView.getColumns().get(tmpFragmentColIndexItemsTab).getColumns().get(tmpColIndex - 2).getCellData(tmpRowIndex);
            }else{
                tmpCell = aTableView.getColumns().get(tmpColIndex).getCellData(tmpRowIndex);
            }
            if(tmpCell == null){
                return;
            }
            else{
                ClipboardContent tmpClipboardContent = new ClipboardContent();
                if(tmpCell.getClass() == String.class){
                    tmpClipboardContent.putString((String) tmpCell);
                }
                else if(tmpCell.getClass() == Integer.class){
                    tmpClipboardContent.putString(((Integer)tmpCell).toString());
                }
                else if(tmpCell.getClass() == Double.class){
                    tmpClipboardContent.putString(((Double)tmpCell).toString());
                }
                else if(tmpCell.getClass() == ImageView.class){
                    Image tmpImage;
                    IAtomContainer tmpAtomContainer;
                    try {
                        if(aTableView.getClass() == FragmentsDataTableView.class){
                            tmpAtomContainer = ((FragmentDataModel) aTableView.getItems().get(tmpRowIndex)).getFirstParentMolecule().getAtomContainer();
                        }
                        else if(aTableView.getClass() == ItemizationDataTableView.class){
                            if(tmpColIndex > 1){
                                String tmpFragmentationName = ((ItemizationDataTableView) aTableView).getFragmentationName();
                                tmpAtomContainer = ((MoleculeDataModel) aTableView.getItems().get(tmpRowIndex)).getFragmentsOfSpecificAlgorithm(tmpFragmentationName).get(tmpColIndex-2).getAtomContainer(); //magic number
                            }
                            else {
                                tmpAtomContainer = ((MoleculeDataModel) aTableView.getItems().get(tmpRowIndex)).getAtomContainer();
                            }
                        }
                        else {
                            tmpAtomContainer = ((MoleculeDataModel) aTableView.getItems().get(tmpRowIndex)).getAtomContainer();
                        }
                        tmpImage = DepictionUtil.depictImageWithZoomAndFillToFitAndWhiteBackground(tmpAtomContainer, 1, GuiDefinitions.GUI_COPY_IMAGE_IMAGE_WIDTH, GuiDefinitions.GUI_COPY_IMAGE_IMAGE_HEIGHT,true, true);
                        tmpClipboardContent.putImage(tmpImage);
                    } catch (CDKException e) {
                        tmpClipboardContent.putImage(((ImageView) tmpCell).getImage());
                    }
                }
                else{
                    return;
                }
                Clipboard.getSystemClipboard().setContent(tmpClipboardContent);
            }
        }
    }
    //
    /**
     * Sets the height for structure images to each MoleculeDataModel object of the items list of the tableView.
     * If image height is too small it will be set to GuiDefinitions.GUI_STRUCTURE_IMAGE_MIN_HEIGHT (50.0)
     *
     * @param aTableView TableView
     * @param aHeight double
     * @param aSettingsContainer SettingsContainer
     */
    public static void setImageStructureHeight(TableView aTableView, double aHeight, SettingsContainer aSettingsContainer){
        double tmpHeight =
                (aHeight - GuiDefinitions.GUI_TABLE_VIEW_HEADER_HEIGHT - GuiDefinitions.GUI_PAGINATION_CONTROL_PANEL_HEIGHT)
                        / aSettingsContainer.getRowsPerPageSetting();
        if(aTableView.getClass().equals(ItemizationDataTableView.class)){
            tmpHeight =
                    (aHeight - 2*GuiDefinitions.GUI_TABLE_VIEW_HEADER_HEIGHT - GuiDefinitions.GUI_PAGINATION_CONTROL_PANEL_HEIGHT)
                            / aSettingsContainer.getRowsPerPageSetting();
        }
        if(tmpHeight < GuiDefinitions.GUI_STRUCTURE_IMAGE_MIN_HEIGHT){
            tmpHeight = GuiDefinitions.GUI_STRUCTURE_IMAGE_MIN_HEIGHT;
        }
        if(aTableView.getClass().equals(ItemizationDataTableView.class)){
            for(MoleculeDataModel tmpMoleculeDataModel : ((IDataTableView)aTableView).getItemsList()){
                tmpMoleculeDataModel.setStructureImageHeight(tmpHeight);
                String tmpFragmentationName = ((ItemizationDataTableView) aTableView).getFragmentationName();
                if(!tmpMoleculeDataModel.hasMoleculeUndergoneSpecificFragmentation(tmpFragmentationName)){
                    continue;
                }
                for(FragmentDataModel tmpFragmentDataModel : tmpMoleculeDataModel.getFragmentsOfSpecificAlgorithm(tmpFragmentationName)){
                    tmpFragmentDataModel.setStructureImageHeight(tmpHeight);
                }
            }
        }
        else{
            for(MoleculeDataModel tmpMoleculeDataModel : ((IDataTableView)aTableView).getItemsList()){
                tmpMoleculeDataModel.setStructureImageHeight(tmpHeight);
            }
        }
    }
    //
    /**
     * Returns the largest number of fragments of one molecule found in the given list for the given fragmentation name
     *
     * @param aListOfMolecules List of MoleculeDataModels
     * @param aFragmentationName String for the fragmentation name
     * @return largest number of fragments of one molecule
     */
    public static int getLargestNumberOfFragmentsForGivenMoleculeListAndFragmentationName(List<MoleculeDataModel> aListOfMolecules, String aFragmentationName){
        int tmpAmount = 0; //tmpAmount is the number of fragments appearing in the molecule with the highest number of fragments
        for (int i = 0; i < aListOfMolecules.size(); i++) {
            if(!aListOfMolecules.get(i).hasMoleculeUndergoneSpecificFragmentation(aFragmentationName)){
                continue;
            }
            HashMap<String, Integer> tmpCurrentFragmentsMap = aListOfMolecules.get(i).getFragmentFrequencyOfSpecificAlgorithm(aFragmentationName);
            if (tmpCurrentFragmentsMap == null) { //redundant, see if clause above
                continue;
            }
            int tmpNrOfFragmentsOfCurrentMolecule = tmpCurrentFragmentsMap.size();
            tmpAmount = Math.max(tmpAmount, tmpNrOfFragmentsOfCurrentMolecule);
        }
        return tmpAmount;
    }
    //
    /**
     * Returns a button with the GUI's standard width and height for buttons and the given text string set as its label.
     *
     * @param aText A text string for its label
     * @return A Button of the GUI's standard size
     */
    public static Button getButtonOfStandardSize(String aText) {
        Button tmpButton = new Button(aText);
        tmpButton.setPrefWidth(GuiDefinitions.GUI_BUTTON_WIDTH_VALUE);
        tmpButton.setMinWidth(GuiDefinitions.GUI_BUTTON_WIDTH_VALUE);
        tmpButton.setMaxWidth(GuiDefinitions.GUI_BUTTON_WIDTH_VALUE);
        tmpButton.setPrefHeight(GuiDefinitions.GUI_BUTTON_HEIGHT_VALUE);
        return tmpButton;
    }
    //</editor-fold>
}
