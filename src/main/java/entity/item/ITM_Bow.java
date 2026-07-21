package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.projectile.PRJ_Arrow;

public class ITM_Bow extends Item {

    public static final String itmName = "Bow";

    public ITM_Bow(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.AIMING);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_bow");
    }

    @Override
    public void use() {
        if (user.getArrows() > 0) {
            if (120 > user.charge) {
                user.charge++;
            }

            super.use();
        }
    }

    @Override
    protected void attack() {

        projectile = new PRJ_Arrow(gp);

        setPower();

        projectile.set(user.getWorldPoint(), user.getDirection(), true, user);
        gp.projectiles.add(projectile);

        user.addArrows(-1);
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