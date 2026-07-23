package entity.object;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

import static application.GamePanel.Direction.*;
import static application.GamePanel.Direction.RIGHT;
import static entity.Entity.Action.THROWING;

public class Object extends Entity {

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

    public void place(Entity user) {

        breakGrab();

        // Direction same as user
        direction = user.getDirection();
        worldPoint.y = user.getWorldPoint().y;

        user.setAction(THROWING);

        // Place in front of user
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> worldPoint.y -= gp.tileSize;
            case DOWN, DOWNLEFT, DOWNRIGHT -> worldPoint.y += gp.tileSize;
            case LEFT -> worldPoint.x -= gp.tileSize;
            case RIGHT -> worldPoint.x += gp.tileSize;
        }

        // If collision detected, place on top of user
        checkCollision();
        if (collisionOn) {
            worldPoint.setLocation(user.getWorldPoint());
        }
    }

    public void toss(Entity user) {

        breakGrab();
        tossed = true;
        canMove = false;
        elevated = true;

        // Start at user's location
        worldPoint.setLocation(user.getWorldPoint());

        // Save starting Y for wall collision
        tWorldY = worldPoint.y;

        // Shift starting point based on user's direction
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

        // Starting values for toss physics
        xT = worldPoint.x;
        yT = worldPoint.y;

        user.setAction(THROWING);
    }
    protected void handleToss() {

        // Toss for 32 frames, run toss physics
        if (++tossCounter < 32) {

            tTime += 25;
            switch (direction) {
                case UP, UPLEFT, UPRIGHT -> {
                    checkCollision();
                    if (collisionOn) {
                        tossed = false;
                    }
                    else {
                        worldPoint.y -= 3;
                    }
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> {
                    checkCollision();
                    if (collisionOn) {
                        tossed = false;
                    }
                    else {
                        worldPoint.y += 4;
                    }
                }
                case LEFT -> {
                    checkCollision();
                    if (collisionOn) {
                        worldPoint.y = tWorldY;
                        tossed = false;
                    }
                    else {
                        getTrajectory(-135);
                    }
                }
                case RIGHT -> {
                    checkCollision();
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

        // Toss disrupted, end toss
        if (!tossed) {
            elevated = false;
            canMove = true;
            tossCounter = 0;
            tTime = 0;
            tWorldY = 0;
            xT = 0;
            yT = 0;

            // If not killed by hazard, run land on ground logic
            gp.cChecker.checkHazard(this);
            if (alive) {
                landOnGround();
            }
        }
    }
    private void getTrajectory(double angle) {
        double tSpeed = 0.25;
        double tG = 0.00065;

        worldPoint.x = (int) (tSpeed * Math.cos(angle * Math.PI / 180.0) * tTime + xT);
        worldPoint.y = (int) (0.5 * tG * tTime * tTime + tSpeed * Math.sin(angle * Math.PI / 180.0) * tTime + yT);
    }

    protected void landOnGround() {
        interactable = true;
    }
    protected void createParticles() {
        Particle.generateParticles(gp, worldPoint, getParticleMaxHealth(), getParticleSpeed(), getParticleColor(), getParticleSize());
    }

    @Override
    public void checkCollision() {

        // No collision detection for grabbed objects
        if (isGrabbed()) return;

        collisionOn = false;

        gp.cChecker.checkTile(this);

        gp.cChecker.checkMovementCollision(this, gp.npcs);
        gp.cChecker.checkMovementCollision(this, gp.enemies);
        gp.cChecker.checkMovementCollision(this, gp.objects);

        // Check hazard at end of toss
        if (!tossed) {
            gp.cChecker.checkHazard(this);
        }
    }

    public void interact(Entity user) {

    }
    public void interact() {

    }

    @Override
    public void draw(Graphics2D g2) {

        if (grabbedBy != null) {
            worldPoint.setLocation(new Point(grabbedBy.getWorldPoint().x, grabbedBy.getWorldPoint().y - grabbedBy.getSprite().getHeight() + 12));
        }

        super.draw(g2);
    }

    public boolean getTossed() {
        return tossed;
    }
    public int getTWorldY() {
        return tWorldY;
    }

    protected int getParticleMaxHealth() {
        return 0;
    }
    protected int getParticleSpeed() {
        return 0;
    }
    protected Color getParticleColor() {
        return new Color(0, 0, 0);
    }
    protected int getParticleSize() {
        return 0;
    }

    @Override
    public DrawLayer getDrawLayer() {
        return isGrabbed() ? DrawLayer.ABOVE : DrawLayer.ENTITY;
    }
}
