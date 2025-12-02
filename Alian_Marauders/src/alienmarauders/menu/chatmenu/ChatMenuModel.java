package alienmarauders.menu.chatmenu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Model for the chat menu.
 * <p>
 * Holds the observable list of online users which is bound to the chat GUI.
 */
public class ChatMenuModel {

    /**
     * Observable list of usernames currently shown in the chat user list.
     */
    public final ObservableList<String> users =
            FXCollections.observableArrayList();

    /**
     * Replaces the current list of users with a new list.
     *
     * @param newUsers the new list of usernames to display; {@code null} is treated as empty
     */
    public void setUsers(List<String> newUsers) {
        users.clear();
        if (newUsers != null) {
            users.addAll(newUsers);
        }
    }

    /**
     * Adds the given username to the list if it is not already present.
     *
     * @param username the username to add; ignored if {@code null} or blank
     */
    public void addUser(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        if (!users.contains(username)) {
            users.add(username);
        }
    }

    /**
     * Removes the given username from the list of users.
     *
     * @param username the username to remove; ignored if {@code null}
     */
    public void removeUser(String username) {
        if (username == null) {
            return;
        }
        users.remove(username);
    }
}
