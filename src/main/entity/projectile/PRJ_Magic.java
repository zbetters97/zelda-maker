package entity.projectile;

import application.GamePanel;

import java.awt.*;

public class PRJ_Magic extends Projectile {

    public static final String prjName = "Magic Projectile";

    public PRJ_Magic(GamePanel gp) {
        super(gp, prjName);

        defaultSpeed = 8;
        speed = defaultSpeed;

        defaultAttack = 2;
        attack = defaultAttack;

        maxHealth = 120;
        health = maxHealth;
        alive = false;

        hitbox = new Rectangle(12, 12, 24, 24);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    @Override
    public void getImages() {
        up1 = up2 = setupImage("/projectiles/magic_up_1");
        down1 = down2 = setupImage("/projectiles/magic_down_1");
        left1 = left2 = setupImage("/projectiles/magic_left_1");
        right1 = right2 = setupImage("/projectiles/magic_right_1");
    }

    @Override
    public void update() {

        checkCollision();
        moveInDirection(direction);

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

        // Can only be deflected with a sword
        return !usingShield;
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
}
