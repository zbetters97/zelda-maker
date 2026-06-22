package entity.projectile;

import application.GamePanel;
import entity.Entity;

public class Projectile extends Entity {

    protected boolean canPickup = false;

    public Projectile(GamePanel gp) {
        super(gp);
    }

    public void set(int worldX, int worldY, GamePanel.Direction direction, boolean alive, Entity user) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        this.alive = alive;
        this.user = user;
    }

    protected void move() {
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> worldY -= speed;
            case DOWN, DOWNLEFT, DOWNRIGHT -> worldY += speed;
            case LEFT -> worldX -= speed;
            case RIGHT -> worldX += speed;
        }
    }

    protected void resetValues() { }
}
