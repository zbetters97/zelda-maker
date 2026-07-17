package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;

public class PRJ_Orb extends Projectile {

    public static final String prjName = "Orb Projectile";
    private final ArrayList<Entity[]> capturableEntities = new ArrayList<>(Arrays.asList(gp.enemy, gp.obj, gp.col));

    public PRJ_Orb(GamePanel gp, Entity user) {
        super(gp, prjName);

        animationSpeed = 4;

        maxHealth = 60;
        health = maxHealth;

        defaultSpeed = 5;
        speed = defaultSpeed;

        defaultAttack = 0;
        attack = defaultAttack;

        knockbackPower = 0;

        this.user = user;
    }

    @Override
    public void getImages() {
        sprite = down1 = setupImage("/projectiles/orb_down_1");
        down2 = setupImage("/projectiles/orb_down_2");
    }

    @Override
    public void update() {
        super.update();
        cycleSprites();
    }

    @Override
    public void checkCollision() {

        collisionOn = false;

        gp.cChecker.checkTile(this);
        checkCapturableCollision();
    }

    private void checkCapturableCollision() {

        int trgIndex;
        Entity target;

        for (Entity[] targets : capturableEntities) {

            trgIndex = gp.cChecker.checkOverlapCollision(this, targets);
            if (trgIndex == -1) continue;

            target = targets[trgIndex];
            if (target != null && target.getAlive()) {

                target.setCaptured(true);
                user.setCapturedTarget(target);

                collisionOn = true;
                return;
            }
        }
    }

    @Override
    protected void getSpriteImage() {
        image = spriteNum == 1 ? down1 : down2;
    }
}
