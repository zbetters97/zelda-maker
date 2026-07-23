package entity.object;

import application.GamePanel;
import entity.Entity;
import entity.collectable.*;

import java.awt.*;

public class OBJ_Grass extends Object {

    public static final String objName = "Grass";

    public OBJ_Grass(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/objects/obj_grass");
    }

    @Override
    public void interact() {
        shatter();
    }

    @Override
    public boolean canCollideWith(Entity target) {
        return false;
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
    protected Color getParticleColor() {
        return new Color(30, 150, 23);
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
