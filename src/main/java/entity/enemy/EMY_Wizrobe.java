package entity.enemy;

import application.GamePanel;
import entity.projectile.PRJ_Magic;

public class EMY_Wizrobe extends Enemy {

    public static final String emyName = "Wizrobe";

    private boolean teleporting = false;

    public EMY_Wizrobe(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 10;

        maxHealth = 16;
        health = maxHealth;

        defaultSpeed = 2;
        speed = defaultSpeed;

        knockbackPower = 0;

        projectile = new PRJ_Magic(gp);
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/enemy/wizzrobe_up_1");
        down1 = setupImage("/enemy/wizzrobe_down_1");
        left1 = setupImage("/enemy/wizzrobe_left_1");
        right1 = setupImage("/enemy/wizzrobe_right_1");

        up2 = setupImage("/enemy/wizzrobe_down_2");
    }

    @Override
    public void update() {

        if (isStuck()) {
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
            move();

            if (checkTeleportTimer()) {
                teleporting = false;
                interactable = true;
                spriteNum = 4;
            }
        }
    }

    private boolean advanceAnimation(int activeSprite) {

        if (++spriteCounter <= animationSpeed) {
            spriteNum = 2;
            return false;
        }

        spriteNum = activeSprite;
        return true;
    }

    @Override
    protected void move() {

        setDirection(30);

        checkCollision();
        if (!collisionOn) {
            moveInDirection(direction);
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

    @Override
    protected void searchForPlayer() {
        if (onPath) {
            if (attackCounter == 45) {
                attack();
            }

            ai.approachPlayer(10);
            ai.isOffPath(gp.player, 8);
        }
        else if (ai.playerWithinRange()) {
            ai.isOnPath(gp.player, 6);
        }
    }

    @Override
    protected void attack() {
        projectile.set(worldPoint, direction, true, this);
        addProjectile(projectile);
    }

    private boolean checkTeleportTimer() {

        if (120 < ++attackCounter) {
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

    @Override
    public void resetValues() {
        super.resetValues();
        teleporting = false;
    }

    @Override
    protected void getSpriteImage() {
        if (spriteNum == 1) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down1;
                case LEFT -> left1;
                case RIGHT -> right1;
            };
        }
        else {
            image = up2;
        }
    }
}