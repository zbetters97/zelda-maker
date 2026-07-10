package entity.projectile;

import application.GamePanel;

public class PRJ_Magic extends Projectile {

    public static final String prjName = "Magic Projectile";

    public PRJ_Magic(GamePanel gp) {
        super(gp, prjName);

        maxHealth = 120;
        health = maxHealth;

        defaultSpeed = 8;
        speed = defaultSpeed;

        defaultAttack = 2;
        attack = defaultAttack;

        knockbackPower = 0;
    }

    @Override
    public void getImages() {
        up1 = setupImage("/projectiles/magic_up_1");
        down1 = setupImage("/projectiles/magic_down_1");
        left1 = setupImage("/projectiles/magic_left_1");
        right1 = setupImage("/projectiles/magic_right_1");
    }

    @Override
    protected boolean canBeDeflected(boolean usingShield) {

        // Can only be deflected with a sword
        return !usingShield;
    }
}
