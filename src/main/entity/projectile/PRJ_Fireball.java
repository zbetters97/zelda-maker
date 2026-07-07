package entity.projectile;

import application.GamePanel;

import java.awt.*;

public class PRJ_Fireball extends Projectile {

    public static final String prjName = "Fireball Projectile";

    public PRJ_Fireball(GamePanel gp) {
        super(gp, prjName);

        defaultSpeed = 7;
        speed = defaultSpeed;

        defaultAttack = 2;
        attack = defaultAttack;

        maxHealth = 60;
        health = maxHealth;
        alive = false;

        hitbox = new Rectangle(12, 12, 24, 24);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    @Override
    public void getImages() {
        up1 = setupImage("/projectiles/fireball_down_1", 35, 35);
        up2 = setupImage("/projectiles/fireball_down_2", 35, 35);
    }

    @Override
    public void update() {

        checkCollision();

        if (!canPickup) {
            moveInDirection(direction);
        }

        health--;
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

    @Override
    protected boolean canBeDeflected(boolean usingShield) {

        // Can only be deflected with a shield
        return usingShield;
    }

    @Override
    protected void checkDeath() {
        super.checkDeath();

        if (collisionOn) {
            resetValues();
        }
    }

    @Override
    public void resetValues() {
        alive = false;
        health = maxHealth;
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
