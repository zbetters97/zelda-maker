package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Arrow;

public class ITM_Bow extends Entity {

    public static final String itmName = "Hylian Bow";

    public ITM_Bow(GamePanel gp) {
        super(gp);

        type = type_item;
        name = itmName;
    }

    public void getImages() {
        image = down1 = setupImage("/items/itm_bow");
    }

    public boolean setCharge(Entity user) {
        if (user.arrows > 0) {

            if (user.charge < 120) {
                user.charge++;
            }

            user.action = Action.AIMING;

            return true;
        }
        else {
            return false;
        }
    }

    public boolean use(Entity user) {

        if (user.arrows > 0) {
            projectile = new PRJ_Arrow(gp);

            if (80 > user.charge && user.charge >= 40) {
                projectile.speed += 3;
                projectile.attack++;
            }
            else if (120 > user.charge && user.charge >= 80) {
                projectile.speed += 4;
                projectile.attack += 2;
            }
            else if (user.charge >= 120) {
                projectile.speed += 5;
                projectile.attack += 3;
            }

            projectile.set(user.worldX, user.worldY, user.direction, true, user);
            projectile.addProjectile(projectile);

            if (user.arrows > 0) {
                user.arrows--;
            }

            user.action = Action.IDLE;
            user.charge = 0;
        }

        return true;
    }
}