package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.example.model.GrantData;
import org.example.service.DataService;
import org.example.service.NavigationService;
import org.example.util.PdfParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableViewController implements BaseController {
    private NavigationService navigationService;
    private DataService dataService;

    @FXML private TableView<GrantData> tableView;
    @FXML private Label statusLabel;
    @FXML private Button loadButton;
    @FXML private TextField searchField;

    @FXML private TableColumn<GrantData, Integer> numberCol;
    @FXML private TableColumn<GrantData, String> iinCol;
    @FXML private TableColumn<GrantData, String> fullNameCol;
    @FXML private TableColumn<GrantData, Integer> scoreCol;
    @FXML private TableColumn<GrantData, String> universityNameCol;
    @FXML private TableColumn<GrantData, String> quotaCol;
    @FXML private TableColumn<GrantData, String> specialtyNameCol;

    private final ObservableList<GrantData> masterData = FXCollections.observableArrayList();

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @Override
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @FXML
    public void initialize() {
        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        iinCol.setCellValueFactory(new PropertyValueFactory<>("iin"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        universityNameCol.setCellValueFactory(new PropertyValueFactory<>("universityName"));
        quotaCol.setCellValueFactory(new PropertyValueFactory<>("quota"));
        specialtyNameCol.setCellValueFactory(new PropertyValueFactory<>("specialtyName"));

        if (dataService != null && dataService.hasData()) {
            masterData.setAll(dataService.getLoadedGrantData());
            statusLabel.setText("Загружено из базы данных " + masterData.size() + " записей.");
        } else {
            statusLabel.setText("База данных пуста. Пожалуйста, загрузите PDF файл.");
        }

        setupSearchFilter();
    }

    @FXML
    private void handleLoadPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите PDF файл с грантами");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(loadButton.getScene().getWindow());

        if (selectedFile != null) {
            Task<Map<String, List<GrantData>>> task = new Task<>() {
                @Override
                protected Map<String, List<GrantData>> call() {
                    Platform.runLater(() -> statusLabel.setText("Идет обработка файла..."));
                    return PdfParser.parseGrantsPdf(selectedFile, dataService);
                }
            };

            task.setOnSucceeded(event -> {
                Map<String, List<GrantData>> groupedData = task.getValue();
                List<GrantData> allData = new ArrayList<>();
                groupedData.values().forEach(allData::addAll);

                masterData.setAll(allData);
                dataService.saveGrantData(allData);
                statusLabel.setText("Загружено и сохранено в базу " + allData.size() + " записей.");
            });

            task.setOnFailed(event -> {
                statusLabel.setText("Ошибка при обработке файла.");
                task.getException().printStackTrace();
            });

            new Thread(task).start();
        }
    }

    private void setupSearchFilter() {
        FilteredList<GrantData> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(grantData -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (grantData.getFullName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (grantData.getIin().toLowerCase().contains(lowerCaseFilter)) return true;
                if (grantData.getUniversityName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (grantData.getSpecialtyName().toLowerCase().contains(lowerCaseFilter)) return true;
                return String.valueOf(grantData.getScore()).contains(lowerCaseFilter);
            });
        });

        tableView.setItems(filteredData);
    }

    @FXML
    private void goBack() {
        navigationService.goBack();
    }
}
