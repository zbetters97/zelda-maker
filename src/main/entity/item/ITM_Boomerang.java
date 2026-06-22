package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Boomerang;

public class ITM_Boomerang extends Entity {

    public static final String itmName = "Hylian Boomerang";

    public ITM_Boomerang(GamePanel gp, Entity user) {
        super(gp);

        type = type_item;
        name = itmName;
        this.user = user;

        projectile = new PRJ_Boomerang(gp);
    }

    public void getImages() {
        image = down1 = setupImage("/items/itm_boomerang");
    }

    public void use() {

        if (!projectile.alive) {
            projectile.set(user.worldX, user.worldY, user.direction, true, user);
            addProjectile(projectile);

            user.action = Action.THROWING;
        }
    }

}