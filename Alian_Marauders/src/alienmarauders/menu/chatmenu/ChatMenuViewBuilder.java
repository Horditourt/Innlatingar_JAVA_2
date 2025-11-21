// src/alienmarauders/menu/chatmenu/ChatMenuViewBuilder.java
package alienmarauders.menu.chatmenu;

import alienmarauders.Styles;
import alienmarauders.SwitchModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class ChatMenuViewBuilder {
    private final ChatMenuModel model;
    private final Runnable goMain;
    private final SwitchModel switchModel;

    private final BorderPane root = new BorderPane();

    public ChatMenuViewBuilder(ChatMenuModel model, SwitchModel switchModel, Runnable goMain) {
        this.model = model;
        this.switchModel = switchModel;
        this.goMain = goMain;
    }

    public Region build() {
        ListView<String> users = new ListView<>(model.users);
        users.setPrefWidth(180);

        TextArea messages = new TextArea();
        messages.setEditable(false);
        messages.setWrapText(true);

        TextField input = new TextField();
        input.setPromptText("Type a message...");
        Button send = new Button("Send");
        Button back = new Button("Main menu");

        send.setOnAction(e -> {
            String txt = input.getText();
            if (txt != null && !txt.isBlank()) {
                messages.appendText("You: " + txt + "\n");
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
}
