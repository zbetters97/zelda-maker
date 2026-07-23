package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Rupee_Green extends Collectable {

    public static final String colName = "Rupee_Green";

    public COL_Rupee_Green(GamePanel gp) {
        super(gp, colName);
        formattedName = "a green rupee";
        description = "Not too shabby!";
        value = 1;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_rupee_green", 38, 38);
    }

    @Override
    public void use(Entity user) {
        gp.ui.addRupees(value);
        alive = false;
    }
}
