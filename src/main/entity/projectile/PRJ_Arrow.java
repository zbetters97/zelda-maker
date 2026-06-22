package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class PRJ_Arrow extends Projectile {

    public static final String prjName = "Arrow Projectile";

    public PRJ_Arrow(GamePanel gp) {
        super(gp);

        type = type_projectile;
        name = prjName;

        defaultSpeed = 3;
        speed = defaultSpeed;

        defaultAttack = 1;
        attack = defaultAttack;

        maxHealth = 120;
        health = maxHealth;
        alive = false;

        hitbox = new Rectangle(12, 16, 24, 24);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    public void getImages() {
        up1 = up2 = setupImage("/projectiles/arrow_up_1", 35, 35);
        down1 = down2 = setupImage("/projectiles/arrow_down_1", 35, 35);
        left1 = left2 = setupImage("/projectiles/arrow_left_1", 35, 35);
        right1 = right2 = setupImage("/projectiles/arrow_right_1", 35, 35);
    }

    public void update() {
        super.update();

        collisionOn = false;

        if (user == gp.player) {
            checkCollision();
        }
        else {
            checkPlayerCollision();
        }

        gp.cChecker.checkTile(this);
        gp.cChecker.checkEntity(this, gp.npc);

        if (!canPickup) {
            move();
        }

        if (collisionOn && alive) {
            canPickup = true;
            speed = 0;
            attack = 0;
        }

        health--;
        checkDeath();
    }

    protected void checkCollision() {
        Entity enemy = getEnemy(this);
        if (enemy != null && enemy != user) {

            damageEnemy(enemy);

            if (speed == 12) {
                collisionOn = false;
                alive = true;
            }
        }
        else {
            collisionOn = false;
        }
    }

    private void checkPlayerCollision() {
        boolean contactPlayer = gp.cChecker.checkPlayer(this);

        if (contactPlayer) {
            damagePlayer(attack);
            resetValues();
        }
    }

    protected void checkDeath() {
        if (health <= 0) {
            resetValues();
        }
    }

    protected void resetValues() {
        alive = false;
        attack = 2;
        speed = 6;
    }
}
