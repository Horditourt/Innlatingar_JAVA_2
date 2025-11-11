import alienmarauders.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        SwitchModel model = new SwitchModel();
        SwitchController switchController = new SwitchController();

        Scene scene = new Scene(switchController.getView(), 800, 600);
        stage.setTitle("Alien Marauders");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
