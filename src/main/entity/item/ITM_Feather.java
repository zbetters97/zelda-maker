package entity.item;

import application.GamePanel;
import entity.Entity;

public class ITM_Feather extends Entity {

    public static final String itmName = "Feather";

    public ITM_Feather(GamePanel gp, Entity user) {
        super(gp, user, itmName);
    }

    @Override
    protected void getImages() {
        image = down1 = setupImage("/items/itm_feather");
    }

    @Override
    protected void use() {
        user.setAction(Action.JUMPING);
        user.setElevated(true);
    }
}