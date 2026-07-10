package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Boomerang;

public class ITM_Boomerang extends Item {

    public static final String itmName = "Hylian Boomerang";

    public ITM_Boomerang(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.THROWING);
        projectile = new PRJ_Boomerang(gp);
    }

    @Override
    protected void getImages() {
        image = setupImage("/items/itm_boomerang");
    }

    @Override
    protected void use() {

        if (!projectile.getAlive()) {
            projectile.set(user.getWorldPoint(), user.getDirection(), true, user);
            addProjectile(projectile);

            super.use();
        }
    }
}