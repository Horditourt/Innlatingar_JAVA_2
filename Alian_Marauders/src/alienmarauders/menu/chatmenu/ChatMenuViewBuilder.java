package alienmarauders.menu.chatmenu;

import alienmarauders.Styles;
import alienmarauders.SwitchModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.function.Consumer;

/**
 * Builds the chat menu view shown inside the Alien Marauders game.
 * <p>
 * Uses a {@link TextFlow} to display richly styled chat messages and a
 * {@link ListView} to show all online users.
 */
public class ChatMenuViewBuilder {

    private final ChatMenuModel model;
    private final SwitchModel switchModel;
    private final Runnable goMain;
    private final Consumer<String> onSend;

    private final BorderPane root = new BorderPane();

    private TextFlow messagesFlow;
    private ScrollPane messagesScroll;

    /**
     * Creates a new builder for the chat menu view.
     *
     * @param model       the chat model providing the list of users
     * @param switchModel the global switch model for background binding
     * @param goMain      callback invoked when the "Main menu" button is clicked
     * @param onSend      callback that will be invoked when the user presses "Send"
     *                    with the text entered in the input field
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
        // === Left: users list ===
        ListView<String> users = new ListView<>(model.users);
        users.setPrefWidth(180);

        // Color self username differently using Styles constants
        users.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);

                String self = model.getSelfUsername();
                if (self != null && self.equals(item)) {
                    setStyle(Styles.CHAT_USER_SELF_CELL_STYLE);
                } else {
                    setStyle(Styles.CHAT_USER_OTHER_CELL_STYLE);
                }
            }
        });

        // === Center: chat messages in TextFlow (inside ScrollPane) ===
        messagesFlow = new TextFlow();
        messagesFlow.setPadding(new Insets(8));

        messagesScroll = new ScrollPane(messagesFlow);
        messagesScroll.setFitToWidth(true);
        messagesScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messagesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // === Bottom: input + buttons ===
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

        // === Top header ===
        Label header = new Label("Chat room");
        header.setPadding(new Insets(8));
        HBox top = new HBox(header);
        top.setAlignment(Pos.CENTER);

        root.setTop(top);
        root.setLeft(users);
        root.setCenter(messagesScroll);
        root.setBottom(bottom);

        // Background bound to model
        root.styleProperty().bind(Styles.backgroundStyle(switchModel.backgroundName, this));

        return root;
    }

    /**
     * Appends a chat message to the message area.
     *
     * @param from   the sender username
     * @param text   the message body
     * @param isSelf {@code true} if this client sent the message
     */
    public void appendChatMessage(String from, String text, boolean isSelf) {
        if (from == null) {
            from = "Unknown";
        }
        if (text == null) {
            text = "";
        }

        Text line = new Text(from + ": " + text + "\n");
        line.setStyle(isSelf
                ? Styles.CHAT_MESSAGE_SELF_STYLE
                : Styles.CHAT_MESSAGE_OTHER_STYLE);

        messagesFlow.getChildren().add(line);
        scrollToBottom();
    }

    /**
     * Appends a system-style message (e.g. join/leave notifications).
     *
     * @param text the system message to append
     */
    public void appendSystemMessage(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        Text line = new Text(text + "\n");
        line.setStyle(Styles.CHAT_MESSAGE_SYSTEM_STYLE);
        messagesFlow.getChildren().add(line);
        scrollToBottom();
    }

    /**
     * Scrolls the message area to the bottom so the latest message is visible.
     */
    private void scrollToBottom() {
        if (messagesScroll != null) {
            messagesScroll.layout();
            messagesScroll.setVvalue(1.0);
        }
    }
}
