package alienmarauders.menu.chatmenu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatMenuModel {
    public final ObservableList<String> users =
        FXCollections.observableArrayList("Ripley", "Deckard", "Leia", "Worf");
}
