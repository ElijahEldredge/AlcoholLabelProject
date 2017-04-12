package com.emeraldElves.alcohollabelproject.UserInterface;

import com.emeraldElves.alcohollabelproject.COLASearch;
import com.emeraldElves.alcohollabelproject.Data.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.commons.lang3.StringEscapeUtils;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Essam on 4/2/2017.
 */
public class SearchController {

    private Main main;
    private String searchTerm;
    private AutoCompletionBinding<String> autoCompletionBinding;
    private Set<String> possibleSuggestions = new HashSet<>();
    @FXML
    private TextField searchField;
    @FXML
    private TableView<SubmittedApplication> resultsTable;
    @FXML
    private TableColumn<SubmittedApplication, String> dateCol;
    @FXML
    private TableColumn<SubmittedApplication, String> manufacturerCol;
    @FXML
    private TableColumn<SubmittedApplication, String> brandCol;
    @FXML
    private TableColumn<SubmittedApplication, String> typeCol;
    @FXML
    private TableColumn<SubmittedApplication, String> contentCol;
    @FXML
    private Button saveBtn;
    @FXML
    private MenuItem contextSaveBtn;
    @FXML
    private Label descriptionLabel;
    @FXML
    private CheckMenuItem filterBeers;
    @FXML
    private CheckMenuItem filterWine;
    @FXML
    private CheckMenuItem filterSpirits;

    private ObservableList<SubmittedApplication> data = FXCollections.observableArrayList();
    private COLASearch search;

    public SearchController() {
        this.search = new COLASearch();
    }

