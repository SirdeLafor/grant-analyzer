package org.example.controller;

import org.example.service.DataService;
import org.example.service.NavigationService;

public interface BaseController {
    void setNavigationService(NavigationService navigationService);
    void setDataService(DataService dataService);
}