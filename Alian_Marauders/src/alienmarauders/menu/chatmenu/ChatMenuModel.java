package alienmarauders.menu.chatmenu;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Model for the chat menu.
 * <p>
 * Holds observable UI state such as online users, the local username and the
 * list of chat lines. The view binds to this model.
 */
public class ChatMenuModel {

    /** Observable list of current online users. */
    public final ObservableList<String> users = FXCollections.observableArrayList();

    /** Observable list of chat lines shown in the UI. */
    public final ObservableList<ChatLine> lines = FXCollections.observableArrayList();

    private final StringProperty selfUsername = new SimpleStringProperty("");

    /**
     * Replaces the list of users in the model.
     *
     * @param newUsers users to display
     */
    public void setUsers(List<String> newUsers) {
        users.setAll(newUsers);
    }

    /**
     * Adds a chat line to the model (view will render it).
     *
     * @param line chat line
     */
    public void addChatLine(ChatLine line) {
        if (line != null) {
            lines.add(line);
        }
    }

    /**
     * Clears all chat lines.
     */
    public void clearLines() {
        lines.clear();
    }

    /**
     * Sets the local client's username (used for styling "self" messages).
     *
     * @param username local username
     */
    public void setSelfUsername(String username) {
        selfUsername.set(username != null ? username : "");
    }

    /**
     * Returns the local client's username.
     *
     * @return username
     */
    public String getSelfUsername() {
        return selfUsername.get();
    }

    /**
     * Returns the property storing the local client's username.
     *
     * @return the {@link StringProperty} for the self username
     */
    public StringProperty selfUsernameProperty() {
        return selfUsername;
    }
}
