package entity.enemy;

import application.GamePanel;

public class EMY_ChuChu_Red extends Enemy {

    public static final String emyName = "Red Chu Chu";

    public EMY_ChuChu_Red(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        maxHealth = 10;
        health = maxHealth;

        defaultSpeed = 0;
        speed = defaultSpeed;

        defaultAttack = 4;
        attack = defaultAttack;
        knockbackPower = 0;

        minTileDistanceToPlayer = 4;
        maxTileDistanceToPlayer = 6;
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/enemy/chuchu_red_down_1");
        sprite = up2 = setupImage("/enemy/chuchu_red_down_2");
        up3 = setupImage("/enemy/chuchu_red_down_3");
    }

    @Override
    protected void searchForPlayer() {

        if (ai.playerWithinRange()) {
            ai.isOnPath(gp.player, minTileDistanceToPlayer);
        }
        else {
            onPath = false;
            spriteNum = 1;
            spriteCounter = 0;
        }
    }

    @Override
    protected void cycleSprites() {

        if (animationSpeed < ++spriteCounter) {

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