package entity.item;

import application.GamePanel;
import entity.Entity;

public class ITM_Cape extends Item {

    public static final String itmName = "Cape";

    public ITM_Cape(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.SOARING);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_cape");
    }

    @Override
    public void use() {
        super.use();
        user.setElevated(true);
    }
}