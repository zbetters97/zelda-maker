package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PRJ_Claw extends Projectile {

    public static final String prjName = "Claw Projectile";

    private Entity grabbedEntity;
    private BufferedImage grabUp1, grabDown1, grabLeft1, grabRight1, chainHor, chainVer;

    public PRJ_Claw(GamePanel gp) {
        super(gp);

        type = type_projectile;
        name = prjName;

        speed = 10;

        maxHealth = 30;
        health = maxHealth;
        alive = false;

        hitbox = new Rectangle(12, 16, 24, 24);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        getClawImages();
    }

    public void getImages() {
        up1 = up2 = setupImage("/projectiles/hookshot_up_1");
        down1 = down2 = setupImage("/projectiles/hookshot_down_1");
        left1 = left2 = setupImage("/projectiles/hookshot_left_1");
        right1 = right2 = setupImage("/projectiles/hookshot_right_1");
    }
    public void getClawImages() {
        grabUp1 = setupImage("/projectiles/hookshot_grab_up_1");
        grabDown1 = setupImage("/projectiles/hookshot_grab_down_1");
        grabLeft1 = setupImage("/projectiles/hookshot_grab_left_1");
        grabRight1 = setupImage("/projectiles/hookshot_grab_right_1");

        chainHor = setupImage("/projectiles/chain_hor");
        chainVer = setupImage("/projectiles/chain_ver");
    }

    public void update() {

        // Max length reached
        if (health <= 0) {
            returnToUser();
        }
        // No object hit
        else {
            move();
            health--;

            collisionOn = false;

            checkGrabbableCollision();
            checkObstacleCollision();
            if (collisionOn) {
                health = 0;
            }
        }

        checkDeath();
    }

    protected void checkGrabbableCollision() {
        Entity enemy = getEnemy(this);
        if (enemy != null) {
            grabbedEntity = enemy;
            collisionOn = true;
        }
    }

    protected void checkObstacleCollision() {
        gp.cChecker.checkTile(this);
        gp.cChecker.checkEntity(this, gp.npc);
    }

    private void returnToUser() {

        // Move backwards to to user
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                if (worldY + gp.tileSize / 2 <= gp.player.worldY) {
                    worldY += 5;
                }
                else {
                    alive = false;
                }
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                if (worldY - gp.tileSize / 2 >= gp.player.worldY) {
                    worldY -= 5;
                }
                else {
                    alive = false;
                }
            }
            case LEFT -> {
                if (worldX + gp.tileSize / 2 <= gp.player.worldX) {
                    worldX += 5;
                }
                else {
                    alive = false;
                }
            }
            case RIGHT -> {
                if (worldX - gp.tileSize / 2 >= gp.player.worldX) {
                    worldX -= 5;
                }
                else {
                    alive = false;
                }
            }
        }

        if (grabbedEntity != null) {
            pullEntity();
        }
    }

    private void pullEntity() {
        grabbedEntity.worldX = worldX;
        grabbedEntity.worldY = worldY;

        // Offset X/Y so entity isn't on top of player
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> grabbedEntity.worldY -= gp.tileSize / 2;
            case DOWN, DOWNLEFT, DOWNRIGHT -> grabbedEntity.worldY += gp.tileSize / 2;
            case LEFT -> grabbedEntity.worldX -= gp.tileSize / 2;
            case RIGHT -> grabbedEntity.worldX += gp.tileSize / 2;
        }
    }

    protected void checkDeath() {
        if (!alive) {
            resetValues();
        }
    }

    protected void resetValues() {
        alive = false;
        collisionOn = false;
        health = maxHealth;
        grabbedEntity = null;
        user.action = Action.IDLE;
    }

    @Override
    public void draw(Graphics2D g2) {

        drawChain(g2);
        super.draw(g2);
    }

    private void drawChain(Graphics2D g2) {

        int startX = user.getScreenX() + gp.tileSize / 2;
        int startY = user.getScreenY() + gp.tileSize / 2;

        int endX = getScreenX() + gp.tileSize / 2;
        int endY = getScreenY() + gp.tileSize / 2;

        switch (direction) {
            case UP, UPLEFT, UPRIGHT, DOWN, DOWNLEFT, DOWNRIGHT -> {
                int step = chainVer.getHeight();
                for (int y = startY; Math.abs(y - endY) > step;) {
                    y += (endY > y) ? step : -step;
                    g2.drawImage(chainVer, startX - chainVer.getWidth() / 2, y - chainVer.getHeight() / 2, null);
                }
            }
            case LEFT, RIGHT -> {
                int step = chainHor.getWidth();
                for (int x = startX; Math.abs(x - endX) > step;) {
                    x += (endX > x) ? step : -step;
                    g2.drawImage(chainHor, x - chainHor.getWidth() / 2, startY - chainHor.getHeight() / 2, null);
                }
            }
        }
    }

    protected void getSpriteImage() {
        if (grabbedEntity != null) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> grabUp1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> grabDown1;
                case LEFT -> grabLeft1;
                case RIGHT -> grabRight1;
            };
        }
        else {
            super.getSpriteImage();
        }
    }
}
