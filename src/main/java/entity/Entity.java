package entity;

import ai.EntityAI;
import application.GamePanel;
import application.GamePanel.Direction;
import entity.collectable.Collectable;
import entity.enemy.Enemy;
import entity.object.Object;
import entity.projectile.Projectile;

import static entity.Entity.Action.*;

import java.awt.*;
import java.awt.image.BufferedImage;
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
        GRABBING(false, false, true),
        CARRYING(true, true, false),
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
            drawOffset = new Point();

    /** COLLISION VALUES */
    protected boolean collisionOn = true;
    protected Rectangle hitbox = new Rectangle(0, 0, 48, 48);
    protected Point hitboxDefaultPoint = new Point();
    protected int hitboxDefaultWidth = hitbox.width;
    protected int hitboxDefaultHeight = hitbox.height;
    protected boolean interactable = true;

    /** MOVEMENT VALUES */
    protected EntityAI ai;
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
    protected String name;
    protected boolean alive = true;
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
    protected boolean dying = false;
    private int dyingCounter = 0;
    protected Collectable loot;
    protected boolean opened = false;
    protected boolean elevated = false;
    protected boolean canSwim = false;
    protected boolean needsWater = false;
    protected boolean shielded = false;

    protected Object grabbedObject;

    /** COMBAT VALUES */
    protected int attack;
    protected int defaultAttack;
    protected int knockbackPower;
    protected Rectangle attackBox = new Rectangle(0, 0, 0, 0);
    protected int attackNum = 1, attackCounter = 0;
    protected int swingSpeed1;
    protected int swingSpeed2;
    protected int swingSpeed3;
    protected boolean knockback;
    protected GamePanel.Direction knockbackDirection;
    protected int knockbackCounter = 0;
    protected boolean buzzing = false;

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
    protected BufferedImage sprite;
    protected BufferedImage
            image,
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

    /**
     * UPDATE
     * Updates the entity
     * Called every frame by GamePanel
     */
    public void update() {
        manageValues();
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
        cycleSprites();
    }

    protected void updateSpinDirection() {
        direction = switch (direction) {
            case UP, UPLEFT, UPRIGHT -> LEFT;
            case DOWN, DOWNLEFT, DOWNRIGHT -> RIGHT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
        };
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
    public void checkCollision() {

        collisionOn = false;

        gp.cChecker.checkTile(this);
        gp.cChecker.checkMovementCollision(this, gp.enemy);
        gp.cChecker.checkMovementCollision(this, gp.npc);
        gp.cChecker.checkMovementCollision(this, gp.obj);

        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        boolean canHurtPlayer = this instanceof Enemy;
        if (contactPlayer && canHurtPlayer) {
            gp.player.takeDamage(this);
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

    /**
     * SET DIRECTION
     * Randomly re-assigns the direction the Entity is facing
     * @param rate Integer frequency of updates (60 = 1 sec)
     */
    protected void setDirection(int rate) {

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

    protected Direction getOppositeDirection(Direction direction) {
        return switch (direction) {
            case UP, UPLEFT, UPRIGHT -> DOWN;
            case DOWN, DOWNLEFT, DOWNRIGHT -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

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
    protected void addProjectile(Projectile projectile) {
        for (int i = 0; i < gp.proj.length; i++) {
            if (gp.proj[i] == null) {
                gp.proj[i] = projectile;
                break;
            }
        }
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

        // Find enemy that intersects collision box
        Enemy enemy = overlapEnemy(this);
        if (enemy != null) {
            enemy.takeDamage(this);
        }

        int proj = gp.cChecker.checkOverlapCollision(this, gp.proj);
        if (proj != -1) {
            Projectile projectile = gp.proj[proj];

            if (projectile.canBeDeflected(false)) {
                projectile.deflect(this);
            }
        }

        int obj = gp.cChecker.checkOverlapCollision(this, gp.obj);
        if (obj != -1) {
            gp.obj[obj].interact();
        }
    }
    private void detectEnemySwordCollision() {
        if (gp.cChecker.checkPlayer(this)) {
            gp.player.takeDamage(this);
        }
    }

    protected Enemy overlapEnemy(Entity entity) {

        Enemy enemy = null;

        int enemyIndex = gp.cChecker.checkOverlapCollision(entity, gp.enemy);
        if (enemyIndex != -1) {
            enemy = gp.enemy[enemyIndex];
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
     * SET KNOCKBACK
     * Starts the knockback animation on the target
     * @param attacker Entity that provided the knockback
     */
    protected void setKnockback(Entity attacker) {

        knockback = true;

        // Direction attacker was facing when hit
        knockbackDirection = attacker.getDirection();

        speed += attacker.getKnockbackPower();
    }

    /**
     * HANDLE KNOCKBACK
     * Runs the knockback animation
     */
    protected void handleKnockback() {

        collisionOn = false;

        // Don't knockback if collision
        checkCollision();
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

    public void takeDamage(Entity attacker) {

        if (invincible || !isAvailable() || isNotInteractable() || !isOnSameElevation(attacker)) {
            return;
        }

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

            if (canBeDeflected(true)) {
                deflect(this);
            }
            else {
                setKnockback(attacker);
            }

            return;
        }

        int damage = attacker.getAttack();

        health -= damage;
        if (health <= 0) {
            dying = true;
        }
        else {
            invincible = true;
            reactToDamage();
        }

        setKnockback(attacker);
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
    /** END COMBAT*/

    public boolean isAvailable() {
        return alive && !dying && action != FALLING && action != DROWNING;
    }

    public boolean isNotInteractable() {
        return !interactable;
    }

    public boolean unableToMove() {
        return !canMove || stunned;
    }

    /**
     * CHECK DEATH
     * Checks if the entity has died
     */
    protected void checkDeath() { }

    protected void dropItem(Collectable droppedItem) {
        for (int i = 0; i < gp.col.length; i++) {
            if (gp.col[i] == null) {
                gp.col[i] = droppedItem;
                gp.col[i].setWorldPoint(worldPoint);
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

        if (stunned) {
            if (90 < ++stunnedCounter) {
                stunned = false;
                stunnedCounter = 0;
            }
        }
    }

    /**
     * RESET VALUES
     * Resets values to defaults
     */
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

        lockedOn = false; lockedOnTarget = null;

        opened = false;
        elevated = false;
    }

    public void resetCounters() {
        spriteNum = 1; spriteCounter = 0;
        attackNum = 1; attackCounter = 0;
        charge = 0; actionLockCounter = 0;
    }

    /**
     * DRAW
     * Draws the sprite data to the graphics
     * @param g2 GamePanel
     */
    public void draw(Graphics2D g2) {

        // Get sprite
        getSpriteImage();

        // Draw sprite
        gp.camera.worldToScreen(worldPoint, screenPoint);
        g2.drawImage(image, screenPoint.x, screenPoint.y, null);

        // Reset opacity
        changeAlpha(g2, 1f);
    }

    protected void getSpriteImage() {
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

    /**
     * CHANGE ALPHA
     * Changes the opacity of the image
     * @param g2 Graphics2D
     * @param alphaValue Opacity value
     */
    protected void changeAlpha(Graphics2D g2, float alphaValue) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
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
        }
    }

    /** GETTERS and SETTERS */
    public BufferedImage getSprite() {
        return sprite;
    }

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

    public EntityAI getAI() {
        return ai;
    }

    public boolean getAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean getDying() {
        return dying;
    }

    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setOnPath(boolean onPath) {
        this.onPath = onPath;
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
    public void setHealth(int health) {
        this.health = health;
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
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public void modifySpeed(int change) {
        speed += change;
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

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }
    public void setStunned(boolean stunned) {
        this.stunned = stunned;
        action = IDLE;
        resetCounters();
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
        return elevated;
    }
    public void setElevated(boolean elevated) {
        this.elevated = elevated;
    }
    public boolean isOnSameElevation(Entity target) {
        return target.getElevated() == elevated;
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

    public boolean getNeedsWater() {
        return needsWater;
    }

    public boolean getBuzzing() {
        return buzzing;
    }

    public void setGrabbedObject(Object grabbedObject) {
        this.grabbedObject = grabbedObject;
    }
}
