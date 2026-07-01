package entity.enemy;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Arrow;

import java.awt.*;
import java.util.Random;

public class EMY_Goblin_Archer extends Entity {

    public static final String emyName = "Archer Goblin";

    public EMY_Goblin_Archer(GamePanel gp, int worldX, int worldY) {
        super(gp);
        this.worldX = worldX * gp.tileSize;
        this.worldY = worldY * gp.tileSize;
        worldXStart = this.worldX;
        worldYStart = this.worldY;

        entity_type = type_enemy;
        name = emyName;

        maxHealth = 12;
        health = maxHealth;
        defaultSpeed = 1;
        speed = defaultSpeed;
        animationSpeed = 16;
        attack = 1;

        hitbox = new Rectangle(8, 16, 32, 32);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        attackBox.width = 48;
        attackBox.height = 48;
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/enemy/goblin_up_1");
        up2 = setupImage("/enemy/goblin_up_2");
        down1 = setupImage("/enemy/goblin_down_1");
        down2 = setupImage("/enemy/goblin_down_2");
        left1 = setupImage("/enemy/goblin_left_1");
        left2 = setupImage("/enemy/goblin_left_2");
        right1 = setupImage("/enemy/goblin_right_1");
        right2 = setupImage("/enemy/goblin_right_2");
    }

    @Override
    public void update() {
        super.update();

        if (!canMove) {
            manageValues();
            return;
        }

        handleLookingAtPlayer();
        setAction();

        updateDirection();

        cycleSprites();
        manageValues();
    }

    private void handleLookingAtPlayer() {

        // Stop moving if in range to shoot at player
        if (lookingAtPlayer(gp.tileSize / 2)) {
            speed = 0;
            attack();
        }
        else {
            speed = defaultSpeed;
        }
    }

    @Override
    public void setAction() {

        // Player found
        if (onPath) {
            isOffPath(gp.player, 10);

            // Player still found, follow path
            if (onPath && playerWithinBounds()) {
                searchPath(getGoalCol(gp.player), getGoalRow(gp.player));
            }
        }
        else {
            // Move in random directions
            setDirection(60);

            // Look for player
            if (playerWithinBounds()) {
                isOnPath(gp.player, 8);
            }
        }
    }

    @Override
    protected void attack() {
        projectile = new PRJ_Arrow(gp);
        projectile.modifySpeed(4);
        projectile.modifyAttack(2);

        // Shoot one arrow every ~2 seconds
        int i = new Random().nextInt(120);
        if (i == 0 && !projectile.alive && actionLockCounter == 0) {
            projectile.set(worldX, worldY, direction, true, this);
            addProjectile(projectile);

            // Force 30 frame delay in between shots
            actionLockCounter = 30;
        }
    }

    @Override
    protected void manageValues() {
        if (actionLockCounter > 0) {
            actionLockCounter--;
        }

        super.manageValues();
    }
}