    public void init(Main main, String searchTerm) {
        this.main = main;
        this.searchTerm = searchTerm;
        dateCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SubmittedApplication, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SubmittedApplication, String> p) {
                DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                Date date = p.getValue().getApplication().getSubmissionDate();
                return new ReadOnlyObjectWrapper<String>(StringEscapeUtils.escapeJava(dateFormat.format(date)));
            }
        });
        manufacturerCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SubmittedApplication, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SubmittedApplication, String> p) {
                return new ReadOnlyObjectWrapper<String>(StringEscapeUtils.escapeJava(p.getValue().getApplication().getAlcohol().getName()));
            }
        });
        brandCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SubmittedApplication, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SubmittedApplication, String> p) {
                return new ReadOnlyObjectWrapper<String>(StringEscapeUtils.escapeJava(p.getValue().getApplication().getAlcohol().getBrandName()));
            }
        });
        typeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SubmittedApplication, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SubmittedApplication, String> p) {
                String alcoholName = p.getValue().getApplication().getAlcohol().getAlcoholType().name();
                switch (alcoholName){
                    case "DISTILLEDSPIRITS":
                        return new ReadOnlyObjectWrapper<String>("Distilled Spirit");
                    case "WINE":
                        return new ReadOnlyObjectWrapper<String>("Wine");
                    case "BEER":
                        return new ReadOnlyObjectWrapper<String>("Beer");
                    default:
                        return new ReadOnlyObjectWrapper<String>("N/A");
                }

            }
        });
        contentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SubmittedApplication, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SubmittedApplication, String> p) {
                return new ReadOnlyObjectWrapper<String>(StringEscapeUtils.escapeJava(String.valueOf(p.getValue().getApplication().getAlcohol().getAlcoholContent())));
            }
        });
        saveBtn.setDisable(data.size() == 0);
        descriptionLabel.setVisible(false);
        contextSaveBtn.setDisable(data.size() == 0);
        resultsTable.setItems(data);
        resultsTable.setRowFactory(tv -> {
            TableRow<SubmittedApplication> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    SubmittedApplication rowData = row.getItem();
                    main.loadDetailedSearchPage(rowData, searchTerm);
                }
            });
            return row;
        });

        //autoCompletionBinding = TextFields.bindAutoCompletion(searchField, possibleSuggestions);


        /*searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {

                autoCompletionBinding.setUserInput(searchField.getText().trim());
                //search(searchField.getText().trim());


            }
        });*/
        refreshSuggestions();
        searchField.setText(searchTerm);
        search(searchTerm);
    }
    public void search(ActionEvent e) {
        Platform.runLater(() -> {
            search(searchField.getText());
        });
    }
    public void onKeyType(KeyEvent e){
        //delay is required for .getText() to get the updated field
        Platform.runLater(() -> {
            search(searchField.getText());



        });
    }
    public void search(String searchTerm) {
        //Remove previous results
        data.remove(0, data.size());

        //Find & add matching applications
        List<SubmittedApplication> resultsList = search.searchByName(searchTerm.trim());
        filterList(resultsList);
        data.addAll(resultsList); //change to resultsList
        descriptionLabel.setText("Showing " + data.size() + " results for \"" + searchTerm + "\"");
        descriptionLabel.setVisible(true);
        saveBtn.setDisable(data.size() == 0);
        contextSaveBtn.setDisable(data.size() == 0);
    }
    private void refreshSuggestions(){
        List<SubmittedApplication> resultsList = search.searchApprovedApplications();
        filterList(resultsList);
        possibleSuggestions.clear();
        /*
        Collections.sort(resultsList, new Comparator<SubmittedApplication>() {
            @Override
            public int compare(SubmittedApplication lhs, SubmittedApplication rhs) {
                return lhs.getApplication().getAlcohol().getBrandName().compareToIgnoreCase(rhs.getApplication().getAlcohol().getBrandName());
            }
        });
        */
        for(SubmittedApplication application: resultsList){
            possibleSuggestions.add(application.getApplication().getAlcohol().getBrandName());
            possibleSuggestions.add(application.getApplication().getAlcohol().getName());
        }

        if (autoCompletionBinding != null){
            autoCompletionBinding.dispose();
        }
        autoCompletionBinding = TextFields.bindAutoCompletion(searchField, possibleSuggestions);
    }
    public void filter(ActionEvent e){
        Platform.runLater(() -> {
            refreshSuggestions();
            search(e);



        });

    }
    public void goHome() {
        main.loadHomepage();
    }

    private void filterList(List<SubmittedApplication> appList){
        appList.removeIf(p -> (filterBeers.isSelected() && p.getApplication().getAlcohol().getAlcoholType() == AlcoholType.BEER));
        appList.removeIf(p -> (filterWine.isSelected() && p.getApplication().getAlcohol().getAlcoholType() == AlcoholType.WINE));
        appList.removeIf(p -> (filterSpirits.isSelected() && p.getApplication().getAlcohol().getAlcoholType() == AlcoholType.DISTILLEDSPIRITS));
    }
    public void saveCSV(ActionEvent e) {


        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save Results");
        fileChooser.setInitialFileName("results.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comma Seperated Values", "*.csv"));
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Comma Seperated Values", "*.csv"));


        File file = fileChooser.showSaveDialog(Main.stage);
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);

                /*
                 * This is a weird way of getting column names, but this way columns names are
                 * always printed out correctly and in the order the user arranged them.
                 */

                //write out column names
                for (int colId = 0; colId < resultsTable.getColumns().size(); colId++) {
                    TableColumn<SubmittedApplication, ?> col = resultsTable.getColumns().get(colId);

                    if (colId > 0) fileWriter.write(",");

                    fileWriter.write(StringEscapeUtils.escapeCsv(col.getText()));
                }
                fileWriter.write("\r\n");

                for (int rowId = 0; rowId < data.size(); rowId++) {
                    //for each row, write out column values
                    for (int colId = 0; colId < resultsTable.getColumns().size(); colId++) {
                        TableColumn<SubmittedApplication, ?> col = resultsTable.getColumns().get(colId);
                        if (colId > 0) fileWriter.write(",");
                        fileWriter.write(StringEscapeUtils.escapeCsv((String) col.getCellObservableValue(rowId).getValue()));
                    }
                    fileWriter.write("\r\n");
                }

                fileWriter.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}