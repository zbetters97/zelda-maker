package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class PRJ_Boomerang extends Projectile {

    public static final String prjName = "Boomerang Projectile";

    private boolean returning = false;

    public PRJ_Boomerang(GamePanel gp) {
        super(gp, prjName);

        animationSpeed = 3;

        defaultSpeed = 8;
        speed = defaultSpeed;

        defaultAttack = 1;
        attack = defaultAttack;

        maxHealth = 30;
        health = maxHealth;
    }

    @Override
    public void getImages() {
        up1 = setupImage("/projectiles/boomerang_down_1");
        up2 = setupImage("/projectiles/boomerang_down_2");
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
    protected void checkPlayerCollision() {

        // Return if player hit
        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        if (contactPlayer) {
            damagePlayer();
            collisionOn = true;
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
    protected void deflect(Entity target) {
        alive = true;
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
        if (!alive || (user != null && !user.isAvailable())) {
            resetValues();
        }
    }

    @Override
    public void resetValues() {
        super.resetValues();
        returning = false;
        user.setAction(Action.IDLE);
    }

    @Override
    protected void getSpriteImage() {
        if (spriteNum == 1) {
            image = up1;
        }
        else {
            image = up2;
        }
    }
}