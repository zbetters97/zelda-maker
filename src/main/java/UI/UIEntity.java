package UI;

import application.GamePanel;

import java.awt.image.BufferedImage;

public class UIEntity {

    private final String name;
    private final BufferedImage sprite;

    public UIEntity(int num) {

        this.name = String.format("%03d", num);

        String filePath = "/tiles/" + name;
        sprite = GamePanel.utility.setupImage(filePath, 48, 48);
    }
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
