package alienmarauders.menu.settingsmenu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

public class SettingsMenuViewBuilder {
    private SettingsMenuModel model;
    private Runnable goMain;

    private final VBox root = new VBox(10);
    private ComboBox<String> bgOptions;
    private ComboBox<String> difficultyOptions;

    public SettingsMenuViewBuilder(SettingsMenuModel model, Runnable goMain) {
        this.model = model;
        this.goMain = goMain;
        
    }

    public ComboBox<String> getBgOptions() {
        return bgOptions;
    }

    public ComboBox<String> getDifficultyOptions() {
        return difficultyOptions;
    }

    public Region build() {
        Label title = new Label("Settings");
        Button back = new Button("Back");
        back.setOnAction(e -> goMain.run());
        bgOptions = new ComboBox<>();
        bgOptions.getItems().addAll("Space", "Planet", "Nebula");
        difficultyOptions = new ComboBox<>();
        difficultyOptions.getItems().addAll("Easy", "Medium", "Hard");

        root.getChildren().addAll(title, back, bgOptions);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20; -fx-background-color: transparent;");
        root.setPickOnBounds(false);
        return root;
    }

}
