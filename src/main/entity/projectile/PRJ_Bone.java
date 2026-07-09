package entity.projectile;

import application.GamePanel;

public class PRJ_Bone extends Projectile {

    public static final String prjName = "Bone Projectile";

    public PRJ_Bone(GamePanel gp) {
        super(gp, prjName);

        animationSpeed = 6;

        maxHealth = 45;
        health = maxHealth;

        defaultSpeed = 6;
        speed = defaultSpeed;

        defaultAttack = 2;
        attack = defaultAttack;

        knockbackPower = 0;
    }

    @Override
    public void getImages() {
        up1 = setupImage("/projectiles/bone_down_1");
        left1 = setupImage("/projectiles/bone_down_2");
    }

    @Override
    protected void getSpriteImage() {
        image = switch (direction) {
            case UP, UPLEFT, UPRIGHT, DOWN, DOWNLEFT, DOWNRIGHT -> up1;
            case LEFT, RIGHT -> left1;
        };
    }
}
