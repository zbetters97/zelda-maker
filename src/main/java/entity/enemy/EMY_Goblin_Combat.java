package entity.enemy;

import application.GamePanel;

public class EMY_Goblin_Combat extends Enemy {

    public static final String emyName = "Combat Goblin";

    public EMY_Goblin_Combat(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 15;

        maxHealth = 12;
        health = maxHealth;

        defaultSpeed = 1;
        speed = defaultSpeed;

        defaultAttack = 4;
        attack = defaultAttack;
        knockbackPower = 2;

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
    protected void getAttackImages() {
        attackUp1 = setupImage("/enemy/goblin_attack_up_1", gp.tileSize, gp.tileSize * 2);
        attackUp2 = setupImage("/enemy/goblin_attack_up_2", gp.tileSize, gp.tileSize * 2);
        attackDown1 = setupImage("/enemy/goblin_attack_down_1", gp.tileSize, gp.tileSize * 2);
        attackDown2 = setupImage("/enemy/goblin_attack_down_2", gp.tileSize, gp.tileSize * 2);
        attackLeft1 = setupImage("/enemy/goblin_attack_left_1", gp.tileSize * 2, gp.tileSize);
        attackLeft2 = setupImage("/enemy/goblin_attack_left_2", gp.tileSize * 2, gp.tileSize);
        attackRight1 = setupImage("/enemy/goblin_attack_right_1", gp.tileSize * 2, gp.tileSize);
        attackRight2 = setupImage("/enemy/goblin_attack_right_2", gp.tileSize * 2, gp.tileSize);
    }

    @Override
    protected void chasePlayer() {

        super.chasePlayer();

        // Decide to attack
        ai.setAttacking(60, gp.tileSize * 3, gp.tileSize);

        // Stop to attack
        if (action == Action.ATTACKING) {
            attacking();
            speed = 0;
        }
        else {
            speed = defaultSpeed;
        }
    }

    @Override
    protected void searchForPlayer() {
        setDirection(60);
        super.searchForPlayer();
    }
}