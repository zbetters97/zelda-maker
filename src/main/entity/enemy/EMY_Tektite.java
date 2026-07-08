package entity.enemy;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class EMY_Tektite extends Entity {

    public static final String emyName = "Tektite";

    public EMY_Tektite(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 8;

        maxHealth = 6;
        health = maxHealth;

        defaultSpeed = 2;
        speed = defaultSpeed;

        canSwim = true;
        needsWater = true;

        hitbox = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    @Override
    protected void getImages() {
        up1 = down1 = left1 = right1 = setupImage("/enemy/tektite_down_1");
        up2 = down2 = left2 = right2 = setupImage("/enemy/tektite_down_2");
    }

    @Override
    protected void setAction() {

        if (speed == 0) {
            pauseMovement();
        }
        else {
            moveATile();
        }
    }

    private void pauseMovement() {

        // Wait 2 seconds before changing direction
        actionLockCounter++;
        if (actionLockCounter >= 120) {
            actionLockCounter = 0;
            speed = defaultSpeed;
            setDirection(0);
        }
    }

    private void moveATile() {

        // Move 1 tile at a time
        actionLockCounter++;
        if (actionLockCounter >= 24) {
            actionLockCounter = 0;
            speed = 0;
        }
    }
}