package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Arrow extends Collectable {

    public static final String colName = "Arrow";

    public COL_Arrow(GamePanel gp) {
        super(gp, colName);
        value = 1;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_arrow", 38, 38);
    }

    @Override
    public void use(Entity user) {
        user.addArrows(value);
        alive = false;
    }
}
