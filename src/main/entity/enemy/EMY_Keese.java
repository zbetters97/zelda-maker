package entity.enemy;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class EMY_Keese extends Entity {

    public static final String emyName = "Keese";

    /**
     * CONSTRUCTOR
     * @param gp GamePanel
     * @param worldX Starting coordinate
     * @param worldY Starting coordinate
     */
    public EMY_Keese(GamePanel gp, int worldX, int worldY) {
        super(gp);

        // Coordinates
        this.worldX = worldX * gp.tileSize;
        this.worldY = worldY * gp.tileSize;
        worldXStart = this.worldX;
        worldYStart = this.worldY;

        // General attributes
        entity_type = type_enemy;
        name = emyName;
       // defaultSpeed = 2;
        defaultSpeed = 0;
        speed = defaultSpeed;
        animationSpeed = 5;
        health = 4;
        attack = 1;

        // Collision attributes
        hitbox = new Rectangle(2, 18, 44, 30);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    /**
     * GET IMAGES
     * Fetches sprites
     * Called by constructor in Entity parent class
     */
    protected void getImages() {
        up1 = down1 = left1 = right1 = setupImage("/enemy/keese_down_1");
        up2 = down2 = left2 = right2 = setupImage("/enemy/keese_down_2");
    }

    public void update() {
        // No action if in knockback state
        if (knockback) {
            handleKnockback();
        }
        else {
            setAction();
            updateDirection();
        }

        manageValues();
    }

    /**
     * SET ACTION
     * Updates the actions the entity will take
     * Called by update() in Entity parent class
     */
    public void setAction() {
        setDirection(25);
    }
}