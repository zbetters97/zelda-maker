package entity.enemy;

import application.GamePanel;

import java.awt.*;

public class EMY_Blade extends Enemy {

    public static final String emyName = "Blade";
    private final Point startingPoint;
    private boolean lookingAtPlayer = false;
    private boolean returning = false;

    public EMY_Blade(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);
        startingPoint = new Point(worldX, worldY);

        defaultSpeed = 5;
        speed = defaultSpeed;

        defaultAttack = 2;
        attack = defaultAttack;
        knockbackPower = 1;

        invincible = true;

        hitbox = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        maxTileDistanceToPlayer = 8;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/enemy/blade_down_1");
    }

    @Override
    public void update() {
        if (returning) {
            returnToStart();
        }
        else {
            setAction();
        }
    }

    private void returnToStart() {

        boolean awayFromX = !(startingPoint.x - 3 <= worldPoint.x && worldPoint.x <= startingPoint.x + 3);
        boolean awayFromY = !(startingPoint.y - 3 <= worldPoint.y && worldPoint.y <= startingPoint.y + 3);

        // Slowly move back towards starting point
        if (awayFromX || awayFromY) {

            collisionOn = false;
            checkCollision();

            if (!collisionOn) {
                move(direction);
            }
        }
        // Completed return
        else {
            returning = false;
            speed = defaultSpeed;
        }
    }

    @Override
    protected void setAction() {

        updateDirection();

        // Able to reach player
        if (lookingAtPlayer) {

            collisionOn = false;
            checkCollision();

            // Move towards player if no collision and not far enough away from start
            if (!collisionOn && !tooFarAway()) {
                move(direction);
            }
            // Collision hit, move back towards starting point
            else {
                returning = true;
                lookingAtPlayer = false;
                speed = 2;
                direction = getOppositeDirection(direction);
            }
        }
    }

    @Override
    protected void updateDirection() {

        // Player too far away
        if (lookingAtPlayer || ai.getTileDistance(gp.player) > maxTileDistanceToPlayer) {
            return;
        }

        int dx = gp.player.getWorldPoint().x - worldPoint.x;
        int dy = gp.player.getWorldPoint().y - worldPoint.y;

        // Find if player is within cross-hairs, determine which direction
        if (dy < 0 && Math.abs(dx) <= gp.tileSize) {
            direction = GamePanel.Direction.UP;
        }
        else if (dy > 0 && Math.abs(dx) <= gp.tileSize) {
            direction = GamePanel.Direction.DOWN;
        }
        else if (dx < 0 && Math.abs(dy) <= gp.tileSize) {
            direction = GamePanel.Direction.LEFT;
        }
        else if (dx > 0 && Math.abs(dy) <= gp.tileSize) {
            direction = GamePanel.Direction.RIGHT;
        }
        else {
            return;
        }

        // Player within cross-hairs and can be reached
        if (pathOpen(direction)) {
            lookingAtPlayer = true;
        }
    }

    private boolean pathOpen(GamePanel.Direction direction) {

        int dx = 0, dy = 0;
        switch (direction) {
            case UP -> dy = -1;
            case DOWN -> dy = 1;
            case LEFT -> dx = -1;
            case RIGHT -> dx = 1;
        }

        int startCol = worldPoint.x / gp.tileSize;
        int startRow = worldPoint.y / gp.tileSize;
        int distance = ai.getTileDistance(gp.player);

        // Loop over each tile between self and player
        for (int i = 0; i <= distance; i++) {

            int col = startCol + dx * i;
            int row = startRow + dy * i;

            // Break if tile has collision
            if (gp.cChecker.checkTileCollision(col, row)) {
                return false;
            }
        }

        // No tile collision found, path is open
        return true;
    }

    private boolean tooFarAway() {

        int dx = Math.abs(worldPoint.x - startingPoint.x);
        int dy = Math.abs(worldPoint.y - startingPoint.y);
        int distance = (dx + dy) / gp.tileSize;

        // Distance traveled is further than max search distance
        return distance > maxTileDistanceToPlayer;
    }

    @Override
    public boolean canBeTargeted() {
        return false;
    }

    @Override
    protected void playHurtAnimation(Graphics2D g2) {

    }

    @Override
    protected void getSpriteImage() {
        image = sprite;
    }

    @Override
    public void setWorldPoint(Point worldPoint) {
        super.setWorldPoint(worldPoint);
        startingPoint.setLocation(new Point(worldPoint));
    }
}