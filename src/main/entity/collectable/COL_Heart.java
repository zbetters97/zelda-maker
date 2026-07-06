package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Heart extends Collectable {

    public static final String colName = "Heart";

    public COL_Heart(GamePanel gp) {
        super(gp, colName);
        value = 1;
    }

    @Override
    protected void getImages() {
        down1 = setupImage("/collectables/col_heart");
    }

    @Override
    public void use(Entity user) {
        user.addHealth(value);
        alive = false;
    }
}
