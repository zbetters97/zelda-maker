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
        super(gp, worldX, worldY, emyName);

        entity_type = type_enemy;
        animationSpeed = 5;

        maxHealth = 4;
        health = maxHealth;

        defaultSpeed = 2;
        speed = defaultSpeed;
        attack = 1;

        // Collision attributes
        hitbox = new Rectangle(2, 18, 44, 30);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    /**
     * GET IMAGES
     * Fetches sprites
     * Called by constructor in Entity parent class
     */
    @Override
    protected void getImages() {
        up1 = down1 = left1 = right1 = setupImage("/enemy/keese_down_1");
        up2 = down2 = left2 = right2 = setupImage("/enemy/keese_down_2");
    }

    @Override
    public void update() {
        super.update();

        if (!canMove) {
            manageValues();
            return;
        }

        setAction();
        updateDirection();

        manageValues();
    }

    /**
     * SET ACTION
     * Updates the actions the entity will take
     * Called by update() in Entity parent class
     */
    @Override
    protected void setAction() {
        setDirection(25);
    }
}