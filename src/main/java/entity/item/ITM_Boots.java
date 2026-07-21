package entity.item;

import application.GamePanel;
import entity.Entity;

public class ITM_Boots extends Item {

    public static final String itmName = "Boots";

    public ITM_Boots(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.RUNNING);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_boots");
    }

    @Override
    public void use() {
        super.use();
    }
}