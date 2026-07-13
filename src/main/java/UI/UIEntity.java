package UI;

import application.GamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class UIEntity {

    private final String name;
    private BufferedImage sprite;

    public UIEntity(String name, String path, GamePanel gp) {
        this.name = name;
        setupImage("/ui/entities/" + path + "/" + name.toLowerCase(), gp);
    }

    /**
     * SETUP IMAGE
     * @param imagePath Path to image file
     */
    private void setupImage(String imagePath, GamePanel gp) {
        try {
            sprite = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath + ".png")
            ));

            sprite = GamePanel.utility.scaleImage(sprite, gp.tileSize, gp.tileSize);
        }
        catch (IOException e) {
            System.out.println("Error loading image:" + e.getMessage());
        }
    }

    public String getName() {
        return name;
    }
    public BufferedImage getSpirte() {
        return sprite;
    }
}
