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

            Entity enemy = getEnemy(this);
            if (enemy != null && enemy != user) {
                // gp.player.damageEnemy(enemy, this, attack, knockbackPower);

                // CONTINUE MOVING IF AT FULL POWER
                if (speed == 12) {
                    collisionOn = false;
                    alive = true;
                }
            }
            else {
                collisionOn = false;
            }
        }
        else {
            boolean contactPlayer = gp.cChecker.checkPlayer(this);

            if (contactPlayer) {
                damagePlayer(attack);
                resetValues();
            }
        }

        gp.cChecker.checkTile(this);
        gp.cChecker.checkEntity(this, gp.npc);

        if (!canPickup) {
            switch (direction) {
                case UP, UPLEFT, UPRIGHT -> worldY -= speed;
                case DOWN, DOWNLEFT, DOWNRIGHT -> worldY += speed;
                case LEFT -> worldX -= speed;
                case RIGHT -> worldX += speed;
            }
        }

        if (collisionOn && alive) {
            canPickup = true;
        }

        health--;
        if (health <= 0) {
            resetValues();
        }
    }

    public void resetValues() {
        attack = 2;
        speed = 6;
        alive = false;
    }
}
