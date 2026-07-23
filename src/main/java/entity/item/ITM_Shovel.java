package entity.item;

import application.GamePanel;
import entity.Entity;

public class ITM_Shovel extends Item {

    public static final String itmName = "Shovel";

    public ITM_Shovel(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.DIGGING);
        formattedName = "the Hylian Shovel";
        description = "Press X to dig for buried tressure!\nYou never know what you might find!";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_shovel");
    }
}