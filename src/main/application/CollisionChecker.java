package application;

import entity.Entity;

public record CollisionChecker(GamePanel gp) {

    /**
     * CHECK TILE
     * Checks if the given entity will collide with a tile
     * @param entity Entity to check collision for
     */
    public void checkTile(Entity entity) {

        // Collision box (left side, right side, top, bottom)
        int entityLeftWorldX = entity.worldX + entity.hitbox.x;
        int entityRightWorldX = entity.worldX + entity.hitbox.x + entity.hitbox.width;
        int entityTopWorldY = entity.worldY + entity.hitbox.y;
        int entityBottomWorldY = entity.worldY + entity.hitbox.y + entity.hitbox.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        // Prevent collision detection out of bounds
        if (entityTopRow <= 0 || entityBottomRow >= gp.maxWorldRow - 1 || entityLeftCol <= 0 || entityRightCol >= gp.maxWorldCol - 1) {
            return;
        }

        // Detect the two tiles player is interacting with
        int tileNum1, tileNum2;

        // Find tile player will interact with, factoring in speed
        switch (entity.getMoveDirection()) {
            case UP -> {
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;

                // Tiles at top-left and top-right
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
            }
            case UPLEFT -> {
                // Tiles at top-left and left-top
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];

                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
            }
            case UPRIGHT -> {
                // Tiles at top-right and right-top
                entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];

                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
            }
            case DOWN -> {
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;

                // Tiles at bottom-left and bottom-right
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
            }
            case DOWNLEFT -> {

                // Tiles at bottom-left and left-bottom
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];

                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
            }
            case DOWNRIGHT -> {
                // Tiles at bottom-right and right-bottom
                entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];

                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
            }
            case LEFT -> {
                entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;

                // Tiles at left-top and left-bottom
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
            }
            case RIGHT -> {
                entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;

                // Tiles at right-top and right-bottom
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
            }
            default -> {
                entity.collisionOn = true;
                return;
            }
        }

        if (gp.tileM.tiles[tileNum1].hasCollision || gp.tileM.tiles[tileNum2].hasCollision) {
            entity.collisionOn = true;
        }
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

                entity.hitbox.x = entity.worldX + entity.hitbox.x;
                entity.hitbox.y = entity.worldY + entity.hitbox.y;
                
                target.hitbox.x = target.worldX + target.hitbox.x;
                target.hitbox.y = target.worldY + target.hitbox.y;

                switch (entity.direction) {
                    case UP -> entity.hitbox.y -= entity.speed;
                    case UPLEFT -> {
                        entity.hitbox.y -= entity.speed;
                        entity.hitbox.x -= entity.speed;
                    }
                    case UPRIGHT -> {
                        entity.hitbox.y -= entity.speed;
                        entity.hitbox.x += entity.speed;
                    }
                    case DOWN -> entity.hitbox.y += entity.speed;
                    case DOWNLEFT -> {
                        entity.hitbox.y += entity.speed;
                        entity.hitbox.x -= entity.speed;
                    }
                    case DOWNRIGHT -> {
                        entity.hitbox.y += entity.speed;
                        entity.hitbox.x += entity.speed;
                    }
                    case LEFT -> entity.hitbox.x -= entity.speed;
                    case RIGHT -> entity.hitbox.x += entity.speed;
                }

                // Reset entity solid area
                entity.hitbox.x = entity.hitboxDefaultX;
                entity.hitbox.y = entity.hitboxDefaultY;

                // Reset object solid area
                target.hitbox.x = target.hitboxDefaultX;
                target.hitbox.y = target.hitboxDefaultY;
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

        entity.hitbox.x = entity.worldX + entity.hitbox.x;
        entity.hitbox.y = entity.worldY + entity.hitbox.y;

        gp.player.hitbox.x = gp.player.worldX + gp.player.hitbox.x;
        gp.player.hitbox.y = gp.player.worldY + gp.player.hitbox.y;

        switch (entity.direction) {
            case UP -> entity.hitbox.y -= entity.speed;
            case DOWN -> entity.hitbox.y += entity.speed;
            case LEFT -> entity.hitbox.x -= entity.speed;
            case RIGHT -> entity.hitbox.x += entity.speed;
            default -> {
                entity.collisionOn = true;
                return false;
            }
        }

        if (entity.hitbox.intersects(gp.player.hitbox)) {
            entity.collisionOn = true;
            contactedPlayer = true;
        }

        // Reset entity solid area
        entity.hitbox.x = entity.hitboxDefaultX;
        entity.hitbox.y = entity.hitboxDefaultY;

        // Reset player solid area
        gp.player.hitbox.x = gp.player.hitboxDefaultX;
        gp.player.hitbox.y = gp.player.hitboxDefaultY;

        return contactedPlayer;
    }
}