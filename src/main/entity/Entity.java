package entity;

import application.GamePanel;
import application.GamePanel.Direction;
import entity.collectable.Collectable;
import entity.projectile.Projectile;

import static entity.Entity.Action.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import static application.GamePanel.Direction.*;

public class Entity {

    /**
     * ACTION enum
     * List of predefined actions an Entity can perform
     */
    public enum Action {
        IDLE(true, true, false),
        ATTACKING(false, false, false),
        SPINCHARGING(true, true, true),
        SPINNING(false, false, true),
        ROLLING(false, true, true),
        GUARDING(true, false, false),
        DIGGING(false, false, false),
        AIMING(true, true, true),
        THROWING(false, false, false),
        JUMPING(true, true, false),
        SOARING(true, true, false),
        FALLING(false, false, false),
        DROWNING(false, false, false);

        private final boolean allowsFacing;
        private final boolean allowsTranslation;
        private final boolean locksFacing;

        Action(boolean allowsFacing, boolean allowsTranslation, boolean locksFacing) {
            this.allowsFacing = allowsFacing;
            this.allowsTranslation = allowsTranslation;
            this.locksFacing = locksFacing;
        }

        public boolean allowsFacing() {
            return allowsFacing;
        }
        public boolean allowsTranslation() {
            return allowsTranslation;
        }
        public boolean locksFacing() { return locksFacing; }
    }

    protected GamePanel gp;

    /** GENERAL ATTRIBUTES */
    protected Point worldPoint = new Point(),
            screenPoint = new Point(),
            startPoint = new Point(),
            tempScreenPoint = new Point();
    private final int bounds = 999;

    /** COLLISION VALUES */
    protected boolean collisionOn = true;
    protected Rectangle hitbox = new Rectangle(0, 0, 48, 48);
    protected Point hitboxDefaultPoint = new Point();
    protected int hitboxDefaultWidth = hitbox.width;
    protected int hitboxDefaultHeight = hitbox.height;

    /** MOVEMENT VALUES */
    protected Direction direction = DOWN;
    protected Action action = IDLE;
    protected int speed = 1;
    protected int defaultSpeed;
    protected boolean canMove = true;
    protected boolean moving = false;
    protected boolean onPath = false;
    protected boolean pathCompleted = false;

    /** Z-TARGETING */
    protected boolean lockedOn;
    protected Entity lockedOnTarget;
    protected Direction lockonDirection;
    public final static int maxZTargetDistance = 7;

    /** ANIMATION VALUES */
    protected int actionLockCounter = 0;
    protected int animationSpeed;

    /** RPG VALUES */
    protected String name = "";
    public boolean alive = true;
    protected int maxHealth = 1;
    protected int health = 1;
    protected int value = 0;
    protected int maxRupees = 99;
    protected int rupees = 0;
    protected Entity item;
    protected boolean invincible = false;
    protected int invincibleCounter = 0;
    protected boolean stunned = false;
    protected int stunnedCounter = 0;
    public boolean dying = false;
    private int dyingCounter = 0;
    protected Collectable loot;
    protected boolean opened = false;
    protected boolean isElevated = false;
    protected boolean canSwim = false;

    /** COMBAT VALUES */
    protected int attack;
    protected int defaultAttack;
    protected Rectangle attackBox = new Rectangle(0, 0, 0, 0);
    protected int attackNum = 1, attackCounter = 0;
    protected int swingSpeed1;
    protected int swingSpeed2;
    protected int swingSpeed3;
    protected boolean knockback;
    protected GamePanel.Direction knockbackDirection;
    protected int knockbackCounter = 0;

    /** INVENTORY VALUES */
    protected int arrows = 0;

    /** PROJECTILE VALUES */
    public Projectile projectile;
    public Entity user;
    public int charge = 0;
    protected boolean latchable = false;

    /** SPRITE ATTRIBUTES */
    protected int spriteNum = 1;
    protected int spriteCounter = 0;
    public BufferedImage image;
    protected BufferedImage
            up1, up2, up3, down1, down2, down3, left1, left2, left3, right1, right2, right3,
            attackUp1, attackUp2, attackUp3, attackUp4, attackDown1, attackDown2, attackDown3, attackDown4,
            attackLeft1, attackLeft2, attackLeft3, attackLeft4, attackRight1, attackRight2, attackRight3, attackRight4;

