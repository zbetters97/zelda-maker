package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Orb;

public class ITM_Rod extends Item {

    public static final String itmName = "Item Rod";

    public ITM_Rod(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.SWINGING);
        projectile = new PRJ_Orb(gp, user);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_rod");
    }

    @Override
    public void use() {

        if (user.getCapturedTarget() != null) {
            user.getCapturedTarget().setCaptured(false);
            user.setCapturedTarget(null);
            super.use();

            return;
        }

        if (!projectile.getAlive()) {
            projectile.set(user.getWorldPoint(), user.getDirection(), true, user);
            addProjectile(projectile);

            super.use();
        }
    }
}