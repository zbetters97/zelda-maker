package application;

import entity.Entity;
import tile.Tile;

import java.awt.*;

import static entity.Entity.Action.*;

public record CollisionChecker(GamePanel gp) {

    /**
     * CHECK TILE
     * Checks if the given entity will collide with a tile
     * @param entity Entity to check collision for
     */
    public void checkTile(Entity entity) {

        Rectangle box = entity.getWorldHitbox();

        // Prevent collision detection out of bounds
        if (box.y <= 0 || box.y + box.height >= gp.maxWorldRow * gp.tileSize ||
                box.x <= 0 || box.x + box.width >= gp.maxWorldCol * gp.tileSize) {
            return;
        }

        Point delta = new Point();

        switch (entity.getMoveDirection()) {
            case UP -> delta.y -= entity.getSpeed();
            case DOWN -> delta.y = entity.getSpeed();
            case LEFT -> delta.x -= entity.getSpeed();
            case RIGHT -> delta.x = entity.getSpeed();
            case UPLEFT -> {
                delta.x -= entity.getSpeed();
                delta.y -= entity.getSpeed();
            }
            case UPRIGHT -> {
                delta.x = entity.getSpeed();
                delta.y -= entity.getSpeed();
            }
            case DOWNLEFT -> {
                delta.x -= entity.getSpeed();
                delta.y = entity.getSpeed();
            }
            case DOWNRIGHT -> {
                delta.x = entity.getSpeed();
                delta.y = entity.getSpeed();
            }
        }

        box.translate(delta.x, delta.y);

        int leftCol = box.x / gp.tileSize;
        int rightCol = (box.x + box.width - 1) / gp.tileSize;
        int topRow = box.y / gp.tileSize;
        int bottomRow = (box.y + box.height - 1) / gp.tileSize;

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {

                Tile tile = getTileAtColRow(col, row);
                if (tile == null) {
                    continue;
                }

                checkTileCollision(entity, tile);
            }
        }
    }
    private Tile getTileAtColRow(int col, int row) {
        return gp.tileM.tiles[gp.tileM.mapTileNum[gp.currentMap][col][row]];
    }
    private void checkTileCollision(Entity entity, Tile tile) {
        // Bottomless pits
        if (tile.isPit) {

            // Entity in air
            if (entity.getElevated()) {
                return;
            }

            // NPCs and enemies
            if (entity.getType() == entity.type_npc || entity.getType() == entity.type_enemy) {
                entity.setCollision(true);
            }
        }
        // Water
        else if (tile.isWater) {

            // Entity in air or can swim
            if (entity.getElevated() || entity.getCanSwim()) {
                return;
            }

            // NPCs and enemies
            if (entity.getType() == entity.type_npc || entity.getType() == entity.type_enemy) {
                entity.setCollision(true);
            }
        }
        // Collision titles
        else if (tile.hasCollision) {
            entity.setCollision(true);
        }
        // Fish enemies cannot move outside of water
        else if (entity.getNeedsWater()) {
            entity.setCollision(true);
        }
    }

    public void checkHazard(Entity entity) {
        Tile tile = getCurrentTile(entity);

        if (tile.isPit) {
            handlePit(entity);
        }
        else if (tile.isWater) {
            handleWater(entity);
        }
        // Player is on ground, set safe X/Y
        else if (entity == gp.player && !gp.player.getElevated()) {
            setSafePoint();
        }
    }
    private Tile getCurrentTile(Entity entity) {

        Point center = entity.getCenterPoint();

        int col = center.x / gp.tileSize;
        int row = center.y / gp.tileSize;

        return getTileAtColRow(col, row);
    }
    private void handlePit(Entity entity) {
        if (entity.getElevated()) {
            return;
        }

        if (entity == gp.player) {
            gp.player.setAction(FALLING);
            gp.player.shiftToCenter();
        }
        else {
            entity.setAlive(false);
        }
    }
    private void handleWater(Entity entity) {
        if (entity.getElevated() || entity.getCanSwim()) {
            return;
        }

        if (entity == gp.player) {
            gp.player.setAction(DROWNING);
            gp.player.shiftToCenter();
        }
        else {
            entity.setAlive(false);
        }
    }
    private void setSafePoint() {

        Point center = gp.player.getCenterPoint();

        int col = center.x / gp.tileSize;
        int row = center.y / gp.tileSize;

        gp.player.safePoint = new Point(col * gp.tileSize, row * gp.tileSize);
    }

    public int checkMovementCollision(Entity entity, Entity[][] targets) {

        int entityIndex = -1;

        Rectangle futureRect = entity.getWorldHitbox();

        switch (entity.getMoveDirection()) {
            case UP -> futureRect.y -= entity.getSpeed();
            case UPLEFT -> {
                futureRect.x -= entity.getSpeed();
                futureRect.y -= entity.getSpeed();
            }
            case UPRIGHT -> {
                futureRect.x += entity.getSpeed();
                futureRect.y -= entity.getSpeed();
            }
            case DOWN -> futureRect.y += entity.getSpeed();
            case DOWNLEFT -> {
                futureRect.x -= entity.getSpeed();
                futureRect.y += entity.getSpeed();
            }
            case DOWNRIGHT -> {
                futureRect.x += entity.getSpeed();
                futureRect.y += entity.getSpeed();
            }
            case LEFT -> futureRect.x -= entity.getSpeed();
            case RIGHT -> futureRect.x += entity.getSpeed();
        }

        Rectangle currentRect = entity.getWorldHitbox();

        for (int i = 0; i < targets[gp.currentMap].length; i++) {

            Entity target = targets[gp.currentMap][i];

            if (target == null || target == entity || !target.isAvailable()) {
                continue;
            }

            Rectangle targetRect = target.getWorldHitbox();

            boolean alreadyIntersecting = currentRect.intersects(targetRect);
            boolean willIntersect = futureRect.intersects(targetRect);
            boolean canCollide = entity.canCollideWith(target) && target.canCollideWith(entity);

            if (!alreadyIntersecting && willIntersect && canCollide) {
                entity.setCollision(true);
                entityIndex = i;
                break;
            }
        }

        return entityIndex;
    }
    public int checkOverlapCollision(Entity entity, Entity[][] targets) {

        int entityIndex = -1;

        Rectangle entityRect = entity.getWorldHitbox();

        for (int i = 0; i < targets[gp.currentMap].length; i++) {

            Entity target = targets[gp.currentMap][i];

            if (target == null || target == entity || !target.isAvailable()) {
                continue;
            }

            Rectangle targetRect = target.getWorldHitbox();
            boolean canCollide = entity.canCollideWith(target) && target.canCollideWith(entity);

            if (entityRect.intersects(targetRect) && canCollide) {
                entityIndex = i;
                break;
            }
        }

        return entityIndex;
    }

    /**
     * CHECK PLAYER
     * Checks if the given entity will collide with the player entity
     * @param entity Entity to check collision for
     */
    public boolean checkPlayer(Entity entity) {

        if (!gp.player.isAvailable() || !entity.isAvailable()) {
            return false;
        }

        Rectangle entityRect = entity.getWorldHitbox();
        Rectangle playerRect = gp.player.getWorldHitbox();

        switch (entity.getDirection()) {
            case UP -> entityRect.y -= entity.getSpeed();
            case DOWN -> entityRect.y += entity.getSpeed();
            case LEFT -> entityRect.x -= entity.getSpeed();
            case RIGHT -> entityRect.x += entity.getSpeed();
            default -> {
                entity.setCollision(true);
                return false;
            }
        }

        if (!entityRect.intersects(playerRect)) {
            return false;
        }

        entity.setCollision(true);

        return entity.isOnSameElevation(gp.player);
    }
}