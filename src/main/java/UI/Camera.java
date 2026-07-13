package UI;

import application.GamePanel;

import java.awt.*;

public class Camera {

    private final GamePanel gp;

    private final Point worldPoint = new Point();
    private final Point screenPoint = new Point();

    public Camera(GamePanel gp) {
        this.gp = gp;

        screenPoint.setLocation(
                gp.screenWidth / 2 - gp.tileSize / 2,
                gp.screenHeight / 2 - gp.tileSize / 2
        );
    }

    public void follow(Point worldPoint) {
        this.worldPoint.setLocation(worldPoint);
    }

    public void worldToScreen(Point worldPoint, Point screen) {

        int viewX = Math.clamp(this.worldPoint.x - screenPoint.x, 0,
                gp.worldWidth - gp.screenWidth);

        int viewY = Math.clamp(this.worldPoint.y - screenPoint.y, 0,
                gp.worldHeight - gp.screenHeight);

        screen.setLocation(
                worldPoint.x - viewX,
                worldPoint.y - viewY
        );
    }

    public boolean isVisible(Point worldPoint, int size) {

        int viewX = Math.clamp(this.worldPoint.x - screenPoint.x, 0,
                gp.worldWidth - gp.screenWidth);

        int viewY = Math.clamp(this.worldPoint.y - screenPoint.y, 0,
                gp.worldHeight - gp.screenHeight);

        return worldPoint.x + size >= viewX &&
                worldPoint.x - size <= viewX + gp.screenWidth &&
                worldPoint.y + size >= viewY &&
                worldPoint.y - size <= viewY + gp.screenHeight;
    }

    public Point getWorldPoint() {
        return worldPoint;
    }
}