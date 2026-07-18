package entity.enemy;

import application.GamePanel;
import entity.projectile.PRJ_Bone;

import java.awt.*;

public class EMY_Stalfos extends Enemy {

    public static final String emyName = "Stalfos";

    private int jumpCounter = 0;

    public EMY_Stalfos(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 15;

        maxHealth = 8;
        health = maxHealth;

        defaultSpeed = 1;
        speed = defaultSpeed;

        projectile = new PRJ_Bone(gp);

        minTileDistanceToPlayer = 5;
        maxTileDistanceToPlayer = 7;
    }

    @Override
    protected void getImages() {
        sprite = up1 = setupImage("/enemy/stalfos_down_1");
        up2 = setupImage("/enemy/stalfos_down_2");
    }

    @Override
    protected void getAttackImages() {
        attackUp1 = setupImage("/enemy/stalfos_down_1");
    }

    @Override
    protected void setAction() {

        if (isStuck()) return;

        if (isCaptured()) {
            handleCapture();
            manageValues();
            return;
        }

        // Jumping out of the way
        if (action == Action.JUMPING) {
            speed = 3;
            interactable = false;
        }
        else if (decideToJump()) {
            jumpAway();
        }
        else {
            super.setAction();
        }
    }

    private boolean decideToJump() {

        boolean playerAttacking = gp.player.getAction() == Action.ATTACKING;
        boolean playerInRange = ai.getTileDistance(gp.player) < 2;
        boolean playerIsFacing = direction == getOppositeDirection(gp.player.getDirection());

        // Player swings sword towards Stalfos, jump out of the way
        return playerAttacking && playerInRange && playerIsFacing;
    }

    private void jumpAway() {
        action = Action.JUMPING;
        direction = gp.player.getDirection();
    }

    @Override
    protected void chasePlayer() {
        super.chasePlayer();
        attack();
    }

    @Override
    protected void attack() {
        useProjectile(projectile, 2);
    }

    @Override
    protected void handleCapture() {

        if (action == Action.ATTACKING) {
            useProjectile(projectile);
            action = Action.IDLE;
        }
    }

    @Override
    protected void manageValues() {
        if (actionLockCounter > 0) {
            actionLockCounter--;
        }

        if (action == Action.JUMPING) {
            if (15 < ++jumpCounter) {
                jumpCounter = 0;
                action = Action.IDLE;
                interactable = true;
                speed = defaultSpeed;
            }
        }

        super.manageValues();
    }

    @Override
    public void resetValues() {
        super.resetValues();
        interactable = true;
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);

        if (action == Action.JUMPING) {
            g2.setColor(Color.BLACK);
            g2.fillOval(screenPoint.x + 10, screenPoint.y + 40, 30, 10);
        }
    }

    @Override
    protected void getSpriteImage() {

        if (action == Action.JUMPING) {
            image = attackUp1;
            drawOffset.y -= 15;
        }
        else {
            if (spriteNum == 1) {
                image = up1;
            }
            else {
                image = up2;
            }
        }
    }
}