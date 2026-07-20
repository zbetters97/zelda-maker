package entity.enemy;

import application.GamePanel;
import entity.projectile.PRJ_Beam;

import static application.GamePanel.Direction.*;

public class EMY_Beamos extends Enemy {

    public static final String emyName = "Beamos";
    private boolean playerFound = false;

    public EMY_Beamos(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 15;

        maxHealth = 12;
        health = maxHealth;

        defaultSpeed = 0;
        speed = defaultSpeed;

        defaultAttack = 0;
        attack = defaultAttack;
        knockbackPower = 0;

        interactable = false;

        projectile = new PRJ_Beam(gp);

        minTileDistanceToPlayer = 3;
        maxTileDistanceToPlayer = 5;
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/enemy/beamos_up_1");
        up2 = setupImage("/enemy/beamos_up_2");
        sprite = down1 = setupImage("/enemy/beamos_down_1");
        down2 = setupImage("/enemy/beamos_down_2");
        left1 = setupImage("/enemy/beamos_left_1");
        left2 = setupImage("/enemy/beamos_left_2");
        right1 = setupImage("/enemy/beamos_right_1");
        right2 = setupImage("/enemy/beamos_right_2");
    }

    @Override
    public void update() {

        searchForPlayer();

        if (!playerFound) {
            cycleSprites();
        }

        manageValues();
    }

    @Override
    protected void searchForPlayer() {

        boolean withinBounds = ai.getTileDistance(gp.player) <= maxTileDistanceToPlayer;
        boolean withinRange = ai.withinRange(gp.player, gp.tileSize);

        playerFound = withinBounds && withinRange;

        if (playerFound) {
            attack();
            spriteNum = 1;
            spriteCounter = 0;
            playerFound = false;
        }
    }

    @Override
    protected void cycleSprites() {

        if (animationSpeed < ++spriteCounter) {

            // Turn to next direction
            if (spriteNum == 1) {
                spriteNum = 2;
            }
            // Find next direction when turned all the way
            else {
                rotate();
                spriteNum = 1;
            }

            spriteCounter = 0;
        }
    }

    @Override
    public void rotate() {
        direction = switch (direction) {
            case UP, UPLEFT, UPRIGHT -> RIGHT;
            case DOWN, DOWNLEFT, DOWNRIGHT -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
        };
    }

    @Override
    protected void attack() {
        useProjectile(projectile, 1);
    }

    @Override
    public boolean canTakeLoot() {
        return false;
    }

    @Override
    protected void manageValues() {
        if (actionLockCounter > 0) {
            actionLockCounter--;
        }

        super.manageValues();
    }

    @Override
    public void resetValues(){
        super.resetValues();
        playerFound = false;
    }
}