package alienmarauders;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;

public final class Styles {
    private Styles() {
    }

    /**
     * Returns a StringBinding you can bind directly to any Region's
     * styleProperty().
     * Example:
     * root.styleProperty().bind(Styles.backgroundStyle(app.backgroundName, this));
     */
    public static StringBinding backgroundStyle(StringProperty backgroundName, Object resourceAnchor) {
        return Bindings.createStringBinding(() -> {
            String file = switch (backgroundName.get()) {
                case "Planet" -> "/alienmarauders/images/planets.png";
                case "Nebula" -> "/alienmarauders/images/nebula.png";
                default -> "/alienmarauders/images/space.png";
            };
            String url = resourceAnchor.getClass().getResource(file).toExternalForm();
            return """
                    -fx-background-color: black;
                    -fx-background-image: url('%s');
                    -fx-background-size: cover;
                    -fx-background-repeat: no-repeat;
                    -fx-background-position: center center;
                    """.formatted(url);
        }, backgroundName);
    }

    public static String gameOverStyle() {
        return """
                -fx-text-fill: white;
                -fx-font-size: 48px;
                -fx-background-color: rgba(0,0,0,0.6);
                -fx-padding: 30 50 30 50;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: white;
                -fx-border-width: 2;
                -fx-alignment: center;
                """;
    }

    public static String waveBannerStyle() {
        return """
                -fx-text-fill: white;
                -fx-font-size: 32px;
                -fx-background-color: rgba(0,0,0,0.6);
                -fx-padding: 10 25 10 25;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-border-color: white;
                -fx-border-width: 2;
                """;
    }

    /** Style for the current user's entry in the user list. */
    public static final String CHAT_USER_SELF_CELL_STYLE =
            "-fx-text-fill: deepskyblue; -fx-font-weight: bold;";

    /** Style for other users in the user list. */
    public static final String CHAT_USER_OTHER_CELL_STYLE =
            "-fx-text-fill: red;";

    /** Style for chat messages sent by this client (Text in TextFlow). */
    public static final String CHAT_MESSAGE_SELF_STYLE =
            "-fx-fill: deepskyblue;";

    /** Style for chat messages sent by other users (Text in TextFlow). */
    public static final String CHAT_MESSAGE_OTHER_STYLE =
            "-fx-fill: red;";

    /** Style for system messages (join/leave, errors). */
    public static final String CHAT_MESSAGE_SYSTEM_STYLE =
            "-fx-fill: gray; -fx-font-style: italic;";

}
