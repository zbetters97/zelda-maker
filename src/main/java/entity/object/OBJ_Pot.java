package entity.object;

import application.GamePanel;
import entity.Entity;
import entity.collectable.*;
import entity.enemy.Enemy;

import java.awt.*;

public class OBJ_Pot extends Object {

    public static final String objName = "Pot";

    public OBJ_Pot(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
        setLoot();

        hitbox = new Rectangle(4, 16, 40, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        availableAction = "GRAB";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/objects/obj_pot");
    }

    @Override
    public boolean canCollideWith(Entity target) {
        return !target.getElevated();
    }

    private void setLoot() {
        int rand = (int) (Math.random() * 10);
        loot = switch (rand) {
            case 0, 1, 2, 3, 4 -> new COL_Rupee_Green(gp);
            case 5, 6, 7 -> new COL_Heart(gp);
            case 8, 9 -> new COL_Rupee_Blue(gp);
            default -> new COL_Rupee_Red(gp);
        };
    }

    @Override
    public void interact(Entity user) {

        if (!gp.keyH.aPressed) return;

        user.setAction(Action.GRABBING);
        user.setGrabbedObject(this);
    }

    @Override
    public void interact() {
        alive = false;
        super.landOnGround();
        dropItem(loot);
    }

    @Override
    protected void landOnGround() {

        Enemy enemy = overlapEnemy(this);
        if (enemy != null) {
            enemy.takeDamage(this);
        }

        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        if (contactPlayer) {
            gp.player.takeDamage(this);
        }

        alive = false;
        dropItem(loot);
        createParticles();
    }

    @Override
    protected void getSpriteImage() {
        image = sprite;
    }

    @Override
    protected Color getParticleColor() {
        return new Color(150, 83, 23);
    }
    @Override
    protected int getParticleSize() {
        return 9;
    }
    @Override
    protected int getParticleSpeed() {
        return 1;
    }
    @Override
    protected int getParticleMaxHealth() {
        return 16;
    }
}
