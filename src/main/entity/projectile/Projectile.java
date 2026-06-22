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

    public void addProjectile(Projectile projectile) {

        for (int i = 0; i < gp.projectile[0].length; i++) {
            if (gp.projectile[gp.currentMap][i] == null) {
                gp.projectile[gp.currentMap][i] = projectile;
                break;
            }
        }
    }
}
