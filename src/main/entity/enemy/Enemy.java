package entity.enemy;

import ai.EntityAI;
import application.GamePanel;
import entity.Entity;

import java.awt.*;

import static entity.Entity.Action.ATTACKING;

public class Enemy extends Entity {

    private boolean stunned = false;
    private int stunnedCounter = 0;

    protected int minTileDistanceToPlayer = 1;
    protected int maxTileDistanceToPlayer = 1;

    public Enemy(GamePanel gp, int worldX, int worldY, String emyName) {
        super(gp, worldX, worldY, emyName);

        entity_type = type_enemy;

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

        if (isStuck()) {
            return;
        }

        setAction();
        updateDirection();

        manageValues();
    }

    protected boolean isStuck() {

        if (knockback || !canMove) {

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
    protected void manageValues() {
        if (stunned) {
            stunnedCounter++;

            if (40 < stunnedCounter) {
                stunnedCounter = 0;
                stunned = false;
            }
        }
    }

    @Override
    public void resetValues() {
        super.resetValues();

        stunned = false; stunnedCounter = 0;
    }

    @Override
    public void draw(Graphics2D g2) {

        adjustOffCenter();
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
        g2.drawImage(image, tempScreenPoint.x, tempScreenPoint.y, null);

        // Draw hitbox (debug)
        g2.drawRect(tempScreenPoint.x + hitbox.x, tempScreenPoint.y + hitbox.y, hitbox.width, hitbox.height);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));

        g2.drawString(onPath + "" , tempScreenPoint.x, tempScreenPoint.y + 20);
        g2.setColor(Color.RED);

        // Reset opacity
        changeAlpha(g2, 1f);
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
        } else if (spriteNum == 2) {
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
                    tempScreenPoint.y -= up1.getHeight();
                    yield attackUp1;
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown1;
                case LEFT -> {
                    tempScreenPoint.x -= left1.getWidth();
                    yield attackLeft1;
                }
                case RIGHT -> attackRight1;
            };
        } else if (attackNum == 2) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> {
                    tempScreenPoint.y -= up1.getHeight();
                    yield attackUp2;
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown2;
                case LEFT -> {
                    tempScreenPoint.x -= left1.getWidth();
                    yield attackLeft2;
                }
                case RIGHT -> attackRight2;
            };
        }
    }
}
