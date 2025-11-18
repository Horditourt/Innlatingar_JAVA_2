package alienmarauders.menu.settingsmenu;

import alienmarauders.SwitchModel;
import alienmarauders.Styles;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

public class SettingsMenuViewBuilder {
    //private final SettingsMenuModel model;
    private final Runnable goMain;
    private final SwitchModel switchModel;

    private final VBox root = new VBox(10);
    private final ComboBox<String> bgOptions = new ComboBox<>();
    private final ComboBox<String> difficultyOptions = new ComboBox<>();

    public SettingsMenuViewBuilder(SettingsMenuModel model, SwitchModel switchModel, Runnable goMain) {
        //this.model = model;
        this.switchModel = switchModel;
        this.goMain = goMain;
    }

    public Region build() {
        Label title = new Label("Settings");
        Button back = new Button("Main menu");
        back.setOnAction(e -> goMain.run());

        // Background choices
        bgOptions.getItems().setAll("Space", "Planet", "Nebula");
        bgOptions.setValue(switchModel.backgroundName.get()); // initial UI matches model
        Bindings.bindBidirectional(bgOptions.valueProperty(), switchModel.backgroundName);

        // Difficulty choices
        difficultyOptions.getItems().setAll("Easy", "Medium", "Hard");
        difficultyOptions.setValue(switchModel.difficulty.get()); // initial UI matches model
        Bindings.bindBidirectional(difficultyOptions.valueProperty(), switchModel.difficulty);

        root.getChildren().addAll(title, new Label("Background:"), bgOptions,
                new Label("Difficulty:"), difficultyOptions,
                back);
        root.setAlignment(Pos.CENTER);
        root.setPickOnBounds(false);

        // Bind the background style (no listeners)
        root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));

        return root;
    }
}
