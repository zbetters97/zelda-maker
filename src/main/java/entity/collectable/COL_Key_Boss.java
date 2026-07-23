package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Key_Boss extends Collectable {

    public static final String colName = "Key_Boss";

    public COL_Key_Boss(GamePanel gp) {
        super(gp, colName);
        formattedName = "a Boss Key";
        description = "Use this on any boss door!\nOne time only.";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_key_boss", 38, 38);
    }

    @Override
    public void use(Entity user) {
        user.setHasBossKey(true);
        alive = false;
    }
}
