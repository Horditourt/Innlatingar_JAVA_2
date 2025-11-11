package alienmarauders;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SwitchModel {
    public final BooleanProperty mainMenuActive     = new SimpleBooleanProperty(true);
    public final BooleanProperty chatMenuActive     = new SimpleBooleanProperty(false);
    public final BooleanProperty settingsMenuActive = new SimpleBooleanProperty(false);
    public final BooleanProperty gameActive         = new SimpleBooleanProperty(false);
    
    // Background: "Space" | "Planet" | "Nebula"
    public final StringProperty backgroundName = new SimpleStringProperty("Space");

    // Difficulty: "Easy" | "Medium" | "Hard"
    public final StringProperty difficulty = new SimpleStringProperty("Medium");

    

}
