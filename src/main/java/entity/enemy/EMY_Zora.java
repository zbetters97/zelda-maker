package entity.enemy;

import application.GamePanel;
import entity.projectile.PRJ_Fireball;

import java.awt.*;
import java.util.Random;

public class EMY_Zora extends Enemy {

    public static final String emyName = "Zora";

    public EMY_Zora(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 12;

        maxHealth = 10;
        health = maxHealth;

        defaultSpeed = 0;
        speed = defaultSpeed;

        canSwim = true;
        needsWater = true;

        projectile = new PRJ_Fireball(gp);

        hitbox = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        minTileDistanceToPlayer = 5;
        maxTileDistanceToPlayer = 9;
    }

    @Override
    protected void getImages() {
        up1 = down1 = left1 = right1 = setupImage("/enemy/zora_down_1");
        up2 = down2 = left2 = right2 = setupImage("/enemy/zora_down_2");
    }

    protected void getAttackImages() {
        attackUp1 = attackDown1 = attackLeft1 = attackRight1 = setupImage("/enemy/zora_attack_down_1");
        attackUp2 = attackDown2 = attackLeft2 = attackRight2 = setupImage("/enemy/zora_attack_down_1");
    }

    public void update() {

        if (isStuck()) {
            return;
        }

        setAction();
        manageValues();
    }

    @Override
    protected void chasePlayer() {

        spriteNum = 2;
        interactable = true;

        if (action == Action.IDLE) {
            prepareAttack();
        }
        else {
            attack();
        }
    }

    private void prepareAttack() {
        int i = new Random().nextInt(100);
        if (i == 0 && !projectile.getAlive() && actionLockCounter == 0) {
            action = Action.ATTACKING;
        }
    }

    @Override
    protected void attack() {

        if (++spriteCounter == 30) {
            projectile.set(worldPoint, direction, true, this);
            addProjectile(projectile);

            actionLockCounter = 30;
        }
        else if (60 < spriteCounter) {
            action = Action.IDLE;
            spriteCounter = 0;
        }
    }

    @Override
    protected void searchForPlayer() {
        super.searchForPlayer();

        spriteNum = 1;
        spriteCounter = 0;
        action = Action.IDLE;
        interactable = false;
        actionLockCounter = 0;
    }

    @Override
    protected void manageValues() {
        if (action == Action.IDLE && actionLockCounter > 0) {
            actionLockCounter--;
        }

        super.manageValues();
    }
}