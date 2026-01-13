package geo_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/geo_project/main.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 700);
        stage.setTitle("PokéMap - Avistamientos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.setProperty("prism.order", "sw"); // 🔥 CLAVE
        System.setProperty("prism.verbose", "true"); // opcional (debug)
        launch(args);
    }
}