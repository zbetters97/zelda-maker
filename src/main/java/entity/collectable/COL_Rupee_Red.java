package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Rupee_Red extends Collectable {

    public static final String colName = "Red Rupee";

    public COL_Rupee_Red(GamePanel gp) {
        super(gp, colName);
        value = 10;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_rupee_red");
    }

    @Override
    public void use(Entity user) {
        gp.ui.setRupeeChange(value);
        alive = false;
    }
}
