package alienmarauders.menu.chatmenu;

import alienmarauders.Controller;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

public class ChatMenuViewBuilder {
    private final VBox root = new VBox(10);

    public ChatMenuViewBuilder(Controller controller) {
        Label title = new Label("Chat Menu");
        Button back = new Button("Back");
        back.setOnAction(e -> controller.goMain());

        root.getChildren().addAll(title, back);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20; -fx-background-color: transparent;");
        root.setPickOnBounds(false);
    }

    public Region getRoot() { return root; }
}