    /** ENTITY TYPES */
    protected int entity_type = 0;
    public final int type_npc = 1;
    public final int type_enemy = 2;
    public final int type_item = 3;

    /** OBJECT TYPES */
    public final int type_collectable = 4;
    public final int type_object = 5;
    public final int type_projectile = 6;


    /** CONSTRUCTORS */
    public Entity(GamePanel gp) {
        this.gp = gp;
        getImages();
    }
    public Entity(GamePanel gp, String name) {
        this.gp = gp;
        this.name = name;
        getImages();
    }
    public Entity(GamePanel gp, int worldX, int worldY) {
        this.gp = gp;

        worldPoint.setLocation(worldX * gp.tileSize, worldY * gp.tileSize);
        startPoint.setLocation(worldX * gp.tileSize, worldY * gp.tileSize);

        getImages();
    }
    public Entity(GamePanel gp, int worldX, int worldY, String name) {
        this.gp = gp;
        this.name = name;

        worldPoint.setLocation(worldX * gp.tileSize, worldY * gp.tileSize);
        startPoint.setLocation(worldX * gp.tileSize, worldY * gp.tileSize);

        getImages();
    }
    public Entity(GamePanel gp, Entity user, String name) {
        this.gp = gp;
        this.user = user;
        this.name = name;

        entity_type = type_item;

        getImages();
    }

    /**
     * GET IMAGE
     */
    protected void getImages() { }

