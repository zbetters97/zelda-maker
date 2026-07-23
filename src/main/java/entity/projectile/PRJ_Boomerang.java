package entity.projectile;

import application.GamePanel;
import entity.Entity;
import entity.enemy.Enemy;

import java.awt.*;

public class PRJ_Boomerang extends Projectile {

    public static final String prjName = "Boomerang Projectile";

    private boolean returning = false;
    private Entity grabbedEntity;

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

        Entity loot = gp.cChecker.checkOverlapCollision(this, gp.collectables);
        if (loot != null) grabbedEntity = loot;
    }

    @Override
    protected boolean checkEnemyCollision() {

        Enemy enemy = gp.cChecker.checkOverlapCollision(this, gp.enemies);
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
        image = spriteNum == 1 ? up1 : up2;
    }
}