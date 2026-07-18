package entity.enemy;

import application.GamePanel;
import entity.projectile.PRJ_Boomerang;

public class EMY_Goblin_Boomerang extends Enemy {

    public static final String emyName = "Goblin_Boomerang";

    public EMY_Goblin_Boomerang(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 15;

        maxHealth = 8;
        health = maxHealth;

        defaultSpeed = 1;
        speed = defaultSpeed;

        projectile = new PRJ_Boomerang(gp);

        minTileDistanceToPlayer = 5;
        maxTileDistanceToPlayer = 8;
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/enemy/goblin_up_1");
        up2 = setupImage("/enemy/goblin_up_2");
        sprite = down1 = setupImage("/enemy/goblin_down_1");
        down2 = setupImage("/enemy/goblin_down_2");
        left1 = setupImage("/enemy/goblin_left_1");
        left2 = setupImage("/enemy/goblin_left_2");
        right1 = setupImage("/enemy/goblin_right_1");
        right2 = setupImage("/enemy/goblin_right_2");
    }

    @Override
    protected void chasePlayer() {
        handleLookingAtPlayer();
        super.chasePlayer();
    }

    private void handleLookingAtPlayer() {

        // Stop moving if in range to shoot at player
        if (ai.lookingAtPlayer(gp.tileSize / 2)) {
            speed = 0;
            attack();
        }
        else {
            speed = defaultSpeed;
        }
    }

    @Override
    protected void attack() {
        useProjectile(projectile, 2);
    }

    @Override
    protected void searchForPlayer() {
        setDirection(60);
        super.searchForPlayer();
    }

    @Override
    protected void handleCapture() {

        if (action == Action.ATTACKING) {
            speed = defaultSpeed;
            useProjectile(projectile);
            action = Action.IDLE;
        }
    }

    @Override
    protected void manageValues() {

        // Force 30 frame delay between throws
        if (actionLockCounter > 0 && !projectile.getAlive()) {
            actionLockCounter--;
        }

        super.manageValues();
    }
}