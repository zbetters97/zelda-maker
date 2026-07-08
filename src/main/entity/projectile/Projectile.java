package entity.projectile;

import application.GamePanel;
import entity.Entity;
import entity.enemy.Enemy;

import java.awt.*;

public class Projectile extends Entity {

    protected boolean canPickup = false;

    public Projectile(GamePanel gp, String name) {
        super(gp, name);
        entity_type = type_projectile;
    }

    public void set(Point worldPoint, GamePanel.Direction direction, boolean alive, Entity user) {
        this.worldPoint.setLocation(worldPoint);
        this.direction = direction;
        this.alive = alive;
        this.user = user;

        shiftPosition();
    }
    private void shiftPosition() {

        // Avoids potential collision issue
        switch (direction) {
            case UP, UPLEFT, UPRIGHT, DOWN, DOWNLEFT, DOWNRIGHT -> this.worldPoint.x += 3;
            case LEFT, RIGHT -> this.worldPoint.y += 3;
        }
    }

    @Override
    public boolean canCollideWith(Entity target) {

        // Ignore enemies on a different elevation
        if (target.getType() == type_enemy || target == gp.player) {
            return isOnSameElevation(target);
        }

        return true;
    }

    protected boolean checkEnemyCollision() {

        Enemy enemy = overlapEnemy(this);
        if (enemy != null) {
            enemy.damageEnemy(this);
            collisionOn = true;
            return true;
        }

        return false;
    }

    protected void checkPlayerCollision() {

        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        if (contactPlayer) {
            damagePlayer(this);
            alive = false;
        }
        else {
            collisionOn = false;
        }
    }

    protected void checkObjectCollision() {

        int object = gp.cChecker.checkOverlapCollision(this, gp.obj);
        if (object != -1 ) {
            gp.obj[gp.currentMap][object].interact();
            health = 0;
            collisionOn = true;
        }
        else {
            object = gp.cChecker.checkMovementCollision(this, gp.obj);
            if (object != -1 ) {
                gp.obj[gp.currentMap][object].interact();
                health = 0;
                collisionOn = true;
            }
        }
    }

    @Override
    protected void checkDeath() {
        if (health <= 0 || !alive) {
            resetValues();
        }
    }
}
