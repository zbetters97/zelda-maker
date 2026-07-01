package entity.item;

import application.GamePanel;
import entity.Entity;

public class ITM_Cape extends Entity {

    public static final String itmName = "Cape";

    public ITM_Cape(GamePanel gp, Entity user) {
        super(gp, user, itmName);
    }

    @Override
    protected void getImages() {
        image = down1 = setupImage("/items/itm_cape");
    }

    @Override
    protected void use() {
        user.setAction(Action.SOARING);
        user.setElevated(true);
    }
}