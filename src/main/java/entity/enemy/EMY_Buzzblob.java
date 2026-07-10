package entity.enemy;

import application.GamePanel;

public class EMY_Buzzblob extends Enemy {

    public static final String emyName = "BuzzBlob";

    private int cycle = 0;

    public EMY_Buzzblob(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 10;

        maxHealth = 6;
        health = maxHealth;

        defaultSpeed = 1;
        speed = defaultSpeed;

        minTileDistanceToPlayer = 3;
        maxTileDistanceToPlayer = 6;
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/enemy/buzzblob_down_1");
        sprite = up2 = setupImage("/enemy/buzzblob_down_2");
        up3 = setupImage("/enemy/buzzblob_down_3");
    }

    @Override
    protected void getAttackImages() {
        attackUp1 = setupImage("/enemy/buzzblob_attack_down_1");
        attackUp2 = setupImage("/enemy/buzzblob_attack_down_2");
    }

    @Override
    protected void chasePlayer() {
        super.chasePlayer();

        if (action != Action.ATTACKING) {
            ai.setAttacking(180, gp.tileSize * 3, gp.tileSize * 3);
        }
    }

    @Override
    protected void searchForPlayer() {
        setDirection(60);
        super.searchForPlayer();
    }

    @Override
    protected void cycleSprites() {

        if (animationSpeed < ++spriteCounter) {
            if (action == Action.ATTACKING) {
                cycleBuzzingSprites();
            }
            else {
                cycleIdleSprites();
            }

            spriteCounter = 0;
        }
    }
    private void cycleBuzzingSprites() {
        if (attackNum == 1) {
            attackNum = 2;
        }
        else if (attackNum == 2) {
            attackNum = 1;
        }
    }
    private void cycleIdleSprites() {

        // 1 -> 2 -> 3 -> 2 -> 1
        if (spriteNum == 1) {
            spriteNum = 2;
        }
        else if (spriteNum == 2 && cycle == 0) {
            spriteNum = 3;
            cycle++;
        }
        else if (spriteNum == 2 && cycle == 1) {
            spriteNum = 1;
            cycle = 0;
        }
        else if (spriteNum == 3) {
            spriteNum = 2;
        }
    }

    @Override
    protected void manageValues() {
        if (action == Action.ATTACKING) {
            attack = 2;
            buzzing = true;

            if (120 < ++attackCounter) {
                action = Action.IDLE;
                attackCounter = 0;
            }
        }
        else {
            attack = defaultAttack;
            buzzing = false;
        }

        super.manageValues();
    }

    @Override
    protected void getSpriteImage() {

        if (action == Action.ATTACKING) {
            if (attackNum == 1) {
                image = attackUp1;
            }
            else {
                image = attackUp2;
            }
        }
        else {
            if (spriteNum == 1) {
                image = up1;
            }
            else if (spriteNum == 2) {
                image = up2;
            }
            else {
                image = up3;
            }
        }
    }
}