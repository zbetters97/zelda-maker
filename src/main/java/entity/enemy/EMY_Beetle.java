package entity.enemy;

import application.GamePanel;
import entity.Entity;

public class EMY_Beetle extends Enemy {

    public static final String emyName = "Beetle";

    public EMY_Beetle(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 15;

        maxHealth = 16;
        health = maxHealth;

        defaultSpeed = 1;
        speed = defaultSpeed;
        defaultAttack = 2;
        attack = defaultAttack;

        shielded = true;

        minTileDistanceToPlayer = 3;
        maxTileDistanceToPlayer = 6;
    }

    @Override
    protected void getImages() {
        sprite = up1 = setupImage("/enemy/beetle_down_1");
        up2 = setupImage("/enemy/beetle_down_2");
    }

    @Override
    protected void getAttackImages() {
        attackUp1 = setupImage("/enemy/beetle_attack_down_1");
        attackUp2 = setupImage("/enemy/beetle_attack_down_2");
    }

    @Override
    public void update() {

        if (isStuck()) return;

        if (isCaptured()) {
            handleCapture();
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
    protected void searchForPlayer() {
        setDirection(60);
        super.searchForPlayer();
    }

    private void runShieldTimer() {

        if (60 <= ++attackCounter && attackCounter < 120) {
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