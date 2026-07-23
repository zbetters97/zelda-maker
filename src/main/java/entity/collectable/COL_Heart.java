package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Heart extends Collectable {

    public static final String colName = "Heart";

    public COL_Heart(GamePanel gp) {
        super(gp, colName);
        formattedName = "a heart";
        description = "You feel just a little better!";
        value = 1;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_heart", 38, 38);
    }

    @Override
    public void use(Entity user) {
        user.addHealth(value);
        alive = false;
    }
}
