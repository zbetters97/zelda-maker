package entity.object;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class OBJ_Bomb extends Object {

    public static final String objName = "Bomb";

    private boolean lit = false;

    public OBJ_Bomb(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);

        animationSpeed = 30;

        defaultSpeed = gp.tileSize / 2;
        speed = defaultSpeed;

        maxHealth = 300;
        health = maxHealth;
        opened = true;

        defaultAttack = 6;
        attack = defaultAttack;
        knockbackPower = 2;

        hitbox = new Rectangle(6, 6, 36, 36);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        availableAction = "GRAB";
    }

    @Override
    protected void getImages() {
        sprite = up1 = setupImage("/objects/obj_bomb_off");
        up2 = setupImage("/objects/obj_bomb_on");
    }

    @Override
    public void update() {
        super.update();

        if (lit) {
            lightFuse();
        }
    }

    @Override
    public void interact(Entity user) {

        if (!gp.keyH.aPressed) return;

        user.setAction(Action.GRABBING);
        user.setGrabbedObject(this);
        resetValues();

        // Light the fuse
        lit = true;
    }

    @Override
    public void interact() {

        // Explode if hit by entity
        explode();
    }

    private void lightFuse() {

        cycleSprites();

        // Timer ran out, explode
        if (--health <= 0) {
            explode();
        }
    }

    @Override
    protected void cycleSprites() {

        if (animationSpeed < ++spriteCounter) {

            // Don't start ticking animation until 240 frames left
            if (spriteNum == 1 && health < 240) {
                spriteNum = 2;
            }
            else {
                spriteNum = 1;
            }

            // Speed up animation speed
            animationSpeed -= 3;
            spriteCounter = 0;
        }
    }

    private void explode() {
        createParticles();
        alive = false;
    }

    @Override
    public boolean canCollideWith(Entity target) {
        return false;
    }

    @Override
    public void resetValues() {
        animationSpeed = 30;
        spriteCounter = 0;
        spriteNum = 1;
        health = maxHealth;
        lit = false;
        interactable = true;
    }

    @Override
    protected int getParticleSize() {
        return 7;
    }
    @Override
    protected int getParticleSpeed() {
        return 1;
    }
    @Override
    protected int getParticleMaxHealth() {
        return 20;
    }

    @Override
    protected void getSpriteImage() {
        if (spriteNum == 1) {
            image = up1;
        }
        else {
            image = up2;
        }
    }
}
