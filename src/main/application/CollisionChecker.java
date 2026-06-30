package application;

import entity.Entity;
import tile.Tile;

import static entity.Entity.Action.DROWNING;
import static entity.Entity.Action.FALLING;

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
        // Player is on ground, save safe X/Y
        else if (entity == gp.player && !gp.player.getElevated()) {
            saveSafePoint();
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
        }
        else {
            entity.alive = false;
        }
    }
    private void saveSafePoint() {

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

    /**
     * CHECK ENTITY
     * @param entity Entity to check collision for
     * @param targets List of entities to check collision on
     * @return Index of entity in list
     */
    public int checkEntity(Entity entity, Entity[][] targets) {

        int entityIndex = -1;

        for (int i = 0; i < targets[gp.currentMap].length; i++) {
            if (targets[gp.currentMap][i] != null && targets[gp.currentMap][i] != entity) {

                Entity target = targets[gp.currentMap][i];

                entity.getHitbox().x = entity.getWorldX() + entity.getHitbox().x;
                entity.getHitbox().y = entity.getWorldY() + entity.getHitbox().y;
                
                target.getHitbox().x = target.getWorldX() + target.getHitbox().x;
                target.getHitbox().y = target.getWorldY() + target.getHitbox().y;

                switch (entity.getMoveDirection()) {
                    case UP -> entity.getHitbox().y -= entity.getSpeed();
                    case UPLEFT -> {
                        entity.getHitbox().y -= entity.getSpeed();
                        entity.getHitbox().x -= entity.getSpeed();
                    }
                    case UPRIGHT -> {
                        entity.getHitbox().y -= entity.getSpeed();
                        entity.getHitbox().x += entity.getSpeed();
                    }
                    case DOWN -> entity.getHitbox().y += entity.getSpeed();
                    case DOWNLEFT -> {
                        entity.getHitbox().y += entity.getSpeed();
                        entity.getHitbox().x -= entity.getSpeed();
                    }
                    case DOWNRIGHT -> {
                        entity.getHitbox().y += entity.getSpeed();
                        entity.getHitbox().x += entity.getSpeed();
                    }
                    case LEFT -> entity.getHitbox().x -= entity.getSpeed();
                    case RIGHT -> entity.getHitbox().x += entity.getSpeed();
                }

                boolean intersects = entity.getHitbox().intersects(target.getHitbox());
                boolean canCollide = entity.canCollideWith(target) && target.canCollideWith(entity);

                if (intersects && canCollide) {
                    entity.setCollision(true);
                    entityIndex = i;
                }

                // Reset entity solid area
                entity.getHitbox().x = entity.getHitboxDefaultX();
                entity.getHitbox().y = entity.getHitboxDefaultY();

                // Reset object solid area
                target.getHitbox().x = target.getHitboxDefaultX();
                target.getHitbox().y = target.getHitboxDefaultY();
            }
        }

        return entityIndex;
    }

    /**
     * CONTACT PLAYER
     * Checks if the given entity will collide with the player entity
     * @param entity Entity to check collision for
     */
    public boolean checkPlayer(Entity entity) {

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