import alienmarauders.*;
import alienmarauders.game.GameController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private SwitchController switchController;

    @Override
    public void start(Stage stage) {
        switchController = new SwitchController();

        Scene scene = new Scene(switchController.getView(), 800, 600);
        stage.setTitle("Alien Marauders");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Called by the JavaFX runtime when the application is about to exit.
     * <p>
     * We use this hook to shut down the game controller and its underlying
     * executor service (via {@link GameController#shutdown()}), ensuring
     * that no background threads are left running after the window closes.
     */
    @Override
    public void stop() {
        if (switchController != null) {
            GameController gameController = switchController.getGameController();
            if (gameController != null) {
                gameController.shutdown();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
