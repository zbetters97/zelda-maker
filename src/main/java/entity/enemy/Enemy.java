package entity.enemy;

import ai.EntityAI;
import application.GamePanel;
import entity.Entity;

import java.awt.*;

import static entity.Entity.Action.ATTACKING;

public class Enemy extends Entity {

    protected int minTileDistanceToPlayer = 1;
    protected int maxTileDistanceToPlayer = 1;

    public Enemy(GamePanel gp, int worldX, int worldY, String emyName) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 12;

        defaultAttack = 1;
        attack = defaultAttack;
        knockbackPower = 1;

        ai = new EntityAI(gp, this);

        hitbox = new Rectangle(8, 16, 32, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        attackBox.width = 48;
        attackBox.height = 48;

        swingSpeed1 = 15;
        swingSpeed2 = 45;

        getAttackImages();
    }

    protected void getAttackImages() {

    }

    @Override
    public void update() {

        if (isStuck()) return;

        if (isCaptured()) {
            handleCapture();
            manageValues();
            return;
        }

        setAction();
        updateDirection();

        manageValues();
    }

    protected boolean isStuck() {

        if (knockback || unableToMove()) {

            if (knockback) {
                handleKnockback();
            }
            manageValues();

            return true;
        }

        return false;
    }

    protected void setAction() {
        ai.isOffPath(gp.player, maxTileDistanceToPlayer);

        if (onPath && ai.playerWithinRange()) {
            chasePlayer();
        }
        else {
            searchForPlayer();
        }
    }

    protected void chasePlayer() {
        if (isCaptured()) return;
        ai.searchPath(gp.player);
    }

    protected void searchForPlayer() {
        if (ai.playerWithinRange()) {
            ai.isOnPath(gp.player, minTileDistanceToPlayer);
        }
        else {
            onPath = false;
        }
    }

    @Override
    public boolean canTakeLoot() {
        return true;
    }

    @Override
    public void resetValues() {
        super.resetValues();
        stunned = false;
        stunnedCounter = 0;
    }

    @Override
    public void draw(Graphics2D g2) {

        if (!drawing) return;

        drawOffset.setLocation(0, 0);
        getSpriteImage();

        // Flash sprite if hurt
        if (invincible) {
            playHurtAnimation(g2);
        }
        // Dying animation
        else if (dying) {
            playDyingAnimation(g2);
        }

        // Draw sprite
        gp.camera.worldToScreen(worldPoint, screenPoint);
        g2.drawImage(image, screenPoint.x + drawOffset.x, screenPoint.y + drawOffset.y, null);

        g2.setColor(Color.RED);
        g2.drawRect(screenPoint.x + hitbox.x, screenPoint.y + hitbox.y, hitbox.width, hitbox.height);

        // Reset opacity
        changeAlpha(g2, 1f);

        // Draw held loot
        drawLoot(g2);
    }

    protected void getSpriteImage() {
        if (action == ATTACKING) {
            getAttackImage();
        }
        else {
            getIdleImage();
        }
    }
    private void getIdleImage() {
        if (spriteNum == 1) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down1;
                case LEFT -> left1;
                case RIGHT -> right1;
            };
        }
        else if (spriteNum == 2) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down2;
                case LEFT -> left2;
                case RIGHT -> right2;
            };
        }
    }
    private void getAttackImage() {
        if (attackNum == 1) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> {
                    drawOffset.y -= up1.getHeight();
                    yield attackUp1;
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown1;
                case LEFT -> {
                    drawOffset.x -= left1.getWidth();
                    yield attackLeft1;
                }
                case RIGHT -> attackRight1;
            };
        }
        else if (attackNum == 2) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> {
                    drawOffset.y -= up1.getHeight();
                    yield attackUp2;
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown2;
                case LEFT -> {
                    drawOffset.x -= left1.getWidth();
                    yield attackLeft2;
                }
                case RIGHT -> attackRight2;
            };
        }
    }
}
