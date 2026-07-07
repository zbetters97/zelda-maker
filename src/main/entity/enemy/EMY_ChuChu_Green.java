package entity.enemy;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class EMY_ChuChu_Green extends Entity {

    public static final String emyName = "Green Chu Chu";

    public EMY_ChuChu_Green(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        entity_type = type_enemy;
        animationSpeed = 12;

        maxHealth = 6;
        health = maxHealth;

        defaultSpeed = 0;
        speed = defaultSpeed;
        attack = 1;

        hitbox = new Rectangle(2, 18, 44, 30);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    @Override
    protected void getImages() {
        up1 = down1 = left1 = right1 = setupImage("/enemy/chuchu_green_down_1");
        up2 = down2 = left2 = right2 = setupImage("/enemy/chuchu_green_down_2");
        up3 = down3 = left3 = right3 = setupImage("/enemy/chuchu_green_down_3");
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

    @Override
    protected void setAction() {

        isOffPath(gp.player, 5);

        if (onPath && playerWithinBounds()) {
            searchPath(getGoalCol(gp.player), getGoalRow(gp.player));
        }
        else {
            if (playerWithinBounds()) {
                isOnPath(gp.player, 4);
            }
            else {
                onPath = false;
                spriteNum = 1;
                spriteCounter = 0;
            }
        }
    }

    @Override
    protected void cycleSprites() {

        spriteCounter++;
        if (animationSpeed < spriteCounter) {

            if (onPath) {
                speed = 1;

                if (spriteNum == 1 || spriteNum == 2) {
                    spriteNum = 3;
                }
                else {
                    spriteNum = 2;
                }
            }
            else {
                spriteNum = 1;
                speed = defaultSpeed;
            }

            spriteCounter = 0;
        }
    }

    @Override
    protected void getSpriteImage() {
        if (spriteNum == 1) {
            image = up1;
        }
        else if (spriteNum == 2) {
            image = up2;
        }
        else {
            image = up3;
        }
    }
}