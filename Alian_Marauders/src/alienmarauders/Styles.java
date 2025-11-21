package alienmarauders;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;

public final class Styles {
    private Styles() {}

    /**
     * Returns a StringBinding you can bind directly to any Region's styleProperty().
     * Example:
     *   root.styleProperty().bind(Styles.backgroundStyle(app.backgroundName, this));
     */
    public static StringBinding backgroundStyle(StringProperty backgroundName, Object resourceAnchor) {
        return Bindings.createStringBinding(() -> {
            String file = switch (backgroundName.get()) {
                case "Planet" -> "/alienmarauders/images/planets.png";
                case "Nebula" -> "/alienmarauders/images/nebula.png";
                default       -> "/alienmarauders/images/space.png";
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
}
