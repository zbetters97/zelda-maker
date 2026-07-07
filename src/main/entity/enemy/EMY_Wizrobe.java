package entity.enemy;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Magic;

import java.awt.*;

public class EMY_Wizrobe extends Entity {

    public static final String emyName = "Wizrobe";

    private boolean teleporting = false;

    public EMY_Wizrobe(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        entity_type = type_enemy;
        animationSpeed = 10;

        maxHealth = 16;
        health = maxHealth;

        defaultSpeed = 2;
        speed = defaultSpeed;

        projectile = new PRJ_Magic(gp);

        hitbox = new Rectangle(8, 16, 32, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

    }

    @Override
    protected void getImages() {
        up1 = setupImage("/enemy/wizzrobe_up_1");
        down1 = setupImage("/enemy/wizzrobe_down_1");
        left1 = setupImage("/enemy/wizzrobe_left_1");
        right1 = setupImage("/enemy/wizzrobe_right_1");

        up2 = down2 = left2 = right2 = setupImage("/enemy/wizzrobe_down_2");
    }

    @Override
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
        if (teleporting) {
            updateTeleport();
        }
        else {
            updateAttackState();
        }
    }

    private void updateTeleport() {
        if (advanceAnimation(4)) {
            move(direction);

            if (checkTeleportTimer()) {
                teleporting = false;
                interactable = true;
                spriteNum = 4;
            }
        }
    }

    private boolean advanceAnimation(int activeSprite) {
        spriteCounter++;

        if (spriteCounter <= animationSpeed) {
            spriteNum = 2;
            return false;
        }

        spriteNum = activeSprite;
        return true;
    }

    @Override
    protected void move(GamePanel.Direction direction) {

        setDirection(30);

        checkCollision();
        if (!collisionOn) {
            moveInDirection(direction);
        }
    }

    @Override
    protected void moveInDirection(GamePanel.Direction direction) {
        switch (direction) {
            case UP -> worldPoint.y -= speed;
            case DOWN -> worldPoint.y += speed;
            case LEFT -> worldPoint.x -= speed;
            case RIGHT -> worldPoint.x += speed;
        }
    }

    private void updateAttackState() {
        if (advanceAnimation(1)) {
            searchForPlayer();
            if (checkTeleportTimer()) {
                teleporting = true;
                interactable = false;
                spriteNum = 2;
            }
        }
    }

    private void searchForPlayer() {
        if (onPath) {
            if (attackCounter == 45) {
                attack();
            }

            approachPlayer(10);
            isOffPath(gp.player, 8);
        }
        else if (playerWithinBounds()) {
            isOnPath(gp.player, 6);
        }
    }

    @Override
    protected void attack() {
        projectile.set(worldPoint, direction, true, this);
        addProjectile(projectile);
    }

    private boolean checkTeleportTimer() {

        attackCounter++;
        if (120 < attackCounter) {
            spriteCounter = 0;
            attackCounter = 0;
            return true;
        }

        return false;
    }

    @Override
    protected void reactToDamage() {
        spriteNum = 2;
        spriteCounter = 0;
        attackCounter = 0;
        teleporting = true;
        interactable = false;
    }
}