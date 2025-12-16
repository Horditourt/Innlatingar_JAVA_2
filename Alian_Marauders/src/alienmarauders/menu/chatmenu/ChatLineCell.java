package alienmarauders.menu.chatmenu;

import alienmarauders.Styles;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * ListCell renderer for {@link ChatLine}.
 * <p>
 * Uses {@link TextFlow} so "self" vs "other" messages can be styled using {@link Styles}.
 */
public class ChatLineCell extends ListCell<ChatLine> {

    /**
     * Updates the cell content based on the {@link ChatLine}.
     *
     * @param item  the chat line item
     * @param empty whether the cell is empty
     */
    @Override
    protected void updateItem(ChatLine item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        TextFlow flow = new TextFlow();
        flow.setLineSpacing(2);

        if (item.getKind() == ChatLineKind.SYSTEM) {
            Text t = new Text(safe(item.getText()));
            t.setStyle(Styles.CHAT_MESSAGE_SYSTEM_STYLE);
            flow.getChildren().add(t);
        } else {
            String from = safe(item.getFrom());
            String text = safe(item.getText());

            Text name = new Text(from + ": ");
            name.setStyle(item.isSelf()
                    ? Styles.CHAT_MESSAGE_SELF_STYLE
                    : Styles.CHAT_MESSAGE_OTHER_STYLE);

            Text body = new Text(text);
            body.setStyle(item.isSelf()
                    ? Styles.CHAT_MESSAGE_SELF_STYLE
                    : Styles.CHAT_MESSAGE_OTHER_STYLE);

            flow.getChildren().addAll(name, body);
        }

        setGraphic(flow);
        setText(null);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
