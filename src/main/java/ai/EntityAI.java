package ai;

import application.GamePanel;
import entity.Entity;

import java.util.Random;

import static application.GamePanel.Direction.*;
import static entity.Entity.Action.ATTACKING;

public class EntityAI {

    private final GamePanel gp;
    private final Entity entity;

    private boolean pathCompleted = false;
    private int counter = 0;
    
    public EntityAI(GamePanel gp, Entity entity) {
        this.gp = gp;
        this.entity = entity;
    }

    public void searchPath(Entity target) {

        if (entity.getAction() == ATTACKING) {
            return;
        }

        int goalCol = getGoalCol(target);
        int goalRow = getGoalRow(target);

        int startCol = (entity.getWorldPoint().x +entity.getHitbox().x) / gp.tileSize;
        int startRow = (entity.getWorldPoint().y +entity.getHitbox().y) / gp.tileSize;

        // SET PATH
        gp.pFinder.setNodes(startCol, startRow, goalCol, goalRow);

        // PATH FOUND
        if (gp.pFinder.search()) {

            // NEXT WORLD X & WORLD Y
            int nextX = gp.pFinder.pathList.getFirst().col * gp.tileSize;
            int nextY = gp.pFinder.pathList.getFirst().row * gp.tileSize;

            // ENTITY hitbox
            int eLeftX = entity.getWorldPoint().x +entity.getHitbox().x;
            int eRightX = entity.getWorldPoint().x +entity.getHitbox().x +entity.getHitbox().width;
            int eTopY = entity.getWorldPoint().y +entity.getHitbox().y;
            int eBottomY = entity.getWorldPoint().y +entity.getHitbox().y +entity.getHitbox().height;

            // FIND DIRECTION TO NEXT NODE
            // UP OR DOWN
            if (eTopY > nextY && eLeftX >= nextX && eRightX < nextX + gp.tileSize) {
                entity.setDirection(UP);
            }
            else if (eTopY < nextY && eLeftX >= nextX && eRightX < nextX + gp.tileSize) {
                entity.setDirection(DOWN);
            }
            // LEFT OR RIGHT
            else if (eTopY >= nextY && eBottomY < nextY + gp.tileSize) {
                if (eLeftX > nextX) {
                    entity.setDirection(LEFT);
                }
                if (eLeftX < nextX) {
                    entity.setDirection(RIGHT);
                }
            }
            // UP OR LEFT
            else if (eTopY > nextY && eLeftX > nextX) {
                entity.setDirection(UP);

                entity.checkCollision();
                if (entity.getCollision()) {
                    entity.setDirection(LEFT);
                }
            }
            // UP OR RIGHT
            else if (eTopY > nextY && eLeftX < nextX) {
                entity.setDirection(UP);

                entity.checkCollision();
                if (entity.getCollision()) {
                    entity.setDirection(RIGHT);
                }
            }
            // DOWN OR LEFT
            else if (eTopY < nextY && eLeftX > nextX) {
                entity.setDirection(DOWN);

                entity.checkCollision();
                if (entity.getCollision()) {
                    entity.setDirection(LEFT);
                }
            }
            // DOWN OR RIGHT
            else if (eTopY < nextY && eLeftX < nextX) {
                entity.setDirection(DOWN);

                entity.checkCollision();
                if (entity.getCollision()) {
                    entity.setDirection(RIGHT);
                }
            }
        }
        // NO PATH FOUND
        else {
            entity.setOnPath(false);
        }

        // GOAL REACHED
        if (!gp.pFinder.pathList.isEmpty()) {
            int nextCol = gp.pFinder.pathList.getFirst().col;
            int nextRow = gp.pFinder.pathList.getFirst().row;
            if (nextCol == goalCol && nextRow == goalRow) {
                pathCompleted = true;
            }
        }
    }

    public boolean playerWithinRange() {

        // Don't search for player if not available
        if (!gp.player.isAvailable()) {
            return false;
        }

        boolean playerWithinBounds = true;

        int tileDistance = (Math.abs(entity.getWorldPoint().x - gp.player.getWorldPoint().x) + Math.abs(entity.getWorldPoint().y - gp.player.getWorldPoint().y)) / gp.tileSize;

        if (tileDistance > 999) {
            playerWithinBounds = false;
        }

        return playerWithinBounds;
    }

    public boolean lookingAtPlayer(int tolerance) {

        int dx = gp.player.getWorldPoint().x - entity.getWorldPoint().x;
        int dy = gp.player.getWorldPoint().y - entity.getWorldPoint().y;

        return switch (entity.getDirection()) {
            case UP -> dy < 0 && Math.abs(dx) <= tolerance;
            case DOWN -> dy > 0 && Math.abs(dx) <= tolerance;
            case LEFT -> dx < 0 && Math.abs(dy) <= tolerance;
            case RIGHT -> dx > 0 && Math.abs(dy) <= tolerance;
            default -> false;
        };
    }

