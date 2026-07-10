package entity.projectile;

import application.GamePanel;

public class PRJ_Beam extends Projectile {

    public static final String prjName = "Beam Projectile";

    public PRJ_Beam(GamePanel gp) {
        super(gp, prjName);

        maxHealth = 120;
        health = maxHealth;

        defaultSpeed = 12;
        speed = defaultSpeed;

        defaultAttack = 2;
        attack = defaultAttack;
    }

    @Override
    public void getImages() {
        up1 = setupImage("/projectiles/beam_down_1");
        left1 = setupImage("/projectiles/beam_left_1");
    }

    @Override
    protected void getSpriteImage() {
        image = switch (direction) {
            case UP, UPLEFT, UPRIGHT, DOWN, DOWNLEFT, DOWNRIGHT -> up1;
            case LEFT, RIGHT -> left1;
        };
    }
}
