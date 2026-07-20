package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Key extends Collectable {

    public static final String colName = "Key";

    public COL_Key(GamePanel gp) {
        super(gp, colName);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_key");
    }

    @Override
    public void use(Entity user) {
        user.addKeys(1);
        alive = false;
    }
}
