package entity.projectile;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class Projectile extends Entity {

    protected boolean canPickup = false;

    public Projectile(GamePanel gp, String name) {
        super(gp, name);
        entity_type = type_projectile;
    }

    @Override
    public void update() {
        if (user != null && user.isNotAvailable()) {
            alive = false;
        }
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
}
