package entity;

import ai.EntityAI;
import application.GamePanel;
import application.GamePanel.Direction;
import entity.collectable.Collectable;
import entity.enemy.Enemy;
import entity.item.Item;
import entity.object.Object;
import entity.projectile.Projectile;

import static entity.Entity.Action.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
        RUNNING(true, true, false),
        AIMING(true, true, true),
        GRABBING(false, false, false),
        CARRYING(true, true, false),
        THROWING(false, false, false),
        JUMPING(true, true, false),
        SWINGING(false, false, false),
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

    /** DRAW LAYER enum */
    public enum DrawLayer {
        GROUND,
        ENTITY,
        ABOVE
    }

    protected GamePanel gp;

    /** GENERAL ATTRIBUTES */
    protected Point worldPoint = new Point(),
            screenPoint = new Point(),
            startPoint = new Point(),
            drawOffset = new Point();

    /** ANIMATION VALUES */
    protected int actionLockCounter = 0;
    protected int animationSpeed;

    /** COLLISION VALUES */
    protected boolean collisionOn = true;
    protected Rectangle hitbox = new Rectangle(0, 0, 48, 48);
    protected Point hitboxDefaultPoint = new Point();
    protected int hitboxDefaultWidth = hitbox.width;
    protected int hitboxDefaultHeight = hitbox.height;
    protected boolean interactable = true;

    /** MOVEMENT VALUES */
    protected Direction direction = DOWN;
    protected Action action = IDLE;
    protected String availableAction = "";
    protected int defaultSpeed = 1, speed = defaultSpeed;
    protected boolean canMove = true;
    protected boolean moving;
    protected boolean onPath;
    protected boolean pathCompleted;
    protected EntityAI ai;

    /** RPG VALUES */
    protected String name, formattedName;
    protected String description;
    protected int maxHealth = 1, health = maxHealth;
    protected int value;
    protected boolean opened, elevated;
    protected boolean canSwim, needsWater;
    protected Entity grabbedBy;
    protected Object grabbedObject;
    protected Entity capturedBy, capturedEntity;

    /** COMBAT VALUES */
    protected boolean alive = true;
    protected int defaultAttack, attack = defaultAttack;
    protected Rectangle attackBox = new Rectangle(0, 0, 0, 0);
    protected int attackNum = 1, attackCounter;
    protected int swingSpeed1, swingSpeed2, swingSpeed3;
    protected int knockbackPower;
    protected GamePanel.Direction knockbackDirection;
    protected boolean knockback, buzzing, shielded, stunned, invincible, dying;
    protected int knockbackCounter, stunnedCounter, invincibleCounter, dyingCounter;

    /** Z-TARGETING */
    protected boolean lockedOn;
    protected Entity lockedOnTarget;
    protected Direction lockonDirection;
    public final static int maxZTargetDistance = 7;

    /** INVENTORY VALUES */
    protected Entity loot;
    protected final ArrayList<Item> items = new ArrayList<>();
    protected Item item;
    protected Entity newItem;
    protected int maxRupees = 99, rupees, keys;
    protected boolean hasBossKey;
    protected int maxArrows = 30, arrows = maxArrows, maxBombs = 30, bombs = maxBombs;

    /** PROJECTILE VALUES */
    public Projectile projectile;
    protected Entity user;
    public int charge;

    /** SPRITE ATTRIBUTES */
    protected boolean drawing = true;
    protected int spriteNum = 1, spriteCounter;
    protected BufferedImage sprite, image,
            up1, up2, up3, down1, down2, left1, left2, right1, right2,
            attackUp1, attackUp2, attackUp3, attackUp4, attackDown1, attackDown2, attackDown3, attackDown4,
            attackLeft1, attackLeft2, attackLeft3, attackLeft4, attackRight1, attackRight2, attackRight3, attackRight4;

    /** CONSTRUCTORS */
    public Entity(GamePanel gp) {
        this(gp, 0, 0, "", null);
    }
    public Entity(GamePanel gp, String name) {
        this(gp, 0, 0, name, null);
    }
    public Entity(GamePanel gp, int worldX, int worldY) {
        this(gp, worldX, worldY, "", null);
    }
    public Entity(GamePanel gp, int worldX, int worldY, String name) {
        this(gp, worldX, worldY, name, null);
    }
    public Entity(GamePanel gp, String name, Entity user) {
        this(gp, 0, 0, name, user);
    }
    private Entity(GamePanel gp, int worldX, int worldY, String name, Entity user) {

        this.gp = gp;
        this.user = user;
        this.name = name;

        if (worldX != 0 || worldY != 0) {
            worldPoint.setLocation(worldX * gp.tileSize,worldY * gp.tileSize);
            startPoint.setLocation(worldX * gp.tileSize,worldY * gp.tileSize);
        }

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
        return GamePanel.utility.setupImage(imagePath, width, height);
    }

    /**
     * SETUP IMAGE
     * @param imagePath Path to image file
     * @return Scaled image
     */
    protected BufferedImage setupImage(String imagePath) {
        return GamePanel.utility.setupImage(gp, imagePath);
    }

    public void update() {

        if (isCaptured()) {
            handleCapture();
        }

        manageValues();
    }

    protected void handleCapture() {

    }

    protected void setAction() { }

    public boolean canCollideWith(Entity target) {
        return true;
    }
    public void forceMove(Direction forcedDirection) {

        direction = fixDirection(forcedDirection);

        collisionOn = false;
        checkCollision();

        if (collisionOn) {
            moving = false;
            return;
        }

        moving = true;
        moveInDirection(direction);

        cycleSprites();
    }
    private Direction fixDirection(Direction direction) {
        return switch (direction) {
            case UP, UPLEFT, UPRIGHT -> UP;
            case DOWN, DOWNLEFT, DOWNRIGHT -> DOWN;
            case LEFT -> LEFT;
            case RIGHT -> RIGHT;
        };
    }

    protected void setDirection(int rate) {

        if (isCaptured()) return;

        if (rate <= ++actionLockCounter) {

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

    protected void updateDirection() {

        if (isCaptured()) return;

        checkCollision();
        move(direction);
        cycleSprites();
    }

    public void checkCollision() {

        collisionOn = false;

        gp.cChecker.checkTile(this);
        gp.cChecker.checkMovementCollision(this, gp.npcs);
        gp.cChecker.checkMovementCollision(this, gp.enemies);
        gp.cChecker.checkMovementCollision(this, gp.objects);

        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        boolean canHurtPlayer = this instanceof Enemy;
        if (contactPlayer && canHurtPlayer) {
            gp.player.takeDamage(this);
        }

        gp.cChecker.checkHazard(this);
    }
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
    public Direction getOppositeDirection(Direction direction) {
        return switch (direction) {
            case UP, UPLEFT, UPRIGHT -> DOWN;
            case DOWN, DOWNLEFT, DOWNRIGHT -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    protected void move() {

    }
    protected void move(Direction direction) {

        if (unableToMove() || collisionOn) {
            moving = false;
            return;
        }

        moving = true;
        moveInDirection(direction);
    }
    public boolean unableToMove() {
        return !canMove || stunned;
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

    protected void cycleSprites() {

        if (animationSpeed < ++spriteCounter && animationSpeed != 0) {

            if (spriteNum == 1) {
                spriteNum = 2;
            }
            else if (spriteNum == 2) {
                spriteNum = 1;
            }

            spriteCounter = 0;
        }
    }

    public void rotate() {
        direction = switch (direction) {
            case UP, UPLEFT, UPRIGHT -> LEFT;
            case DOWN, DOWNLEFT, DOWNRIGHT -> RIGHT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
        };
    }

    protected void useProjectile(Projectile projectile) {

        if (projectile.getAlive()) return;

        projectile.set(worldPoint, direction, true, this);
        gp.projectiles.add(projectile);
    }
    protected void useProjectile(Projectile projectile, int seconds) {

        int i = new Random().nextInt(60 * seconds);
        if (i == 0 && !projectile.alive && actionLockCounter == 0) {
            projectile.set(worldPoint, direction, true, this);
            gp.projectiles.add(projectile);

            // Force 30 frame delay in between shots
            actionLockCounter = 30;
        }
    }

    public boolean canBeTargeted() {
        return isAvailable() && !isCaptured();
    }
    public boolean isAvailable() {
        return alive && !dying && action != FALLING && action != DROWNING;
    }
    public boolean isCaptured() {
        return capturedBy != null;
    }

    protected void attack() {

    }
    protected void attacking() {

        // Prevent glitch
        if (swingSpeed1 == 0 && swingSpeed2 == 0) {
            swingSpeed1 = 3;
            swingSpeed2 = 15;
        }

        if (++attackCounter <= swingSpeed1) {
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

        attackEnemies(this);

        Projectile projectile = gp.cChecker.checkOverlapCollision(this, gp.projectiles);
        if (projectile != null && projectile.canBeDeflected(false)) {
            projectile.deflect(this);
        }

        Object object = gp.cChecker.checkOverlapCollision(this, gp.objects);
        if (object != null) {
            object.interact();
        }
    }
    private void attackEnemies(Entity attacker) {

        // Find enemy that intersects collision box
        for (Enemy enemy : gp.enemies) {
            if (enemy == null) continue;

            if (gp.cChecker.hasOverlapCollision(attacker, enemy)) {
                enemy.takeDamage(attacker);
            }
        }
    }
    private void detectEnemySwordCollision() {

        if (gp.player.getCapturedEntity() == this) {
            detectPlayerSwordCollision();
        }
        else if (gp.cChecker.checkPlayer(this)) {
            gp.player.takeDamage(this);
        }
    }

    public void takeDamage(Entity attacker) {

        if (invincible || !isAvailable() || isNotInteractable() || !isOnSameElevation(attacker)) {
            return;
        }

        // Can't take damage by own captured target
        if (attacker == capturedEntity) return;

        // Target is buzzing, hurt attacker
        if (buzzing && !(attacker instanceof Projectile)) {
            attacker.setStunned(true);
            attacker.takeDamage(this);
            return;
        }
        // Attacker is buzzing, stun target
        else if (attacker.getBuzzing()) {
            setStunned(true);
        }

        // Target blocked with shield
        boolean facingEnemy = getDirection() == getOppositeDirection(attacker.getDirection());
        if (action == GUARDING && facingEnemy) {

            if (attacker.canBeDeflected(true)) {
                attacker.deflect(this);
            }
            else {
                setKnockback(attacker.getDirection(), attacker.getKnockbackPower());
            }

            return;
        }

        // Target protected from damage
        if (!shielded) {
            dealDamage(attacker.getAttack(), attacker.getDirection(), attacker.getKnockbackPower());
        }
    }
    public boolean isNotInteractable() {
        return !interactable;
    }
    public void dealDamage(int damage, Direction direction, int knockbackPower) {

        health -= damage;
        if (health <= 0) {
            dying = true;
        }
        else {
            invincible = true;
            reactToDamage();
        }

        setKnockback(direction, knockbackPower);
    }
    protected void reactToDamage() {

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

    protected void setKnockback(Direction direction, int knockbackPower) {

        knockback = true;

        // Direction attacker was facing when hit
        knockbackDirection = direction;

        speed += knockbackPower;
    }
    protected void handleKnockback() {

        collisionOn = false;
        checkCollision();

        // Don't knockback if collision
        if (collisionOn) {
            resetKnockback();
            return;
        }

        moveInDirection(knockbackDirection);

        // Run for 10 frames
        if (10 <= ++knockbackCounter) {
            resetKnockback();
        }
    }
    private void resetKnockback() {
        knockback = false;
        knockbackCounter = 0;
        speed = defaultSpeed;
    }

    protected void checkDeath() { }

    public boolean canHoldLoot(Entity loot) {
        return false;
    }
    protected void assignLoot() {}
    protected void dropLoot() {

        if (loot == null) assignLoot();

        // Loot must be a collectable or an item
        boolean lootIsValid = loot != null && (loot instanceof Collectable) || (loot instanceof Item);
        if (!lootIsValid) return;

        loot.setWorldPoint(new Point(worldPoint));
        gp.collectables.add(loot);
    }

    public void receiveLoot(Entity loot) {

        if (loot instanceof Collectable) {
            ((Collectable) loot).use(this);
        }
        else if (loot instanceof Item) {
            showReward(loot);
            addItem((Item) loot);
        }

        loot.setAlive(false);
    }
    public void addItem(Item item) {

        item.setUser(this);
        items.add(item);

        if (this.item == null) {
            this.item = item;
        }

        item.setAlive(false);
    }
    public void showReward(Entity newItem) {
        this.newItem = newItem;

        if (newItem == null) return;

        gp.ui.setDialogue("You got " + newItem.getFormattedName() + "!\n" + newItem.getDescription());
        gp.GAME_STATE = gp.DIALOGUE_STATE;
    }

    protected void manageValues() {

        if (stunned) {
            if (90 < ++stunnedCounter) {
                stunned = false;
                stunnedCounter = 0;
            }
        }
    }

    public void resetValues() {

        alive = true;
        action = IDLE;

        resetCounters();

        knockback = false; knockbackCounter = 0;
        invincible = false; invincibleCounter = 0;
        stunned = false; stunnedCounter = 0;
        dying = false; dyingCounter = 0;

        speed = defaultSpeed;
        collisionOn = false;
        canMove = true; moving = false;
        onPath = false; pathCompleted = false;

        grabbedObject = null; grabbedBy = null;
        capturedEntity = null; capturedBy = null;
        lockedOn = false; lockedOnTarget = null;

        opened = false;
        elevated = false;
    }
    public void resetCounters() {
        spriteNum = 1; spriteCounter = 0;
        attackNum = 1; attackCounter = 0;
        charge = 0; actionLockCounter = 0;
    }

    public void draw(Graphics2D g2) {

        if (!drawing) return;

        // Get sprite
        getSpriteImage();

        // Draw sprite
        gp.camera.worldToScreen(worldPoint, screenPoint);
        g2.drawImage(image, screenPoint.x, screenPoint.y, null);

        // Reset opacity
        changeAlpha(g2, 1f);

        // Draw held loot
        drawLoot(g2);
    }
    protected void playHurtAnimation(Graphics2D g2) {

        if (++invincibleCounter % 5 == 0) {
            changeAlpha(g2, 0.2f);
        }

        if (45 < invincibleCounter) {
            invincibleCounter = 0;
            invincible = false;
        }
    }
    protected void playDyingAnimation(Graphics2D g2) {

        invincible = false;

        if (++dyingCounter % 5 == 0) {
            changeAlpha(g2, 0.2f);
        }

        if (40 < dyingCounter) {
            dyingCounter = 0;
            alive = false;
            dropLoot();
        }
    }
    protected void getSpriteImage() {

        if (spriteNum == 1) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down1;
                case LEFT -> left1;
                case RIGHT -> right1;
            };
        }
        else if (spriteNum == 2) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down2;
                case LEFT -> left2;
                case RIGHT -> right2;
            };
        }
    }
    protected void changeAlpha(Graphics2D g2, float alphaValue) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
    }
    protected void drawLoot(Graphics2D g2) {

        if (gp.GAME_STATE != gp.EDIT_STATE || loot == null) return;

        g2.drawImage(loot.getSprite(), screenPoint.x - 10, screenPoint.y - 10, null);
    }

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

    /** GETTERS AND SETTERS */
    public Point getScreenPoint() {
        return new Point(
                worldPoint.x - gp.player.worldPoint.x + gp.player.screenPoint.x,
                worldPoint.y - gp.player.worldPoint.y + gp.player.screenPoint.y
        );
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
    public int getCol() {
        return getCenterX() / gp.tileSize;
    }
    public int getRow() {
        return getCenterY() / gp.tileSize;
    }
    public int getCenterX() {
        return worldPoint.x + sprite.getWidth() / 2;
    }
    public int getCenterY() {
        return worldPoint.y + sprite.getHeight() / 2;
    }

    public boolean getCollision() {
        return collisionOn;
    }
    public void setCollision(boolean collisionOn) {
        this.collisionOn = collisionOn;
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
    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
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
    public String getAvailableAction(Entity user) {
        return availableAction;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public void modifySpeed(int change) {
        speed += change;
    }
    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
    public void setOnPath(boolean onPath) {
        this.onPath = onPath;
    }
    public EntityAI getAI() {
        return ai;
    }

    public String getName() {
        return name;
    }
    public String getFormattedName() {
        return formattedName;
    }
    public String getDescription() {
        return description;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public void addHealth(int change) {
        this.health += change;

        if (health > maxHealth) {
            health = maxHealth;
        }
    }
    public boolean getOpened() {
        return opened;
    }
    public void setOpened(boolean opened) {
        this.opened = opened;
    }
    public boolean getElevated() {
        return elevated;
    }
    public void setElevated(boolean elevated) {
        this.elevated = elevated;
    }
    public boolean isOnSameElevation(Entity target) {
        return target.getElevated() == elevated;
    }
    public boolean getCanSwim() {
        return canSwim;
    }
    public boolean getNeedsWater() {
        return needsWater;
    }
    public void grab(Object target) {
        grabbedObject = target;
        action = GRABBING;
    }
    public void pickup(Object target) {

        if (target == null) return;

        grabbedObject = target;
        grabbedObject.grabbedBy = this;
        grabbedObject.setInteractable(false);

        action = CARRYING;
    }
    public void releaseGrab() {
        grabbedObject = null;
        action = IDLE;
    }
    public void breakGrab() {

        if (grabbedBy != null) {
            grabbedBy.releaseGrab();
            grabbedBy = null;
        }

        interactable = true;
        action = IDLE;
    }
    public boolean isGrabbed() {
        return grabbedBy != null;
    }
    public void capture(Entity target) {

        // Release current target first
        if (capturedEntity != null) {
            capturedEntity.setAction(IDLE);
            capturedEntity.capturedBy = null;
        }

        capturedEntity = target;
        if (target != null) {
            target.capturedBy = this;
        }
    }
    public void releaseCapture() {
        capture(null);
    }
    public void breakCapture() {
        if (capturedBy != null) {
            capturedBy.releaseCapture();
            action = IDLE;
        }
    }
    public Entity getCapturedEntity() {
        return capturedEntity;
    }
    public Entity getCapturedBy() {
        return capturedBy;
    }

    public boolean getAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public int getAttack() {
        return attack;
    }
    public void setAttack(int attack) {
        this.attack = attack;
    }
    public void modifyAttack(int change) {
        attack += change;
    }
    public int getKnockbackPower() {
        return knockbackPower;
    }
    public boolean isKnockedBack() {
        return knockback;
    }
    public boolean getBuzzing() {
        return buzzing;
    }
    public void setStunned(boolean stunned) {
        this.stunned = stunned;
        action = IDLE;
        resetCounters();
    }
    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }
    public boolean getDying() {
        return dying;
    }

    public Entity getLoot() {
        return loot;
    }
    public void setLoot(Entity loot) {
        this.loot = loot;
    }
    public ArrayList<Item> getItems() {
        return items;
    }
    public Entity getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }

    public int getMaxRupees() {
        return maxRupees;
    }
    public void setMaxRupees(int maxRupees) {
        this.maxRupees = maxRupees;
    }
    public int getRupees() {
        return rupees;
    }
    public void setRupees(int rupees) {
        this.rupees = rupees;
    }
    public void addRupees(int amount) {
        this.rupees += amount;
    }
    public int getKeys() {
        return keys;
    }
    public void setKeys(int keys) {
        this.keys = keys;
    }
    public void addKeys(int amount) {
        this.keys += amount;
    }
    public boolean getHasBossKey() {
        return hasBossKey;
    }
    public void setHasBossKey(boolean hasBossKey) {
        this.hasBossKey = hasBossKey;
    }
    public int getMaxArrows() {
        return maxArrows;
    }
    public void setMaxArrows(int maxArrows) {
        this.maxArrows = maxArrows;
    }
    public int getArrows() {
        return arrows;
    }
    public void setArrows(int arrows) {
        this.arrows = arrows;
    }
    public void addArrows(int arrows) {

        this.arrows += arrows;

        if (this.arrows > maxArrows) {
            this.arrows = maxArrows;
        }
    }
    public int getMaxBombs() {
        return maxBombs;
    }
    public void setMaxBombs(int maxBombs) {
        this.maxBombs = maxBombs;
    }
    public int getBombs() {
        return bombs;
    }
    public void setBombs(int bombs) {
        this.bombs= bombs;
    }
    public void addBombs(int bombs) {

        this.bombs += bombs;
        if (this.bombs > maxBombs) {
            this.bombs = bombs;
        }
    }

    public Entity getUser() {
        return user;
    }
    public void setUser(Entity owner) {
        this.user = owner;
    }

    public void setDrawing(boolean drawing) {
        this.drawing = drawing;
    }
    public BufferedImage getSprite() {
        return sprite;
    }
    public DrawLayer getDrawLayer() {
        return DrawLayer.ENTITY;
    }
}
