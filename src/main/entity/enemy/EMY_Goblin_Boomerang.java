package entity.enemy;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Boomerang;

import java.awt.*;

public class EMY_Goblin_Boomerang extends Entity {

    public static final String emyName = "Boomerang Goblin";

    public EMY_Goblin_Boomerang(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        entity_type = type_enemy;
        animationSpeed = 15;

        maxHealth = 8;
        health = maxHealth;

        defaultSpeed = 1;
        speed = defaultSpeed;
        attack = 1;

        hitbox = new Rectangle(8, 16, 32, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        attackBox.width = 48;
        attackBox.height = 48;

        projectile = new PRJ_Boomerang(gp);
        projectile.modifyAttack(1);
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
    protected void setAction() {

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
        useProjectile(projectile, 2);
    }

    @Override
    protected void manageValues() {

        // Force 30 frame delay between throws
        if (actionLockCounter > 0 && !projectile.alive) {
            actionLockCounter--;
        }

        super.manageValues();
    }
}