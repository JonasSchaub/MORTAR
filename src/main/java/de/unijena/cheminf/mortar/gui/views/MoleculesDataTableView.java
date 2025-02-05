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

package de.unijena.cheminf.mortar.gui.views;

import de.unijena.cheminf.mortar.gui.util.GuiDefinitions;
import de.unijena.cheminf.mortar.gui.util.GuiUtil;
import de.unijena.cheminf.mortar.message.Message;
import de.unijena.cheminf.mortar.model.data.MoleculeDataModel;
import de.unijena.cheminf.mortar.model.settings.SettingsContainer;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.util.List;

/**
 * Customized table view for molecules data table view.
 *
 * @author Felix Baensch
 * @version 1.0.1.0
 */
public class MoleculesDataTableView extends TableView implements IDataTableView {

    //<editor-fold desc="private class variables" defaultstate="collapsed">
    /**
     * TableColumn for selection state of the molecule
     */
    private TableColumn<MoleculeDataModel, Boolean> selectionColumn;
    /**
     * TableColumn for name of the molecule
     */
    private TableColumn<MoleculeDataModel, String> nameColumn;
    /**
     * TableColumn for 2D structure of the molecule
     */
    private TableColumn<MoleculeDataModel, Image> structureColumn;
    /**
     * CheckBox in table header to select or deselect all items
     */
    private CheckBox selectAllCheckBox;
    /**
     * Observable list which contains all items to be shown in this tableView not only the displayed ones for this page (Pagination)
     */
    private ObservableList<MoleculeDataModel> itemsObservableList;
    /**
     * ContextMenu ot the TableView
     */
    private ContextMenu contextMenu;
    /**
     * MenuItem of ContextMenu to copy selected cell to clipboard
     */
    private MenuItem copyMenuItem;
    private boolean selectionAllCheckBoxAction;
    //</editor-fold>
    //
    /**
     * Constructor
     */
    public MoleculesDataTableView(){
        super();
        this.setEditable(true);
        this.getSelectionModel().setCellSelectionEnabled(true);
        //-selectionColumn
        this.selectionColumn = new TableColumn<>();
        this.selectAllCheckBox = new CheckBox();
        this.selectAllCheckBox.setAllowIndeterminate(true);
        this.selectAllCheckBox.setSelected(true);
        this.selectionColumn.setGraphic(this.selectAllCheckBox);
        this.selectionColumn.setMinWidth(GuiDefinitions.GUI_MOLECULES_TAB_TABLEVIEW_SELECTION_COLUMN_WIDTH);
        this.selectionColumn.prefWidthProperty().bind(
                this.widthProperty().multiply(0.05) //magic number
        );
        this.selectionColumn.setResizable(false);
        this.selectionColumn.setEditable(true);
        this.selectionColumn.setSortable(false);
        this.selectionColumn.setCellValueFactory(new PropertyValueFactory<>("selection"));
        this.selectionColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
        //-nameColumn
        this.nameColumn = new TableColumn<>(Message.get("MainTabPane.moleculesTab.tableView.nameColumn.header"));
        this.nameColumn.setMinWidth(150); //magic number
        this.nameColumn.prefWidthProperty().bind(
                this.widthProperty().multiply(0.15) //magic number
        );
        this.nameColumn.setResizable(true);
        this.nameColumn.setEditable(false);
        this.nameColumn.setSortable(true);
        this.nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.nameColumn.setCellFactory(TextFieldTableCell.<MoleculeDataModel>forTableColumn());
        this.nameColumn.setStyle("-fx-alignment: CENTER");
        //-structureColumn
        this.structureColumn = new TableColumn<>(Message.get("MainTabPane.moleculesTab.tableView.structureColumn.header"));
        this.structureColumn.setMinWidth(150); //magic number
        this.structureColumn.prefWidthProperty().bind(
                this.widthProperty().multiply(0.7975) //magic number
        );
        this.structureColumn.setResizable(true);
        this.structureColumn.setEditable(false);
        this.structureColumn.setSortable(false);
        this.structureColumn.setCellValueFactory(new PropertyValueFactory("structure"));
        this.structureColumn.setStyle("-fx-alignment: CENTER");
        //
        this.getColumns().addAll(this.selectionColumn, this.nameColumn, this.structureColumn);
        //context menu
        this.contextMenu = new ContextMenu();
        this.setContextMenu(this.contextMenu);
        //-copyMenuItem
        this.copyMenuItem = new MenuItem(Message.get("TableView.contextMenu.copyMenuItem"));
        this.copyMenuItem.setGraphic(new ImageView(new Image("de/unijena/cheminf/mortar/images/copy_icon_16x16.png")));
        this.contextMenu.getItems().add(this.copyMenuItem);
    }
    //
    //<editor-fold desc="public methods" defaultstate="collapsed">
    /**
     * Creates a page for the pagination for the dataTableView based on page index and settings, which shows the imported
     * molecules
     *
     * @param aPageIndex index
     * @param aSettingsContainer SettingsContainer
     * @return Node page of pagination
     */
    public Node createMoleculeTableViewPage(int aPageIndex, SettingsContainer aSettingsContainer){
        int tmpRowsPerPage = aSettingsContainer.getRowsPerPageSetting();
        int tmpFromIndex = aPageIndex * tmpRowsPerPage;
        int tmpToIndex = Math.min(tmpFromIndex + tmpRowsPerPage, this.itemsObservableList.size());
        this.getSelectAllCheckBox().setOnAction(event -> {
            this.selectionAllCheckBoxAction = true;
            for (int i = 0; i < this.itemsObservableList.size(); i++) {
                if(this.getSelectAllCheckBox().isSelected()){
                    this.itemsObservableList.get(i).setSelection(true);
                }
                else if(!this.getSelectAllCheckBox().isSelected()){
                    this.itemsObservableList.get(i).setSelection(false);
                }
            }
            this.selectionAllCheckBoxAction = false;
        });
        this.itemsObservableList.addListener((ListChangeListener) change ->{
            if(this.selectionAllCheckBoxAction){
                // No further action needed with column checkbox data when the select all checkbox is operated on
                return;
            }
            while(change.next()){
                if(change.wasUpdated()){
                    int checked = 0;
                    for(MoleculeDataModel tmpMoleculeDataModel : this.itemsObservableList){
                        if(tmpMoleculeDataModel.isSelected())
                            checked++;
                    }
                    if(checked == this.itemsObservableList.size()){
                        this.getSelectAllCheckBox().setSelected(true);
                        this.getSelectAllCheckBox().setIndeterminate(false);
                    }
                    else if(checked == 0){
                        this.getSelectAllCheckBox().setSelected(false);
                        this.getSelectAllCheckBox().setIndeterminate(false);
                    }
                    else if(checked > 0){
                        this.getSelectAllCheckBox().setSelected(false);
                        this.getSelectAllCheckBox().setIndeterminate(true);
                    }
                }
            }
        });
        List<MoleculeDataModel> tmpItems = this.itemsObservableList.subList(tmpFromIndex, tmpToIndex);
        for(MoleculeDataModel tmpMoleculeDataModel : tmpItems){
            tmpMoleculeDataModel.setStructureImageWidth(this.structureColumn.getWidth());
        }
        this.setItems(FXCollections.observableArrayList(this.itemsObservableList.subList(tmpFromIndex, tmpToIndex)));
        this.scrollTo(0);
        return new BorderPane(this);
    }
    //
    /**
     * Adds a change listener to the height property of table view which sets the height for structure images to
     * each MoleculeDataModel object of the items list and refreshes the table view
     * If image height is too small it will be set to GuiDefinitions.GUI_STRUCTURE_IMAGE_MIN_HEIGHT (50.0)
     *
     * @param aSettingsContainer SettingsContainer
     */
    public void addTableViewHeightListener(SettingsContainer aSettingsContainer){
        this.heightProperty().addListener((observable, oldValue, newValue) -> {
            GuiUtil.setImageStructureHeight(this, newValue.doubleValue(), aSettingsContainer);
            this.refresh();
        });
    }
    //</editor-fold>
    //
    //<editor-fold desc="properties" defaulstate="collapsed">
    /**
     * Returns the column which holds the checkbox to select the corresponding item
     *
     * @return TableColumn
     */
    public TableColumn<MoleculeDataModel, Boolean> getSelectionColumn(){
        return this.selectionColumn;
    }
    //
    /**
     * Returns the column that holds the name of the molecule
     *
     * @return TableColumn
     */
    public TableColumn<MoleculeDataModel, String> getNameColumn(){
        return this.nameColumn;
    }
    //
    /**
     * Returns the column which shows the 2d structure of the molecule
     *
     * @return TableColumn
     */
    public TableColumn<MoleculeDataModel, Image> getStructureColumn() {
        return this.structureColumn;
    }
    //
    /**
     * Returns menu item to copy
     *
     * @return MenuItem
     */
    public MenuItem getCopyMenuItem(){
        return this.copyMenuItem;
    }
    //
    /**
     * Returns checkbox to de/select all molecules
     *
     * @return CheckBox
     */
    public CheckBox getSelectAllCheckBox() { return this.selectAllCheckBox; }
    //
    /**
     * Returns the items of this tableview as a list of {@link MoleculeDataModel} objects
     *
     * @return List items
     */
    public List<MoleculeDataModel> getItemsList()
    {
        return this.itemsObservableList;
    }
    //
    /**
     * Sets the given list of {@link MoleculeDataModel} objects as items of this table view
     *
     * @param aListOfMolecules List
     */
    public void setItemsList(List<MoleculeDataModel> aListOfMolecules) {
        this.itemsObservableList = FXCollections.observableList(aListOfMolecules);
    }
    //</editor-fold>
}
