package UI;

import application.GamePanel;

import java.awt.image.BufferedImage;

public class UIEntity {

    private final String name;
    private final BufferedImage sprite;

    public UIEntity(String name, String path, GamePanel gp) {

        this.name = name;

        String filePath = "/ui/entities/" + path + "/" + name.toLowerCase();
        sprite = GamePanel.utility.setupImage(gp, filePath);
    }

    public String getName() {
        return name;
    }
    public BufferedImage getSprite() {
        return sprite;
    }
}
