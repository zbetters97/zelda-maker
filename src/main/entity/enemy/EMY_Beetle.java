package entity.enemy;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class EMY_Beetle extends Entity {

    public static final String emyName = "Beetle";

    public EMY_Beetle(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        entity_type = type_enemy;
        animationSpeed = 15;

        maxHealth = 16;
        health = maxHealth;

        defaultSpeed = 1;
        speed = defaultSpeed;
        attack = 2;

        shielded = true;

        hitbox = new Rectangle(8, 16, 32, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        getAttackImages();
    }

    @Override
    protected void getImages() {
        up1 = down1 = left1 = right1 = setupImage("/enemy/beetle_down_1");
        up2 = down2 = left2 = right2 = setupImage("/enemy/beetle_down_2");
    }

    private void getAttackImages() {
        attackUp1 = setupImage("/enemy/beetle_attack_down_1");
        attackUp2 = setupImage("/enemy/beetle_attack_down_2");
    }

    @Override
    public void update() {
        super.update();

        if (!canMove) {
            manageValues();
            return;
        }

        if (shielded) {
            setAction();
            updateDirection();
        }
        else {
            runShieldTimer();
        }

        manageValues();
    }

    @Override
    protected void setAction() {

        isOffPath(gp.player, 7);

        if (onPath && playerWithinBounds()) {
            searchPath(getGoalCol(gp.player), getGoalRow(gp.player));
        }
        else {
            setDirection(60);

            if (playerWithinBounds()) {
                isOnPath(gp.player, 5);
            }
            else {
                onPath = false;
            }
        }
    }

    private void runShieldTimer() {

        attackCounter++;
        if (60 <= attackCounter && attackCounter < 120) {
            attackNum = 2;
        }
        else if (120 <= attackCounter) {
            shielded = true;
            attack = defaultAttack;
            speed = defaultSpeed;
            attackNum = 1;
            attackCounter = 0;
        }
    }

    @Override
    protected boolean canBeDeflected(boolean usingShield) {
        return usingShield;
    }

    @Override
    public void deflect(Entity target) {
        shielded = false;

        knockback = true;
        knockbackDirection = getOppositeDirection(direction);
        speed++;
    }

    @Override
    protected void getSpriteImage() {

        if (shielded) {
            if (spriteNum == 1) {
                image = up1;
            }
            else if (spriteNum == 2) {
                image = up2;
            }
        }
        else {
            if (attackNum == 1) {
                image = attackUp1;
            }
            else {
                image = attackUp2;
            }
        }
    }
}