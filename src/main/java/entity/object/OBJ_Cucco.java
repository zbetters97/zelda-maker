package entity.object;

import ai.EntityAI;
import application.GamePanel;
import entity.Entity;
import entity.collectable.*;

import java.awt.*;

public class OBJ_Cucco extends Object {

    public static final String objName = "Cucco";

    private int directionRate = 60;
    private int aggressiveCounter = 0;

    public OBJ_Cucco(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);

        animationSpeed = 10;

        maxHealth = 99;
        health = maxHealth;

        latchable = true;

        defaultSpeed = 1;
        speed = defaultSpeed;

        defaultAttack = 1;
        attack = defaultAttack;
        knockbackPower = 1;

        ai = new EntityAI(gp, this);

        hitbox = new Rectangle(8, 16, 32, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        availableAction = "GRAB";

        getAttackImages();
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/objects/obj_cucco_up_1");
        up2 = setupImage("/objects/obj_cucco_up_2");
        down1 = setupImage("/objects/obj_cucco_down_1");
        down2 = setupImage("/objects/obj_cucco_down_2");
        left1 = setupImage("/objects/obj_cucco_left_1");
        left2 = setupImage("/objects/obj_cucco_left_2");
        sprite = right1 = setupImage("/objects/obj_cucco_right_1");
        right2 = setupImage("/objects/obj_cucco_right_2");
    }
    private void getAttackImages() {
        attackUp1 = setupImage("/objects/obj_cucco_up_1");
        attackUp2 = setupImage("/objects/obj_cucco_up_2");
        attackDown1 = setupImage("/objects/obj_cucco_attack_down_1");
        attackDown2 = setupImage("/objects/obj_cucco_attack_down_2");
        attackLeft1 = setupImage("/objects/obj_cucco_attack_left_1");
        attackLeft2 = setupImage("/objects/obj_cucco_attack_left_2");
        attackRight1 = setupImage("/objects/obj_cucco_attack_right_1");
        attackRight2 = setupImage("/objects/obj_cucco_attack_right_2");
    }

    @Override
    public void update() {

        // Can't move if in the air
        if (tossed) {
            super.handleToss();
            return;
        }

        setAction();
        updateDirection();

        manageValues();
    }

    protected void setAction() {

        // If hostile, chase after player
        if (action == Action.ATTACKING) {
            handleHostile();
        }
        // If idle, move around
        else {
            setDirection(directionRate);
        }
    }

    private void handleHostile() {

        // Search for player
        ai.isOffPath(gp.player, 12);

        if (onPath && ai.playerWithinRange()) {
            chasePlayer();
            checkPlayerCollision();
        }
        else {
            searchForPlayer();
        }
    }

    private void chasePlayer() {
        ai.searchPath(gp.player);
    }

    private void checkPlayerCollision() {

        // Hurt player if contact
        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        if (contactPlayer) {
            gp.player.takeDamage(this);
        }
    }

    private void searchForPlayer() {

        if (ai.playerWithinRange()) {
            ai.isOnPath(gp.player, 10);
        }
        else {
            onPath = false;
        }
    }

    @Override
    public boolean canCollideWith(Entity target) {

        // Can collide if hostile
        return action == Action.ATTACKING;
    }

    @Override
    public void interact(Entity user) {

        // Can be picked up if not hostile
        if (gp.keyH.aPressed && action != Action.ATTACKING) {
            user.grab(this);
        }
    }

    @Override
    public void interact() {

        // React to hit
        if (!invincible) {
            health -= 2;
            invincible = true;
        }
    }

    @Override
    protected void landOnGround() {
        super.landOnGround();

        // Take damage and look around rapidly
        health -= 2;
        speed = 0;
        directionRate = 10;
        animationSpeed = 5;
        stunned = true;
    }

    @Override
    protected void manageValues() {
        super.manageValues();

        // No longer stunned, reset to defaults
        if (!stunned) {
            animationSpeed = 10;
            speed = defaultSpeed;
            directionRate = 60;
        }

        // Lost all health, increase speed and stay hostile for 999 frames
        if (health <= 0) {

            action = Action.ATTACKING;
            speed = 2;

            if (999 < ++aggressiveCounter) {
                resetValues();
            }
        }
    }

    @Override
    public void resetValues() {
        animationSpeed = 10;
        speed = defaultSpeed;
        health = maxHealth;
        directionRate = 60;
        aggressiveCounter = 0;

        stunned = false;
        invincible = false;
        interactable = true;
        action = Action.IDLE;
    }

    @Override
    public void draw(Graphics2D g2) {

        if (!drawing) return;

        // Flash sprite if hurt
        if (invincible) {
            playHurtAnimation(g2);
        }

        super.draw(g2);
    }

    @Override
    protected void getSpriteImage() {

        // If hostile, get attack images
        if (action == Action.ATTACKING) {
            getAttackSprite();
        }
        else {
            super.getSpriteImage();
        }
    }

    private void getAttackSprite() {

        if (spriteNum == 1) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> attackUp1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown1;
                case LEFT -> attackLeft1;
                case RIGHT -> attackRight1;
            };
        }
        else {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> attackUp2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown2;
                case LEFT -> attackLeft2;
                case RIGHT -> attackRight2;
            };
        }
    }

    @Override
    public String getAvailableAction(Entity user) {

        // Cannot pick up if hostile
        if (action == Action.ATTACKING) {
            return "";
        }

        return availableAction;
    }
}
