package entity.projectile;

import application.GamePanel;
import entity.Entity;
import entity.enemy.Enemy;

import static application.GamePanel.Direction.DOWN;
import static application.GamePanel.Direction.UP;

import java.awt.*;

public class Projectile extends Entity {

    protected boolean canPickup = false;

    public Projectile(GamePanel gp, String name) {
        super(gp, name);

        maxHealth = 60;
        health = maxHealth;

        defaultAttack = 1;
        attack = defaultAttack;

        knockbackPower = 1;
        alive = false;

        hitbox = new Rectangle(12, 16, 24, 24);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    public void set(Point worldPoint, GamePanel.Direction direction, boolean alive, Entity user) {
        this.worldPoint.setLocation(worldPoint);
        this.direction = direction;
        this.alive = alive;
        this.user = user;

        this.direction = getCorrectedDirection();

        shiftPosition();
    }

    private GamePanel.Direction getCorrectedDirection() {
        return switch (direction) {
            case UPLEFT, UPRIGHT -> UP;
            case DOWNLEFT, DOWNRIGHT -> DOWN;
            default -> direction;
        };
    }

    private void shiftPosition() {

        // Avoids potential collision issue
        switch (direction) {
            case UP, UPLEFT, UPRIGHT, DOWN, DOWNLEFT, DOWNRIGHT -> this.worldPoint.x += 3;
            case LEFT, RIGHT -> this.worldPoint.y += 3;
        }
    }

    @Override
    public void update() {

        checkCollision();
        moveInDirection(direction);

        health--;
        checkDeath();
    }

    @Override
    public void checkCollision() {

        collisionOn = false;

        gp.cChecker.checkTile(this);
        gp.cChecker.checkMovementCollision(this, gp.npc);
        gp.cChecker.checkMovementCollision(this, gp.obj);
        checkObjectCollision();

        if (user == gp.player) {
            checkEnemyCollision();
        }
        else {
            checkPlayerCollision();
        }
    }
    protected boolean checkEnemyCollision() {

        Enemy enemy = overlapEnemy(this);
        if (enemy != null) {
            enemy.takeDamage(this);
            collisionOn = true;
            return true;
        }

        return false;
    }
    protected void checkPlayerCollision() {

        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        if (contactPlayer) {
            gp.player.takeDamage(this);
            alive = false;
        }
    }
    protected void checkObjectCollision() {

        int object = gp.cChecker.checkOverlapCollision(this, gp.obj);
        if (object != -1 ) {
            gp.obj[object].interact();
            health = 0;
            collisionOn = true;
        }
        else {
            object = gp.cChecker.checkMovementCollision(this, gp.obj);
            if (object != -1 ) {
                gp.obj[object].interact();
                health = 0;
                collisionOn = true;
            }
        }
    }

    protected void returnToUser() {

        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                if (getCenterY() < user.getCenterY()) {
                    worldPoint.y += 5;
                }
                else {
                    alive = false;
                }
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                if (getCenterY() > user.getCenterY()) {
                    worldPoint.y -= 5;
                }
                else {
                    alive = false;
                }
            }
            case LEFT -> {
                if (getCenterX() < user.getCenterX()) {
                    worldPoint.x += 5;
                }
                else {
                    alive = false;
                }
            }
            case RIGHT -> {
                if (getCenterX() > user.getCenterX()) {
                    worldPoint.x -= 5;
                }
                else {
                    alive = false;
                }
            }
        }
    }

    protected void pullEntity(Entity grabbedEntity) {

        grabbedEntity.resetValues();
        grabbedEntity.setCanMove(false);
        grabbedEntity.setDirection(getOppositeDirection(direction));
        grabbedEntity.setElevated(true);

        // Offset X/Y so entity isn't on top of player
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> grabbedEntity.setWorldPointY(worldPoint.y - gp.tileSize / 2);
            case DOWN, DOWNLEFT, DOWNRIGHT -> grabbedEntity.setWorldPointY(worldPoint.y + gp.tileSize / 2);
            case LEFT -> grabbedEntity.setWorldPointX(worldPoint.x - gp.tileSize / 2);
            case RIGHT -> grabbedEntity.setWorldPointX(worldPoint.x + gp.tileSize / 2);
        }
    }

    @Override
    public boolean canCollideWith(Entity target) {

        // Ignore enemies on a different elevation
        if (target instanceof Enemy || target == gp.player) {
            return isOnSameElevation(target);
        }

        return true;
    }

    @Override
    protected void cycleSprites() {

        if (animationSpeed < ++spriteCounter) {
            if (spriteNum == 1) {
                spriteNum = 2;
            }
            else if (spriteNum == 2) {
                spriteNum = 1;
            }

            spriteCounter = 0;
        }
    }

    @Override
    protected void checkDeath() {
        if (health <= 0 || !alive || collisionOn) {
            resetValues();
        }
    }

    @Override
    public void resetValues() {
        alive = false;
        collisionOn = false;
        health = maxHealth;
        attack = defaultAttack;
        speed = defaultSpeed;
    }

    public boolean getCanPickup() {
        return canPickup;
    }

    public void pickup(Entity user) {

    }

    @Override
    public DrawLayer getDrawLayer() {
        return DrawLayer.ABOVE;
    }
}
