package entity.object;

import application.GamePanel;
import entity.Entity;
import entity.collectable.*;
import entity.enemy.Enemy;

import java.awt.*;

public class OBJ_Rock extends Object {

    public static final String objName = "Rock";

    public OBJ_Rock(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);

        defaultAttack = 1;
        attack = defaultAttack;

        hitbox = new Rectangle(4, 16, 40, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        availableAction = "GRAB";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/objects/obj_rock");
    }

    @Override
    public boolean canCollideWith(Entity target) {
        return !target.getElevated();
    }

    @Override
    public void interact(Entity user) {
        if (gp.keyH.aPressed) {
            user.grab(this);
        }
    }

    @Override
    protected void landOnGround() {
        super.landOnGround();

        // Damage enemy if landed
        Enemy enemy = gp.cChecker.checkOverlapCollision(this, gp.enemies);
        if (enemy != null) enemy.takeDamage(this);

        // Damage object if landed
        Object object = gp.cChecker.checkOverlapCollision(this, gp.objects);
        if (object != null) object.takeDamage(this);

        // Damage player if landed
        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        if (contactPlayer) gp.player.takeDamage(this);

        shatter();
    }

    private void shatter() {
        alive = false;
        dropLoot();
        createParticles();
    }

    @Override
    protected void setLoot() {

        if (loot != null) return;

        int rand = (int) (Math.random() * 10);
        loot = switch (rand) {
            case 0, 1, 2, 3, 4 -> new COL_Rupee_Green(gp);
            case 5, 6, 7 -> new COL_Heart(gp);
            case 8, 9 -> new COL_Rupee_Blue(gp);
            default -> new COL_Rupee_Red(gp);
        };
    }

    @Override
    public boolean canTakeLoot(Entity loot) {
        return loot instanceof Collectable;
    }

    @Override
    protected void getSpriteImage() {
        image = sprite;
    }

    @Override
    protected int getParticleMaxHealth() {
        return 16;
    }
    @Override
    protected int getParticleSpeed() {
        return 1;
    }
    @Override
    protected int getParticleSize() {
        return 9;
    }
}
