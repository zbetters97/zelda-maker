package entity.collectable;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class COL_Rupee_Red extends Collectable {

    public static final String colName = "Rupee_Red";

    public COL_Rupee_Red(GamePanel gp) {
        super(gp, colName);
        value = 10;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_rupee_red", 38, 38);
    }

    @Override
    public void use(Entity user) {
        gp.ui.addRupees(value);
        alive = false;
    }
}
