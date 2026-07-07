package entity.enemy;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Fireball;

import java.awt.*;
import java.util.Random;

public class EMY_Zora extends Entity {

    public static final String emyName = "Zora";

    public EMY_Zora(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        entity_type = type_enemy;
        animationSpeed = 12;

        maxHealth = 10;
        health = maxHealth;

        defaultSpeed = 0;
        speed = defaultSpeed;

        canSwim = true;
        needsWater = true;

        hitbox = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        projectile = new PRJ_Fireball(gp);

        getAttackImages();
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
        super.update();

        if (!canMove) {
            manageValues();
            return;
        }

        setAction();
        manageValues();
    }

    @Override
    protected void setAction() {

        if (onPath) {
            targetPlayer();
            isOffPath(gp.player, 6);
        }
        else {
            searchForPlayer();
        }
    }

    private void targetPlayer() {

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
        if (i == 0 && !projectile.alive && actionLockCounter == 0) {
            action = Action.ATTACKING;
        }
    }

    @Override
    protected void attack() {

        spriteCounter++;

        if (spriteCounter == 30) {
            projectile.set(worldPoint, direction, true, this);
            addProjectile(projectile);

            actionLockCounter = 30;
        }
        else if (60 < spriteCounter) {
            action = Action.IDLE;
            spriteCounter = 0;
        }
    }

    private void searchForPlayer() {
        isOnPath(gp.player, 5);

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