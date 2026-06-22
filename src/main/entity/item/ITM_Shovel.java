package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.Projectile;

public class ITM_Shovel extends Entity {

    public static final String itmName = "Wooden Shovel";

    public ITM_Shovel(GamePanel gp, Entity user) {
        super(gp);

        entity_type = type_item;
        name = itmName;
        this.user = user;
    }

    protected void getImages() {
        image = down1 = setupImage("/items/itm_shovel");
    }

    public void use() {
        if (user.action != Action.DIGGING) {
            user.action = Action.DIGGING;
        }
    }
}