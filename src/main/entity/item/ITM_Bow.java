package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Arrow;

public class ITM_Bow extends Entity {

    public static final String itmName = "Hylian Bow";

    public ITM_Bow(GamePanel gp, Entity user) {
        super(gp);

        type = type_item;
        name = itmName;
        this.user = user;
    }

    public void getImages() {
        image = down1 = setupImage("/items/itm_bow");
    }

    protected void use() {
        if (user.arrows > 0) {
            if (120 > user.charge) {
                user.charge++;
            }

            user.setAction(Action.AIMING);
        }
    }

    protected void attack() {

        projectile = new PRJ_Arrow(gp);

        setPower();

        projectile.set(user.getWorldX(), user.getWorldY(), user.getDirection(), true, user);
        addProjectile(projectile);

        user.arrows--;
        user.charge = 0;
        user.setAction(Action.IDLE);
    }

    private void setPower() {
        if (40 <= user.charge && user.charge < 80) {
            projectile.setSpeed(projectile.getSpeed() + 3);
            projectile.attack++;
        }
        else if (80 <= user.charge && user.charge < 120) {
            projectile.setSpeed(projectile.getSpeed() + 5);
            projectile.attack += 2;
        }
        else if (120 <= user.charge) {
            projectile.setSpeed(projectile.getSpeed() + 7);
            projectile.attack += 3;
        }
    }
}