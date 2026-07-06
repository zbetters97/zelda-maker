package entity.object;

import application.GamePanel;
import entity.collectable.COL_Rupee_Green;

public class OBJ_Pot extends Object {

    public static final String objName = "Pot";

    public OBJ_Pot(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
    }

    @Override
    protected void getImages() {
        down1 = setupImage("/objects/obj_pot");
    }

    @Override
    public void interact() {
        alive = false;
        dropItem(new COL_Rupee_Green(gp));
    }
}
