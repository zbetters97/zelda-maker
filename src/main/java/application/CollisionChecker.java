package application;

import entity.Entity;
import entity.enemy.Enemy;
import entity.npc.NPC;
import entity.object.OBJ_DigSpot;
import entity.object.Object;
import tile.Tile;
import tile.TileManager;

import java.awt.*;
import java.util.ArrayList;
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
            if (entity.getElevated() || entity.isKnockedBack()) return;

            // NPCs and Enemies, and Objects
            if (entity instanceof NPC || entity instanceof Enemy || entity instanceof Object) {
                entity.setCollision(true);
            }
        }
        // Water
        else if (tile.isWater()) {

            // Entity in air or can swim
            if (entity.getElevated() || entity.isKnockedBack() || entity.getCanSwim()) return;

            // NPCs and enemies
            if (entity instanceof NPC || entity instanceof Enemy || entity instanceof Object) {
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
        int tileNum = getCurrentTileNum(entity);

        if (tile.isPit()) {
            handlePit(entity);
        }
        else if (tile.isWater()) {
            handleWater(entity);
        }
        else if (tileNum == TileManager.spikeTile) {
            handleSpike(entity);
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
    private int getCurrentTileNum(Entity entity) {

        Point center = entity.getCenterPoint();

        int col = center.x / gp.tileSize;
        int row = center.y / gp.tileSize;

        return gp.tileM.mapTileNum[col][row];
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
    private void handleSpike(Entity entity) {

        // Damage enemy and push back
        entity.dealDamage(1, entity.getOppositeDirection(entity.getDirection()), 1);
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

    public <T extends Entity> T checkMovementCollision(Entity entity, ArrayList<T> targets) {

        Rectangle futureRect = entity.getWorldHitbox();
        int speed = entity.getSpeed();

        switch (entity.getMoveDirection()) {
            case UP -> futureRect.y -= speed;
            case UPLEFT -> {
                futureRect.x -= speed;
                futureRect.y -= speed;
            }
            case UPRIGHT -> {
                futureRect.x += speed;
                futureRect.y -= speed;
            }
            case DOWN -> futureRect.y += speed;
            case DOWNLEFT -> {
                futureRect.x -= speed;
                futureRect.y += speed;
            }
            case DOWNRIGHT -> {
                futureRect.x += speed;
                futureRect.y += speed;
            }
            case LEFT -> futureRect.x -= speed;
            case RIGHT -> futureRect.x += speed;
        }

        Rectangle currentRect = entity.getWorldHitbox();

        for (T target : targets) {

            if (target == null || target == entity || target.isNotInteractable()) {
                continue;
            }

            Rectangle targetRect = target.getWorldHitbox();

            boolean alreadyIntersecting = currentRect.intersects(targetRect);
            boolean willIntersect = futureRect.intersects(targetRect);

            if (!alreadyIntersecting && willIntersect) {

                boolean canCollide = entity.canCollideWith(target) && target.canCollideWith(entity);
                if (canCollide) {
                    entity.setCollision(true);
                }

                return target;
            }
        }

        return null;
    }
    public <T extends Entity> T checkOverlapCollision(Entity entity, ArrayList<T> targets) {

        Rectangle entityRect = entity.getWorldHitbox();

        for (T target : targets) {

            if (target == entity.getUser() || target == null || target == entity || target.isNotInteractable()) {
                continue;
            }

            Rectangle targetRect = target.getWorldHitbox();
            if (entityRect.intersects(targetRect)) {
                return target;
            }
        }

        return null;
    }

    public boolean hasOverlapCollision(Entity entity, Entity target) {

        if (target == null || target == entity || target.isNotInteractable()) {
            return false;
        }

        Rectangle entityRect = entity.getWorldHitbox();
        Rectangle targetRect = target.getWorldHitbox();

        return entityRect.intersects(targetRect);
    }

    public void setOverlapCollision(Entity entity, ArrayList<? extends Entity> targets) {

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

    public Object checkDigSpot(Entity entity) {

        Rectangle futureRect = entity.getWorldHitbox();

        switch (entity.getMoveDirection()) {
            case UP -> futureRect.y -= gp.tileSize;
            case UPLEFT -> {
                futureRect.x -= gp.tileSize;
                futureRect.y -= gp.tileSize;
            }
            case UPRIGHT -> {
                futureRect.x += gp.tileSize;
                futureRect.y -= gp.tileSize;
            }
            case DOWN -> futureRect.y += gp.tileSize;
            case DOWNLEFT -> {
                futureRect.x -= gp.tileSize;
                futureRect.y += gp.tileSize;
            }
            case DOWNRIGHT -> {
                futureRect.x += gp.tileSize;
                futureRect.y += gp.tileSize;
            }
            case LEFT -> futureRect.x -= gp.tileSize;
            case RIGHT -> futureRect.x += gp.tileSize;
        }

        for (Object object : gp.objects) {

            boolean isDigSpot = object.getName().equals(OBJ_DigSpot.objName);
            Rectangle targetRect = object.getWorldHitbox();

            if (isDigSpot && futureRect.intersects(targetRect)) {
                return object;
            }
        }

        return null;
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

        switch (entity.getMoveDirection()) {
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
        ArrayList<ArrayList<? extends Entity>> allTargets = new ArrayList<>();
        allTargets.add(gp.enemies);
        allTargets.add(gp.objects);

        // Iterate over each corresponding tile (surrounding square shape)
        Point offset;
        int col, row;
        for (Map.Entry<Point, Direction> entry : EXPLOSION_OFFSETS.entrySet()) {

            // Shift row/col based on give offset
            offset = entry.getKey();
            col = startingCol + offset.x;
            row = startingRow + offset.y;

            // Detect if out of bounds
            if (col < 0 || row < 0) continue;

            // Change direction for knockback
            Direction direction = entry.getValue();
            entity.setDirection(direction);

            // For each target in given list
            for (ArrayList<? extends Entity> targets : allTargets) {
                for (Entity target : targets) {
                    handleExplosionCollision(entity, target, col, row);
                }
            }

            handleExplosionCollision(entity, gp.player, col, row);
        }
    }
    private void handleExplosionCollision(Entity entity, Entity target, int entityCol, int entityRow) {

        // Skip if not valid
        if (target == null || target == entity || target.isNotInteractable()) {
            return;
        }

        Point center = target.getCenterPoint();

        // Current entity's tile position
        int targetCol = center.x / gp.tileSize;
        int targetRow = center.y / gp.tileSize;

        // Current tile same as detection zone, deal damage
        if (targetCol == entityCol && targetRow == entityRow) {
            target.takeDamage(entity);
        }
    }

    private Tile getTileAtColRow(int col, int row) {
        return gp.tileM.tiles[gp.tileM.mapTileNum[col][row]];
    }

    public boolean checkTileCollision(int col, int row) {

        int tileNum = gp.tileM.mapTileNum[col][row];
        Tile tile = gp.tileM.tiles[tileNum];

        return tile != null && tile.isNotTraversable(tileNum);
    }

    private boolean outOfBounds(Rectangle box) {
        return (box.y <= 0 || box.y + box.height >= gp.maxWorldRow * gp.tileSize ||
                box.x <= 0 || box.x + box.width >= gp.maxWorldCol * gp.tileSize);
    }
}