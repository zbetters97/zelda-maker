package entity.item;

import application.GamePanel;
import entity.Entity;

public class ITM_Shovel extends Entity {

    public static final String itmName = "Wooden Shovel";

    public ITM_Shovel(GamePanel gp, Entity user) {
        super(gp, user, itmName);
    }

    @Override
    protected void getImages() {
        image = down1 = setupImage("/items/itm_shovel");
    }

    @Override
    protected void use() {
        user.setAction(Action.DIGGING);
    }
}