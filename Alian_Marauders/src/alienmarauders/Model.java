package alienmarauders;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Model {
    // Show exactly one at a time. (MainMenu is the default fallback.)
    private final BooleanProperty chatMenuVisible     = new SimpleBooleanProperty(false);
    private final BooleanProperty settingsMenuVisible = new SimpleBooleanProperty(false);
    private final BooleanProperty gameVisible         = new SimpleBooleanProperty(false);

    public BooleanProperty chatMenuVisibleProperty() { return chatMenuVisible; }
    public BooleanProperty settingsMenuVisibleProperty() { return settingsMenuVisible; }
    public BooleanProperty gameVisibleProperty() { return gameVisible; }

    // Convenience setters
    public void showChat()     { setOnly(chatMenuVisible); }
    public void showSettings() { setOnly(settingsMenuVisible); }
    public void showGame()     { setOnly(gameVisible); }
    public void showMain()     { chatMenuVisible.set(false); settingsMenuVisible.set(false); gameVisible.set(false); }

    private void setOnly(BooleanProperty which) {
        chatMenuVisible.set(false);
        settingsMenuVisible.set(false);
        gameVisible.set(false);
        which.set(true);
    }
}
