/* package alienmarauders;

import alienmarauders.menu.chatmenu.ChatMenuViewBuilder;
import alienmarauders.menu.settingsmenu.SettingsMenuViewBuilder;
import alienmarauders.menu.mainmenu.MainMenuViewBuilder;

import java.io.File;

import alienmarauders.game.Game; // Your game view (rename if needed)

import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Region;

public class Controller {
    private final Model model;
    private final ViewBuilder view;
    private final SettingsMenuViewBuilder settingsMenu;

    // Keep references to the view roots
    private final Region mainMenuRoot;
    private final Region chatMenuRoot;
    private final Region settingsMenuRoot;
    private final Region gameRoot;

    public Controller(Model model, ViewBuilder view,
            MainMenuViewBuilder mainMenu,
            ChatMenuViewBuilder chatMenu,
            SettingsMenuViewBuilder settingsMenu,
            Game game) {
        this.model = model;
        this.view = view;
        this.settingsMenu = settingsMenu;

        this.mainMenuRoot = mainMenu.getRoot();
        this.chatMenuRoot = chatMenu.getRoot();
        this.settingsMenuRoot = settingsMenu.getRoot();
        this.gameRoot = game.getRoot();
    }

    public void initialize(Scene scene) {
        // Put all screens into the content layer (order doesn’t matter)
        view.getContentLayer().getChildren().setAll(
                mainMenuRoot, chatMenuRoot, settingsMenuRoot, gameRoot);

        // Hide/show by binding visible -> model flags
        // Also bind managed to visible so hidden nodes don't affect layout
        bindVisibility(mainMenuRoot,
                model.chatMenuVisibleProperty().not()
                        .and(model.settingsMenuVisibleProperty().not())
                        .and(model.gameVisibleProperty().not()));

        bindVisibility(chatMenuRoot, model.chatMenuVisibleProperty());
        bindVisibility(settingsMenuRoot, model.settingsMenuVisibleProperty());
        bindVisibility(gameRoot, model.gameVisibleProperty());

        java.util.Map<String, String> mapDiff = new java.util.LinkedHashMap<>();
        mapDiff.put("Easy", "Easy");
        mapDiff.put("Medium", "Medium");
        mapDiff.put("Hard", "Hard");

        ComboBox<String> comboDiff = settingsMenu.getDifficultyOptions();

        if (comboDiff.getSelectionModel().isEmpty() && !comboDiff.getItems().isEmpty()) {
            comboDiff.getSelectionModel().selectFirst();
        }

        // paint current diff selection
        comboDiff.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, curVal) -> {
            if (curVal == null)
                return;
            String fileNameDiff = mapDiff.getOrDefault(curVal, "Medium"); // cover=true
        });

        // also trigger once on init
        String initialDiff = comboDiff.getSelectionModel().getSelectedItem();
        if (initialDiff != null) {
            String fileNameDiff = mapDiff.getOrDefault(initialDiff, "Medium");
        }

        // Map friendly names -> filenames in alienmarauders/images
        java.util.Map<String, String> mapBG = new java.util.LinkedHashMap<>();
        mapBG.put("Space", "space.png");
        mapBG.put("Planet", "planets.png");
        mapBG.put("Nebula", "nebula.png");

        ComboBox<String> combo = settingsMenu.getBgOptions();

        // select first option if none selected
        if (combo.getSelectionModel().isEmpty() && !combo.getItems().isEmpty()) {
            combo.getSelectionModel().selectFirst();
        }

        // paint current selection
        combo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, curVal) -> {
            if (curVal == null)
                return;
            String fileName = mapBG.getOrDefault(curVal, "space.png");
            String fullPath = new File("src/alienmarauders/images", fileName).getPath();
            view.setStaticBackground(fullPath, true); // cover=true
        });

        // also trigger once on init
        String initial = combo.getSelectionModel().getSelectedItem();
        if (initial != null) {
            String fileName = mapBG.getOrDefault(initial, "space.png");
            String fullPath = new File("src/alienmarauders/images", fileName).getPath();
            view.setStaticBackground(fullPath, true);
        }

    }

    private void bindVisibility(Region node, javafx.beans.value.ObservableValue<Boolean> isVisible) {
        node.visibleProperty().bind(isVisible);
        node.managedProperty().bind(node.visibleProperty());
    }

    // Expose actions (wire to buttons inside each view)
    public void goMain() {
        model.showMain();
    }

    public void goChat() {
        model.showChat();
    }

    public void goSettings() {
        model.showSettings();
    }

    public void goGame() {
        model.showGame();
    }
}
 */