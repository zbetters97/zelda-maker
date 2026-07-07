package entity.projectile;

import application.GamePanel;

import java.awt.*;

public class PRJ_Boomerang extends Projectile {

    public static final String prjName = "Boomerang Projectile";

    private boolean returning = false;

    public PRJ_Boomerang(GamePanel gp) {
        super(gp, prjName);

        speed = 8;
        animationSpeed = 3;

        maxHealth = 30;
        health = maxHealth;
        alive = false;

        hitbox = new Rectangle(12, 12, 24, 24);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
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
        super.update();

        if (returning) {
            returnToUser();
        }
        else {
            moveInDirection(direction);
            health--;

            checkCollision();
            if (health <= 0 || collisionOn) {
                returning = true;
            }
        }

        cycleSprites();
        checkDeath();
    }

    @Override
    protected void checkCollision() {

        collisionOn = false;

        gp.cChecker.checkTile(this);
        gp.cChecker.checkMovementCollision(this, gp.npc);
        gp.cChecker.checkMovementCollision(this, gp.obj);
        checkObjectCollision();

        if (user == gp.player) {
            checkEnemyCollision();
        }
        else {
            checkPlayerCollision();
        }
    }

    private void returnToUser() {

        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                if (getCenterY() < user.getCenterY()) {
                    worldPoint.y += 5;
                }
                else {
                    alive = false;
                }
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                if (getCenterY() > user.getCenterY()) {
                    worldPoint.y -= 5;
                }
                else {
                    alive = false;
                }
            }
            case LEFT -> {
                if (getCenterX() < user.getCenterX()) {
                    worldPoint.x += 5;
                }
                else {
                    alive = false;
                }
            }
            case RIGHT -> {
                if (getCenterX() > user.getCenterX()) {
                    worldPoint.x -= 5;
                }
                else {
                    alive = false;
                }
            }
        }
    }

    @Override
    protected boolean canBeDeflected(boolean usingShield) {

        // Can only be deflected with shield
        return usingShield;
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