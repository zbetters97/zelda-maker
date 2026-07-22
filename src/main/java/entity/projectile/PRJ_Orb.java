package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.awt.*;
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

        if (capturedEntity != null) {
            handleCapture();
        }
        else {
            super.update();
        }

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

        // Iterate over entities that can be captured
        for (ArrayList<? extends Entity> targets : capturableEntities) {

            // Hit target, capture
            target = gp.cChecker.checkOverlapCollision(this, targets);
            if (target != null && target.getAlive()) {

                user.capture(target);
                capturedEntity = target;
                animationSpeed = 8;

                return;
            }
        }
    }

    @Override
    protected void handleCapture() {

        // Entity currently captured, follow entity
        if (capturedEntity.isCaptured()) {
            worldPoint.setLocation(new Point(capturedEntity.getWorldPoint()));
        }
        // Entity no longer captured, reset
        else {
            alive = false;
            resetValues();
        }
    }

    @Override
    public void resetValues() {
        super.resetValues();
        capturedEntity = null;
    }

    @Override
    public void draw(Graphics2D g2) {

        // Slightly transparent if attached to entity
        if (capturedEntity != null) {
            changeAlpha(g2, 0.6f);
        }

        super.draw(g2);
    }

    @Override
    protected void getSpriteImage() {
        image = spriteNum == 1 ? up1 : up2;
    }
}
