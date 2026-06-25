package entity.enemy;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class EMY_Goblin_Combat extends Entity {

    public static final String emyName = "Combat Goblin";

    public EMY_Goblin_Combat(GamePanel gp, int worldX, int worldY) {
        super(gp);
        this.worldX = worldX * gp.tileSize;
        this.worldY = worldY * gp.tileSize;
        worldXStart = this.worldX;
        worldYStart = this.worldY;

        type = type_enemy;
        name = emyName;

        maxHealth = 12;
        health = maxHealth;
        defaultSpeed = 1;
        speed = defaultSpeed;
        animationSpeed = 10;
        attack = 4;

        hitbox = new Rectangle(8, 16, 32, 32);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        swingSpeed1 = 15;
        swingSpeed2 = 45;

        attackBox.width = 48;
        attackBox.height = 48;

        getAttackImages();
    }

    public void getImages() {
        up1 = setupImage("/enemy/goblin_up_1");
        up2 = setupImage("/enemy/goblin_up_2");
        down1 = setupImage("/enemy/goblin_down_1");
        down2 = setupImage("/enemy/goblin_down_2");
        left1 = setupImage("/enemy/goblin_left_1");
        left2 = setupImage("/enemy/goblin_left_2");
        right1 = setupImage("/enemy/goblin_right_1");
        right2 = setupImage("/enemy/goblin_right_2");
    }
    public void getAttackImages() {
        attackUp1 = setupImage("/enemy/goblin_attack_up_1", gp.tileSize, gp.tileSize * 2);
        attackUp2 = setupImage("/enemy/goblin_attack_up_2", gp.tileSize, gp.tileSize * 2);
        attackDown1 = setupImage("/enemy/goblin_attack_down_1", gp.tileSize, gp.tileSize * 2);
        attackDown2 = setupImage("/enemy/goblin_attack_down_2", gp.tileSize, gp.tileSize * 2);
        attackLeft1 = setupImage("/enemy/goblin_attack_left_1", gp.tileSize * 2, gp.tileSize);
        attackLeft2 = setupImage("/enemy/goblin_attack_left_2", gp.tileSize * 2, gp.tileSize);
        attackRight1 = setupImage("/enemy/goblin_attack_right_1", gp.tileSize * 2, gp.tileSize);
        attackRight2 = setupImage("/enemy/goblin_attack_right_2", gp.tileSize * 2, gp.tileSize);
    }

    public void update() {
        if (knockback) {
            handleKnockback();
            manageValues();
            return;
        }

        if (action == Action.ATTACKING) {
            attack();
        }

        setAction();

        updateDirection();

        cycleSprites();
        manageValues();
    }

    public void setAction() {

        if (onPath) {

            isOffPath(gp.player, 8);

            if (onPath && playerWithinBounds()) {

                searchPath(getGoalCol(gp.player), getGoalRow(gp.player));
                setAttacking(60, gp.tileSize * 3, gp.tileSize);

                if (action == Action.ATTACKING) {
                    speed = 0;
                }
                else {
                    speed = defaultSpeed;
                }
            }
        }
        else {
            setDirection(60);

            if (playerWithinBounds()) {
                isOnPath(gp.player, 5);
            }
            else {
                onPath = false;
            }
        }
    }

    protected void reactToDamage() {
        resetValues();
    }
}