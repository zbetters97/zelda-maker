package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Rupee_Blue extends Collectable {

    public static final String colName = "Blue Rupee";

    public COL_Rupee_Blue(GamePanel gp) {
        super(gp, colName);
        value = 5;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_rupee_blue");
    }

    @Override
    public void use(Entity user) {
        gp.ui.setRupeeChange(value);
        alive = false;
    }
}
