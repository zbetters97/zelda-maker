package entity.item;

import application.GamePanel;
import entity.Entity;

public class ITM_Shovel extends Entity {

    public static final String itmName = "Wooden Shovel";

    public ITM_Shovel(GamePanel gp) {
        super(gp);

        entity_type = type_item;
        name = itmName;
    }

    protected void getImages() {
        image = down1 = setupImage("/items/itm_shovel");
    }

    public boolean use(Entity user) {
        if (user.action != Action.DIGGING) {
            user.action = Action.DIGGING;
        }

        return true;
    }
}