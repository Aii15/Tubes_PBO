package tubes_pbo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 1200, 900);
        // Ensure stylesheet loads from resources
        try {
            String css = App.class.getResource("/tubes_pbo/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ex) {
            System.err.println("Warning: styles.css not found on classpath: " + ex.getMessage());
        }

        stage.setTitle("Impactful Coffee - Catalog");
        stage.setScene(scene);
        // Launch maximized so window fills screen on start
        try {
            stage.setMaximized(true);
        } catch (Exception ex) {
            // fallback: if maximizing not supported, set full screen
            stage.setFullScreen(false);
        }
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}