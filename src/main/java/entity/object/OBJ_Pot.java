package entity.object;

import application.GamePanel;
import entity.Entity;
import entity.collectable.*;

public class OBJ_Pot extends Object {

    public static final String objName = "Pot";

    public OBJ_Pot(GamePanel gp, int worldX, int worldY, Collectable loot) {
        super(gp, worldX, worldY, objName);
        this.loot = loot;
    }
    public OBJ_Pot(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
        setLoot();
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
    public void interact() {
        alive = false;
        dropItem(loot);
    }

    @Override
    protected void getSpriteImage() {
        image = sprite;
    }
}
