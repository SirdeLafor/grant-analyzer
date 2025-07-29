package org.example.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.example.MainApp;
import org.example.controller.BaseController;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;

public class NavigationService {
    private final StackPane root;
    private final Stack<Node> history = new Stack<>();
    private final DataService dataService;

    public NavigationService(StackPane root, DataService dataService) {
        this.root = root;
        this.dataService = dataService;
    }

    public void navigateTo(String fxmlFile) {
        try {
            URL resourceUrl = MainApp.class.getResource("/org/example/" + fxmlFile);
            if (resourceUrl == null) {
                System.err.println("Не удалось найти FXML файл: " + fxmlFile);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent parent = loader.load();

            BaseController controller = loader.getController();
            controller.setNavigationService(this);
            controller.setDataService(dataService);

            if (!root.getChildren().isEmpty()) {
                history.push(root.getChildren().get(0));
            }
            root.getChildren().setAll(parent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goBack() {
        if (!history.isEmpty()) {
            root.getChildren().setAll(history.pop());
        }
    }
}
