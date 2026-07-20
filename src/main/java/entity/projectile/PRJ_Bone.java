package entity.projectile;

import application.GamePanel;

public class PRJ_Bone extends Projectile {

    public static final String prjName = "Bone Projectile";

    public PRJ_Bone(GamePanel gp) {
        super(gp, prjName);

        animationSpeed = 6;

        defaultSpeed = 6;
        speed = defaultSpeed;

        defaultAttack = 2;
        attack = defaultAttack;

        knockbackPower = 0;
    }

    @Override
    public void getImages() {
        sprite = up1 = setupImage("/projectiles/bone_down_1");
        up2 = setupImage("/projectiles/bone_down_2");
    }

    @Override
    public void update() {
        super.update();

        cycleSprites();
    }

    @Override
    protected void getSpriteImage() {
        image = spriteNum == 1 ? up1 : up2;
    }
}
