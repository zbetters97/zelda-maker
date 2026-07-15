package entity.object;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class OBJ_Bomb extends Object {

    public static final String objName = "Bomb";

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

        hitbox = new Rectangle(12, 15, 24, 27);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        availableAction = "GRAB";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/objects/obj_bomb");
    }

    @Override
    public void update() {
        super.update();

        if (opened) {
            lightFuse();
        }
    }

    @Override
    public void interact(Entity user) {

        if (!gp.keyH.aPressed) return;

        user.setAction(Action.GRABBING);
        user.setGrabbedObject(this);
        resetValues();
        opened = true;
    }

    @Override
    public void interact() {
        explode();
    }

    private void lightFuse() {

        cycleSprites();

        health--;
        if (health <= 0) {
            explode();
        }
    }

    @Override
    protected void cycleSprites() {

        if (animationSpeed < ++spriteCounter) {
            if (spriteNum == 1 && health < 240) {
                spriteNum = 2;
            }
            else {
                spriteNum = 1;
            }

            spriteCounter = 0;
            animationSpeed -= 3;
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
        opened = false;
    }

    @Override
    public void draw(Graphics2D g2) {

        if (!drawing) return;

        if (spriteNum == 2) {
           changeAlpha(g2, 0.5f);
        }

        super.draw(g2);

        changeAlpha(g2, 1f);
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
        image = sprite;
    }
}
