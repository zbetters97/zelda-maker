package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PRJ_Claw extends Projectile {

    public static final String prjName = "Claw Projectile";

    private Entity grabbedEntity;
    private boolean latched = false;
    private BufferedImage grabUp1, grabDown1, grabLeft1, grabRight1, chainHor, chainVer;

    public PRJ_Claw(GamePanel gp, Entity user) {
        super(gp);

        entity_type = type_projectile;
        name = prjName;

        speed = 10;

        maxHealth = 30;
        health = maxHealth;
        alive = false;

        this.user = user;

        hitbox = new Rectangle(12, 16, 24, 24);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        getClawImages();
    }

    @Override
    public void getImages() {
        up1 = up2 = setupImage("/projectiles/hookshot_up_1");
        down1 = down2 = setupImage("/projectiles/hookshot_down_1");
        left1 = left2 = setupImage("/projectiles/hookshot_left_1");
        right1 = right2 = setupImage("/projectiles/hookshot_right_1");
    }
    private void getClawImages() {
        grabUp1 = setupImage("/projectiles/hookshot_grab_up_1");
        grabDown1 = setupImage("/projectiles/hookshot_grab_down_1");
        grabLeft1 = setupImage("/projectiles/hookshot_grab_left_1");
        grabRight1 = setupImage("/projectiles/hookshot_grab_right_1");

        chainHor = setupImage("/projectiles/chain_hor");
        chainVer = setupImage("/projectiles/chain_ver");
    }

    @Override
    public void update() {

        // Max length reached or Entity hit
        if (health <= 0) {
            if (latched) {
                moveUser();
            }
            else {
                returnToUser();
            }
        }
        // No object hit
        else {
            move();
            health--;

            collisionOn = false;

            checkCollision();
            if (collisionOn) {
                health = 0;
            }
        }

        checkDeath();
    }

    @Override
    protected void checkCollision() {
        checkGrabbableCollision();
        checkObstacleCollision();
        checkLatchableCollision();
    }
    private void checkGrabbableCollision() {

        Entity target = overlapEnemy(this);

        if (target != null) {
            grabbedEntity = target;
            collisionOn = true;
        }
    }
    private void checkLatchableCollision() {
        int object = gp.cChecker.checkOverlapCollision(this, gp.obj);

        if (object != -1) {
            latched = true;
            grabbedEntity = gp.obj[gp.currentMap][object];
            health = 0;
        }
    }
    private void checkObstacleCollision() {
        gp.cChecker.checkTile(this);
        gp.cChecker.checkOverlapCollision(this, gp.npc);
    }

    private void moveUser() {

        if (user.getCollision()) {
            user.setElevated(false);
            alive = false;
            return;
        }

        user.setElevated(true);

        int stopX = grabbedEntity.getWorldX() + gp.tileSize;
        int stopY = grabbedEntity.getWorldY() + gp.tileSize;

        int userWorldX = user.getWorldX();
        int userWorldY = user.getWorldY();

        // Move user towards latched
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                if (userWorldY > stopY) {
                    user.setWorldY(userWorldY - 5);
                }
                else {
                    alive = false;
                }
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                if (userWorldY < stopY) {
                    user.setWorldY(userWorldY + 5);
                }
                else {
                    alive = false;
                }
            }
            case LEFT -> {
                if (userWorldX > stopX) {
                    user.setWorldX(userWorldX - 5);
                }
                else {
                    alive = false;
                }
            }
            case RIGHT -> {
                if (userWorldX < stopX) {
                    user.setWorldX(userWorldX + 5);
                }
                else {
                    alive = false;
                }
            }
        }
    }

    private void returnToUser() {

        // Move backwards to user
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

        if (grabbedEntity != null) {
            pullEntity();
        }
    }
    private void pullEntity() {

        grabbedEntity.resetValues();
        grabbedEntity.setDirection(getOppositeDirection(direction));
        grabbedEntity.setElevated(true);

        // Offset X/Y so entity isn't on top of player
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> grabbedEntity.setWorldY(worldY - gp.tileSize / 2);
            case DOWN, DOWNLEFT, DOWNRIGHT -> grabbedEntity.setWorldY(worldY + gp.tileSize / 2);
            case LEFT -> grabbedEntity.setWorldX(worldX - gp.tileSize / 2);
            case RIGHT -> grabbedEntity.setWorldX(worldX + gp.tileSize / 2);
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
        alive = false;
        collisionOn = false;
        latched = false;
        health = maxHealth;
        user.setElevated(false);
        user.setAction(Action.IDLE);

        if (grabbedEntity != null) {
            grabbedEntity.setElevated(false);
            grabbedEntity = null;
        }
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

    @Override
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