    /**
     * SETUP IMAGE
     * @param imagePath Path to image file
     * @param width Width of image
     * @param height Height of image
     * @return Scaled image
     */
    protected BufferedImage setupImage(String imagePath, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath + ".png")
            ));
            return GamePanel.utility.scaleImage(image, width, height);
        }
        catch (IOException e) {
            System.out.println("Error loading image:" + e.getMessage());
            return null;
        }
    }

    /**
     * SETUP IMAGE
     * @param imagePath Path to image file
     * @return Scaled image
     */
    protected BufferedImage setupImage(String imagePath) {

        BufferedImage image = null;

        try {
            image = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath + ".png")
            ));
            image = GamePanel.utility.scaleImage(image, gp.tileSize, gp.tileSize);
        }
        catch (IOException e) {
            System.out.println("Error loading image:" + e.getMessage());
        }

        return image;
    }

    /**
     * UPDATE
     * Updates the entity
     * Called every frame by GamePanel
     */
    public void update() {
        if (knockback) {
            handleKnockback();
            manageValues();
            // return;
        }
    }

    /**
     * SET ACTION
     */
    protected void setAction() { }

    /**
     * UPDATE DIRECTION
     * Handles logic involving moving the entity
     */
    protected void updateDirection() {
        checkCollision();
        move(direction);

        if (moving) {
            cycleSprites();
        }
    }

    /**
     * GET MOVE DIRECTION
     * Called by CollisionDetector
     * @return Current direction of the entity
     */
    public Direction getMoveDirection() {
        if (knockback) {
            return knockbackDirection;
        }
        else if (lockedOn || action.locksFacing()) {
            return lockonDirection;
        }
        else {
            return direction;
        }
    }

    /**
     * CHECK COLLISION
     * Checks if the entity collides with something
     */
    protected void checkCollision() {
        collisionOn = false;

        gp.cChecker.checkTile(this);
        gp.cChecker.checkMovementCollision(this, gp.enemy);
        gp.cChecker.checkMovementCollision(this, gp.npc);
        gp.cChecker.checkMovementCollision(this, gp.obj);

        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        boolean canHurtPlayer = entity_type == type_enemy;
        if (contactPlayer && canHurtPlayer) {
            damagePlayer(this);
        }

        gp.cChecker.checkHazard(this);
    }

    public boolean canCollideWith(Entity target) {
        return true;
    }

    /**
     * MOVE
     * Repositions the entity's X, Y based on direction and speed
     * Called by updateDirection() if o collision
     */
    protected void move(Direction direction) {

        if (!canMove || collisionOn) {
            moving = false;
            return;
        }

        moving = true;

        moveInDirection(direction);
    }
    protected void moveInDirection(Direction movingDirection) {
        switch (movingDirection) {
            case UP -> worldPoint.y -= speed;
            case UPLEFT -> {
                worldPoint.y -= (int) (speed - 0.5);
                worldPoint.x -= (int) (speed - 0.5);
            }
            case UPRIGHT -> {
                worldPoint.y -= (int) (speed - 0.5);
                worldPoint.x += (int) (speed - 0.5);
            }
            case DOWN -> worldPoint.y += speed;
            case DOWNLEFT -> {
                worldPoint.y += (int) (speed - 0.5);
                worldPoint.x -= (int) (speed - 0.5);
            }
            case DOWNRIGHT -> {
                worldPoint.y += speed;
                worldPoint.x += (int) (speed - 0.5);
            }
            case LEFT -> worldPoint.x -= speed;
            case RIGHT-> worldPoint.x += speed;
        }
    }

    /**
     * CYCLE SPRITES
     * Changes the animation counter for draw to render the correct sprite
     */
    protected void cycleSprites() {
        spriteCounter++;
        if (spriteCounter > animationSpeed && animationSpeed != 0) {

            if (spriteNum == 1) {
                spriteNum = 2;
            }
            else if (spriteNum == 2) {
                spriteNum = 1;
            }

            spriteCounter = 0;
        }
    }

    /**
     * SET DIRECTION
     * Randomly re-assigns the direction the Entity is facing
     * @param rate Integer frequency of updates (60 = 1 sec)
     */
    protected void setDirection(int rate) {

        actionLockCounter++;
        if (actionLockCounter >= rate) {

            int dir = 1 + (int) (Math.random() * 4);
            if (dir == 1) {
                direction = UP;
            }
            else if (dir == 2) {
                direction = DOWN;
            }
            else if (dir == 3) {
                direction = LEFT;
            }
            else {
                direction = RIGHT;
            }

            actionLockCounter = 0;
        }
    }

    protected Direction getOppositeDirection(Direction direction) {
        return switch (direction) {
            case UP, UPLEFT, UPRIGHT -> DOWN;
            case DOWN, DOWNLEFT, DOWNRIGHT -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    /** PATH FINDING */
    public void searchPath(int goalCol, int goalRow) {

        if (action == ATTACKING) {
            return;
        }

        int startCol = (worldPoint.x + hitbox.x) / gp.tileSize;
        int startRow = (worldPoint.y + hitbox.y) / gp.tileSize;

        // SET PATH
        gp.pFinder.setNodes(startCol, startRow, goalCol, goalRow);

        // PATH FOUND
        if (gp.pFinder.search()) {

            // NEXT WORLD X & WORLD Y
            int nextX = gp.pFinder.pathList.getFirst().col * gp.tileSize;
            int nextY = gp.pFinder.pathList.getFirst().row * gp.tileSize;

            // ENTITY hitbox
            int eLeftX = worldPoint.x + hitbox.x;
            int eRightX = worldPoint.x + hitbox.x + hitbox.width;
            int eTopY = worldPoint.y + hitbox.y;
            int eBottomY = worldPoint.y + hitbox.y + hitbox.height;

            // FIND DIRECTION TO NEXT NODE
            // UP OR DOWN
            if (eTopY > nextY && eLeftX >= nextX && eRightX < nextX + gp.tileSize) {
                direction = UP;
            }
            else if (eTopY < nextY && eLeftX >= nextX && eRightX < nextX + gp.tileSize) {
                direction = DOWN;
            }
            // LEFT OR RIGHT
            else if (eTopY >= nextY && eBottomY < nextY + gp.tileSize) {
                if (eLeftX > nextX) {
                    direction = LEFT;
                }
                if (eLeftX < nextX) {
                    direction = RIGHT;
                }
            }
            // UP OR LEFT
            else if (eTopY > nextY && eLeftX > nextX) {
                direction = UP;

                checkCollision();
                if (collisionOn) {
                    direction = LEFT;
                }
            }
            // UP OR RIGHT
            else if (eTopY > nextY && eLeftX < nextX) {
                direction = UP;

                checkCollision();
                if (collisionOn) {
                    direction = RIGHT;
                }
            }
            // DOWN OR LEFT
            else if (eTopY < nextY && eLeftX > nextX) {
                direction = DOWN;

                checkCollision();
                if (collisionOn) {
                    direction = LEFT;
                }
            }
            // DOWN OR RIGHT
            else if (eTopY < nextY && eLeftX < nextX) {
                direction = DOWN;

                checkCollision();
                if (collisionOn) {
                    direction = RIGHT;
                }
            }
        }
        // NO PATH FOUND
        else {
            onPath = false;
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

    public boolean playerWithinBounds() {

        // Don't search for player if not available
        if (gp.player.isNotAvailable()) {
            return false;
        }

        boolean playerWithinBounds = true;

        int tileDistance = (Math.abs(startPoint.x - gp.player.worldPoint.x) + Math.abs(startPoint.y - gp.player.worldPoint.y)) / gp.tileSize;

        if (tileDistance > bounds) {
            playerWithinBounds = false;
        }

        return playerWithinBounds;
    }
    public void isOnPath(Entity target, int distance) {
        if (getTileDistance(target) < distance) {
            onPath = true;
        }
    }
    public void isOffPath(Entity target, int distance) {
        if (getTileDistance(target) > distance || !withinBounds()) {
            onPath = false;
        }
    }
    public int getGoalCol(Entity target) {
        return target.getCenterX() / gp.tileSize;
    }
    public int getGoalRow(Entity target) {
        return target.getCenterY() / gp.tileSize;
    }

    public int getTileDistance(Entity target) {
        return (getXDistance(target) + getYDistance(target)) / gp.tileSize;
    }
    public int getXDistance(Entity target) {
        return Math.abs(getCenterX() - target.getCenterX());
    }
    public int getYDistance(Entity target) {
        return Math.abs(getCenterY() - target.getCenterY());
    }
    public int getCenterX() {
        return worldPoint.x + left1.getWidth() / 2;
    }
    public int getCenterY() {
        return worldPoint.y + up1.getHeight() / 2;
    }

    public boolean withinBounds() {

        boolean withinBounds = true;

        Direction tempDirection;
        int tempWorldX = worldPoint.x;
        int tempWorldY = worldPoint.y;

        if (lockedOn) {
            tempDirection = lockonDirection;
        }
        else {
            tempDirection = direction;
        }

        switch (tempDirection) {
            case UP -> tempWorldY -= speed;
            case UPLEFT -> {
                tempWorldY -= speed - 1;
                tempWorldX -= speed - 1;
            }
            case UPRIGHT -> {
                tempWorldY -= speed - 1;
                tempWorldX += speed - 1;
            }
            case DOWN -> tempWorldY += speed;
            case DOWNLEFT -> {
                tempWorldY += speed - 1;
                tempWorldX -= speed - 1;
            }
            case DOWNRIGHT -> {
                tempWorldY += speed;
                tempWorldX += speed - 1;
            }
            case LEFT -> tempWorldX -= speed;
            case RIGHT -> tempWorldX += speed;
        }

        int tileDistance = (Math.abs(startPoint.x - tempWorldX) + Math.abs(startPoint.y - tempWorldY)) / gp.tileSize;

        if (tileDistance > bounds) {
            withinBounds = false;
        }

        return withinBounds;
    }

    protected boolean lookingAtPlayer(int tolerance) {

        int dx = gp.player.worldPoint.x - worldPoint.x;
        int dy = gp.player.worldPoint.y - worldPoint.y;

        return switch (direction) {
            case UP -> dy < 0 && Math.abs(dx) <= tolerance;
            case DOWN -> dy > 0 && Math.abs(dx) <= tolerance;
            case LEFT -> dx < 0 && Math.abs(dy) <= tolerance;
            case RIGHT -> dx > 0 && Math.abs(dy) <= tolerance;
            default -> false;
        };
    }
    /** END PATH FINDING*/

    /**
     * USE
     * Initiates using the Entity
     */
    protected void use() { }

    protected void useProjectile(Projectile projectile, int seconds) {

        int i = new Random().nextInt(60 * seconds);
        if (i == 0 && !projectile.alive && actionLockCounter == 0) {
            projectile.set(worldPoint, direction, true, this);
            addProjectile(projectile);

            // Force 30 frame delay in between shots
            actionLockCounter = 30;
        }
    }

    /**
     * ADD PROJECTILE
     * Adds new projectile entity to gp projectile list
     * @param projectile Projectile to be added
     */
    protected void addProjectile(Projectile projectile) {
        for (int i = 0; i < gp.proj[0].length; i++) {
            if (gp.proj[gp.currentMap][i] == null) {
                gp.proj[gp.currentMap][i] = projectile;
                break;
            }
        }
    }

    /** COMBAT */
    protected void attack() {

    }
    protected void setAttacking(int rate, int straight, int horizontal) {

        boolean targetInRange = false;
        int xDis = getXDistance(gp.player);
        int yDis = getYDistance(gp.player);

        // If player is attacking within hitbox
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                if (gp.player.getCenterY() < getCenterY() && yDis < straight && xDis < horizontal) {
                    targetInRange = true;
                }
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                if (gp.player.getCenterY() > getCenterY() && yDis < straight && xDis < horizontal) {
                    targetInRange = true;
                }
            }
            case LEFT -> {
                if (gp.player.getCenterX() < getCenterX() && xDis < straight && yDis < horizontal) {
                    targetInRange = true;
                }
            }
            case RIGHT -> {
                if (gp.player.getCenterX() > getCenterX() && xDis < straight && yDis < horizontal) {
                    targetInRange = true;
                }
            }
        }

        // Player is within range
        if (targetInRange) {

            // Random chance to attack player
            int i = new Random().nextInt(rate);
            if (i == 0) {
                spriteNum = 1;
                spriteCounter = 0;
                action = ATTACKING;
            }
        }
    }
    /**
     * ATTACK
     * Attack logic for specific entity
     */
    protected void attacking() {

        attackCounter++;

        // Prevent glitch
        if (swingSpeed1 == 0 && swingSpeed2 == 0) {
            swingSpeed1 = 3;
            swingSpeed2 = 15;
        }

        if (attackCounter <= swingSpeed1) {
            attackNum = 1;
        }
        if (attackCounter <= swingSpeed2 && swingSpeed1 < attackCounter) {
            attackNum = 2;
            adjustSwingHitbox();
        }

        // Reset values
        if (swingSpeed2 < attackCounter) {
            attackNum = 1;
            attackCounter = 0;
            action = IDLE;
        }
    }
    protected void adjustSwingHitbox() {
        // Save current X/Y
        Point currentWorldPoint = new Point(worldPoint);

        adjustAttackBox();

        if (this == gp.player) {
            detectPlayerSwordCollision();
        }
        else {
            detectEnemySwordCollision();
        }

        // Restore hitbox
        worldPoint.setLocation(currentWorldPoint);
        hitbox.width = hitboxDefaultWidth;
        hitbox.height = hitboxDefaultHeight;
    }
    private void adjustAttackBox() {
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                worldPoint.y -= attackBox.height + hitbox.y;
                hitbox.height = attackBox.height;
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                worldPoint.y += attackBox.height - hitbox.y;
                hitbox.height = attackBox.height;
            }
            case LEFT -> {
                worldPoint.x -= attackBox.width;
                hitbox.width = attackBox.width;
            }
            case RIGHT -> {
                worldPoint.x += attackBox.width - hitbox.y;
                hitbox.width = attackBox.width;
            }
        }
    }
    protected void detectPlayerSwordCollision() {
        // Find enemy that intersects collision box
        Entity enemy = overlapEnemy(this);

        // Sword collides with enemy, apply damage
        if (enemy != null && !enemy.invincible) {
            damageEnemy(enemy);
        }

        int proj = gp.cChecker.checkOverlapCollision(this, gp.proj);
        if (proj != -1) {
            Projectile projectile = gp.proj[gp.currentMap][proj];

            if (projectile.canBeDeflected(false)) {
                projectile.deflect(this);
            }
        }

        int obj = gp.cChecker.checkOverlapCollision(this, gp.obj);
        if (obj != -1) {
            gp.obj[gp.currentMap][obj].interact();
        }
    }
    private void detectEnemySwordCollision() {
        if (gp.cChecker.checkPlayer(this)) {
            damagePlayer(this);
        }
    }

    protected Entity moveIntoEnemy(Entity entity) {

        Entity enemy = null;

        int enemyIndex = gp.cChecker.checkMovementCollision(entity, gp.enemy);
        if (enemyIndex != -1) {
            enemy = gp.enemy[gp.currentMap][enemyIndex];
        }

        return enemy;
    }
    protected Entity overlapEnemy(Entity entity) {

        Entity enemy = null;

        int enemyIndex = gp.cChecker.checkOverlapCollision(entity, gp.enemy);
        if (enemyIndex != -1) {
            enemy = gp.enemy[gp.currentMap][enemyIndex];
        }

        return enemy;
    }

    /**
     * SHIFT TO CENTER
     * Moves the current entity to the center of the current tile
     * Useful for Pit/Water tile handling
     */
    public void shiftToCenter() {
        Point center = getCenterPoint();

        worldPoint.setLocation(
                (center.x / gp.tileSize) * gp.tileSize,
                (center.y / gp.tileSize) * gp.tileSize
        );
    }
    public Point getCenterPoint() {
        return new Point(
                worldPoint.x + hitbox.x + hitbox.width / 2,
                worldPoint.y + hitbox.y + hitbox.height / 2
        );
    }

    /**
     * DAMAGE ENEMY
     * Handles logic when an enemy is hit by an attack
     * @param target Enemy being damaged
     */
    protected void damageEnemy(Entity target) {

        // Damage same as player attack value
        int damage = attack;

        // Keep damage at or above 0
        if (damage < 0) {
            damage = 0;
        }

        // Damage target
        target.health -= damage;
        target.invincible = true;
        target.reactToDamage();

        // Target loses all health, start dying animation
        if (target.health <= 0) {
            target.dying = true;
        }

        // Push target back
        setKnockback(target, this, 1);
    }

    protected void reactToDamage() {

    }

    /**
     * SET KNOCKBACK
     * Starts the knockback animation on the target
     * @param target Entity hit by knockback
     * @param attacker Entity that provided the knockback
     * @param knockbackPower Power of the knockback
     */
    protected void setKnockback(Entity target, Entity attacker, int knockbackPower) {
        target.knockback = true;

        // Direction attacker was facing when hit
        target.knockbackDirection = attacker.getMoveDirection();

        target.speed += knockbackPower;
    }

    /**
     * HANDLE KNOCKBACK
     * Runs the knockback animation
     */
    protected void handleKnockback() {

        collisionOn = false;
        checkCollision();

        // Don't knockback if collision
        if (collisionOn) {
            knockback = false;
            knockbackCounter = 0;
            speed = defaultSpeed;
            return;
        }

        moveInDirection(knockbackDirection);

        // Run for 10 frames
        knockbackCounter++;
        if (knockbackCounter == 10) {
            knockback = false;
            knockbackCounter = 0;
            speed = defaultSpeed;
        }
    }

    /**
     * DAMAGE PLAYER
     * Handles logic for damaging the player
     * @param enemy The enemy attacking the player
     */
    protected void damagePlayer(Entity enemy) {

        // Player can't be damaged
        if (gp.player.invincible || gp.player.isNotAvailable()) {
            return;
        }

        int damage = enemy.attack;

        // Keep damage at or above 0
        if (damage < 0) {
            damage = 0;
        }

        // Knockback player
        setKnockback(gp.player, enemy, 1);

        // Player blocked with shield
        boolean facingEnemy = gp.player.getDirection() == getOppositeDirection(enemy.getDirection());
        if (gp.player.getAction() == GUARDING && facingEnemy) {

            if (enemy.canBeDeflected(true)) {
                enemy.deflect(gp.player);
            }

            return;
        }

        // Damage player
        gp.player.health -= damage;
        gp.player.invincible = true;
    }

    protected boolean canBeDeflected(boolean usingShield) {
        return false;
    }

    protected void deflect(Entity target) {
        collisionOn = false;
        alive = true;
        health = maxHealth;
        user = target;
        direction = target.getDirection();
        speed = defaultSpeed;
    }
    /** END COMBAT*/

    public boolean isNotAvailable() {
        return !alive || dying || action == FALLING || action == DROWNING;
    }

    /**
     * CHECK DEATH
     * Checks if the entity has died
     */
    protected void checkDeath() { }

    protected void dropItem(Collectable droppedItem) {
        for (int i = 0; i < gp.col[0].length; i++) {
            if (gp.col[gp.currentMap][i] == null) {
                gp.col[gp.currentMap][i] = droppedItem;
                gp.col[gp.currentMap][i].setWorldPoint(worldPoint);
                break;
            }
        }
    }

    /**
     * MANAGE VALUES
     * Resets or reassigns entity attributes
     * Called at the end of update
     */
    protected void manageValues() {
        // Shield after taking damage
        if (invincible) {
            invincibleCounter++;

            // Refresh time
            if (invincibleCounter > 45) {
                invincibleCounter = 0;
                invincible = false;
            }
        }
        else if (stunned) {
            stunnedCounter++;

            if (stunnedCounter > 45) {
                stunnedCounter = 0;
                stunned = false;
            }
        }
    }

    /**
     * RESET VALUES
     * Resets values to defaults
     */
    public void resetValues() {

        action = IDLE;
        alive = true;

        attackNum = 1; attackCounter = 0;
        knockback = false; knockbackCounter = 0;
        invincible = false; invincibleCounter = 0;
        stunned = false; stunnedCounter = 0;
        dying = false; dyingCounter = 0;

        speed = defaultSpeed;
        collisionOn = false;
        canMove = true; moving = false;
        onPath = false; pathCompleted = false;

        lockedOn = false; lockedOnTarget = null;

        charge = 0;
        actionLockCounter = 0;
        spriteNum = 1; spriteCounter = 0;

        opened = false;
        isElevated = false;
    }

    /**
     * DRAW
     * Draws the sprite data to the graphics
     * @param g2 GamePanel
     */
    public void draw(Graphics2D g2) {

        adjustOffCenter();
        getSpriteImage();

        // Flash sprite if hurt
        if (invincible && entity_type == type_enemy) {
            playHurtAnimation(g2);
        }

        // Dying animation
        if (dying) {
            playDyingAnimation(g2);
        }

        // Draw sprite
        g2.drawImage(image, tempScreenPoint.x, tempScreenPoint.y, null);

        // Draw hitbox (debug)
        g2.drawRect(tempScreenPoint.x + hitbox.x, tempScreenPoint.y + hitbox.y, hitbox.width, hitbox.height);

        // Reset opacity
        changeAlpha(g2, 1f);
    }

    /**
     * OFF CENTER
     * Adjusts X, Y if near edge
     */
    public void adjustOffCenter() {

        tempScreenPoint = new Point(getScreenPoint());

        if (gp.player.worldPoint.x < gp.player.screenPoint.x) {
            tempScreenPoint.x = worldPoint.x;
        }
        if (gp.player.worldPoint.y < gp.player.screenPoint.y) {
            tempScreenPoint.y = worldPoint.y;
        }

        // From player to right-edge of screen
        int rightOffset = gp.screenWidth - gp.player.screenPoint.x;

        //  From player to right-edge of world
        if (rightOffset > gp.worldWidth - gp.player.worldPoint.x) {
            tempScreenPoint.x = gp.screenWidth - (gp.worldWidth - worldPoint.x);
        }

        //  From player to bottom-edge of screen
        int bottomOffSet = gp.screenHeight - gp.player.screenPoint.y;

        //  From player to bottom-edge of world
        if (bottomOffSet > gp.worldHeight - gp.player.worldPoint.y) {
            tempScreenPoint.y = gp.screenHeight - (gp.worldHeight - worldPoint.y);
        }
    }

    /** GET CURRENT SPRITE TO DRAW **/
    protected void getSpriteImage() {
        if (action == ATTACKING) {
            getAttackImage();
        }
        else {
            getIdleImage();
        }
    }
    private void getIdleImage() {
        if (spriteNum == 1) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down1;
                case LEFT -> left1;
                case RIGHT -> right1;
            };
        } else if (spriteNum == 2) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down2;
                case LEFT -> left2;
                case RIGHT -> right2;
            };
        }
    }
    private void getAttackImage() {
        if (attackNum == 1) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> {
                    tempScreenPoint.y -= up1.getHeight();
                    yield attackUp1;
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown1;
                case LEFT -> {
                    tempScreenPoint.x -= left1.getWidth();
                    yield attackLeft1;
                }
                case RIGHT -> attackRight1;
            };
        } else if (attackNum == 2) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> {
                    tempScreenPoint.y -= up1.getHeight();
                    yield attackUp2;
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown2;
                case LEFT -> {
                    tempScreenPoint.x -= left1.getWidth();
                    yield attackLeft2;
                }
                case RIGHT -> attackRight2;
            };
        }
    }

    /** SPRITE ANIMATIONS **/
    protected void playHurtAnimation(Graphics2D g2) {
        if (invincibleCounter % 5 == 0) {
            changeAlpha(g2, 0.2f);
        }
    }
    private void playDyingAnimation(Graphics2D g2) {

        invincible = false;

        dyingCounter++;
        if (dyingCounter % 5 == 0) {
            changeAlpha(g2, 0.2f);
        }

        if (dyingCounter >= 40) {
            alive = false;
        }
    }

    /**
     * CHANGE ALPHA
     * Changes the opacity of the image
     * @param g2 Graphics2D
     * @param alphaValue Opacity value
     */
    protected void changeAlpha(Graphics2D g2, float alphaValue) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
    }

    /** GETTERS and SETTERS */
    public Point getScreenPoint() {
        return new Point(
                worldPoint.x - gp.player.worldPoint.x + gp.player.screenPoint.x,
                worldPoint.y - gp.player.worldPoint.y + gp.player.screenPoint.y
        );
    }
    public Point getTempScreenPoint() {
        return tempScreenPoint;
    }

    public Point getWorldPoint() {
        return worldPoint;
    }
    public int getWorldPointY() {
        return worldPoint.y;
    }
    public void setWorldPoint(Point worldPoint) {
        this.worldPoint.setLocation(worldPoint);
    }
    public void setWorldPointX(int x) {
        this.worldPoint.x = x;
    }
    public void setWorldPointY(int y) {
        this.worldPoint.y = y;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }
    public Rectangle getWorldHitbox() {
        return new Rectangle(
                worldPoint.x + hitboxDefaultPoint.x,
                worldPoint.y + hitboxDefaultPoint.y,
                hitbox.width,
                hitbox.height);
    }

    public int getType() {
        return entity_type;
    }

    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean getCollision() {
        return collisionOn;
    }
    public void setCollision(boolean collisionOn) {
        this.collisionOn = collisionOn;
    }

    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }
    public void addHealth(int change) {
        this.health += change;

        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public int getMaxRupees() {
        return maxRupees;
    }

    public int getRupees() {
        return rupees;
    }
    public void addRupees(int amount) {
        this.rupees += amount;
    }

    public int getSpeed() {
        return speed;
    }
    public void modifySpeed(int change) {
        speed += change;
    }

    public void modifyAttack(int change) {
        attack += change;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean getOpened() {
        return opened;
    }
    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean getElevated() {
        return isElevated;
    }
    public void setElevated(boolean isElevated) {
        this.isElevated = isElevated;
    }
    public boolean isOnSameElevation(Entity target) {
        return target.getElevated() == isElevated;
    }

    public Entity getItem() {
        return item;
    }

    public int getArrows() {
        return arrows;
    }
    public void addArrows(int arrows) {
        this.arrows += arrows;
    }

    public boolean isLatchable() {
        return latchable;
    }

    public boolean getCanSwim() {
        return canSwim;
    }
}
