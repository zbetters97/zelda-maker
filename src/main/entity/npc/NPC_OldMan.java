package entity.npc;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class NPC_OldMan extends Entity {

    public static final String npcName = "Old Man";

    /**
     * CONSTRUCTOR
     * @param gp GamePanel
     * @param worldX Starting coordinate
     * @param worldY Starting coordinate
     */
    public NPC_OldMan(GamePanel gp, int worldX, int worldY) {
        super(gp);

        // Coordinates
        this.worldX = worldX * gp.tileSize;
        this.worldY = worldY * gp.tileSize;
        worldXStart = this.worldX;
        worldYStart = this.worldY;

        // General attributes
        entity_type = type_npc;
        name = npcName;
        speed = 1;
        defaultSpeed = speed;
        animationSpeed = 15;

        // Collision attributes
        hitbox = new Rectangle(8, 16, 32, 32);
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
    @Override
    protected void getImages() {
        up1 = setupImage("/npc/oldman_up_1");
        up2 = setupImage("/npc/oldman_up_2");
        down1 = setupImage("/npc/oldman_down_1");
        down2 = setupImage("/npc/oldman_down_2");
        left1 = setupImage("/npc/oldman_left_1");
        left2 = setupImage("/npc/oldman_left_2");
        right1 = setupImage("/npc/oldman_right_1");
        right2 = setupImage("/npc/oldman_right_2");
    }

    /**
     * SET ACTION
     * Updates the actions the entity will take
     * Called by update() in Entity parent class
     */
    @Override
    protected void setAction() {
        setDirection(60);
    }
}