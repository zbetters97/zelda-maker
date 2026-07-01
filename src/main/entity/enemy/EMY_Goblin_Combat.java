package entity.enemy;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class EMY_Goblin_Combat extends Entity {

    public static final String emyName = "Combat Goblin";

    public EMY_Goblin_Combat(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        entity_type = type_enemy;
        animationSpeed = 15;

        maxHealth = 12;
        health = maxHealth;

        defaultSpeed = 1;
        speed = defaultSpeed;
        attack = 4;

        hitbox = new Rectangle(8, 16, 32, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        swingSpeed1 = 15;
        swingSpeed2 = 45;

        attackBox.width = 48;
        attackBox.height = 48;

        getAttackImages();
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
    private void getAttackImages() {
        attackUp1 = setupImage("/enemy/goblin_attack_up_1", gp.tileSize, gp.tileSize * 2);
        attackUp2 = setupImage("/enemy/goblin_attack_up_2", gp.tileSize, gp.tileSize * 2);
        attackDown1 = setupImage("/enemy/goblin_attack_down_1", gp.tileSize, gp.tileSize * 2);
        attackDown2 = setupImage("/enemy/goblin_attack_down_2", gp.tileSize, gp.tileSize * 2);
        attackLeft1 = setupImage("/enemy/goblin_attack_left_1", gp.tileSize * 2, gp.tileSize);
        attackLeft2 = setupImage("/enemy/goblin_attack_left_2", gp.tileSize * 2, gp.tileSize);
        attackRight1 = setupImage("/enemy/goblin_attack_right_1", gp.tileSize * 2, gp.tileSize);
        attackRight2 = setupImage("/enemy/goblin_attack_right_2", gp.tileSize * 2, gp.tileSize);
    }

    @Override
    public void update() {
        super.update();

        if (!canMove) {
            manageValues();
            return;
        }

        if (action == Action.ATTACKING) {
            attacking();
        }

        setAction();

        updateDirection();

        cycleSprites();
        manageValues();
    }

    @Override
    public void setAction() {

        // Player found
        if (onPath) {
            isOffPath(gp.player, 8);

            // Player still found
            if (onPath && playerWithinBounds()) {

                // Follow path
                searchPath(getGoalCol(gp.player), getGoalRow(gp.player));

                // Decide to attack
                setAttacking(60, gp.tileSize * 3, gp.tileSize);

                // Stop to attack
                if (action == Action.ATTACKING) {
                    speed = 0;
                }
                else {
                    speed = defaultSpeed;
                }
            }
        }
        else {
            // Move in random directions
            setDirection(60);

            // Look for player
            if (playerWithinBounds()) {
                isOnPath(gp.player, 5);
            }
        }
    }
}