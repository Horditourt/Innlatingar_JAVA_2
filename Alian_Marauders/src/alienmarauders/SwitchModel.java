package alienmarauders;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SwitchModel {
    public final BooleanProperty mainMenuActive     = new SimpleBooleanProperty(true);
    public final BooleanProperty chatMenuActive     = new SimpleBooleanProperty(false);
    public final BooleanProperty settingsMenuActive = new SimpleBooleanProperty(false);
    public final BooleanProperty gameActive         = new SimpleBooleanProperty(false);
}
