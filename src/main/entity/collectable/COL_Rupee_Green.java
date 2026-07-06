package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Rupee_Green extends Collectable {

    public static final String colName = "Green Rupee";

    public COL_Rupee_Green(GamePanel gp) {
        super(gp, colName);
    }

    @Override
    protected void getImages() {
        down1 = setupImage("/collectables/col_rupee_green");
    }

    @Override
    public void use(Entity user) {
        alive = false;
    }
}
