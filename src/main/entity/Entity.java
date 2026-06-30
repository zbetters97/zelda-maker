package entity;

import application.GamePanel;
import application.GamePanel.Direction;
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
    protected int worldX, worldY;
    protected int screenX, screenY;
    protected int worldXStart, worldYStart;
    protected int tempScreenX, tempScreenY;
    private final int bounds = 999;

    /** MOVEMENT VALUES */
    protected Direction direction = DOWN;
    protected Action action = IDLE;
    protected int speed = 1;
    protected int defaultSpeed;
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
    protected String name;
    public boolean alive = true;
    public int health;
    public int maxHealth;
    protected Entity item;
    protected boolean invincible = false;
    protected int invincibleCounter = 0;
    public boolean dying = false;
    private int dyingCounter = 0;
    protected boolean opened = false;
    protected boolean isElevated = false;

    /** COMBAT VALUES */
    public int attack;
    public int defaultAttack;
    protected Rectangle attackBox = new Rectangle(0, 0, 0, 0);
    protected int attackNum = 1, attackCounter = 0;
    protected int swingSpeed1;
    protected int swingSpeed2;
    protected int swingSpeed3;
    protected boolean knockback;
    protected GamePanel.Direction knockbackDirection;
    protected int knockbackCounter = 0;

    /** COLLISION VALUES */
    protected boolean collisionOn = true;
    protected boolean canMove = true;
    protected Rectangle hitbox = new Rectangle(0, 0, 48, 48);
    protected int hitboxDefaultX;
    protected int hitboxDefaultY;
    protected int hitboxDefaultWidth = hitbox.width;
    protected int hitboxDefaultHeight = hitbox.height;

    /** INVENTORY VALUES */
    public int arrows = 0;

    /** PROJECTILE VALUES */
    public Projectile projectile;
    public Entity user;
    public int charge = 0;
    protected boolean grabbable = false;

    /** SPRITE ATTRIBUTES */
    protected int spriteNum = 1;
    protected int spriteCounter = 0;
    public BufferedImage image;
    protected BufferedImage
            up1, up2,  down1, down2, left1, left2, right1, right2,
            attackUp1, attackUp2, attackUp3, attackUp4, attackDown1, attackDown2, attackDown3, attackDown4,
            attackLeft1, attackLeft2, attackLeft3, attackLeft4, attackRight1, attackRight2, attackRight3, attackRight4;

    /** ENTITY TYPES */
    protected int entity_type = 0;
    public final int type_npc = 1;
    public final int type_enemy = 2;
    public final int type_item = 3;

    /** OBJECT TYPES */
    public final int type_object = 4;
    public final int type_projectile = 5;

    /**
     * CONSTRUCTOR
     * @param gp GamePanel
     */
    public Entity(GamePanel gp) {
        this.gp = gp;
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
        gp.cChecker.checkEntity(this, gp.obj);

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

        switch (direction) {
            case UP -> worldY -= speed;
            case UPLEFT -> {
                worldY -= (int) (speed - 0.5);
                worldX -= (int) (speed - 0.5);
            }
            case UPRIGHT -> {
                worldY -= (int) (speed - 0.5);
                worldX += (int) (speed - 0.5);
            }
            case DOWN -> worldY += speed;
            case DOWNLEFT -> {
                worldY += (int) (speed - 0.5);
                worldX -= (int) (speed - 0.5);
            }
            case DOWNRIGHT -> {
                worldY += speed;
                worldX += (int) (speed - 0.5);
            }
            case LEFT -> worldX -= speed;
            case RIGHT-> worldX += speed;
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

        Direction oppositeDirection = switch (direction) {
            case UP, UPLEFT, UPRIGHT -> DOWN;
            case DOWN, DOWNLEFT, DOWNRIGHT -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };

        return oppositeDirection;
    }

    /** PATH FINDING */
    public void searchPath(int goalCol, int goalRow) {

        if (action == ATTACKING) {
            return;
        }

        int startCol = (worldX + hitbox.x) / gp.tileSize;
        int startRow = (worldY + hitbox.y) / gp.tileSize;

        // SET PATH
        gp.pFinder.setNodes(startCol, startRow, goalCol, goalRow);

        // PATH FOUND
        if (gp.pFinder.search()) {

            // NEXT WORLD X & WORLD Y
            int nextX = gp.pFinder.pathList.getFirst().col * gp.tileSize;
            int nextY = gp.pFinder.pathList.getFirst().row * gp.tileSize;

            // ENTITY hitbox
            int eLeftX = worldX + hitbox.x;
            int eRightX = worldX + hitbox.x + hitbox.width;
            int eTopY = worldY + hitbox.y;
            int eBottomY = worldY + hitbox.y + hitbox.height;

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

        boolean playerWithinBounds = true;

        int tileDistance = (Math.abs(worldXStart - gp.player.worldX) + Math.abs(worldYStart - gp.player.worldY)) / gp.tileSize;

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
        int goalCol = (target.worldX + target.hitbox.x) / gp.tileSize;
        return goalCol;
    }
    public int getGoalRow(Entity target) {
        int goalRow = (target.worldY + target.hitbox.y) / gp.tileSize;
        return goalRow;
    }

    public int getTileDistance(Entity target) {
        int tileDistance = (getXDistance(target) + getYDistance(target)) / gp.tileSize;
        return tileDistance;
    }
    public int getXDistance(Entity target) {
        int xDistance = Math.abs(getCenterX() - target.getCenterX());
        return xDistance;
    }
    public int getYDistance(Entity target) {
        int yDistance = Math.abs(getCenterY() - target.getCenterY());
        return yDistance;
    }
    public int getCenterX() {
        int centerX = worldX + left1.getWidth() / 2;
        return centerX;
    }
    public int getCenterY() {
        int centerY = worldY + up1.getHeight() / 2;
        return centerY;
    }

    public boolean withinBounds() {

        boolean withinBounds = true;

        Direction tempDirection;
        int tempWorldX = worldX;
        int tempWorldY = worldY;

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

        int tileDistance = (Math.abs(worldXStart - tempWorldX) + Math.abs(worldYStart - tempWorldY)) / gp.tileSize;

        if (tileDistance > bounds) {
            withinBounds = false;
        }

        return withinBounds;
    }
    /** END PATH FINDING*/

    protected void interact(Entity user) {}

    /**
     * USE
     * Initiates using the Entity
     */
    protected void use() { }

    /**
     * ADD PROJECTILE
     * Adds new projectile entity to gp projectile list
     * @param projectile Projectile to be added
     */
    protected void addProjectile(Projectile projectile) {
        for (int i = 0; i < gp.projectile[0].length; i++) {
            if (gp.projectile[gp.currentMap][i] == null) {
                gp.projectile[gp.currentMap][i] = projectile;
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

        if (swingSpeed1 >= attackCounter) {
            attackNum = 1;
        }
        if (swingSpeed2 >= attackCounter && attackCounter > swingSpeed1) {

            attackNum = 2;

            // Save current X/Y
            int currentWorldX = worldX;
            int currentWorldY = worldY;

            // Adjust X/Y
            switch (direction) {
                case UP, UPLEFT, UPRIGHT -> {
                    worldY -= attackBox.height + hitbox.y;
                    hitbox.height = attackBox.height;
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> {
                    worldY += attackBox.height - hitbox.y;
                    hitbox.height = attackBox.height;
                }
                case LEFT -> {
                    worldX -= attackBox.width;
                    hitbox.width = attackBox.width;
                }
                case RIGHT -> {
                    worldX += attackBox.width - hitbox.y;
                    hitbox.width = attackBox.width;
                }
            }

            if (gp.cChecker.checkPlayer(this)) {
                damagePlayer(this);
            }

            // Restore hitbox
            worldX = currentWorldX;
            worldY = currentWorldY;
            hitbox.width = hitboxDefaultWidth;
            hitbox.height = hitboxDefaultHeight;
        }

        // Reset values
        if (attackCounter > swingSpeed2) {
            attackNum = 1;
            attackCounter = 0;
            action = IDLE;
        }
    }

    /**
     * GET ENEMY
     * Finds if the passed entity collides with an entity in gp.enemy
     * @param entity Target to check for collision on
     * @return An enemy that the target may be intersecting with
     */
    protected Entity getEnemy(Entity entity) {

        Entity enemy = null;

        int enemyIndex = gp.cChecker.checkEntity(entity, gp.enemy);
        if (enemyIndex != -1) {
            enemy = gp.enemy[gp.currentMap][enemyIndex];
        }

        return enemy;
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

        // Move in knockback direction
        switch (knockbackDirection) {
            case UP, UPLEFT, UPRIGHT -> worldY -= speed;
            case DOWN, DOWNLEFT, DOWNRIGHT -> worldY += speed;
            case LEFT -> worldX -= speed;
            case RIGHT -> worldX += speed;
        }

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
        if (gp.player.invincible) {
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
        if (gp.player.getAction() == GUARDING &&
                gp.player.getDirection() == getOppositeDirection(enemy.getDirection())) {
            return;
        }

        // Damage player
        gp.player.health -= damage;
        gp.player.invincible = true;
    }
    /** END COMBAT*/

    /**
     * CHECK DEATH
     * Checks if the entity has died
     */
    protected void checkDeath() { }

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
     * RESURRECT
     * Brings entity back to life
     */
    public void resurrect() {
        resetValues();
        alive = true;
        health = maxHealth;
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
        if (invincible) {
            playHurtAnimation(g2);
        }

        // Dying animation
        if (dying) {
            playDyingAnimation(g2);
        }

        // Draw sprite
        g2.drawImage(image, tempScreenX, tempScreenY, null);

        // Draw hitbox (debug)
        g2.drawRect(tempScreenX + hitbox.x, tempScreenY + hitbox.y, hitbox.width, hitbox.height);

        // Reset opacity
        changeAlpha(g2, 1f);
    }

    /**
     * OFF CENTER
     * Adjusts X, Y if near edge
     */
    public void adjustOffCenter() {
        tempScreenX = getScreenX();
        tempScreenY = getScreenY();

        if (gp.player.worldX < gp.player.screenX) {
            tempScreenX = worldX;
        }
        if (gp.player.worldY < gp.player.screenY) {
            tempScreenY = worldY;
        }

        // From player to right-edge of screen
        int rightOffset = gp.screenWidth - gp.player.screenX;

        //  From player to right-edge of world
        if (rightOffset > gp.worldWidth - gp.player.worldX) {
            tempScreenX = gp.screenWidth - (gp.worldWidth - worldX);
        }

        //  From player to bottom-edge of screen
        int bottomOffSet = gp.screenHeight - gp.player.screenY;

        //  From player to bottom-edge of world
        if (bottomOffSet > gp.worldHeight - gp.player.worldY) {
            tempScreenY = gp.screenHeight - (gp.worldHeight - worldY);
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
                    tempScreenY -= up1.getHeight();
                    yield attackUp1;
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown1;
                case LEFT -> {
                    tempScreenX -= left1.getWidth();
                    yield attackLeft1;
                }
                case RIGHT -> attackRight1;
            };
        } else if (attackNum == 2) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> {
                    tempScreenY -= up1.getHeight();
                    yield attackUp2;
                }
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown2;
                case LEFT -> {
                    tempScreenX -= left1.getWidth();
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
    public int getScreenX() {
        return worldX - gp.player.worldX + gp.player.screenX;
    }
    public int getScreenY() {
        return worldY - gp.player.worldY + gp.player.screenY;
    }

    public int getTempScreenX() {
        return tempScreenX;
    }
    public int getTempScreenY() {
        return tempScreenY;
    }

    public int getWorldX() {
        return worldX;
    }
    public void setWorldX(int worldX) {
        this.worldX = worldX;
    }
    public int getWorldY() {
        return worldY;
    }
    public void setWorldY(int worldY) {
        this.worldY = worldY;
    }

    public int getType() {
        return entity_type;
    }

    public String getName() {
        return name;
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

    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
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

    public Rectangle getHitbox() {
        return hitbox;
    }
    public int getHitboxDefaultX() {
        return hitboxDefaultX;
    }
    public int getHitboxDefaultY() {
        return hitboxDefaultY;
    }
}
