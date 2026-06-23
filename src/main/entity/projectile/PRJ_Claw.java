package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class PRJ_Claw extends Projectile {

    public static final String prjName = "Claw Projectile";

    private Entity grabbedEntity;

    public PRJ_Claw(GamePanel gp) {
        super(gp);

        type = type_projectile;
        name = prjName;

        speed = 10;

        maxHealth = 30;
        health = maxHealth;
        alive = false;

        hitbox = new Rectangle(12, 16, 24, 24);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        getGrabImages();
    }

    public void getImages() {
        up1 = up2 = setupImage("/projectiles/hookshot_up_1");
        down1 = down2 = setupImage("/projectiles/hookshot_down_1");
        left1 = left2 = setupImage("/projectiles/hookshot_left_1");
        right1 = right2 = setupImage("/projectiles/hookshot_right_1");
    }
    public void getGrabImages() {
        grabUp1 = setupImage("/projectiles/hookshot_grab_up_1");
        grabDown1 = setupImage("/projectiles/hookshot_grab_down_1");
        grabLeft1 = setupImage("/projectiles/hookshot_grab_left_1");
        grabRight1 = setupImage("/projectiles/hookshot_grab_right_1");
    }

    public void update() {

        // Max length reached
        if (health <= 0) {
            returnToUser();
        }
        // No object hit
        else {
            move();
            health--;

            checkCollision();
            if (collisionOn) {
                health = 0;
            }
        }

        checkDeath();
    }

    protected void checkCollision() {
        collisionOn = false;

        Entity enemy = getEnemy(this);
        if (enemy != null) {
            grabbedEntity = enemy;
            collisionOn = true;
        }
    }

    private void returnToUser() {

        // Move backwards to to user
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                if (worldY + gp.tileSize / 2 <= gp.player.worldY) {
                    worldY += 5;
                }
                else {
                    alive = false;
                }
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                if (worldY - gp.tileSize / 2 >= gp.player.worldY) {
                    worldY -= 5;
                }
                else {
                    alive = false;
                }
            }
            case LEFT -> {
                if (worldX + gp.tileSize / 2 <= gp.player.worldX) {
                    worldX += 5;
                }
                else {
                    alive = false;
                }
            }
            case RIGHT -> {
                if (worldX - gp.tileSize / 2 >= gp.player.worldX) {
                    worldX -= 5;
                }
                else {
                    alive = false;
                }
            }
        }

        if (grabbedEntity != null) {
            pullEntity();
        }
    }

    private void pullEntity() {
        grabbedEntity.worldX = worldX;
        grabbedEntity.worldY = worldY;

        // Offset X/Y so entity isn't on top of player
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> grabbedEntity.worldY -= gp.tileSize / 2;
            case DOWN, DOWNLEFT, DOWNRIGHT -> grabbedEntity.worldY += gp.tileSize / 2;
            case LEFT -> grabbedEntity.worldX -= gp.tileSize / 2;
            case RIGHT -> grabbedEntity.worldX += gp.tileSize / 2;
        }
    }

    protected void checkDeath() {
        if (!alive) {
            resetValues();
        }
    }

    protected void resetValues() {
        alive = false;
        collisionOn = false;
        health = maxHealth;
        user.action = Action.IDLE;
    }
}
