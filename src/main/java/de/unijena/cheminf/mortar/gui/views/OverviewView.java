/*
 * MORTAR - MOlecule fRagmenTAtion fRamework
 * Copyright (C) 2022  Felix Baensch, Jonas Schaub (felix.baensch@w-hs.de, jonas.schaub@uni-jena.de)
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

package de.unijena.cheminf.mortar.gui.views;

import de.unijena.cheminf.mortar.gui.util.GuiDefinitions;
import de.unijena.cheminf.mortar.gui.util.GuiUtil;
import de.unijena.cheminf.mortar.message.Message;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import java.util.Objects;

/**
 * View class of the overview view.
 *
 * @author Samuel Behr
 * @version 1.0.0.0
 */
public class OverviewView extends AnchorPane {

    //<editor-fold desc="private class variables" defaultstate="collapsed">
    /**
     * Grid pane used to style the view.
     */
    private GridPane mainGridPane;
    /**
     * Grid pane that holds the displayed structure images and can be reconfigured by the user.
     */
    private GridPane structureGridPane;
    /**
     * Horizontal box that holds the nodes placed in the bottom-left corner of the view being the text fields and
     * apply button for the reconfiguration of the structure grid pane.
     */
    private HBox bottomLeftHBox;
    /**
     * Horizontal box that holds the nodes placed in the bottom-right corner of the view.
     */
    private HBox bottomRightHBox;
    /**
     * Text field for columns per page input.
     */
    private TextField columnsPerPageTextField;
    /**
     * Text field for rows per page input.
     */
    private TextField rowsPerPageTextField;
    /**
     * Button to apply changes to the structure grid pane configuration.
     */
    private Button applyButton;
    /**
     * Button to close the view.
     */
    private Button closeButton;
    /**
     * Pagination that holds the structure grid pane and enables the user to switch pages.
     */
    private Pagination pagination;
    /**
     * Vertical box to be shown when the dimensions of the structure images fell below a limit.
     */
    private VBox imageDimensionsBelowLimitVBox;
    //</editor-fold>
    //
    //<editor-fold desc="Constructor" defaultstate="collapsed">
    /**
     * Constructor.
     *
     * Initializes the main components of the overview view and does the basic styling. The grid pane to display the
     * structure images that is to be hold by the pagination node of the overview view gets generated, configured and
     * styled. The horizontal boxes holding the text fields and buttons of the lower side of the view are not yet being
     * added to the main grid pane of the view and are advised to be added after adding the pagination node to ensure
     * the accessibility of all view components.
     * No event listeners are being added to any components.
     */
    public OverviewView(int aRowsPerPage, int aColumnsPerPage) {
        super();
        //mainGridPane to style the view and set up its components
        this.mainGridPane = new GridPane();
        this.mainGridPane.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));
        this.getChildren().add(this.mainGridPane);
        OverviewView.setTopAnchor(this.mainGridPane, 0.0);
        OverviewView.setRightAnchor(this.mainGridPane, 0.0);
        OverviewView.setLeftAnchor(this.mainGridPane,0.0);
        OverviewView.setBottomAnchor(this.mainGridPane, 0.0);
        RowConstraints tmpRowCon1 = new RowConstraints();
        tmpRowCon1.setFillHeight(true);
        tmpRowCon1.setVgrow(Priority.ALWAYS);
        this.mainGridPane.getRowConstraints().add(tmpRowCon1);
        RowConstraints tmpRowCon2 = new RowConstraints();
        tmpRowCon2.setMaxHeight(GuiDefinitions.GUI_CONTROL_CONTAINER_HEIGHT);
        tmpRowCon2.setMinHeight(GuiDefinitions.GUI_CONTROL_CONTAINER_HEIGHT);
        tmpRowCon2.setPrefHeight(GuiDefinitions.GUI_CONTROL_CONTAINER_HEIGHT);
        tmpRowCon2.setVgrow(Priority.ALWAYS);
        this.mainGridPane.getRowConstraints().add(tmpRowCon2);
        ColumnConstraints tmpColCon1 = new ColumnConstraints();
        tmpColCon1.setHgrow(Priority.ALWAYS);
        tmpColCon1.setMaxWidth(GuiDefinitions.GUI_SPACING_VALUE);
        tmpColCon1.setMinWidth(GuiDefinitions.GUI_SPACING_VALUE);
        tmpColCon1.setPrefWidth(GuiDefinitions.GUI_SPACING_VALUE);
        this.mainGridPane.getColumnConstraints().add(tmpColCon1);
        ColumnConstraints tmpColCon2 = new ColumnConstraints();
        tmpColCon2.setFillWidth(true);
        tmpColCon2.setHgrow(Priority.ALWAYS);
        this.mainGridPane.getColumnConstraints().add(tmpColCon2);
        ColumnConstraints tmpColCon3 = new ColumnConstraints();
        tmpColCon1.setHgrow(Priority.ALWAYS);
        tmpColCon1.setMaxWidth(GuiDefinitions.GUI_SPACING_VALUE);
        tmpColCon1.setMinWidth(GuiDefinitions.GUI_SPACING_VALUE);
        tmpColCon1.setPrefWidth(GuiDefinitions.GUI_SPACING_VALUE);
        this.mainGridPane.getColumnConstraints().add(tmpColCon3);
        //
        //initialization, styling and initial configuration of the structureGridPane
        this.structureGridPane = new GridPane();
        /*
        upper and lower border: extend the image frame to grid line width; right and left border: extend the image frame
        to grid line width and add a spacing with a width dependent on the grid line width
         */
        this.structureGridPane.setStyle(
                "-fx-background-color: LIGHTGREY; " +
                "-fx-border-color: LIGHTGREY; " +
                "-fx-border-width: " +
                        + (GuiDefinitions.OVERVIEW_VIEW_STRUCTURE_GRID_PANE_GRIDLINES_WIDTH / 2) + "px " +
                        + ((GuiDefinitions.OVERVIEW_VIEW_STRUCTURE_GRID_PANE_BORDER_TO_GRIDLINES_WIDTH_RATIO - 0.5)
                                * GuiDefinitions.OVERVIEW_VIEW_STRUCTURE_GRID_PANE_GRIDLINES_WIDTH) + "px " +
                        + (GuiDefinitions.OVERVIEW_VIEW_STRUCTURE_GRID_PANE_GRIDLINES_WIDTH / 2) + "px " +
                        + ((GuiDefinitions.OVERVIEW_VIEW_STRUCTURE_GRID_PANE_BORDER_TO_GRIDLINES_WIDTH_RATIO - 0.5)
                                * GuiDefinitions.OVERVIEW_VIEW_STRUCTURE_GRID_PANE_GRIDLINES_WIDTH) + "px; " +
                "-fx-effect: innershadow(gaussian, rgba(100, 100, 100, 0.9), " +
                        GuiDefinitions.OVERVIEW_VIEW_STRUCTURE_GRID_PANE_GRIDLINES_WIDTH / 2 + ", 0, 0, " +
                        GuiDefinitions.OVERVIEW_VIEW_STRUCTURE_GRID_PANE_GRIDLINES_WIDTH / 8 + ")"
        );
        this.configureStructureGridPane(aRowsPerPage, aColumnsPerPage);
        //
        /*
        initialization of the bottomLeftHBox and its components; the components are the text fields for rows and columns
        per page input, their labels, tooltips and the apply button to reconfigure the structure grid pane; the
        bottomLeftHBox needs to be added in the controller after the pagination has been added to the mainGridPane to
        make sure all components are set up correct and accessible
         */
        this.bottomLeftHBox = new HBox();
        this.bottomLeftHBox.setPadding(new Insets(1.2 * GuiDefinitions.GUI_INSETS_VALUE,
                GuiDefinitions.GUI_INSETS_VALUE, GuiDefinitions.GUI_INSETS_VALUE, GuiDefinitions.GUI_INSETS_VALUE));
        this.bottomLeftHBox.setSpacing(GuiDefinitions.GUI_SPACING_VALUE);
        //
        this.columnsPerPageTextField = new TextField();
        this.columnsPerPageTextField.setMinWidth(GuiDefinitions.GUI_TEXT_FIELD_PREF_WIDTH_VALUE);
        this.columnsPerPageTextField.setPrefWidth(GuiDefinitions.GUI_TEXT_FIELD_PREF_WIDTH_VALUE);
        this.columnsPerPageTextField.setMaxWidth(GuiDefinitions.GUI_SETTINGS_TEXT_FIELD_MAX_WIDTH_VALUE);
        this.columnsPerPageTextField.setAlignment(Pos.CENTER_RIGHT);
        TextFormatter<Integer> tmpFormatter2 = new TextFormatter<>(GuiUtil.getStringToIntegerConverter(),
                aColumnsPerPage, GuiUtil.getIntegerFilter());   //TODO: use new GuiUtil method for IntegerFilter after merge
        this.columnsPerPageTextField.setTextFormatter(tmpFormatter2);
        Label tmpColumnsPerPageLabel = new Label(Message.get("OverviewView.columnsPerPageLabel.text"));
        tmpColumnsPerPageLabel.setMinWidth(GuiDefinitions.GUI_TEXT_FIELD_PREF_WIDTH_VALUE / 3);
        tmpColumnsPerPageLabel.setPrefWidth(GuiDefinitions.GUI_TEXT_FIELD_PREF_WIDTH_VALUE / 3);
        tmpColumnsPerPageLabel.setMaxWidth(GuiDefinitions.GUI_SETTINGS_TEXT_FIELD_MAX_WIDTH_VALUE / 2);
        tmpColumnsPerPageLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        Tooltip tmpColumnsPerPageTooltip = new Tooltip(Message.get("OverviewView.columnsPerPageLabel.tooltip"));
        tmpColumnsPerPageLabel.setTooltip(tmpColumnsPerPageTooltip);
        this.columnsPerPageTextField.setTooltip(tmpColumnsPerPageTooltip);
        this.rowsPerPageTextField = new TextField();
        this.rowsPerPageTextField.setMinWidth(GuiDefinitions.GUI_TEXT_FIELD_PREF_WIDTH_VALUE);
        this.rowsPerPageTextField.setPrefWidth(GuiDefinitions.GUI_TEXT_FIELD_PREF_WIDTH_VALUE);
        this.rowsPerPageTextField.setMaxWidth(GuiDefinitions.GUI_SETTINGS_TEXT_FIELD_MAX_WIDTH_VALUE);
        this.rowsPerPageTextField.setAlignment(Pos.CENTER_RIGHT);
        TextFormatter<Integer> tmpFormatter1 = new TextFormatter<>(GuiUtil.getStringToIntegerConverter(),
                aRowsPerPage, GuiUtil.getIntegerFilter());  //TODO: use new GuiUtil method for IntegerFilter after merge
        this.rowsPerPageTextField.setTextFormatter(tmpFormatter1);
        Label tmpRowsPerPageLabel = new Label(Message.get("OverviewView.rowsPerPageLabel.text"));
        tmpRowsPerPageLabel.setMinWidth(GuiDefinitions.GUI_TEXT_FIELD_PREF_WIDTH_VALUE / 3);
        tmpRowsPerPageLabel.setPrefWidth(GuiDefinitions.GUI_TEXT_FIELD_PREF_WIDTH_VALUE / 3);
        tmpRowsPerPageLabel.setMaxWidth(GuiDefinitions.GUI_SETTINGS_TEXT_FIELD_MAX_WIDTH_VALUE / 2);
        tmpRowsPerPageLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        Tooltip tmpRowsPerPageTooltip = new Tooltip(Message.get("OverviewView.rowsPerPageLabel.tooltip"));
        tmpRowsPerPageLabel.setTooltip(tmpRowsPerPageTooltip);
        this.rowsPerPageTextField.setTooltip(tmpRowsPerPageTooltip);
        //
        this.applyButton = new Button(Message.get("OverviewView.applyButton.text"));
        this.applyButton.setPrefWidth(GuiDefinitions.GUI_BUTTON_WIDTH_VALUE);
        this.applyButton.setMinWidth(GuiDefinitions.GUI_BUTTON_WIDTH_VALUE);
        this.applyButton.setMaxWidth(GuiDefinitions.GUI_BUTTON_WIDTH_VALUE);
        this.applyButton.setPrefHeight(GuiDefinitions.GUI_BUTTON_HEIGHT_VALUE);
        this.applyButton.setTooltip(new Tooltip(Message.get("OverviewView.applyButton.tooltip")));
        //
        this.bottomLeftHBox.getChildren().addAll(tmpColumnsPerPageLabel, this.columnsPerPageTextField,
                tmpRowsPerPageLabel, this.rowsPerPageTextField, this.applyButton);
        //
        /*
        initialization of the bottomRightHBox and its component, the close button; the bottomRightHBox needs to be added
        in the controller after the pagination has been added to the mainGridPane to make sure all components are set up
        correct and accessible
         */
        this.bottomRightHBox = new HBox();
        this.bottomRightHBox.setPadding(new Insets(1.2 * GuiDefinitions.GUI_INSETS_VALUE,
                GuiDefinitions.GUI_INSETS_VALUE, GuiDefinitions.GUI_INSETS_VALUE, GuiDefinitions.GUI_INSETS_VALUE));
        this.closeButton = new Button(Message.get("OverviewView.closeButton.text"));
        this.closeButton.setPrefWidth(GuiDefinitions.GUI_BUTTON_WIDTH_VALUE);
        this.closeButton.setMinWidth(GuiDefinitions.GUI_BUTTON_WIDTH_VALUE);
        this.closeButton.setMaxWidth(GuiDefinitions.GUI_BUTTON_WIDTH_VALUE);
        this.closeButton.setPrefHeight(GuiDefinitions.GUI_BUTTON_HEIGHT_VALUE);
        this.closeButton.setTooltip(new Tooltip(Message.get("OverviewView.closeButton.tooltip")));
        //
        this.bottomRightHBox.getChildren().add(this.closeButton);
        //
        /*
        initialization of the imageDimensionsBelowLimitVBox that is meant to be shown when the dimensions of the
        structure images fall below a defined limit
         */
        Label tmpImageDimensionsBelowLimitLabel = new Label(
                Message.get("OverviewView.imageDimensionsBelowLimitLabel.text")
        );
        tmpImageDimensionsBelowLimitLabel.setStyle("-fx-alignment: CENTER");
        tmpImageDimensionsBelowLimitLabel.setPadding(new Insets(0.0, 0.0, 20.0, 0.0));
        Label tmpImageDimensionsBelowLimitInfoLabel = new Label(
                Message.get("OverviewView.imageDimensionsBelowLimitInfoLabel.text")
        );
        tmpImageDimensionsBelowLimitInfoLabel.setStyle("-fx-alignment: CENTER");
        Tooltip tmpImageDimensionsBelowLimitLabelTooltip = new Tooltip(
                Message.get("OverviewView.imageDimensionsBelowLimitLabel.tooltip")  //TODO: adapt tooltip after integration of overview view settings
        );
        tmpImageDimensionsBelowLimitLabel.setTooltip(tmpImageDimensionsBelowLimitLabelTooltip);
        tmpImageDimensionsBelowLimitInfoLabel.setTooltip(tmpImageDimensionsBelowLimitLabelTooltip);
        this.imageDimensionsBelowLimitVBox = new VBox(
                tmpImageDimensionsBelowLimitLabel,
                tmpImageDimensionsBelowLimitInfoLabel
        );
        this.imageDimensionsBelowLimitVBox.setStyle("-fx-alignment: CENTER");
    }
    //</editor-fold>
    //
    //<editor-fold desc="public methods" defaultstate="collapsed">
    /**
     * Configures the structure grid pane depending on the chosen numbers for rows and columns of structure images to
     * be displayed per page.
     *
     * @param aRowsPerPage Integer value for rows of structure images to be displayed per page
     * @param aColumnsPerPage Integer value for columns of structure images to be displayed per page
     */
    public void configureStructureGridPane(int aRowsPerPage, int aColumnsPerPage) {
        if (this.structureGridPane == null) {
            this.structureGridPane = new GridPane();
        } else {
            this.structureGridPane.getRowConstraints().clear();
            this.structureGridPane.getColumnConstraints().clear();
        }
        for (int i = 0; i < aRowsPerPage; i++) {
            RowConstraints tmpRowCon = new RowConstraints();
            tmpRowCon.setVgrow(Priority.ALWAYS);
            tmpRowCon.setPercentHeight(100.0 / aRowsPerPage);
            this.structureGridPane.getRowConstraints().add(tmpRowCon);
        }
        for (int i = 0; i < aColumnsPerPage; i++) {
            ColumnConstraints tmpColCon = new ColumnConstraints();
            tmpColCon.setHgrow(Priority.ALWAYS);
            tmpColCon.setPercentWidth(100.0 / aColumnsPerPage);
            this.structureGridPane.getColumnConstraints().add(tmpColCon);
        }
        this.structureGridPane.setAlignment(Pos.CENTER);
    }
    //
    /**
     * Adds the pagination node to the main grid pane of the overview view at a fixed position and initializes the
     * pagination class variable.
     *
     * @param aPagination Pagination to be added to the overview view
     */
    public void addPaginationToMainGridPane(Pagination aPagination) {
        Objects.requireNonNull(aPagination, "aPagination (instance of Pagination) is null");
        this.pagination = aPagination;
        this.addNodeToMainGridPane(this.pagination, 0, 0, 3, 2);
    }
    //
    /**
     * Adds the overview view's bottomLeftHBox to the main grid pane.
     */
    public void addBottomLeftHBoxToMainGridPane() {
        this.addNodeToMainGridPane(this.bottomLeftHBox, 0, 1, 1, 1);
    }
    //
    /**
     * Adds the overview view's bottomRightHBox to the main grid pane.
     */
    public void addBottomRightHBoxToMainGridPane() {
        this.addNodeToMainGridPane(this.bottomRightHBox, 2, 1, 1, 1);
    }
    //</editor-fold>
    //
    //<editor-fold desc="private methods" defaultstate="collapsed">
    /**
     * Adds a node to the main grid pane of the overview view with a specified position.
     *
     * @param aNode Node to be added to the main grid pane
     * @param aColIndex Integer value for index of column the node should be added to
     * @param aRowIndex Integer value for index of row the node should be added to
     * @param aColSpan Integer value for number of columns for the node to span
     * @param aRowSpan Integer value for number of rows for the node to span
     */
    private void addNodeToMainGridPane(Node aNode, int aColIndex, int aRowIndex, int aColSpan, int aRowSpan){
        this.mainGridPane.add(aNode, aColIndex, aRowIndex, aColSpan, aRowSpan);
    }
    //</editor-fold>
    //
    //<editor-fold desc="public properties" defaultstate="collapsed">
    /**
     * Returns the overview view's button for applying of new grid configurations.
     *
     * @return Button
     */
    public Button getApplyButton() {
        return this.applyButton;
    }
    //
    /**
     * Returns the overview view's close button.
     *
     * @return Button
     */
    public Button getCloseButton() {
        return this.closeButton;
    }
    //
    /**
     * Returns the text field for rows per page entry of the user.
     *
     * @return TextField
     */
    public TextField getRowsPerPageTextField() {
        return this.rowsPerPageTextField;
    }
    //
    /**
     * Returns the text field for columns per page entry of the user.
     *
     * @return TextField
     */
    public TextField getColumnsPerPageTextField() {
        return this.columnsPerPageTextField;
    }
    //
    /**
     * Returns the overview view's pagination node that holds the content of the view.
     *
     * @return Pagination node
     */
    public Pagination getPagination() {
        return this.pagination;
    }
    //
    /**
     * Returns the main grid pane that is used for the positioning of the view's components.
     *
     * @return GridPane
     */
    public GridPane getMainGridPane() {
        return this.mainGridPane;
    }
    //
    /**
     * Returns the structure grid pane that holds the structure images.
     *
     * @return GridPane
     */
    public GridPane getStructureGridPane() {
        return this.structureGridPane;
    }
    //
    /**
     * Returns a vertical box that is meant to be placed in the structureGridPane if the structure image dimensions
     * fell below a defined limit.
     *
     * @return VBox
     */
    public VBox getImageDimensionsBelowLimitVBox() {
        return this.imageDimensionsBelowLimitVBox;
    }
    //</editor-fold>

}
