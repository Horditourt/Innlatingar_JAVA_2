import alienmarauders.*;
import alienmarauders.game.Game;
import alienmarauders.menu.chatmenu.ChatMenuViewBuilder;
import alienmarauders.menu.mainmenu.MainMenuViewBuilder;
import alienmarauders.menu.settingsmenu.SettingsMenuViewBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Model model = new Model();
        ViewBuilder view = new ViewBuilder();

        // Static background for ALL screens (cover=true fills window)
        view.setStaticBackground("src/alienmarauders/images/space.png", true);

        // Create controller first so subviews can call back
        Controller controller = new Controller(
                model, view,
                new MainMenuViewBuilder(null), // temp, will rewire below
                new ChatMenuViewBuilder(null),
                new SettingsMenuViewBuilder(null),
                new Game(null)
        );

        // Recreate subviews now that controller exists (so buttons can call it)
        MainMenuViewBuilder mainMenu = new MainMenuViewBuilder(controller);
        ChatMenuViewBuilder chatMenu = new ChatMenuViewBuilder(controller);
        SettingsMenuViewBuilder settingsMenu = new SettingsMenuViewBuilder(controller);
        Game game = new Game(controller);

        // Rebuild controller with proper references
        Controller wired = new Controller(model, view, mainMenu, chatMenu, settingsMenu, game);

        Scene scene = new Scene(view.getRoot(), 800, 600);
        wired.initialize(scene);

        stage.setTitle("Alien Marauders");
        stage.setScene(scene);
        stage.show();

        // Start on main menu
        model.showMain();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