    public void approachPlayer(int rate) {

        if (rate <= ++counter) {

            if (getXDistance(gp.player) >= getYDistance(gp.player)) {
                if (gp.player.getCenterX() < entity.getCenterX()) {
                    entity.setDirection(LEFT);
                }
                else {
                    entity.setDirection(RIGHT);
                }
            }
            else if (getXDistance(gp.player) < getYDistance(gp.player)) {
                if (gp.player.getCenterY() < entity.getCenterY()) {
                    entity.setDirection(UP);
                }
                else {
                    entity.setDirection(DOWN);
                }
            }

            counter = 0;
        }
    }

    public void setAttacking(int rate, int straight, int horizontal) {

        boolean targetInRange = false;
        int xDis = getXDistance(gp.player);
        int yDis = getYDistance(gp.player);

        // If player is attacking within hitbox
        switch (entity.getDirection()) {
            case UP, UPLEFT, UPRIGHT -> {
                if (gp.player.getCenterY() < entity.getCenterY() && yDis < straight && xDis < horizontal) {
                    targetInRange = true;
                }
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                if (gp.player.getCenterY() > entity.getCenterY() && yDis < straight && xDis < horizontal) {
                    targetInRange = true;
                }
            }
            case LEFT -> {
                if (gp.player.getCenterX() < entity.getCenterX() && xDis < straight && yDis < horizontal) {
                    targetInRange = true;
                }
            }
            case RIGHT -> {
                if (gp.player.getCenterX() > entity.getCenterX() && xDis < straight && yDis < horizontal) {
                    targetInRange = true;
                }
            }
        }

        // Player is within range
        if (targetInRange) {

            // Random chance to attack player
            int i = new Random().nextInt(rate);
            if (i == 0) {
                entity.setAction(ATTACKING);
            }
        }
    }

    public void isOnPath(Entity target, int distance) {
        if (getTileDistance(target) < distance) {
            entity.setOnPath(true);
        }
    }
    public void isOffPath(Entity target, int distance) {
        if (getTileDistance(target) > distance || !withinBounds()) {
            entity.setOnPath(false);
        }
    }

    public int getTileDistance(Entity target) {
        return (getXDistance(target) + getYDistance(target)) / gp.tileSize;
    }

    public boolean withinRange(Entity target, int buffer) {

        int dx = target.getCenterX() - entity.getCenterX();
        int dy = target.getCenterY() - entity.getCenterY();

        return switch (entity.getDirection()) {
            case UP -> dy < 0 && Math.abs(dx) <= buffer;
            case UPLEFT -> dy < 0 && dx < 0;
            case UPRIGHT -> dy < 0 && dx > 0;
            case DOWN -> dy > 0 && Math.abs(dx) <= buffer;
            case DOWNLEFT -> dy > 0 && dx < 0;
            case DOWNRIGHT -> dy > 0 && dx > 0;
            case LEFT -> dx < 0 && Math.abs(dy) <= buffer;
            case RIGHT -> dx > 0 && Math.abs(dy) <= buffer;
        };
    }

    private int getGoalCol(Entity target) {
        return target.getCenterX() / gp.tileSize;
    }
    private int getGoalRow(Entity target) {
        return target.getCenterY() / gp.tileSize;
    }

    private int getXDistance(Entity target) {
        return Math.abs(entity.getCenterX() - target.getCenterX());
    }
    private int getYDistance(Entity target) {
        return Math.abs(entity.getCenterY() - target.getCenterY());
    }

    private boolean withinBounds() {

        boolean withinBounds = true;

        GamePanel.Direction tempDirection = entity.getMoveDirection();
        int tempWorldX = entity.getWorldPoint().x;
        int tempWorldY = entity.getWorldPoint().y;

        switch (tempDirection) {
            case UP -> tempWorldY -= entity.getSpeed();
            case UPLEFT -> {
                tempWorldY -= entity.getSpeed() - 1;
                tempWorldX -= entity.getSpeed() - 1;
            }
            case UPRIGHT -> {
                tempWorldY -= entity.getSpeed() - 1;
                tempWorldX += entity.getSpeed() - 1;
            }
            case DOWN -> tempWorldY += entity.getSpeed();
            case DOWNLEFT -> {
                tempWorldY += entity.getSpeed() - 1;
                tempWorldX -= entity.getSpeed() - 1;
            }
            case DOWNRIGHT -> {
                tempWorldY += entity.getSpeed();
                tempWorldX += entity.getSpeed() - 1;
            }
            case LEFT -> tempWorldX -= entity.getSpeed();
            case RIGHT -> tempWorldX += entity.getSpeed();
        }

        int tileDistance = (Math.abs(entity.getWorldPoint().x - tempWorldX) + Math.abs(entity.getWorldPoint().y - tempWorldY)) / gp.tileSize;

        if (tileDistance > 999) {
            withinBounds = false;
        }

        return withinBounds;
    }
}
