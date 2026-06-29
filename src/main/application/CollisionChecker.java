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
        int entityLeftWorldX = entity.getWorldX() + entity.getHitbox().x;
        int entityRightWorldX = entity.getWorldX() + entity.getHitbox().x + entity.getHitbox().width;
        int entityTopWorldY = entity.getWorldY() + entity.getHitbox().y;
        int entityBottomWorldY = entity.getWorldY() + entity.getHitbox().y + entity.getHitbox().height;

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
                entityTopRow = (entityTopWorldY - entity.getSpeed()) / gp.tileSize;

                // Tiles at top-left and top-right
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
            }
            case UPLEFT -> {
                // Tiles at top-left and left-top
                entityTopRow = (entityTopWorldY - entity.getSpeed()) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];

                entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / gp.tileSize;
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
            }
            case UPRIGHT -> {
                // Tiles at top-right and right-top
                entityTopRow = (entityTopWorldY - entity.getSpeed()) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];

                entityRightCol = (entityRightWorldX + entity.getSpeed()) / gp.tileSize;
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityTopRow];
            }
            case DOWN -> {
                entityBottomRow = (entityBottomWorldY + entity.getSpeed()) / gp.tileSize;

                // Tiles at bottom-left and bottom-right
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
            }
            case DOWNLEFT -> {

                // Tiles at bottom-left and left-bottom
                entityBottomRow = (entityBottomWorldY + entity.getSpeed()) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];

                entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / gp.tileSize;
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
            }
            case DOWNRIGHT -> {
                // Tiles at bottom-right and right-bottom
                entityBottomRow = (entityBottomWorldY + entity.getSpeed()) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];

                entityRightCol = (entityRightWorldX + entity.getSpeed()) / gp.tileSize;
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityRightCol][entityBottomRow];
            }
            case LEFT -> {
                entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / gp.tileSize;

                // Tiles at left-top and left-bottom
                tileNum1 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[gp.currentMap][entityLeftCol][entityBottomRow];
            }
            case RIGHT -> {
                entityRightCol = (entityRightWorldX + entity.getSpeed()) / gp.tileSize;

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

                if (entity.getHitbox().intersects(target.getHitbox())) {
                    entity.collisionOn = true;
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
                entity.collisionOn = true;
                return false;
            }
        }

        if (entity.getHitbox().intersects(gp.player.getHitbox())) {
            entity.collisionOn = true;
            contactedPlayer = true;
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