package entity.projectile;

import application.GamePanel;

public class PRJ_Fireball extends Projectile {

    public static final String prjName = "Fireball Projectile";

    public PRJ_Fireball(GamePanel gp) {
        super(gp, prjName);

        defaultSpeed = 7;
        speed = defaultSpeed;

        defaultAttack = 2;
        attack = defaultAttack;

        maxHealth = 60;
        health = maxHealth;
    }

    @Override
    public void getImages() {
        up1 = setupImage("/projectiles/fireball_down_1", 35, 35);
        up2 = setupImage("/projectiles/fireball_down_2", 35, 35);
    }

    @Override
    protected boolean canBeDeflected(boolean usingShield) {

        // Can only be deflected with a shield
        return usingShield;
    }

    @Override
    protected void getSpriteImage() {
        if (spriteNum == 1) {
            image = up1;
        }
        else {
            image = up2;
        }
    }
}
