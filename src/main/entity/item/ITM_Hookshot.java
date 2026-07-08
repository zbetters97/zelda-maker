package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Claw;

public class ITM_Hookshot extends Entity {

    public static final String itmName = "Hookshot";

    public ITM_Hookshot(GamePanel gp, Entity user) {
        super(gp, user, itmName);
        projectile = new PRJ_Claw(gp, user);
    }

    @Override
    protected void getImages() {
        image = down1 = setupImage("/items/itm_hookshot");
    }

    @Override
    protected void use() {

        if (!projectile.getAlive()) {
            projectile.set(user.getWorldPoint(), user.getDirection(), true, user);
            addProjectile(projectile);

            user.setAction(Action.THROWING);
        }
    }
}