package org.example.controller;

import javafx.fxml.FXML;
import org.example.service.DataService;
import org.example.service.NavigationService;

public class HomeController implements BaseController {

    private NavigationService navigationService;
    private DataService dataService;

    @Override
    public void setNavigationService(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @Override
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @FXML
    private void handleShowTable() {
        navigationService.navigateTo("table-view.fxml");
    }
}