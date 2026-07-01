package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Arrow;

public class ITM_Bow extends Entity {

    public static final String itmName = "Hylian Bow";

    public ITM_Bow(GamePanel gp, Entity user) {
        super(gp, user, itmName);
    }

    @Override
    protected void getImages() {
        image = down1 = setupImage("/items/itm_bow");
    }

    @Override
    protected void use() {
        if (user.arrows > 0) {
            if (120 > user.charge) {
                user.charge++;
            }

            user.setAction(Action.AIMING);
        }
    }

    @Override
    protected void attack() {

        projectile = new PRJ_Arrow(gp);

        setPower();

        projectile.set(user.getWorldPoint(), user.getDirection(), true, user);
        addProjectile(projectile);

        user.arrows--;
        user.charge = 0;
        user.setAction(Action.IDLE);
    }

    private void setPower() {
        if (40 <= user.charge && user.charge < 80) {
            projectile.modifySpeed(3);
            projectile.modifyAttack(1);
        }
        else if (80 <= user.charge && user.charge < 120) {
            projectile.modifySpeed(5);
            projectile.modifyAttack(2);
        }
        else if (120 <= user.charge) {
            projectile.modifySpeed(7);
            projectile.modifyAttack(3);
        }
    }
}