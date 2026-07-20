package UI;

import application.GamePanel;

import java.awt.image.BufferedImage;

public record UITile(BufferedImage sprite) {

    public UITile(int num) {

        String filePath = "/tiles/" + String.format("%03d", num);
        this(GamePanel.utility.setupImage(filePath, 48, 48));
    }
}
