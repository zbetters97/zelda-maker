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

        // Collision box (top, bottom, left, right)
        int entityTopWorldY = entity.getWorldY() + entity.getHitbox().y;
        int entityBottomWorldY = entity.getWorldY() + entity.getHitbox().y + entity.getHitbox().height - 1;
        int entityLeftWorldX = entity.getWorldX() + entity.getHitbox().x;
        int entityRightWorldX = entity.getWorldX() + entity.getHitbox().x + entity.getHitbox().width - 1;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        // Prevent collision detection out of bounds
        if (entityTopRow <= 0 || entityBottomRow >= gp.maxWorldRow - 1 || entityLeftCol <= 0 || entityRightCol >= gp.maxWorldCol - 1) {
            return;
        }

        int dx = 0;
        int dy = 0;

        switch (entity.getMoveDirection()) {
            case UP -> dy -= entity.getSpeed();
            case UPLEFT -> {
                dx -= entity.getSpeed();
                dy -= entity.getSpeed();
            }
            case UPRIGHT -> {
                dx = entity.getSpeed();
                dy -= entity.getSpeed();
            }
            case DOWN -> dy = entity.getSpeed();
            case DOWNLEFT -> {
                dx -= entity.getSpeed();
                dy = entity.getSpeed();
            }
            case DOWNRIGHT -> {
                dx = entity.getSpeed();
                dy = entity.getSpeed();
            }
            case LEFT -> dx -= entity.getSpeed();
            case RIGHT -> dx = entity.getSpeed();
        }

        int leftCol = (entityLeftWorldX + dx) / gp.tileSize;
        int rightCol = (entityRightWorldX + dx) / gp.tileSize;
        int topRow = (entityTopWorldY + dy) / gp.tileSize;
        int bottomRow = (entityBottomWorldY + dy) / gp.tileSize;

        // Detect the two tiles player is interacting with
        Tile tile1 = null, tile2 = null;

        switch (entity.getMoveDirection()) {
            case UP -> {
                tile1 = getTileAtColRow(leftCol, topRow);
                tile2 = getTileAtColRow(rightCol, topRow);
            }
            case UPLEFT -> {
                tile1 = getTileAtColRow(entityLeftCol, topRow);
                tile2 = getTileAtColRow(leftCol, topRow);
            }
            case UPRIGHT -> {
                tile1 = getTileAtColRow(entityRightCol, topRow);
                tile2 = getTileAtColRow(rightCol, topRow);
            }
            case DOWN -> {
                tile1 = getTileAtColRow(leftCol, bottomRow);
                tile2 = getTileAtColRow(rightCol, bottomRow);
            }
            case DOWNLEFT -> {
                tile1 = getTileAtColRow(entityLeftCol, bottomRow);
                tile2 = getTileAtColRow(leftCol, bottomRow);
            }
            case DOWNRIGHT -> {
                tile1 = getTileAtColRow(entityRightCol, bottomRow);
                tile2 = getTileAtColRow(rightCol, bottomRow);
            }
            case LEFT -> {
                tile1 = getTileAtColRow(leftCol, topRow);
                tile2 = getTileAtColRow(leftCol, bottomRow);
            }
            case RIGHT -> {
                tile1 = getTileAtColRow(rightCol, topRow);
                tile2 = getTileAtColRow(rightCol, bottomRow);
            }
        }

        checkTileCollision(entity, tile1, tile2);
    }
    private Tile getTileAtColRow(int col, int row) {
        return gp.tileM.tiles[gp.tileM.mapTileNum[gp.currentMap][col][row]];
    }
    private void checkTileCollision(Entity entity, Tile tile1, Tile tile2) {
        // Bottomless pits
        if (tile1.isPit || tile2.isPit) {

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
        if (tile1.isWater || tile2.isWater) {

            // Entity in air
            if (entity.getElevated()) {
                return;
            }

            // NPCs and enemies
            if (entity.getType() == entity.type_npc || entity.getType() == entity.type_enemy) {
                entity.setCollision(true);
            }
        }
        // Collision titles
        else if (tile1.hasCollision || tile2.hasCollision) {
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
        int centerX = entity.getWorldX() + entity.getHitbox().x + entity.getHitbox().width / 2;
        int centerY = entity.getWorldY() + entity.getHitbox().y + entity.getHitbox().height / 2;

        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;

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
            entity.alive = false;
        }
    }
    private void handleWater(Entity entity) {
        if (entity.getElevated()) {
            return;
        }

        if (entity == gp.player) {
            gp.player.setAction(DROWNING);
            gp.player.shiftToCenter();
        }
        else {
            entity.alive = false;
        }
    }
    private void setSafePoint() {

        // Get current X/Y based on hitbox center
        int centerX = gp.player.getWorldX() + gp.player.getHitbox().x + gp.player.getHitbox().width / 2;
        int centerY = gp.player.getWorldY() + gp.player.getHitbox().y + gp.player.getHitbox().height / 2;

        // Snap to tile size
        int safeX = (centerX / gp.tileSize) * gp.tileSize;
        int safeY = (centerY / gp.tileSize) * gp.tileSize;

        // Store restore point
        gp.player.safeWorldX = safeX;
        gp.player.safeWorldY = safeY;
    }

    public int checkMovementCollision(Entity entity, Entity[][] targets) {

        int entityIndex = -1;

        Rectangle futureRect = getWorldHitbox(entity);

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

        Rectangle currentRect = getWorldHitbox(entity);

        for (int i = 0; i < targets[gp.currentMap].length; i++) {

            Entity target = targets[gp.currentMap][i];

            if (target == null || target == entity) {
                continue;
            }

            Rectangle targetRect = getWorldHitbox(target);

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

        Rectangle entityRect = getWorldHitbox(entity);

        for (int i = 0; i < targets[gp.currentMap].length; i++) {

            Entity target = targets[gp.currentMap][i];

            if (target == null || target == entity) {
                continue;
            }

            Rectangle targetRect = getWorldHitbox(target);
            boolean canCollide = entity.canCollideWith(target) && target.canCollideWith(entity);

            if (entityRect.intersects(targetRect) && canCollide) {
                entityIndex = i;
                break;
            }
        }

        return entityIndex;
    }
    private Rectangle getWorldHitbox(Entity entity) {

        Rectangle rect = new Rectangle(entity.getHitbox());

        rect.x = entity.getWorldX() + entity.getHitboxDefaultX();
        rect.y = entity.getWorldY() + entity.getHitboxDefaultY();

        return rect;
    }

    /**
     * CONTACT PLAYER
     * Checks if the given entity will collide with the player entity
     * @param entity Entity to check collision for
     */
    public boolean checkPlayer(Entity entity) {

        if (gp.player.getAction() == FALLING || gp.player.getAction() == DROWNING) {
            return false;
        }

        boolean contactedPlayer = false;

        entity.getHitbox().x = entity.getWorldX() + entity.getHitbox().x;
        entity.getHitbox().y = entity.getWorldY() + entity.getHitbox().y;

        gp.player.getHitbox().x = gp.player.getWorldX() + gp.player.getHitbox().x;
        gp.player.getHitbox().y = gp.player.getWorldY() + gp.player.getHitbox().y;

        switch (entity.getDirection()) {
            case UP -> entity.getHitbox().y -= entity.getSpeed();
            case DOWN -> entity.getHitbox().y += entity.getSpeed();
            case LEFT -> entity.getHitbox().x -= entity.getSpeed();
            case RIGHT -> entity.getHitbox().x += entity.getSpeed();
            default -> {
                entity.setCollision(true);
                return false;
            }
        }

        if (entity.getHitbox().intersects(gp.player.getHitbox())) {
            entity.setCollision(true);

            // Player and self are on same elevation, player contacted
            contactedPlayer = entity.isOnSameElevation(gp.player);
        }

        // Reset entity solid area
        entity.getHitbox().x = entity.getHitboxDefaultX();
        entity.getHitbox().y = entity.getHitboxDefaultY();

        // Reset player solid area
        gp.player.getHitbox().x = gp.player.getHitboxDefaultX();
        gp.player.getHitbox().y = gp.player.getHitboxDefaultY();

        return contactedPlayer;
    }
}