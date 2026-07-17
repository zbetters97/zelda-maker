package entity.projectile;

import application.GamePanel;
import entity.Entity;
import entity.collectable.Collectable;
import entity.enemy.Enemy;

import java.awt.*;

public class PRJ_Boomerang extends Projectile {

    public static final String prjName = "Boomerang Projectile";

    private boolean returning = false;
    private Collectable grabbedEntity;

    public PRJ_Boomerang(GamePanel gp) {
        super(gp, prjName);

        animationSpeed = 3;

        maxHealth = 30;
        health = maxHealth;

        defaultSpeed = 8;
        speed = defaultSpeed;

        knockbackPower = 2;
    }

    @Override
    public void getImages() {
        sprite = up1 = setupImage("/projectiles/boomerang_down_1");
        up2 = setupImage("/projectiles/boomerang_down_2");
    }

    @Override
    public void update() {
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
    public void checkCollision() {
        super.checkCollision();

        int colIndex = gp.cChecker.checkOverlapCollision(this, gp.col);
        if (colIndex != -1) {
            grabbedEntity = gp.col[colIndex];
        }
    }

    @Override
    protected boolean checkEnemyCollision() {

        Enemy enemy = overlapEnemy(this);
        if (enemy != null) {
            enemy.setStunned(true);
            enemy.takeDamage(this);
            collisionOn = true;
        }

        return true;
    }

    @Override
    protected void checkPlayerCollision() {

        // Return if player hit
        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        if (contactPlayer) {
            gp.player.takeDamage(this);
            collisionOn = true;
        }
    }

    @Override
    protected void returnToUser() {
        super.returnToUser();

        if (grabbedEntity != null) {
            pullEntity(grabbedEntity);
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
    protected void checkDeath() {
        if (!alive || (user != null && !user.isAvailable())) {
            resetValues();
        }
    }

    @Override
    public void resetValues() {
        super.resetValues();
        returning = false;
        user.resetCounters();
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