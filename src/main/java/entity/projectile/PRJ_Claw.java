package entity.projectile;

import application.GamePanel;
import entity.Entity;
import entity.object.OBJ_Cucco;
import entity.object.Object;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PRJ_Claw extends Projectile {

    public static final String prjName = "Claw Projectile";

    private Entity grabbedEntity;
    private boolean returning = false, latched = false;
    private BufferedImage grabUp1, grabDown1, grabLeft1, grabRight1, chainHor, chainVer;

    public PRJ_Claw(GamePanel gp, Entity user) {
        super(gp, prjName);

        maxHealth = 30;
        health = maxHealth;

        defaultSpeed = 10;
        speed = defaultSpeed;

        this.user = user;

        getClawImages();
    }

    @Override
    public void getImages() {
        up1 = setupImage("/projectiles/hookshot_up_1");
        sprite = down1 = setupImage("/projectiles/hookshot_down_1");
        left1 = setupImage("/projectiles/hookshot_left_1");
        right1 = setupImage("/projectiles/hookshot_right_1");
    }

    private void getClawImages() {
        grabUp1 = setupImage("/projectiles/hookshot_grab_up_1");
        grabDown1 = setupImage("/projectiles/hookshot_grab_down_1");
        grabLeft1 = setupImage("/projectiles/hookshot_grab_left_1");
        grabRight1 = setupImage("/projectiles/hookshot_grab_right_1");

        chainHor = setupImage("/projectiles/chain_hor");
        chainVer = setupImage("/projectiles/chain_ver");
    }

    @Override
    public void update() {

        if (returning) {
            if (latched) {
                moveUser();
            }
            else {
                returnToUser();
            }
        }
        else {
            moveInDirection(direction);
            health--;

            checkCollision();
            if (health <= 0 || collisionOn) {
                returning = true;
            }
        }

        checkDeath();
    }

    @Override
    public void checkCollision() {

        collisionOn = false;

        checkGrabbableCollision();
        checkLatchableCollision();
        checkObstacleCollision();
    }
    private void checkGrabbableCollision() {

        Entity target = checkGrabbableEntity();
        if (target != null) {

            collisionOn = true;

            // Enemy shocks user, don't pull
            if (target.getBuzzing()) {
                user.takeDamage(target);
            }
            else {
                grabbedEntity = target;
            }
        }
    }
    private Entity checkGrabbableEntity() {

        Entity target = gp.cChecker.checkOverlapCollision(this, gp.enemies);
        if (target == null) {
            target = gp.cChecker.checkOverlapCollision(this, gp.collectables);
        }

        return target;
    }

    private void checkLatchableCollision() {

        Object object = gp.cChecker.checkMovementCollision(this, gp.objects);
        if (object != null && object.isLatchable()) {
            grabbedEntity = object;
            returning = true;

            // Pull Cucco towards self, otherwise pull towards object
            boolean isCucco = object.getName().equals(OBJ_Cucco.objName);
            if (!isCucco) latched = true;
        }
    }
    private void checkObstacleCollision() {
        gp.cChecker.checkTile(this);
        gp.cChecker.checkMovementCollision(this, gp.npcs);
        checkObjectCollision();
    }

    private void moveUser() {

        if (user.getCollision()) {
            user.setElevated(false);
            alive = false;
            return;
        }

        user.setElevated(true);
        
        Point stopPoint = new Point(grabbedEntity.getWorldPoint().x + gp.tileSize, grabbedEntity.getWorldPoint().y + gp.tileSize);
        Point userPoint = new Point(user.getWorldPoint().x, user.getWorldPoint().y);

        // Move user towards latched
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                if (userPoint.y > stopPoint.y) {
                    user.setWorldPointY(userPoint.y - 5);
                }
                else {
                    alive = false;
                }
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                if (userPoint.y < stopPoint.y) {
                    user.setWorldPointY(userPoint.y + 5);
                }
                else {
                    alive = false;
                }
            }
            case LEFT -> {
                if (userPoint.x > stopPoint.x) {
                    user.setWorldPointX(userPoint.x - 5);
                }
                else {
                    alive = false;
                }
            }
            case RIGHT -> {
                if (userPoint.x < stopPoint.x) {
                    user.setWorldPointX(userPoint.x + 5);
                }
                else {
                    alive = false;
                }
            }
        }
    }

    @Override
    protected void returnToUser() {
        super.returnToUser();

        if (grabbedEntity != null) {
            pullEntity(grabbedEntity);
        }
    }

    @Override
    protected void checkDeath() {
        if (!alive || (user != null && !user.isAvailable())) {
            resetValues();
        }
    }

    @Override
    public void resetValues() {
        super.resetValues();

        latched = false;
        returning = false;
        health = maxHealth;
        user.resetCounters();
        user.setElevated(false);
        user.setAction(Action.IDLE);

        if (grabbedEntity != null) {
            grabbedEntity.setElevated(false);
            grabbedEntity.setCanMove(true);
            grabbedEntity = null;
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        if (!drawing) return;

        drawChain(g2);
        super.draw(g2);
    }

    private void drawChain(Graphics2D g2) {

        Point startPoint = new Point(user.getScreenPoint().x + gp.tileSize / 2, user.getScreenPoint().y + gp.tileSize / 2);
        Point endPoint = new Point(getScreenPoint().x + gp.tileSize / 2, getScreenPoint().y + gp.tileSize / 2);

        switch (direction) {
            case UP, UPLEFT, UPRIGHT, DOWN, DOWNLEFT, DOWNRIGHT -> {
                int step = chainVer.getHeight();
                for (int y = startPoint.y; Math.abs(y - endPoint.y) > step;) {
                    y += (endPoint.y > y) ? step : -step;
                    g2.drawImage(chainVer, startPoint.x - chainVer.getWidth() / 2, y - chainVer.getHeight() / 2, null);
                }
            }
            case LEFT, RIGHT -> {
                int step = chainHor.getWidth();
                for (int x = startPoint.x; Math.abs(x - endPoint.x) > step;) {
                    x += (endPoint.x > x) ? step : -step;
                    g2.drawImage(chainHor, x - chainHor.getWidth() / 2, startPoint.y - chainHor.getHeight() / 2, null);
                }
            }
        }
    }

    @Override
    protected void getSpriteImage() {
        if (grabbedEntity != null) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> grabUp1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> grabDown1;
                case LEFT -> grabLeft1;
                case RIGHT -> grabRight1;
            };
        }
        else {
            super.getSpriteImage();
        }
    }
}
