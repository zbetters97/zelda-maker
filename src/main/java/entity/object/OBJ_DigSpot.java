package entity.object;

import application.GamePanel;
import entity.Entity;
import entity.collectable.COL_Heart;
import entity.collectable.COL_Rupee_Blue;
import entity.collectable.COL_Rupee_Green;
import entity.collectable.COL_Rupee_Red;

import java.awt.*;

public class OBJ_DigSpot extends Object {

    public static final String objName = "DigSpot";

    public OBJ_DigSpot(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
        interactable = false;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/objects/obj_digspot");
    }

    @Override
    public boolean canCollideWith(Entity target) {
        return target.getAction() == Action.DIGGING;
    }

    @Override
    public void interact(Entity user) {

        if (user.getAction() != Action.DIGGING) return;

        alive = false;
        dropLoot();
        createParticles();
    }

    protected void assignLoot() {

        int rand = (int) (Math.random() * 10);
        loot = switch (rand) {
            case 0, 1, 2, 3, 4 -> new COL_Rupee_Green(gp);
            case 5, 6, 7 -> new COL_Heart(gp);
            case 8, 9 -> new COL_Rupee_Blue(gp);
            default -> new COL_Rupee_Red(gp);
        };
    }

    @Override
    public boolean canHoldLoot(Entity loot) {
        return true;
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

    @Override
    public DrawLayer getDrawLayer() {
        return DrawLayer.GROUND;
    }
}
