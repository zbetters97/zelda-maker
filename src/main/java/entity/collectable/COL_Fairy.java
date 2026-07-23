package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Fairy extends Collectable {

    public static final String colName = "Fairy";

    public COL_Fairy(GamePanel gp) {
        super(gp, colName);
        formattedName = "a fairy";
        description = "You regained all your health!";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_fairy", 38, 38);
    }

    @Override
    public void use(Entity user) {
        user.setHealth(user.getMaxHealth());
        user.setAlive(true);
        alive = false;
    }
}
