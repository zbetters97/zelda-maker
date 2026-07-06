package entity.projectile;

import application.GamePanel;

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
        alive = false;

        hitbox = new Rectangle(4, 8, 24, 24);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    @Override
    public void getImages() {
        up1 = up2 = setupImage("/projectiles/arrow_up_1", 35, 35);
        down1 = down2 = setupImage("/projectiles/arrow_down_1", 35, 35);
        left1 = left2 = setupImage("/projectiles/arrow_left_1", 35, 35);
        right1 = right2 = setupImage("/projectiles/arrow_right_1", 35, 35);
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
    protected boolean checkEnemyCollision() {
        boolean enemyHit = super.checkEnemyCollision();
        if (enemyHit) {
            alive = speed >= defaultSpeed + 6;
            collisionOn = !alive;
        }

        return alive;
    }

    @Override
    public void resetValues() {
        alive = false;
        attack = defaultAttack;
        speed = defaultSpeed;
    }
}
