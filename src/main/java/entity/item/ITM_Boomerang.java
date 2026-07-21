package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Boomerang;

public class ITM_Boomerang extends Item {

    public static final String itmName = "Boomerang";

    public ITM_Boomerang(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.THROWING);
        projectile = new PRJ_Boomerang(gp);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_boomerang");
    }

    @Override
    public void use() {

        if (!projectile.getAlive()) {
            projectile.set(user.getWorldPoint(), user.getDirection(), true, user);
            gp.projectiles.add(projectile);

            super.use();
        }
    }
}