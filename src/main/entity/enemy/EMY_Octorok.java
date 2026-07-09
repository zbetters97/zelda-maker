package entity.enemy;

import application.GamePanel;
import entity.projectile.PRJ_Seed;

import java.awt.*;

import static application.GamePanel.Direction.*;

public class EMY_Octorok extends Enemy {

    public static final String emyName = "Octorok";

    public EMY_Octorok(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 15;

        maxHealth = 8;
        health = maxHealth;

        defaultSpeed = 1;
        speed = defaultSpeed;

        lockedOn = true;
        lockonDirection = DOWN;
        canSwim = true;
        needsWater = true;

        projectile = new PRJ_Seed(gp);

        hitbox = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        minTileDistanceToPlayer = 6;
        maxTileDistanceToPlayer = 10;
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

        if (isStuck()) {
            return;
        }

        setAction();

        if (onPath) {
            move();
        }

        cycleSprites();
        manageValues();
    }

    @Override
    protected void chasePlayer() {
        attack();
    }

    @Override
    protected void move() {

        setDirection();

        checkCollision();
        if (!collisionOn) {
            moveInDirection(lockonDirection);
        }
    }

    private void setDirection() {
        switch (direction) {
            case UP, DOWN -> {
                if (gp.player.getCenterX() >= getCenterX()) {
                    lockonDirection = RIGHT;
                }
                else if (gp.player.getCenterX() < getCenterX()) {
                    lockonDirection = LEFT;
                }
            }
            case LEFT, RIGHT -> {
                if (gp.player.getCenterY() >= getCenterY()) {
                    lockonDirection = DOWN;
                }
                else if (gp.player.getCenterY() < getCenterY()) {
                    lockonDirection = UP;
                }
            }
        }
    }

    @Override
    protected void attack() {
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