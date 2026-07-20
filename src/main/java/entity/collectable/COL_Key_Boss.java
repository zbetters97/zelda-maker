package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Key_Boss extends Collectable {

    public static final String colName = "Key_Boss";

    public COL_Key_Boss(GamePanel gp) {
        super(gp, colName);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_key_boss");
    }

    @Override
    public void use(Entity user) {
        user.setHasBossKey(true);
        alive = false;
    }
}
