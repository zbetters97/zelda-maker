package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class PRJ_Boomerang extends Projectile {

    public static final String prjName = "Boomerang Projectile";

    private boolean returning = false;

    public PRJ_Boomerang(GamePanel gp) {
        super(gp);

        entity_type = type_projectile;
        name = prjName;

        speed = 8;
        animationSpeed = 3;

        maxHealth = 30;
        health = maxHealth;
        alive = false;

        hitbox = new Rectangle(12, 12, 24, 24);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    @Override
    public void getImages() {
        up1 = down1 = left1 = right1 = setupImage("/projectiles/boomerang_down_1");
        up2 = down2 = left2 = right2 = setupImage("/projectiles/boomerang_down_2");
    }

    @Override
    public void update() {

        checkCollision();

        if (returning) {
            returnToUser();
        }
        else {
            move();
            health--;
        }

        cycleSprites();
        checkDeath();
    }

    @Override
    protected void checkCollision() {
        collisionOn = false;

        Entity enemy = getEnemy(this);
        if (enemy != null) {
            damageEnemy(enemy);
        }

        gp.cChecker.checkTile(this);
        gp.cChecker.checkEntity(this, gp.enemy);
        int npcIndex = gp.cChecker.checkEntity(this, gp.npc);

        if (health <= 0 || npcIndex != -1 || collisionOn) {
            returning = true;
        }
    }

    private void returnToUser() {
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                if (worldY + gp.tileSize / 2 <= gp.player.getWorldY()) {
                    worldY += 5;
                }
                else {
                    alive = false;
                }
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                if (worldY - gp.tileSize / 2 >= gp.player.getWorldY()) {
                    worldY -= 5;
                }
                else {
                    alive = false;
                }
            }
            case LEFT -> {
                if (worldX + gp.tileSize / 2 <= gp.player.getWorldX()) {
                    worldX += 5;
                }
                else {
                    alive = false;
                }
            }
            case RIGHT -> {
                if (worldX - gp.tileSize / 2 >= gp.player.getWorldX()) {
                    worldX -= 5;
                }
                else {
                    alive = false;
                }
            }
        }
    }

    @Override
    protected void cycleSprites() {

        spriteCounter++;

        if (spriteCounter > animationSpeed) {
            if (spriteNum == 1) {
                spriteNum = 2;
            }
            else if (spriteNum == 2) {
                spriteNum = 1;
            }

            spriteCounter = 0;
        }
    }

    @Override
    protected void checkDeath() {
        if (!alive) {
            resetValues();
        }
    }

    @Override
    public void resetValues() {
        returning = false;
        alive = false;
        collisionOn = false;
        health = maxHealth;
        user.setAction(Action.IDLE);
    }
}