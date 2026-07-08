package entity.projectile;

import application.GamePanel;

public class PRJ_Seed extends Projectile {

    public static final String prjName = "Seed Projectile";

    public PRJ_Seed(GamePanel gp) {
        super(gp, prjName);

        defaultSpeed = 7;
        speed = defaultSpeed;

        defaultAttack = 2;
        attack = defaultAttack;

        maxHealth = 45;
        health = maxHealth;
    }

    @Override
    public void getImages() {
        up1 = setupImage("/projectiles/seed_down_1", 35, 35);
    }

    @Override
    protected boolean canBeDeflected(boolean usingShield) {

        // Can be deflected with sword or shield
        return true;
    }

    @Override
    protected void getSpriteImage() {
        image = up1;
    }
}
