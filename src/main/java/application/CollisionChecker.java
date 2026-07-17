package application;

import entity.Entity;
import entity.enemy.Enemy;
import entity.npc.NPC;
import entity.object.Object;
import tile.Tile;
import tile.TileManager;

import java.awt.*;
import java.util.Map;

import static application.GamePanel.Direction;
import static entity.Entity.Action.*;

public record CollisionChecker(GamePanel gp) {

    // Offset points for explosion radius, directions for knockback
    private static final Map<Point, Direction> EXPLOSION_OFFSETS = Map.ofEntries(
            Map.entry(new Point(0, 0), Direction.UP), // center
            Map.entry(new Point(0, -1), Direction.UP), // up
            Map.entry(new Point(-1, -1), Direction.UP), // up-left
            Map.entry(new Point(1, -1), Direction.UP), // up-right
            Map.entry(new Point(0, 1), Direction.DOWN), // down
            Map.entry(new Point(-1, 1), Direction.DOWN), // down-left
            Map.entry(new Point(1, 1), Direction.DOWN), // down-right
            Map.entry(new Point(-1, 0), Direction.LEFT), // left
            Map.entry(new Point(1, 0), Direction.RIGHT) // right
    );

    /**
     * CHECK TILE
     * Checks if the given entity will collide with a tile
     * @param entity Entity to check collision for
     */
    public void checkTile(Entity entity) {

        Rectangle box = entity.getWorldHitbox();
        if (outOfBounds(box)) return;

        Point delta = new Point();

        switch (entity.getMoveDirection()) {
            case UP -> delta.y -= entity.getSpeed();
            case UPLEFT -> {
                delta.x -= entity.getSpeed();
                delta.y -= entity.getSpeed();
            }
            case UPRIGHT -> {
                delta.x = entity.getSpeed();
                delta.y -= entity.getSpeed();
            }
            case DOWN -> delta.y = entity.getSpeed();
            case DOWNLEFT -> {
                delta.x -= entity.getSpeed();
                delta.y = entity.getSpeed();
            }
            case DOWNRIGHT -> {
                delta.x = entity.getSpeed();
                delta.y = entity.getSpeed();
            }
            case LEFT -> {
                delta.x -= entity.getSpeed();

                // Shift thrown objects down to avoid top wall collision
                if (entity instanceof Object obj && obj.getTossed()) {
                    delta.y += obj.getTWorldY() - obj.getWorldPoint().y;
                }
            }
            case RIGHT -> {
                delta.x = entity.getSpeed();

                // Shift thrown objects down to avoid top wall collision
                if (entity instanceof Object obj && obj.getTossed()) {
                    delta.y += obj.getTWorldY() - obj.getWorldPoint().y;
                }
            }
        }

        box.translate(delta.x, delta.y);

        if (outOfBounds(box)) {
            entity.setCollision(true);
            return;
        }

        int leftCol = box.x / gp.tileSize;
        int rightCol = (box.x + box.width - 1) / gp.tileSize;
        int topRow = box.y / gp.tileSize;
        int bottomRow = (box.y + box.height - 1) / gp.tileSize;

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {

                Tile tile = getTileAtColRow(col, row);
                if (tile == null) continue;

                checkTileCollision(entity, tile);
            }
        }
    }
    private void checkTileCollision(Entity entity, Tile tile) {

        // Bottomless pits
        if (tile.isPit()) {

            // Entity in air
            if (entity.getElevated()) return;

            // NPCs and enemies
            if (entity instanceof NPC || entity instanceof Enemy) {
                entity.setCollision(true);
            }
        }
        // Water
        else if (tile.isWater()) {

            // Entity in air or can swim
            if (entity.getElevated() || entity.getCanSwim()) return;

            // NPCs and enemies
            if (entity instanceof NPC || entity instanceof Enemy) {
                entity.setCollision(true);
            }
        }
        // Collision titles
        else if (tile.hasCollision()) {
            entity.setCollision(true);
        }
        // Fish enemies cannot move outside of water
        else if (entity.getNeedsWater()) {
            entity.setCollision(true);
        }
    }

    public void checkHazard(Entity entity) {

        if (outOfBounds(entity.getWorldHitbox())) return;

        Tile tile = getCurrentTile(entity);

        if (tile.isPit()) {
            handlePit(entity);
        }
        else if (tile.isWater()) {
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

        if (entity.getElevated()) return;

        if (entity == gp.player) {
            gp.player.setAction(FALLING);
            gp.player.shiftToCenter();
        }
        else {
            entity.setAlive(false);
        }
    }
    private void handleWater(Entity entity) {

        if (entity.getElevated() || entity.getCanSwim()) return;

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

    public boolean checkIce(Entity entity) {

        Point center = entity.getCenterPoint();

        int col = center.x / gp.tileSize;
        int row = center.y / gp.tileSize;

        return gp.tileM.mapTileNum[col][row] == TileManager.iceTile;
    }

    public int checkMovementCollision(Entity entity, Entity[] targets) {

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

        for (int i = 0; i < targets.length; i++) {

            Entity target = targets[i];

            if (target == null || target == entity || target.isNotInteractable()) {
                continue;
            }

            Rectangle targetRect = target.getWorldHitbox();

            boolean alreadyIntersecting = currentRect.intersects(targetRect);
            boolean willIntersect = futureRect.intersects(targetRect);

            if (!alreadyIntersecting && willIntersect) {

                entityIndex = i;

                boolean canCollide = entity.canCollideWith(target) && target.canCollideWith(entity);
                if (canCollide) {
                    entity.setCollision(true);
                }

                break;
            }
        }

        return entityIndex;
    }
    public int checkOverlapCollision(Entity entity, Entity[] targets) {

        int entityIndex = -1;

        Rectangle entityRect = entity.getWorldHitbox();

        for (int i = 0; i < targets.length; i++) {

            Entity target = targets[i];

            if (target == null || target == entity || target.isNotInteractable()) {
                continue;
            }

            Rectangle targetRect = target.getWorldHitbox();
            if (entityRect.intersects(targetRect)) {
                entityIndex = i;
                break;
            }
        }

        return entityIndex;
    }

    public boolean hasOverlapCollision(Entity entity, Entity target) {

        if (target == null || target == entity || target.isNotInteractable()) {
            return false;
        }

        Rectangle entityRect = entity.getWorldHitbox();
        Rectangle targetRect = target.getWorldHitbox();

        return entityRect.intersects(targetRect);
    }

    public void setOverlapCollision(Entity entity, Entity[] targets) {

        Rectangle entityRect = entity.getWorldHitbox();

        for (Entity target : targets) {

            if (target == null || target == entity || target.isNotInteractable()) {
                continue;
            }

            Rectangle targetRect = target.getWorldHitbox();
            if (entityRect.intersects(targetRect)) {
                entity.setCollision(true);
                break;
            }
        }
    }

    /**
     * CHECK PLAYER
     * Checks if the given entity will collide with the player entity
     * @param entity Entity to check collision for
     */
    public boolean checkPlayer(Entity entity) {

        if (gp.player.isNotInteractable() || entity.isNotInteractable()) {
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

    public void checkExplosion(Entity entity) {

        // Current col, row of given entity
        Point center = entity.getCenterPoint();
        int startingCol = center.x / gp.tileSize;
        int startingRow = center.y / gp.tileSize;

        // Target player, enemies, and objects
        Entity[][] allTargets = {
                { gp.player },
                gp.enemy,
                gp.obj
        };

        // Iterate over each corresponding tile (surrounding square shape)
        int col, row;
        for (Map.Entry<Point, Direction> entry : EXPLOSION_OFFSETS.entrySet()) {

            // Shift row/col based on give offset
            Point offset = entry.getKey();
            col = startingCol + offset.x;
            row = startingRow + offset.y;

            // Detect if out of bounds
            if (col < 0 || row < 0) continue;

            // Change direction for knockback
            Direction direction = entry.getValue();
            entity.setDirection(direction);

            // Detect if any entity is on the current tile
            for (Entity[] targets : allTargets) {
                handleExplosionCollision(entity, targets, col, row);
            }
        }
    }
    private void handleExplosionCollision(Entity entity, Entity[] targets, int entityCol, int entityRow) {

        Point center;
        int targetCol, targetRow;

        // For each target in given list
        for (Entity target : targets) {

            // Skip if not valid
            if (target == null || target == entity || target.isNotInteractable()) {
                continue;
            }

            center = target.getCenterPoint();

            // Current entity's tile position
            targetCol = center.x / gp.tileSize;
            targetRow = center.y / gp.tileSize;

            // Current tile same as detection zone, deal damage
            if (targetCol == entityCol && targetRow == entityRow) {
                target.takeDamage(entity);
            }
        }
    }

    private Tile getTileAtColRow(int col, int row) {
        return gp.tileM.tiles[gp.tileM.mapTileNum[col][row]];
    }

    private boolean outOfBounds(Rectangle box) {
        return (box.y <= 0 || box.y + box.height >= gp.maxWorldRow * gp.tileSize ||
                box.x <= 0 || box.x + box.width >= gp.maxWorldCol * gp.tileSize);
    }
}