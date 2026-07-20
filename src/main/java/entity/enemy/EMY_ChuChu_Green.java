package entity.enemy;

import application.GamePanel;

public class EMY_ChuChu_Green extends Enemy {

    public static final String emyName = "ChuChu_Green";

    public EMY_ChuChu_Green(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        maxHealth = 6;
        health = maxHealth;

        defaultSpeed = 0;
        speed = defaultSpeed;

        knockbackPower = 0;

        minTileDistanceToPlayer = 3;
        maxTileDistanceToPlayer = 5;
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/enemy/chuchu_green_down_1");
        sprite = up2 = setupImage("/enemy/chuchu_green_down_2");
        up3 = setupImage("/enemy/chuchu_green_down_3");
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
        image = spriteNum == 1 ? up1 :
                spriteNum == 2 ? up2 : up3;
    }
}