/* package alienmarauders;

import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.File;

public class ViewBuilder {
    private final StackPane root = new StackPane(); // background + content layer
    private final StackPane contentLayer = new StackPane();

    public ViewBuilder() {
        // Add content layer above background
        root.getChildren().add(contentLayer);
    }

    public Parent getRoot() { return root; }
    public StackPane getContentLayer() { return contentLayer; }

    /** Static background from disk: e.g., images/space.png */

    /* 
    public void setStaticBackground(String diskPath, boolean cover) {
        File f = new File(diskPath);
        Image img = new Image(f.toURI().toString(), true);

        BackgroundSize size = new BackgroundSize(
                100, 100, true, true,
                !cover,  // contain when not covering
                cover    // cover when covering
        );
        BackgroundImage bg = new BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                size
        );
        root.setBackground(new Background(bg));
    }
} */