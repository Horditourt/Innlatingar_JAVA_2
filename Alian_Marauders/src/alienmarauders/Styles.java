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
         *
         * @param backgroundName the name of the background ("Space", "Planet",
         *                       "Nebula")
         * @param resourceAnchor an object whose class loader can access the resources
         * @return a binding producing inline CSS for the background
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

        /**
         * Base style for buttons.
         *
         * @return inline CSS for a consistent button look
         */
        public static String buttonBaseStyle() {
                return """
                                -fx-text-fill: white;
                                -fx-font-size: 18px;
                                -fx-background-color: rgba(0,0,0,0.5);
                                -fx-padding: 10 25 10 25;
                                -fx-background-radius: 12;
                                -fx-border-radius: 12;
                                -fx-border-color: white;
                                -fx-border-width: 2;
                                -fx-alignment: center;
                                """;
        }

        /**
         * Style for the wave banner overlay.
         *
         * @return inline CSS for a wave banner label
         */
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

        /**
         * Style for the "Game Over" overlay label.
         *
         * @return inline CSS for the game over label
         */
        public static String gameOverStyle() {
                return """
                                -fx-text-fill: white;
                                -fx-font-size: 48px;
                                -fx-font-weight: bold;
                                -fx-background-color: rgba(0,0,0,0.6);
                                -fx-padding: 15 30 15 30;
                                -fx-background-radius: 12;
                                -fx-border-radius: 12;
                                -fx-border-color: white;
                                -fx-border-width: 2;
                                """;
        }

        /**
         * Returns inline CSS for the chat/menu root container.
         * <p>
         * This project stores UI styling in {@link Styles} rather than external CSS
         * files.
         * Use with {@code root.setStyle(Styles.menuRootStyle());}.
         *
         * @return inline CSS for a semi-transparent menu panel
         */
        public static String menuRootStyle() {
                return """
                                -fx-background-color: rgba(0,0,0,0.35);
                                -fx-background-radius: 12;
                                -fx-border-radius: 12;
                                -fx-border-color: rgba(255,255,255,0.35);
                                -fx-border-width: 1;
                                """;
        }

        /**
         * Returns inline CSS for menu screen titles (e.g., "Chat", "Online").
         *
         * @return inline CSS for a title label
         */
        public static String menuTitleStyle() {
                return """
                                -fx-text-fill: white;
                                -fx-font-size: 28px;
                                -fx-font-weight: bold;
                                """;
        }

        /** Style for the current user's entry in the user list. */
        public static final String CHAT_USER_SELF_CELL_STYLE = "-fx-text-fill: deepskyblue; -fx-font-weight: bold;";

        /** Style for other users in the user list. */
        public static final String CHAT_USER_OTHER_CELL_STYLE = "-fx-text-fill: red;";

        /** Style for chat messages sent by this client (Text in TextFlow). */
        public static final String CHAT_MESSAGE_SELF_STYLE = "-fx-fill: deepskyblue;";

        /** Style for chat messages sent by other users (Text in TextFlow). */
        public static final String CHAT_MESSAGE_OTHER_STYLE = "-fx-fill: red;";

        /** Style for system messages (join/leave, errors). */
        public static final String CHAT_MESSAGE_SYSTEM_STYLE = "-fx-fill: gray; -fx-font-style: italic;";
}
