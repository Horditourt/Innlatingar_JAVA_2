import alienmarauders.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        SwitchController switchController = new SwitchController();

        Scene scene = new Scene(switchController.getView(), 1000, 800);
        stage.setTitle("Alien Marauders");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
