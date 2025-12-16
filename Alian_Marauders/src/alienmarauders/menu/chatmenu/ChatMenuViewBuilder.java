package alienmarauders.menu.chatmenu;

import alienmarauders.Styles;
import alienmarauders.SwitchModel;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Builds the chat menu view.
 * <p>
 * The view observes {@link ChatMenuModel} and renders its state. The controller
 * updates only the model to avoid tight coupling.
 */
public class ChatMenuViewBuilder {

    private final ChatMenuModel model;
    private final SwitchModel switchModel;
    private final Runnable goMain;
    private final Consumer<String> onSend;

    /**
     * Creates a new view builder for the chat menu.
     *
     * @param model       the chat model to bind to
     * @param switchModel global switch model (used for background binding)
     * @param goMain      callback used when the user presses "Back"
     * @param onSend      callback used when the user presses "Send"
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
     * <p>
     * Background is bound to {@link SwitchModel#backgroundName} using
     * {@link Styles#backgroundStyle(javafx.beans.property.StringProperty, Object)}.
     *
     * @return the root {@link Region} containing the chat UI
     */
    public Region build() {
        BorderPane root = new BorderPane();

        root.styleProperty().bind(
                Bindings.concat(
                        Styles.backgroundStyle(switchModel.backgroundName, this),
                        "\n-fx-background-color: rgba(255,255,255,0.85);"
                )
        );

        // === Left: users list ===
        ListView<String> users = new ListView<>(model.users);
        users.setPrefWidth(220);

        Label usersTitle = new Label("Online");
        usersTitle.setStyle(Styles.menuTitleStyle());

        VBox left = new VBox(10, usersTitle, users);
        left.setPadding(new Insets(20));
        left.setAlignment(Pos.TOP_LEFT);

        // === Center: messages list ===
        Label chatTitle = new Label("Chat");
        chatTitle.setStyle(Styles.menuTitleStyle());

        ListView<ChatLine> chatList = new ListView<>(model.lines);
        chatList.setFocusTraversable(false);
        chatList.setCellFactory(lv -> new ChatLineCell());

        // Auto-scroll when new lines are added
        model.lines.addListener((ListChangeListener<ChatLine>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    chatList.scrollTo(model.lines.size() - 1);
                }
            }
        });

        VBox center = new VBox(10, chatTitle, chatList);
        center.setPadding(new Insets(20));
        VBox.setVgrow(chatList, Priority.ALWAYS);

        // === Bottom: input ===
        TextField input = new TextField();
        input.setPromptText("Type a message...");
        input.setOnAction(e -> sendAndClear(input));

        Button sendBtn = new Button("Send");
        sendBtn.setOnAction(e -> sendAndClear(input));

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goMain.run());

        HBox bottom = new HBox(10, input, sendBtn, backBtn);
        bottom.setPadding(new Insets(20));
        bottom.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(input, Priority.ALWAYS);

        root.setLeft(left);
        root.setCenter(center);
        root.setBottom(bottom);
        root.setPickOnBounds(false);

        return root;
    }

    private void sendAndClear(TextField input) {
        String text = input.getText();
        input.clear();
        if (text != null) {
            onSend.accept(text.trim());
        }
    }
}
