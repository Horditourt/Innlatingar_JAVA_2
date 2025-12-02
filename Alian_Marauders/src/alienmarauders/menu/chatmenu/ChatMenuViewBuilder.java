// src/alienmarauders/menu/chatmenu/ChatMenuViewBuilder.java
package alienmarauders.menu.chatmenu;

import alienmarauders.Styles;
import alienmarauders.SwitchModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.function.Consumer;

/**
 * Builds the chat menu view shown inside the Alien Marauders game.
 * <p>
 * The view exposes a callback for sending chat messages and provides a
 * convenience method for appending new messages to the message area.
 */
public class ChatMenuViewBuilder {

    private final ChatMenuModel model;
    private final Runnable goMain;
    private final SwitchModel switchModel;
    private final Consumer<String> onSend;

    private final BorderPane root = new BorderPane();

    // We keep references so the controller can append messages, etc.
    private TextArea messages;

    /**
     * Creates a new builder for the chat menu view.
     *
     * @param model      the chat model providing the list of users
     * @param switchModel the global switch model for background binding
     * @param goMain     callback invoked when the "Main menu" button is clicked
     * @param onSend     callback that will be invoked when the user presses "Send"
     *                   with the text entered in the input field
     */
    public ChatMenuViewBuilder(ChatMenuModel model,
                               SwitchModel switchModel,
                               Runnable goMain,
                               Consumer<String> onSend) {
        this.model = model;
        this.switchModel = switchModel;
        this.goMain = goMain;
        this.onSend = onSend;
    }

    /**
     * Builds and returns the root region of the chat menu.
     *
     * @return the root {@link Region} containing the chat UI
     */
    public Region build() {
        ListView<String> users = new ListView<>(model.users);
        users.setPrefWidth(180);

        messages = new TextArea();
        messages.setEditable(false);
        messages.setWrapText(true);

        TextField input = new TextField();
        input.setPromptText("Type a message.");
        Button send = new Button("Send");
        Button back = new Button("Main menu");

        send.setOnAction(e -> {
            String txt = input.getText();
            if (txt != null && !txt.isBlank()) {
                if (onSend != null) {
                    onSend.accept(txt);
                }
                input.clear();
            }
        });
        back.setOnAction(e -> goMain.run());

        HBox bottom = new HBox(8, input, send, back);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(8));
        HBox.setHgrow(input, Priority.ALWAYS);

        Label header = new Label("Chat room");
        header.setFont(Font.font(18));
        HBox top = new HBox(header);
        top.setPadding(new Insets(10));
        top.setAlignment(Pos.CENTER);

        root.setTop(top);
        root.setLeft(users);
        root.setCenter(messages);
        root.setBottom(bottom);

        // Background bound to model
        root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));
        return root;
    }

    /**
     * Appends a single line to the message area.
     *
     * @param line the line of text to append; {@code null} is ignored
     */
    public void appendMessage(String line) {
        if (messages == null || line == null) {
            return;
        }
        if (!messages.getText().isEmpty() && !messages.getText().endsWith("\n")) {
            messages.appendText("\n");
        }
        messages.appendText(line + "\n");
    }
}
