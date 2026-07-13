package UI;

import application.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Cursor {

    private final GamePanel gp;

    private final Point worldPoint = new Point();

    private final BufferedImage cursor, cursor_select;

    public Cursor(GamePanel gp) {
        this.gp = gp;

        cursor = GamePanel.utility.setupImage("/ui/ui_cursor", gp.tileSize, gp.tileSize);
        cursor_select = GamePanel.utility.setupImage("/ui/ui_cursor_select", gp.tileSize, gp.tileSize);
    }

    public void moveUp() {
        worldPoint.y = Math.max(0, worldPoint.y - gp.tileSize);
    }
    public void moveDown() {
        worldPoint.y = Math.min(gp.worldHeight - gp.tileSize, worldPoint.y + gp.tileSize);
    }
    public void moveLeft() {
        worldPoint.x = Math.max(0, worldPoint.x - gp.tileSize);
    }
    public void moveRight() {
        worldPoint.x = Math.min(gp.worldWidth - gp.tileSize, worldPoint.x + gp.tileSize);
    }

    public BufferedImage getCursor() {
        return cursor;
    }
    public BufferedImage getSelect() {
        return cursor_select;
    }

    public Point getWorldPoint() {
        return worldPoint;
    }

    public void setWorldPoint(Point worldPoint) {
        int newX = Math.round((float) worldPoint.x / gp.tileSize) * gp.tileSize;
        int newY = Math.round((float) worldPoint.y / gp.tileSize) * gp.tileSize;

        this.worldPoint.setLocation(newX, newY);
    }

    public int getWorldX() {
        return worldPoint.x;
    }
    public int getWorldY() {
        return worldPoint.y;
    }
}
