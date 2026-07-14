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

        maxHealth = 240;
        health = maxHealth;

        defaultAttack = 6;
        attack = defaultAttack;
        knockbackPower = 2;
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
        user.setAction(Action.GRABBING);
        user.setGrabbedObject(this);
        resetValues();
    }

    @Override
    public void interact() {
        explode();
    }

    @Override
    protected void endThrow() {
        opened = true;
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
            if (spriteNum == 1) {
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
        alive = false;
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

        if (spriteNum == 2) {
           changeAlpha(g2, 0.5f);
        }

        super.draw(g2);

        changeAlpha(g2, 1f);
    }

    @Override
    protected void getSpriteImage() {
        image = sprite;
    }
}
