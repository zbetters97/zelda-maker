package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Rupee_Blue extends Collectable {

    public static final String colName = "Rupee_Blue";

    public COL_Rupee_Blue(GamePanel gp) {
        super(gp, colName);
        value = 5;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_rupee_blue", 38, 38);
    }

    @Override
    public void use(Entity user) {
        gp.ui.addRupees(value);
        alive = false;
    }
}
