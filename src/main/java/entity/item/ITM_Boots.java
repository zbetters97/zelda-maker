package entity.item;

import application.GamePanel;
import entity.Entity;

public class ITM_Boots extends Item {

    public static final String itmName = "Boots";

    public ITM_Boots(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.RUNNING);
        formattedName = "the Pegasus Boots";
        description = "Press and hold X to run fast!\nIt may feel slippery!";
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