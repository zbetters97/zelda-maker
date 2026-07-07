package entity.enemy;

import application.GamePanel;
import application.GamePanel.Direction;
import entity.Entity;
import entity.projectile.PRJ_Seed;

import java.awt.*;

import static application.GamePanel.Direction.*;

public class EMY_Octorok extends Entity {

    public static final String emyName = "Octorok";

    public EMY_Octorok(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        entity_type = type_enemy;
        animationSpeed = 15;

        maxHealth = 8;
        health = maxHealth;

        defaultSpeed = 1;
        speed = defaultSpeed;

        lockedOn = true;
        lockonDirection = DOWN;
        canSwim = true;
        needsWater = true;

        hitbox = new Rectangle(8, 16, 32, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/enemy/octo_up_1");
        up2 = setupImage("/enemy/octo_up_2");
        down1 = setupImage("/enemy/octo_down_1");
        down2 = setupImage("/enemy/octo_down_2");
        left1 = setupImage("/enemy/octo_left_1");
        left2 = setupImage("/enemy/octo_left_2");
        right1 = setupImage("/enemy/octo_right_1");
        right2 = setupImage("/enemy/octo_right_2");
    }

    public void update() {
        super.update();

        if (!canMove) {
            manageValues();
            return;
        }

        setAction();

        if (onPath) {
            move(direction);
        }

        cycleSprites();
        manageValues();
    }

    @Override
    protected void setAction() {

        // Player found
        if (onPath) {
            isOffPath(gp.player, 10);
            attack();
        }
        // Look for player
        else if (playerWithinBounds()) {
            isOnPath(gp.player, 6);
        }
    }

    @Override
    protected void move(Direction direction) {
        switch (direction) {
            case UP, DOWN -> {
                if (gp.player.getCenterX() > getCenterX()) {
                    lockonDirection = RIGHT;
                    checkCollision();
                    if (!collisionOn) {
                        worldPoint.x += speed;
                    }
                }
                else if (gp.player.getCenterX() < getCenterX()) {
                    lockonDirection = LEFT;
                    checkCollision();
                    if (!collisionOn) {
                        worldPoint.x -= speed;
                    }
                }
            }
            case LEFT, RIGHT -> {
                if (gp.player.getCenterY() > getCenterY()) {
                    lockonDirection = DOWN;
                    checkCollision();
                    if (!collisionOn) {
                        worldPoint.y += speed;
                    }
                }
                else if (gp.player.getCenterY() < getCenterY()) {
                    lockonDirection = UP;
                    checkCollision();
                    if (!collisionOn) {
                        worldPoint.y -= speed;
                    }
                }
            }
        }

    }

    @Override
    protected void attack() {
        projectile = new PRJ_Seed(gp);
        useProjectile(projectile, 2);
    }

    @Override
    protected void manageValues() {
        if (actionLockCounter > 0) {
            actionLockCounter--;
        }

        super.manageValues();
    }
}