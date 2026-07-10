package entity.item;

import application.GamePanel;
import entity.Entity;

public class ITM_Feather extends Item {

    public static final String itmName = "Feather";

    public ITM_Feather(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.JUMPING);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_feather");
    }

    @Override
    protected void use() {
        super.use();
        user.setElevated(true);
    }
}