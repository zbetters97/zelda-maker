package application;

import entity.Entity;
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

    /** Offset points for explosion radius, directions for knockback */
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

        Rectangle futureRect = entity.getWorldHitbox();
        if (outOfBounds(futureRect)) return;

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

        futureRect.translate(delta.x, delta.y);

        if (outOfBounds(futureRect)) {
            entity.setCollision(true);
            return;
        }

        int leftCol = futureRect.x / gp.tileSize;
        int rightCol = (futureRect.x + futureRect.width - 1) / gp.tileSize;
        int topRow = futureRect.y / gp.tileSize;
        int bottomRow = (futureRect.y + futureRect.height - 1) / gp.tileSize;

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {

                Tile tile = gp.tileM.tiles[gp.tileM.mapTileNum[col][row]];
                if (tile == null) continue;

                setEntityTileCollision(entity, tile);
            }
        }
    }

    /**
     * SET ENTITY TILE COLLISION
     * Turns on entity collision based on tile and various factors
     * @param entity Entity interacting with the tile
     * @param tile Tile to check collision on
     */
    private void setEntityTileCollision(Entity entity, Tile tile) {

        // Bottomless pits
        if (tile.isPit()) {

            // Entity in air
            if (entity.getElevated() || entity.isKnockedBack()) return;

            // Collision on if not player
            if (entity != gp.player) {
                entity.setCollision(true);
            }
        }
        // Water
        else if (tile.isWater()) {

            // Entity in air or can swim
            if (entity.getElevated() || entity.isKnockedBack() || entity.getCanSwim()) return;

            // Collision on if not player
            if (entity != gp.player) {
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

    /**
     * CHECK TILE COLLISION
     * Checks if the given tile has collision
     * @param col Column tile is on
     * @param row Row tile is on
     * @return True if tile is not traversable, false if it is
     */
    public boolean checkTileCollision(int col, int row) {

        int tileNum = gp.tileM.mapTileNum[col][row];
        Tile tile = gp.tileM.tiles[tileNum];

        return tile != null && tile.isNotTraversable(tileNum);
    }

    /**
     * CHECK TILE HAZARD
     * Runs hazard logic if the given entity is standing on a hazardous tile
     * @param entity Entity that is on top of the tile
     */
    public void checkTileHazard(Entity entity) {

        if (outOfBounds(entity.getWorldHitbox())) return;

        int tileNum = getCurrentTileNum(entity);
        Tile tile = gp.tileM.tiles[tileNum];
        if (tile == null) return;

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
    private int getCurrentTileNum(Entity entity) {

        Point center = entity.getCenterPoint();

        int col = center.x / gp.tileSize;
        int row = center.y / gp.tileSize;

        if (col < 0 || row < 0 || gp.maxWorldCol < col || gp.maxWorldRow < row) {
            return 0;
        }

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

        gp.player.setSafePoint(new Point(col * gp.tileSize, row * gp.tileSize));
    }

    /**
     * CHECK ICE TILE
     * Checks if the given entity is standing on ice
     * @param entity Entity standing on ice
     * @return True if entity is standing on ice, false if not
     */
    public boolean checkIceTile(Entity entity) {

        Point center = entity.getCenterPoint();

        int col = center.x / gp.tileSize;
        int row = center.y / gp.tileSize;

        return gp.tileM.mapTileNum[col][row] == TileManager.iceTile;
    }

    /**
     * CHECK MOVEMENT COLLISION
     * Checks if the given entity will move into a target in the list
     * Turns on entity collision if entity will collide into target
     * Ignores if entity is already colliding with target
     * @param entity Entity that is moving
     * @param targets List of entities to check movement collision on
     * @return Target that given entity will move into, null if not
     * @param <T> Objects that extend Entity class
     */
    public <T extends Entity> T checkMovementCollision(Entity entity, ArrayList<T> targets) {

        Rectangle currentRect = entity.getWorldHitbox();

        Rectangle futureRect = entity.getWorldHitbox();
        shiftRectangle(futureRect, entity.getMoveDirection(), entity.getSpeed());

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

    /**
     * CHECK OVERLAP COLLISION
     * Checks if the given entity is overlapping with a target in the list
     * @param entity Entity that is overlapping
     * @param targets List of entities to check overlap collision on
     * @return Target that given entity is overlapping with, null if not
     * @param <T> Objects that extend Entity class
     */
    public <T extends Entity> T checkOverlapCollision(Entity entity, ArrayList<T> targets) {

        Rectangle entityRect = entity.getWorldHitbox();

        for (T target : targets) {

            if (target == null || target == entity || target == entity.getUser() || target.isNotInteractable()) {
                continue;
            }

            Rectangle targetRect = target.getWorldHitbox();
            if (entityRect.intersects(targetRect)) {
                return target;
            }
        }

        return null;
    }

    /**
     * SET OVERLAP COLLISION
     * Turns on entity collision if it is currently overlapping with a target in the list
     * @param entity Entity that is overlapping
     * @param targets List of entities to check overlap collision on
     */
    public void setOverlapCollision(Entity entity, ArrayList<? extends Entity> targets) {

        Rectangle entityRect = entity.getWorldHitbox();

        for (Entity target : targets) {

            if (target == null || target == entity || target.isNotInteractable()) {
                continue;
            }

            Rectangle targetRect = target.getWorldHitbox();
            if (entityRect.intersects(targetRect)) {
                entity.setCollision(true);
                return;
            }
        }
    }

    /**
     * HAS OVERLAP COLLISION
     * Checks if entity is currently overlapping with target
     * @param entity Entity that is overlapping
     * @param target Entity to check overlap on
     * @return True if entity is overlapping target, false if not
     */
    public boolean hasOverlapCollision(Entity entity, Entity target) {

        if (target == null || target == entity || target.isNotInteractable()) {
            return false;
        }

        Rectangle entityRect = entity.getWorldHitbox();
        Rectangle targetRect = target.getWorldHitbox();

        return entityRect.intersects(targetRect);
    }

    /**
     * CHECK NPC
     * Checks if the given entity is looking at and within a half a tile of an NPC
     * @param entity Entity that is interacting with NPCs
     * @return the NPC the entity is looking at, null if not
     */
    public NPC checkNPC(Entity entity) {

        Rectangle futureRect = entity.getWorldHitbox();
        shiftRectangle(futureRect, entity.getMoveDirection(), gp.tileSize / 2);

        for (NPC npc : gp.npcs) {
            Rectangle targetRect = npc.getWorldHitbox();
            if (futureRect.intersects(targetRect)) {
                return npc;
            }
        }

        return null;
    }

    /**
     * CHECK DIG SPOT
     * Checks if the given entity within a tile of a DigSpot Object
     * @param entity Entity that is looking for a DigSpot
     * @return The DigSpot the entity is interacting with, null if not
     */
    public Object checkDigSpot(Entity entity) {

        Rectangle futureRect = entity.getWorldHitbox();
        shiftRectangle(futureRect, entity.getMoveDirection(), gp.tileSize);

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
     * Turns on entity collision if so
     * @param entity Entity to check collision for
     * @return True if entity is colliding with player and on same elevation, false if not
     */
    public boolean checkPlayer(Entity entity) {

        if (gp.player.isNotInteractable() || entity.isNotInteractable()) {
            return false;
        }

        Rectangle playerRect = gp.player.getWorldHitbox();

        Rectangle entityRect = entity.getWorldHitbox();
        shiftRectangle(entityRect, entity.getMoveDirection(), entity.getSpeed());

        if (!entityRect.intersects(playerRect)) {
            return false;
        }

        entity.setCollision(true);

        return entity.isOnSameElevation(gp.player);
    }

    /**
     * CHECK EXPLOSION
     * Loops over a 3x3 square around the given entity and runs explosion logic
     * @param entity Entity that is exploding (usually a Bomb Object)
     */
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

    /**
     * HANDLE EXPLOSION LOGIC
     * Damages the target if it is within radius of the explosion
     * Does nothing if the target is not interactable
     * @param entity The entity that is exploding
     * @param target The target that the entity is colliding with
     * @param entityCol The column of the explosion radius
     * @param entityRow The row of the explosion radius
     */
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

    /**
     * SHIFT RECTANGLE
     * Moves the given hitbox in the direction it is facing
     * Used for future collision detection
     * @param rectangle Hitbox of an entity
     * @param direction Direction rectangle is facing
     * @param offset Integer to shift
     */
    private void shiftRectangle(Rectangle rectangle, Direction direction, int offset) {

        switch (direction) {
            case UP -> rectangle.y -= offset;
            case UPLEFT -> {
                rectangle.x -= offset;
                rectangle.y -= offset;
            }
            case UPRIGHT -> {
                rectangle.x += offset;
                rectangle.y -= offset;
            }
            case DOWN -> rectangle.y += offset;
            case DOWNLEFT -> {
                rectangle.x -= offset;
                rectangle.y += offset;
            }
            case DOWNRIGHT -> {
                rectangle.x += offset;
                rectangle.y += offset;
            }
            case LEFT -> rectangle.x -= offset;
            case RIGHT -> rectangle.x += offset;
        }
    }

    /**
     * OUT OF BOUNDS
     * Check if the given hitbox is out of world boundary
     * @param rectangle Hitbox to check out of bounds on
     * @return True if out of world boundary, false if not
     */
    private boolean outOfBounds(Rectangle rectangle) {
        return (rectangle.y <= 0 || rectangle.y + rectangle.height >= gp.maxWorldRow * gp.tileSize ||
                rectangle.x <= 0 || rectangle.x + rectangle.width >= gp.maxWorldCol * gp.tileSize);
    }
}