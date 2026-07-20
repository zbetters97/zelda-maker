package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;

public class PRJ_Orb extends Projectile {

    public static final String prjName = "Orb Projectile";
    private final ArrayList<ArrayList<? extends Entity>> capturableEntities = new ArrayList<>(Arrays.asList(gp.enemies, gp.objects, gp.collectables));

    public PRJ_Orb(GamePanel gp, Entity user) {
        super(gp, prjName);

        animationSpeed = 4;

        maxHealth = 40;
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
        sprite = up1 = setupImage("/projectiles/orb_down_1");
        up2 = setupImage("/projectiles/orb_down_2");
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

        Entity target;

        for (ArrayList<? extends Entity> targets : capturableEntities) {

            target = gp.cChecker.checkOverlapCollision(this, targets);
            if (target != null && target.getAlive()) {

                user.capture(target);

                collisionOn = true;
                return;
            }
        }
    }

    @Override
    protected void getSpriteImage() {
        image = spriteNum == 1 ? up1 : up2;
    }
}
