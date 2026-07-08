package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class PRJ_Arrow extends Projectile {

    public static final String prjName = "Arrow Projectile";

    public PRJ_Arrow(GamePanel gp) {
        super(gp, prjName);

        defaultSpeed = 5;
        speed = defaultSpeed;

        defaultAttack = 1;
        attack = defaultAttack;

        maxHealth = 120;
        health = maxHealth;

        hitbox = new Rectangle(4, 8, 24, 24);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    @Override
    public void getImages() {
        up1 = setupImage("/projectiles/arrow_up_1", 35, 35);
        down1 = setupImage("/projectiles/arrow_down_1", 35, 35);
        left1 = setupImage("/projectiles/arrow_left_1", 35, 35);
        right1 = setupImage("/projectiles/arrow_right_1", 35, 35);
    }

    @Override
    public void update() {

        checkCollision();

        if (!canPickup) {
            moveInDirection(direction);
        }

        if (collisionOn && alive) {
            canPickup = true;
            speed = 0;
            attack = 0;
        }

        health--;
        checkDeath();
    }

    @Override
    protected boolean checkEnemyCollision() {

        boolean enemyHit = super.checkEnemyCollision();
        if (enemyHit) {
            alive = speed >= defaultSpeed + 6;
            collisionOn = !alive;
        }

        return alive;
    }

    @Override
    protected boolean canBeDeflected(boolean usingShield) {

        // Can only be deflected using sword
        return !usingShield;
    }

    @Override
    protected void deflect(Entity target) {
        super.deflect(target);

        speed = 6;
        attack = 2;
    }

    @Override
    protected void checkDeath() {
        if (health <= 0 || !alive) {
            resetValues();
        }
    }

    @Override
    public void pickup(Entity user) {
        user.addArrows(1);
        resetValues();
    }
}
