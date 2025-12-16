package alienmarauders.menu.settingsmenu;

import alienmarauders.SwitchModel;
import alienmarauders.Styles;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Builds the settings menu view.
 * <p>
 * Settings values are bound bidirectionally to {@link SwitchModel} properties,
 * so the controller does not need to read or write JavaFX controls directly.
 */
public class SettingsMenuViewBuilder {

    private final SwitchModel switchModel;

    private Runnable onBackToMain;

    private final VBox root = new VBox(10);
    private final ComboBox<String> bgOptions = new ComboBox<>();
    private final ComboBox<String> difficultyOptions = new ComboBox<>();

    private boolean built = false;

    /**
     * Creates a new settings menu view builder.
     *
     * @param model      the settings model (kept for consistency with the existing architecture)
     * @param switchModel global switch model used for background and difficulty bindings
     */
    public SettingsMenuViewBuilder(SettingsMenuModel model, SwitchModel switchModel) {
        this.switchModel = switchModel;
    }

    /**
     * Sets the callback invoked when the user presses "Main menu".
     *
     * @param action the action to run
     */
    public void setOnBackToMain(Runnable action) {
        this.onBackToMain = action;
    }

    /**
     * Builds (once) and returns the root region of the settings menu.
     *
     * @return the root {@link Region}
     */
    public Region build() {
        if (built) {
            return root;
        }
        built = true;

        Label title = new Label("Settings");
        Button back = new Button("Main menu");
        back.setOnAction(e -> runIfSet(onBackToMain));

        // Background choices
        bgOptions.getItems().setAll("Space", "Planet", "Nebula");
        bgOptions.setValue(switchModel.backgroundName.get()); // initial UI matches model
        Bindings.bindBidirectional(bgOptions.valueProperty(), switchModel.backgroundName);

        // Difficulty choices
        difficultyOptions.getItems().setAll("Easy", "Medium", "Hard");
        difficultyOptions.setValue(switchModel.difficulty.get()); // initial UI matches model
        Bindings.bindBidirectional(difficultyOptions.valueProperty(), switchModel.difficulty);

        root.getChildren().addAll(
                title,
                new Label("Background:"), bgOptions,
                new Label("Difficulty:"), difficultyOptions,
                back
        );
        root.setAlignment(Pos.CENTER);
        root.setPickOnBounds(false);

        // Bind the background style (no listeners)
        root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));

        return root;
    }

    /**
     * Runs the given action if it is non-null.
     *
     * @param action the runnable to execute
     */
    private void runIfSet(Runnable action) {
        if (action != null) {
            action.run();
        }
    }
}
