package entity;

import application.GamePanel;
import entity.projectile.Projectile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static application.GamePanel.Direction.*;

public class Entity {

    /**
     * ACTION enum
     * List of predefined actions an Entity can perform
     */
    public enum Action {
        IDLE(true, true),
        ATTACKING(false, false),
        SPINCHARGING(true, true),
        SPINNING(false, false),
        ROLLING(false, true),
        GUARDING(true, false),
        DIGGING(false, false),
        AIMING(true, true),
        THROWING(false, false);

        private final boolean allowsFacing;
        private final boolean allowsTranslation;

        Action(boolean allowsFacing, boolean allowsTranslation) {
            this.allowsFacing = allowsFacing;
            this.allowsTranslation = allowsTranslation;
        }

        public boolean allowsFacing() {
            return allowsFacing;
        }
        public boolean allowsTranslation() {
            return allowsTranslation;
        }
    }

    protected GamePanel gp;

    /* GENERAL ATTRIBUTES */
    public int worldX, worldY;
    protected int worldXStart, worldYStart;
    protected int tempScreenX, tempScreenY;
    public String name;
    public int type;

    /* MOVEMENT VALUES */
    public GamePanel.Direction direction = DOWN;
    public Action action;
    public int speed = 1;
    protected int defaultSpeed;
    protected boolean moving = false;

    /* ANIMATION VALUES */
    private int actionLockCounter = 0;
    protected int animationSpeed;

    /* ATTACK VALUES */
    protected int swingSpeed1;
    protected int swingSpeed2;
    protected int swingSpeed3;

    /* RPG VALUES */
    public boolean alive = true;
    public int health;
    public int maxHealth;
    public int attack;
    public int defaultAttack;
    public Entity currentItem;
    protected boolean invincible = false;
    protected int invincibleCounter = 0;
    protected boolean knockback;
    protected GamePanel.Direction knockbackDirection;
    protected int knockbackCounter = 0;
    public boolean dying = false;
    private int dyingCounter = 0;

    /* COLLISION VALUES */
    public boolean collisionOn = true;
    protected boolean canMove = true;
    public Rectangle hitbox = new Rectangle(0, 0, 48, 48);
    public int hitboxDefaultX;
    public int hitboxDefaultY;
    protected int hitboxDefaultWidth = hitbox.width;
    protected int hitboxDefaultHeight = hitbox.height;
    protected Rectangle attackBox = new Rectangle(0, 0, 0, 0);

    /* INVENTORY VALUES */
    public int arrows = 0;

    /* PROJECTILE VALUES */
    public Projectile projectile;
    public Entity user;
    public int charge = 0;

    /* SPRITE ATTRIBUTES */
    public BufferedImage image;
    protected BufferedImage up1, up2,  down1, down2, left1, left2, right1, right2;
    protected int spriteNum = 1;
    protected int spriteCounter = 0;

    /* ENTITY TYPES */
    protected int entity_type;
    protected final int type_npc = 0;
    protected final int type_enemy = 1;
    protected final int type_item = 2;

    /* OBJECT TYPES */
    public final int type_projectile = 3;

    /**
     * CONSTRUCTOR
     * @param gp GamePanel
     */
    public Entity(GamePanel gp) {
        this.gp = gp;
        getImages();
    }

    /* CHILD FUNCTIONS */
    /**
     * GET IMAGE
     */
    protected void getImages() { }

    /**
     * SET ACTION
     */
    protected void setAction() { }
    /* END CHILD FUNCTIONS */

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

        // No action if in knockback state
        if (knockback) {
            handleKnockback();
        }
        else {
            setAction();
            updateDirection();
        }

        manageValues();
    }

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
     * CHECK COLLISION
     * Checks if the entity collides with something
     */
    protected void checkCollision() {
        collisionOn = false;

        gp.cChecker.checkTile(this);
        boolean contactPlayer = gp.cChecker.checkPlayer(this);
        boolean canHurtPlayer = entity_type == type_enemy;

        if (contactPlayer && canHurtPlayer) {
            damagePlayer(attack);
        }
    }

    /**
     * MOVE
     * Repositions the entity's X, Y based on direction and speed
     * Called by updateDirection() if o collision
     */
    protected void move(GamePanel.Direction direction) {
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

    /**
     * GET MOVE DIRECTION
     * Called by CollisionDetector
     * @return Current direction of the entity
     */
    public GamePanel.Direction getMoveDirection() {
        if (knockback) {
            return knockbackDirection;
        }
        else {
            return direction;
        }
    }

    protected void attack() {

    }

    /**
     * USE
     * Initiates using the Entity
     */
    protected void use() { }

    protected void addProjectile(Projectile projectile) {
        for (int i = 0; i < gp.projectile[0].length; i++) {
            if (gp.projectile[gp.currentMap][i] == null) {
                gp.projectile[gp.currentMap][i] = projectile;
                break;
            }
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

        // Keep damage above 0
        if (damage < 0) {
            damage = 1;
        }

        // Damage target
        target.health -= damage;
        target.invincible = true;

        // Target loses all health, start dying animation
        if (target.health <= 0) {
            target.dying = true;
        }

        // Push target back
        setKnockback(target, this, 1);
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
        target.knockbackDirection = attacker.direction;

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
     * @param attack Attack value of the weapon used
     */
    protected void damagePlayer(int attack) {

        // Player can't be damaged
        if (gp.player.invincible) {
            return;
        }

        int damage = attack;

        // Keep damage at or above 0
        if (damage < 0) {
            damage = 0;
        }

        // Damage player
        gp.player.health -= damage;
        gp.player.invincible = true;

        // Knockback player
        setKnockback(gp.player, this, 1);
    }

    protected void checkDeath() { }

    /**
     * MANAGE VALUES
     * Resets or reassigns entity attributes
     * called at the end of update
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
     * DRAW
     * Draws the sprite data to the graphics
     * @param g2 GamePanel
     */
    public void draw(Graphics2D g2) {

        offCenter();

        // Match image to sprite direction
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
    protected void offCenter() {
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

    /**
     * GET SCREEN X
     * @return Screen X relative to player
     */
    public int getScreenX() {
        return worldX - gp.player.worldX + gp.player.screenX;
    }

    /**
     * GET SCREEN Y
     * @return Screen Y relative to player
     */
    public int getScreenY() {
        return worldY - gp.player.worldY + gp.player.screenY;
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
}
