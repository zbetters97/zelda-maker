package entity.object;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

import static application.GamePanel.Direction.*;
import static application.GamePanel.Direction.RIGHT;
import static entity.Entity.Action.THROWING;

public class Object extends Entity {

    protected boolean grabbed = false;
    protected boolean tossed = false;
    private int tossCounter;
    private double tTime = 0;
    private int tWorldY = 0;
    private double xT = 0;
    private double yT = 0;

    public Object(GamePanel gp, int worldX, int worldY, String objName) {
        super(gp, worldX, worldY, objName);

        hitbox = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    @Override
    public void update() {
        if (tossed) {
            handleToss();
        }
    }

    public void toss(Entity user) {

        tossed = true;
        grabbed = false;
        canMove = false;
        collisionOn = false;
        tWorldY = user.getWorldPoint().y;

        worldPoint.setLocation(user.getWorldPoint());

        direction = switch (user.getDirection()) {
            case UP, UPLEFT, UPRIGHT -> {
                worldPoint.y -= user.getSprite().getHeight() + 6;
                yield UP;
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                worldPoint.y += user.getSprite().getHeight() - 12;
                yield DOWN;
            }
            case LEFT -> {
                worldPoint.y -= user.getSprite().getHeight() + 6;
                yield LEFT;
            }
            case RIGHT -> {
                worldPoint.y -= user.getSprite().getHeight() + 6;
                yield RIGHT;
            }
        };

        xT = worldPoint.x;
        yT = worldPoint.y;

        user.setAction(THROWING);
    }
    protected void handleToss() {

        if (++tossCounter < 32) {

            tTime += 25;
            switch (direction) {
                case UP, UPLEFT, UPRIGHT -> {
                    checkTossCollision();
                    if (collisionOn) {
                        tossed = false;
                    }
                    else {
                        worldPoint.y -= 3;
                    }
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> {
                    checkTossCollision();
                    if (collisionOn) {
                        tossed = false;
                    }
                    else {
                        worldPoint.y += 4;
                    }
                }
                case LEFT -> {
                    checkTossCollision();
                    if (collisionOn) {
                        worldPoint.y = tWorldY;
                        tossed = false;
                    }
                    else {
                        getTrajectory(-135);
                    }
                }
                case RIGHT -> {
                    checkTossCollision();
                    if (collisionOn) {
                        worldPoint.y = tWorldY;
                        tossed = false;
                    }
                    else {
                        getTrajectory(-45);
                    }
                }
            }
        }
        else {
            tossed = false;
        }

        if (!tossed) {
            canMove = true;
            tossCounter = 0;
            tTime = 0;
            tWorldY = 0;
            xT = 0;
            yT = 0;

            gp.cChecker.checkHazard(this);
            if (alive) endThrow();
        }
    }
    private void checkTossCollision() {
        gp.cChecker.checkTile(this);
        gp.cChecker.checkMovementCollision(this, gp.npc);
        gp.cChecker.checkMovementCollision(this, gp.enemy);
        gp.cChecker.checkMovementCollision(this, gp.obj);
    }
    private void getTrajectory(double angle) {
        double tSpeed = 0.25;
        double tG = 0.00065;

        worldPoint.x = (int) (tSpeed * Math.cos(angle * Math.PI / 180.0) * tTime + xT);
        worldPoint.y = (int) (0.5 * tG * tTime * tTime + tSpeed * Math.sin(angle * Math.PI / 180.0) * tTime + yT);
    }

    protected void endThrow() {

    }

    public void interact(Entity user) {

    }

    public void interact() {

    }

    @Override
    public void draw(Graphics2D g2) {
        if (!grabbed) {
            super.draw(g2);
        }
    }

    public void setGrabbed(boolean grabbed) {
        this.grabbed = grabbed;
    }
    public boolean getTossed() {
        return tossed;
    }
    public int getTWorldY() {
        return tWorldY;
    }
}
