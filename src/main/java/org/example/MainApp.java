package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.service.DataService;
import org.example.service.DatabaseService;
import org.example.service.NavigationService;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        // Создаем единые экземпляры сервисов
        DatabaseService databaseService = new DatabaseService();
        DataService dataService = new DataService(databaseService);

        NavigationService navigationService = new NavigationService(root, dataService);
        navigationService.navigateTo("home-view.fxml");

        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/org/example/styles.css").toExternalForm());

        primaryStage.setTitle("Анализатор грантов");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}