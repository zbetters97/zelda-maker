package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Rupee_Green extends Collectable {

    public static final String colName = "Rupee_Green";

    public COL_Rupee_Green(GamePanel gp) {
        super(gp, colName);
        value = 1;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_rupee_green");
    }

    @Override
    public void use(Entity user) {
        gp.ui.setRupeeChange(value);
        alive = false;
    }
}
