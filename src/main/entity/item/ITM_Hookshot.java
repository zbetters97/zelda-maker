package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Claw;

public class ITM_Hookshot extends Entity {

    public static final String itmName = "Hookshot";

    public ITM_Hookshot(GamePanel gp, Entity user) {
        super(gp);

        entity_type = type_item;
        name = itmName;
        this.user = user;

        projectile = new PRJ_Claw(gp, user);
    }

    public void getImages() {
        image = down1 = setupImage("/items/itm_hookshot");
    }

    public void use() {

        if (!projectile.alive) {
            projectile.set(user.getWorldX(), user.getWorldY(), user.getDirection(), true, user);
            addProjectile(projectile);

            user.setAction(Action.THROWING);
        }
    }
}