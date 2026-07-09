package entity;

import ai.EntityAI;
import application.GamePanel;
import application.GamePanel.Direction;
import entity.enemy.Enemy;
import entity.item.*;
import entity.projectile.Projectile;

import static entity.Entity.Action.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static application.GamePanel.Direction.*;

public class Player extends Entity {

    /** X/Y VALUES */
    public Point safePoint;

    private final ArrayList<Item> items = new ArrayList<>();
    private int currentItemSlot = 0;

    /** ANIMATION HANDLERS */
    private int spinCharge = 0;
    private int
            attackNum = 1, attackCounter = 0,
            digNum = 1, digCounter = 0,
            aimNum = 1, aimCounter = 0,
            throwNum = 1, throwCounter = 0,
            jumpNum = 1, jumpCounter = 0,
            damageNum = 1, damageCounter = 0;

    /** SPRITE IMAGES */
    private BufferedImage
            rollUp1, rollUp2, rollUp3, rollUp4, rollDown1, rollDown2, rollDown3, rollDown4,
            rollLeft1, rollLeft2, rollLeft3, rollLeft4, rollRight1, rollRight2, rollRight3, rollRight4,

            guardUp1, guardUp2, guardDown1, guardDown2,
            guardLeft1, guardLeft2, guardRight1, guardRight2,

            spinUp1, spinUp2, spinDown1, spinDown2,
            spinLeft1, spinLeft2, spinRight1, spinRight2,

            throwUp1, throwUp2, throwDown1, throwDown2,
            throwLeft1, throwLeft2, throwRight1, throwRight2,

            digUp1, digUp2, digDown1, digDown2,
            digLeft1, digLeft2, digRight1, digRight2,

            aimUp1, aimUp2, aimDown1, aimDown2,
            aimLeft1, aimLeft2, aimRight1, aimRight2,

            jumpUp1, jumpUp2, jumpUp3, jumpDown1, jumpDown2, jumpDown3,
            jumpLeft1, jumpLeft2, jumpLeft3, jumpRight1, jumpRight2, jumpRight3,

            soarUp1, soarDown1, soarLeft1, soarRight1,

            fall1, fall2, fall3,
            drown1;

    /**
     * CONSTRUCTOR
     * @param gp GamePanel
     */
    public Player(GamePanel gp) {
        super(gp, "Link");

        // Player position locked to center of screen
        screenPoint = new Point(gp.screenWidth / 2 - (gp.tileSize / 2), gp.screenHeight / 2 - (gp.tileSize / 2));

        // AI pathfinder
        ai = new EntityAI(gp, this);

        // Hitbox
        hitbox = new Rectangle(8, 12, 32, 34);
        hitboxDefaultPoint = new Point(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        // Attack box
        attackBox.width = 44;
        attackBox.height = 42;

        // Attack speed
        swingSpeed1 = 2;
        swingSpeed2 = 5;
        swingSpeed3 = 10;
    }

    /** GET IMAGES */
    @Override
    protected void getImages() {
        up1 = setupImage("/player/boy_up_1");
        up2 = setupImage("/player/boy_up_2");
        down1 = setupImage("/player/boy_down_1");
        down2 = setupImage("/player/boy_down_2");
        left1 = setupImage("/player/boy_left_1");
        left2 = setupImage("/player/boy_left_2");
        right1 = setupImage("/player/boy_right_1");
        right2 = setupImage("/player/boy_right_2");
        getAllImages();
    }
    private void getAllImages() {
        getRollImages();
        getGuardImages();
        getAttackImages();
        getSpinImages();
        getThrowImages();
        getDigImages();
        getAimImages();
        getJumpImages();
        getSoarImages();
        getFallImages();
        getDrownImages();
    }
    private void getRollImages() {
        rollUp1 = setupImage("/player/boy_roll_up_1");
        rollUp2 = setupImage("/player/boy_roll_up_2");
        rollUp3 = setupImage("/player/boy_roll_up_3");
        rollUp4 = setupImage("/player/boy_roll_up_4");
        rollDown1 = setupImage("/player/boy_roll_down_1");
        rollDown2 = setupImage("/player/boy_roll_down_2");
        rollDown3 = setupImage("/player/boy_roll_down_3");
        rollDown4 = setupImage("/player/boy_roll_down_4");
        rollLeft1 = setupImage("/player/boy_roll_left_1");
        rollLeft2 = setupImage("/player/boy_roll_left_2");
        rollLeft3 = setupImage("/player/boy_roll_left_3");
        rollLeft4 = setupImage("/player/boy_roll_left_4");
        rollRight1 = setupImage("/player/boy_roll_right_1");
        rollRight2 = setupImage("/player/boy_roll_right_2");
        rollRight3 = setupImage("/player/boy_roll_right_3");
        rollRight4 = setupImage("/player/boy_roll_right_4");
    }
    private void getGuardImages() {
        guardUp1 = setupImage("/player/boy_guard_up_1");
        guardUp2 = setupImage("/player/boy_guard_up_2");
        guardDown1 = setupImage("/player/boy_guard_down_1");
        guardDown2 = setupImage("/player/boy_guard_down_2");
        guardLeft1 = setupImage("/player/boy_guard_left_1");
        guardLeft2 = setupImage("/player/boy_guard_left_2");
        guardRight1 = setupImage("/player/boy_guard_right_1");
        guardRight2 = setupImage("/player/boy_guard_right_2");
    }
    private void getAttackImages() {
        attackUp1 = setupImage("/player/boy_attack_kokiri_up_1", gp.tileSize * 2, gp.tileSize);
        attackUp2 = setupImage("/player/boy_attack_kokiri_up_2", gp.tileSize * 2, gp.tileSize * 2);
        attackUp3 = setupImage("/player/boy_attack_kokiri_up_3", gp.tileSize, gp.tileSize * 2);
        attackUp4 = setupImage("/player/boy_attack_kokiri_up_4", gp.tileSize, gp.tileSize * 2);
        attackDown1 = setupImage("/player/boy_attack_kokiri_down_1", gp.tileSize * 2, gp.tileSize);
        attackDown2 = setupImage("/player/boy_attack_kokiri_down_2", gp.tileSize * 2, gp.tileSize * 2);
        attackDown3 = setupImage("/player/boy_attack_kokiri_down_3", gp.tileSize, gp.tileSize * 2);
        attackDown4 = setupImage("/player/boy_attack_kokiri_down_4", gp.tileSize, gp.tileSize * 2);
        attackLeft1 = setupImage("/player/boy_attack_kokiri_left_1", gp.tileSize, gp.tileSize * 2);
        attackLeft2 = setupImage("/player/boy_attack_kokiri_left_2", gp.tileSize * 2, gp.tileSize * 2);
        attackLeft3 = setupImage("/player/boy_attack_kokiri_left_3", gp.tileSize * 2, gp.tileSize);
        attackLeft4 = setupImage("/player/boy_attack_kokiri_left_4", gp.tileSize * 2, gp.tileSize);
        attackRight1 = setupImage("/player/boy_attack_kokiri_right_1", gp.tileSize, gp.tileSize * 2);
        attackRight2 = setupImage("/player/boy_attack_kokiri_right_2", gp.tileSize * 2, gp.tileSize * 2);
        attackRight3 = setupImage("/player/boy_attack_kokiri_right_3", gp.tileSize * 2, gp.tileSize);
        attackRight4 = setupImage("/player/boy_attack_kokiri_right_4", gp.tileSize * 2, gp.tileSize);
    }
    private void getSpinImages() {
        spinUp1 = setupImage("/player/boy_spin_kokiri_up_1", gp.tileSize * 2, gp.tileSize * 2);
        spinUp2 = setupImage("/player/boy_spin_kokiri_up_2", gp.tileSize, gp.tileSize * 2);
        spinDown1 = setupImage("/player/boy_spin_kokiri_down_1", gp.tileSize * 2, gp.tileSize * 2);
        spinDown2 = setupImage("/player/boy_spin_kokiri_down_2", gp.tileSize, gp.tileSize * 2);
        spinLeft1 = setupImage("/player/boy_spin_kokiri_left_1", gp.tileSize * 2, gp.tileSize * 2);
        spinLeft2 = setupImage("/player/boy_spin_kokiri_left_2", gp.tileSize * 2, gp.tileSize);
        spinRight1 = setupImage("/player/boy_spin_kokiri_right_1", gp.tileSize * 2, gp.tileSize * 2);
        spinRight2 = setupImage("/player/boy_spin_kokiri_right_2", gp.tileSize * 2, gp.tileSize);
    }
    private void getThrowImages() {
        throwUp1 = setupImage("/player/boy_throw_up_1");
        throwUp2 = setupImage("/player/boy_throw_up_2");
        throwDown1 = setupImage("/player/boy_throw_down_1");
        throwDown2 = setupImage("/player/boy_throw_down_2");
        throwLeft1 = setupImage("/player/boy_throw_left_1");
        throwLeft2 = setupImage("/player/boy_throw_left_2");
        throwRight1 = setupImage("/player/boy_throw_right_1");
        throwRight2 = setupImage("/player/boy_throw_right_2");
    }
    private void getDigImages() {
        digUp1 = setupImage("/player/boy_dig_up_1");
        digUp2 = setupImage("/player/boy_dig_up_2");
        digDown1 = setupImage("/player/boy_dig_down_1");
        digDown2 = setupImage("/player/boy_dig_down_2");
        digLeft1 = setupImage("/player/boy_dig_left_1");
        digLeft2 = setupImage("/player/boy_dig_left_2");
        digRight1 = setupImage("/player/boy_dig_right_1");
        digRight2 = setupImage("/player/boy_dig_right_2");
    }
    private void getAimImages() {
        aimUp1 = setupImage("/player/boy_aim_up_1");
        aimUp2 = setupImage("/player/boy_aim_up_2");
        aimDown1 = setupImage("/player/boy_aim_down_1");
        aimDown2 = setupImage("/player/boy_aim_down_2");
        aimLeft1 = setupImage("/player/boy_aim_left_1");
        aimLeft2 = setupImage("/player/boy_aim_left_2");
        aimRight1 = setupImage("/player/boy_aim_right_1");
        aimRight2 = setupImage("/player/boy_aim_right_2");
    }
    private void getJumpImages() {
        jumpUp1 = setupImage("/player/boy_jump_up_1");
        jumpUp2 = setupImage("/player/boy_jump_up_2");
        jumpUp3 = setupImage("/player/boy_jump_up_3");
        jumpDown1 = setupImage("/player/boy_jump_down_1");
        jumpDown2 = setupImage("/player/boy_jump_down_2");
        jumpDown3 = setupImage("/player/boy_jump_down_3");
        jumpLeft1 = setupImage("/player/boy_jump_left_1");
        jumpLeft2 = setupImage("/player/boy_jump_left_2");
        jumpLeft3 = setupImage("/player/boy_jump_left_3");
        jumpRight1 = setupImage("/player/boy_jump_right_1");
        jumpRight2 = setupImage("/player/boy_jump_right_2");
        jumpRight3 = setupImage("/player/boy_jump_right_3");
    }
    private void getSoarImages() {
        soarUp1 = setupImage("/player/boy_soar_up_1");
        soarDown1 = setupImage("/player/boy_soar_down_1");
        soarLeft1 = setupImage("/player/boy_soar_left_1");
        soarRight1 = setupImage("/player/boy_soar_right_1");
    }
    private void getFallImages() {
        fall1 = setupImage("/player/boy_fall_1");
        fall2 = setupImage("/player/boy_fall_2");
        fall3 = setupImage("/player/boy_fall_3");
    }
    private void getDrownImages() {
        drown1 = setupImage("/player/boy_drown");
    }

    /**
     * SET DEFAULT VALUES
     * Resets all attributes to base values
     */
    public void setDefaultValues() {
        setDefaultAnimationValues();
        setDefaultRPGValues();
        setDefaultPosition();
    }

    /**
     * SET DEFAULT ANIMATION VALUES
     */
    private void setDefaultAnimationValues() {

        animationSpeed = 10;
        defaultSpeed = 3;
        speed = defaultSpeed;
        action = IDLE;
    }

    /**
     * SET DEFAULT POSITON
     */
    private void setDefaultPosition() {

        worldPoint = new Point(gp.tileSize * 23, gp.tileSize * 21);
        safePoint = new Point(worldPoint.x, worldPoint.y);

        gp.currentMap = 0;
        gp.currentArea = gp.outside;
    }

    private void setDefaultRPGValues() {

        maxHealth = 16;
        health = maxHealth;

        defaultAttack = 2;
        attack = defaultAttack;
        knockbackPower = 1;

        arrows = 50;

        items.addAll(Arrays.asList(
                new ITM_Shovel(gp, this),
                new ITM_Boomerang(gp, this),
                new ITM_Feather(gp, this),
                new ITM_Bow(gp, this),
                new ITM_Hookshot(gp, this),
                new ITM_Cape(gp, this)
        ));

        item = items.getFirst();
    }

    /**
     * UPDATE
     * Updates player character based on user inputs
     * Called by GamePanel every frame
     */
    @Override
    public void update() {

        if (knockback) {
            checkCollision();
            handleKnockback();
            manageValues();
            return;
        }

        // Allow A press only when Idle
        if (action == IDLE) {
            handleActionInput();
        }

        // Update player behavior based on current action
        updateAction();

        // Update lockon direction if allowed to update facing
        if (lockedOn && action.allowsFacing()) {
            zTargeting();
        }

        // Read directional input if current action allows
        if (action.allowsFacing()) {
            handleMovementInput();
        }
        // Auto-move player if action denies input but allows movement
        else if (action.allowsTranslation()) {
            move();
        }

        manageValues();
    }

    /**
     * HANDLE ACTION INPUT
     * Updates action based on button pressed
     * Called by update() if player is IDLE
     */
    private void handleActionInput() {

        // Action button
        if (gp.keyH.aPressed) {
            startAction();
        }
        // Z-target
        else if (gp.keyH.lPressed) {
            startZTarget();
        }
        // Swing sword
        else if (gp.keyH.bPressed) {
            action = ATTACKING;
        }
        // Shield guard
        else if (gp.keyH.rPressed) {
            action = GUARDING;
            spriteNum = 1;
            spriteCounter = 0;
        }
        // Use item
        else if (gp.keyH.xPressed) {
            useItem();
        }
        // Switch item
        else if (gp.keyH.startPressed) {
           switchItem();
        }
    }

    /**
     * START ACTION
     * Updates action based on current situation
     * Called by handleActionInput() when A pressed
     */
    private void startAction() {

        gp.keyH.aPressed = false;

        // Different action based on current status
        if (action == IDLE) {
            // Cooldown needed for rolling
            if (moving && actionLockCounter == 0) {
                startRoll();
            } else {
                interactObject();
            }
        }
    }
    /**
     * START ROLL
     * Initiates roll logic
     * Called by startAction() when player initiates ROLL
     */
    private void startRoll() {
        action = ROLLING;
        spriteNum = 1;
        spriteCounter = 0;
        lockonDirection = direction;
        actionLockCounter = 30;
    }
    private void interactObject() {
        int object = gp.cChecker.checkMovementCollision(this, gp.obj);

        if (object != -1) {
            gp.obj[gp.currentMap][object].interact(this);
        }
    }

    /** Z-TARGETING */
    private void startZTarget() {

        gp.keyH.lPressed = false;

        // Find new target
        Entity newTarget = findZTarget();

        // New target found
        if (newTarget != null) {

            // Not currently locked on
            if (lockedOnTarget == null) {
                lockedOnTarget = newTarget;
                lockedOn = true;
            }
            // Already locked on
            else {
                // Moving backwards, turn off lock
                if (Objects.equals(direction, getOppositeDirection(lockonDirection))) {
                    lockedOnTarget = null;
                    lockedOn = false;
                }
                // Switch targets
                else {
                    lockedOnTarget = newTarget;
                    lockedOn = true;
                }
            }
        }
        // No target found
        else if (lockedOnTarget != null) {
            lockedOnTarget = null;
            lockedOn = false;
        }
    }
    private Entity findZTarget() {

        Entity target = null;
        int currentDistance = maxZTargetDistance;

        for (Entity e : gp.enemy[gp.currentMap]) {

            if (e != null && e != lockedOnTarget && e.isAvailable()) {

                int enemyDistance = ai.getTileDistance(e);
                if (enemyDistance < currentDistance) {
                    currentDistance = enemyDistance;
                    target = e;
                }
            }
        }

        return target;
    }
    private void zTargeting() {

        // Locked target within 8 tiles
        if (lockedOnTarget != null && ai.getTileDistance(lockedOnTarget) < maxZTargetDistance) {

            // Target alive
            if (lockedOnTarget.isAvailable()) {
                direction = getZTargetDirection(lockedOnTarget);
                lockonDirection = direction;
            }
            // Target defeated
            else {
                lockedOnTarget = null;
                lockedOn = false;

                // Find new target
                findZTarget();
            }
        }
        // Target out of range
        else {
            lockedOnTarget = null;
            lockedOn = false;
        }
    }
    private Direction getZTargetDirection(Entity target) {

        Direction zDirection = direction;

        // Player X/Y
        int px = (worldPoint.x + (gp.tileSize / 2));
        int py = (worldPoint.y + (gp.tileSize / 2));

        // Target X/Y
        int ex = (target.worldPoint.x + (gp.tileSize / 2));
        int ey = (target.worldPoint.y + (gp.tileSize / 2));

        if (py > ey && py - ey >= Math.abs(px - ex)) // SOUTH
        {
            zDirection = UP;
        }
        else if (py >= ey && px > ex) // EAST / SOUTHEAST
        {
            zDirection = LEFT;
        }
        else if (py >= ey && ex > px) // WEST / SOUTHWEST
        {
            zDirection = RIGHT;
        }
        else if (ey > py && ey - py >= Math.abs(px - ex)) // NORTH
        {
            zDirection = DOWN;
        }
        else if (ey > py && px > ex) // EAST / NORTHEAST
        {
            zDirection = LEFT;
        }
        else if (ey > py && ex > px) // WEST / NORTHWEST
        {
            zDirection = RIGHT;
        }

        return zDirection;
    }
    /* END Z-TARGETING */

    /**
     * USE ITEM
     * Initiates the item use function
     * Called by handleActionInput()
     */
    private void useItem() {

        // Item equipped
        if (item != null) {
            switch (item.name) {
                case ITM_Shovel.itmName, ITM_Boomerang.itmName, ITM_Hookshot.itmName -> {
                    gp.keyH.xPressed = false;
                    item.use();
                }
                case ITM_Bow.itmName -> {
                    lockonDirection = direction;
                    item.use();
                }
                case ITM_Feather.itmName, ITM_Cape.itmName -> {
                    gp.keyH.xPressed = false;
                    lockonDirection = direction;
                    item.use();
                }
            }
        }
        // No equipped item
        else {
            gp.keyH.xPressed = false;
        }
    }

    private void switchItem() {
        gp.keyH.startPressed = false;

        currentItemSlot++;
        if (items.size() <= currentItemSlot) {
            currentItemSlot = 0;
        }

        item = items.get(currentItemSlot);
    }

    /**
     * UPDATE ACTION
     * Calls the action method based on current player action
     * Called by update()
     */
    private void updateAction() {
        switch (action) {
            case ROLLING -> rolling();
            case GUARDING -> guarding();
            case ATTACKING -> attacking();
            case SPINCHARGING -> spinCharging();
            case SPINNING -> spinning();
            case THROWING -> throwing();
            case DIGGING -> digging();
            case AIMING -> aiming();
            case JUMPING, SOARING -> jumping();
            case FALLING, DROWNING -> takingDamage();
        }
    }

    /**
     * HANDLE MOVEMENT INPUT
     * Calls directionPressed() to update direction when an arrow key is pressed
     * Called by update() if current action allows
     */
    private void handleMovementInput() {
        if (gp.keyH.upPressed || gp.keyH.downPressed || gp.keyH.leftPressed || gp.keyH.rightPressed) {
            updateDirection();
        }
        else {
            moving = false;
        }
    }

    /**
     * UPDATE DIRECTION
     * Handles player movement logic
     * Called by handleMovementInput() when an arrow key is pressed
     */
    @Override
    protected void updateDirection() {
       updateFacing();

       if (!action.allowsTranslation()) {
           moving = false;
           return;
       }

       move();
       cycleSprites();
    }

    /**
     * UPDATE FACING
     * Sets new player direction
     * Called by directionPressed() if current action allows
     */
    private void updateFacing() {

        Direction nextDirection = direction;

        boolean up = gp.keyH.upPressed;
        boolean down = gp.keyH.downPressed;
        boolean left = gp.keyH.leftPressed;
        boolean right = gp.keyH.rightPressed;

        if (up && left) nextDirection = UPLEFT;
        else if (up && right) nextDirection = UPRIGHT;
        else if (down && left) nextDirection = DOWNLEFT;
        else if (down && right)  nextDirection = DOWNRIGHT;
        else if (up) nextDirection = UP;
        else if (down) nextDirection = DOWN;
        else if (left) nextDirection = LEFT;
        else if (right) nextDirection = RIGHT;

        // Do not change actual direction if face-locked
        if (lockedOn || action.locksFacing()) {
            lockonDirection = nextDirection;
        }
        else {
            direction = nextDirection;
        }
    }

    /**
     * MOVE
     * Repositions the player's X, Y based on direction and speed
     * Called by updateDirection() if no collision
     */
    protected void move() {
        Direction newDirection = getMoveDirection();

        collisionOn = false;
        checkCollision();

        super.move(newDirection);
    }

    /**
     * CHECK COLLISION
     * Checks if player has collided with anything
     * Called by move()
     */
    @Override
    public void checkCollision() {

        collisionOn = false;

        checkObstacleCollision();
        checkHarmfulCollision();
        checkInteractiveCollision();
    }
    private void checkObstacleCollision() {
        gp.cChecker.checkTile(this);
        gp.cChecker.checkMovementCollision(this, gp.npc);
        gp.cChecker.checkMovementCollision(this, gp.obj);
    }
    private void checkHarmfulCollision() {

        gp.cChecker.checkHazard(this);

        Enemy enemy = moveIntoEnemy(this);
        if (enemy != null && !enemy.invincible) {
            takeDamage(enemy);
        }
    }
    private void checkInteractiveCollision() {

        int col = gp.cChecker.checkOverlapCollision(this, gp.col);
        if (col != -1) {
            gp.col[gp.currentMap][col].use(this);
        }

        int proj = gp.cChecker.checkOverlapCollision(this, gp.proj);
        if (proj != -1) {
            Projectile projectile = gp.proj[gp.currentMap][proj];
            if (projectile.getCanPickup()) {
                projectile.pickup(this);
            }
        }
    }

    private Enemy moveIntoEnemy(Entity entity) {

        Enemy enemy = null;

        int enemyIndex = gp.cChecker.checkMovementCollision(entity, gp.enemy);
        if (enemyIndex != -1) {
            enemy = gp.enemy[gp.currentMap][enemyIndex];
        }

        return enemy;
    }

    /**
     * ROLLING
     * Handles logic for when the player rolls
     * Called by getAction() when action = ROLLING
     */
    private void rolling() {

        speed = 5;

        if (++spriteCounter < 6) {
            spriteNum = 1;
        }
        else if (spriteCounter < 10) {
            spriteNum = 2;
        }
        else if (spriteCounter < 15) {
            spriteNum = 3;
        }
        else if (spriteCounter < 20) {
            spriteNum = 4;
        }
        else {
            spriteNum = 1;
            spriteCounter = 0;
            action = IDLE;
            speed = defaultSpeed;
        }
    }

    /**
     * GUARDING
     * Handles logic for when the player shield guards
     * Called by getAction() when action = GUARDING
     */
    private void guarding() {

        // Activate guard when R is held down
        if (gp.keyH.rPressed) {
            if (spriteCounter < 15) {
                spriteCounter++;
            }

            if (spriteCounter < 7) {
                spriteNum = 1;
            }
            else {
                spriteNum = 2;
            }
        }
        // Release guard when player releases R
        else {
            spriteNum = 1;
            spriteCounter = 0;
            action = IDLE;
        }
    }

    /**
     * ATTACKING
     * Handles logic for swinging sword
     * Called by updateAction() when action is ATTACKING
     */
    @Override
    protected void attacking() {

        // Increase spin charge if player holds B button
        if (gp.keyH.bPressed) {
            spinCharge++;
        }
        else {
            spinCharge = 0;
        }

        if (++attackCounter <= swingSpeed1) {
            attackNum = 1;
        }
        else if (attackCounter <= swingSpeed2) {
            attackNum = 2;
        }
        else if (attackCounter <= swingSpeed3) {
            attackNum = 3;
            adjustSwingHitbox();
        }
        else {
            action = IDLE;

            // Spin charge ready for spin attack
            if (swingSpeed3 < spinCharge && gp.keyH.bPressed) {
                action = SPINCHARGING;
                lockonDirection = direction;
            }

            attackCounter = 0;
            spinCharge = 0;
        }
    }

    /**
     * CHARGE SPIN
     * Charges spin attack while holding B
     * Called by getAction() when action == CHARGING
     */
    private void spinCharging() {

        // Player holding B to charge
        if (gp.keyH.bPressed) {
            if (charge < 120) {
                charge += 2;
            }

            speed = 2;
        }
        // Charge is ready, start spin and reset values
        else if (120 <= charge) {
            updateSpinDirection();

            charge = 0;
            attackNum = 1;
            spriteNum = 0;
            speed = defaultSpeed;
            action = SPINNING;
        }
        // Player released B, charge not ready, reset to idle
        else {
            charge = 0;
            attackNum = 1;
            attackCounter = 0;
            speed = defaultSpeed;
            action = IDLE;
        }
    }

    /**
     * SPIN ATTACKING
     * Handles sword spin attack logic
     * Called by getAction() when action == SPINNING
     */
    private void spinning() {

        if (++attackCounter < 3) {
            attackNum = 1;
        }
        else if (attackCounter < 6) {
            attackNum = 2;
            adjustSpinHitbox();
        }
        else {
            // Reset sprite counter to animate attack for all 4 directions
            attackNum = 1;
            attackCounter = 0;

            // Full rotation
            if (spriteNum < 3) {
               updateSpinDirection();
            }

            // Run 4 times for 4 directions, then stop spin
            spriteNum++;
            if (spriteNum == 4) {
                spriteNum = 1;
                action = IDLE;
            }
        }
    }

    /**
     * ADJUST SPIN HITBOX
     * Modifies hitbox to accommodate for spin attack
     * Called by spinning()
     */
    private void adjustSpinHitbox() {

        // Save current X/Y
        Point currentWorldPoint = new Point(worldPoint.x, worldPoint.y);

        adjustSpinAttackBox();
        detectPlayerSwordCollision();

        // Reset X/Y and Hitbox
        worldPoint.setLocation(currentWorldPoint);
        hitbox.width = hitboxDefaultWidth;
        hitbox.height = hitboxDefaultHeight;
    }
    private void adjustSpinAttackBox() {
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                worldPoint.y -= attackBox.height + hitbox.y;
                hitbox.height = attackBox.height;
                hitbox.width *= 2;
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                worldPoint.x -= attackBox.width;
                worldPoint.y += attackBox.height - hitbox.y;
                hitbox.height = attackBox.height;
                hitbox.width *= 2;
            }
            case LEFT -> {
                worldPoint.x -= attackBox.width;
                worldPoint.y -= attackBox.height;
                hitbox.width = attackBox.width;
                hitbox.height *= 2;
            }
            case RIGHT -> {
                worldPoint.x += attackBox.width - hitbox.y;
                hitbox.width = attackBox.width;
                hitbox.height *= 2;
            }
        }
    }

    /**
     * THROWING
     * Runs throwing animation and logic
     * Called by startAction() when player action is THROWING
     */
    private void throwing() {

        if (++throwCounter <= 6) {
            throwNum = 1;
        }
        else {
            throwNum = 2;
        }

        if (28 < throwCounter && action == IDLE) {
            throwNum = 1;
            throwCounter = 0;
        }
    }

    /**
     * DIGGING
     * Runs digging animation and logic
     * Called by startAction() when player action is DIGGING
     */
    private void digging() {

        if (++digCounter <= 12) {
            digNum = 1;
        }
        else if (digCounter < 24 ) {
            digNum = 2;
        }
        else {
            digNum = 1;
            digCounter = 0;
            action = IDLE;
        }
    }

    /**
     * AIMING
     * Runs aiming animation and logic
     * Called by startAction() when player action is AIMING
     */
    private void aiming() {

        speed = 2;

        if (moving) {

            if (10 < ++aimCounter) {
                aimNum++;

                if (2 < aimNum) {
                    aimNum = 1;
                }

                aimCounter = 0;
            }
        } else {
            if (charge <= 6) {
                aimNum = 1;
            }
            else {
                aimNum = 2;
            }
        }

        if (gp.keyH.xPressed) {
            item.use();
        }
        else {
            item.attack();
            speed = defaultSpeed;
        }
    }

    /**
     * JUMPING
     * Runs jumping animation and logic
     * Called by startAction() when player action is JUMPING
     */
    private void jumping() {

        if (++jumpCounter <= 6) {
            jumpNum = 1;
        }
        else if (12 <= jumpCounter && jumpCounter < 18) {
            jumpNum = 2;
        }
        else if (jumpCounter < 27) {
            jumpNum = 3;
        }
        else if (28 <= jumpCounter) {
            if (action == SOARING) {
                jumpNum = 4;
                soaring();
            }
            else {
                jumpNum = 1;
                jumpCounter = 0;
                elevated = false;
                action = IDLE;
            }
        }
    }

    /**
     * SOARING
     * Runs soaring animation and logic
     * Called by jumping() after player jumps and is SOARING
     */
    private void soaring() {

        if (jumpCounter < 60) {
            soarInDirection();
        }
        else if (70 <= jumpCounter) {
            jumpNum = 1;
            jumpCounter = 0;
            elevated = false;
            action = IDLE;

            gp.cChecker.checkHazard(this);
        }
    }

    /**
     * SOAR IN DIRECTION
     * Automatically moves the player forward if no collision
     * Called by soaring() when jumpCounter < 60
     */
    private void soarInDirection() {

        // Don't move if colliding with something
        checkCollision();
        if (collisionOn) return;

        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> worldPoint.y--;
            case DOWN, DOWNLEFT, DOWNRIGHT -> worldPoint.y++;
            case LEFT -> worldPoint.x--;
            case RIGHT -> worldPoint.x++;
        }
    }

    private void takingDamage() {

        speed = 0;
        knockback = false;

        if (++damageCounter <= 6) {
            damageNum = 1;
        }
        else if (damageCounter < 18) {
            damageNum = 2;
        }
        else if (damageCounter < 24) {
            damageNum = 3;
        }
        else if (damageCounter < 60) {
            damageNum = 4;
        }
        else if (80 <= damageCounter) {
            damageNum = 1;
            damageCounter = 0;
            health -= 2;
            invincible = true;
            
            worldPoint.setLocation(safePoint);
            action = IDLE;

            resetHandlers();
        }
    }

    private void resetHandlers() {

        resetCounters();

        speed = defaultSpeed;
        collisionOn = false; canMove = true;
        elevated = false;

        knockback = false; knockbackCounter = 0;
        lockedOn = false; lockedOnTarget = null;
    }

    /**
     * MANAGE VALUES
     * Resets or reassigns player attributes
     * Called by update() at the end
     */
    @Override
    protected void manageValues() {

        if (stunned) {
            stunnedCounter++;
            if (60 < stunnedCounter) {
                stunned = false;
                stunnedCounter = 0;
            }
        }

        // Decrease cooldown if filled
        if (0 < actionLockCounter) {
            actionLockCounter--;
        }
    }

    @Override
    public void resetValues() {
        super.resetValues();
        resetHandlers();
    }

    @Override
    public void resetCounters() {
        super.resetCounters();

        spinCharge = 0;
        digNum = 1; digCounter = 0;
        aimNum = 1; aimCounter = 0;
        throwNum = 1; throwCounter = 0;
        jumpNum = 1; jumpCounter = 0;
        damageNum = 1; damageCounter = 0;
    }

    /**
     * DRAW
     * Draws the sprite data to the graphics
     * Called by GamePanel every frame
     * @param g2 GamePanel
     */
    @Override
    public void draw(Graphics2D g2) {

        offCenter();
        getSpriteImage(g2);

        if (invincible) {
           playHurtAnimation(g2);
        }

        g2.drawImage(image, tempScreenPoint.x, tempScreenPoint.y, null);

        // Reset opacity
        changeAlpha(g2, 1f);
    }

    /**
     * OFF CENTER
     * Adjusts X, Y if near edge
     */
    private void offCenter() {
        tempScreenPoint.setLocation(screenPoint);

        if (worldPoint.x < screenPoint.x) {
            tempScreenPoint.x = worldPoint.x;
        }
        if (worldPoint.y < screenPoint.y) {
            tempScreenPoint.y = worldPoint.y;
        }

        // From player to right-edge of screen
        int rightOffset = gp.screenWidth - screenPoint.x;

        //  From player to right-edge of world
        if (rightOffset > gp.worldWidth - worldPoint.x) {
            tempScreenPoint.x = gp.screenWidth - (gp.worldWidth - worldPoint.x);
        }

        // From player to bottom-edge of screen
        int bottomOffSet = gp.screenHeight - screenPoint.y;

        //  From player to bottom-edge of world
        if (bottomOffSet > gp.worldHeight - worldPoint.y) {
            tempScreenPoint.y = gp.screenHeight - (gp.worldHeight - worldPoint.y);
        }
    }

    /** GET CURRENT SPRITE TO DRAW */
    private void getSpriteImage(Graphics2D g2) {
        image = switch (action) {
            case IDLE -> getIdleSprite();
            case ROLLING -> getRollingSprite();
            case GUARDING -> getGuardSprite();
            case ATTACKING, SPINCHARGING -> getAttackSprite();
            case SPINNING ->  getSpinSprite();
            case THROWING -> getThrowSprite();
            case DIGGING -> getDigSprite();
            case AIMING -> getAimSprite();
            case JUMPING, SOARING -> getJumpSprite(g2);
            case FALLING -> getFallSprite();
            case DROWNING -> getDrownSprite();
        };
    }
    private BufferedImage getIdleSprite() {
        BufferedImage idleSprite;

        if (spriteNum == 1) {
            idleSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down1;
                case LEFT -> left1;
                case RIGHT -> right1;
            };
        } else {
            idleSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down2;
                case LEFT -> left2;
                case RIGHT -> right2;
            };
        }

        return idleSprite;
    }
    private BufferedImage getRollingSprite() {
        BufferedImage rollingSprite = rollUp1;

        rollingSprite = switch (direction) {
            case UP, UPLEFT, UPRIGHT -> switch (spriteNum) {
                case 1 -> rollUp1;
                case 2 -> rollUp2;
                case 3 -> rollUp3;
                case 4 -> rollUp4;
                default -> rollingSprite;
            };
            case DOWN, DOWNLEFT, DOWNRIGHT -> switch (spriteNum) {
                case 1 -> rollDown1;
                case 2 -> rollDown2;
                case 3 -> rollDown3;
                case 4 -> rollDown4;
                default -> rollingSprite;
            };
            case LEFT -> switch (spriteNum) {
                case 1 -> rollLeft1;
                case 2 -> rollLeft2;
                case 3 -> rollLeft3;
                case 4 -> rollLeft4;
                default -> rollingSprite;
            };
            case RIGHT -> switch (spriteNum) {
                case 1 -> rollRight1;
                case 2 -> rollRight2;
                case 3 -> rollRight3;
                case 4 -> rollRight4;
                default -> rollingSprite;
            };
        };

        return rollingSprite;
    }
    private BufferedImage getGuardSprite() {
        BufferedImage guardSprite;

        if (spriteNum == 1) {
            guardSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> guardUp1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> guardDown1;
                case LEFT -> guardLeft1;
                case RIGHT -> guardRight1;
            };
        } else {
            guardSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> guardUp2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> guardDown2;
                case LEFT -> guardLeft2;
                case RIGHT -> guardRight2;
            };
        }

        return guardSprite;
    }
    private BufferedImage getAttackSprite() {

        BufferedImage attackSprite = attackUp1;

        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                if (attackNum > 1) {
                    tempScreenPoint.y -= gp.tileSize;
                }
                attackSprite = switch (attackNum) {
                    case 1 -> attackUp1;
                    case 2 -> attackUp2;
                    case 3 -> {
                        if (spriteNum == 1) {
                            yield attackUp3;
                        }
                        else {
                            yield attackUp4;
                        }
                    }
                    default -> attackSprite;
                };
            }
            case DOWN, DOWNLEFT, DOWNRIGHT-> {
                if (attackNum == 1 || attackNum == 2) {
                    tempScreenPoint.x -= gp.tileSize;
                }
                attackSprite = switch (attackNum) {
                    case 1 -> attackDown1;
                    case 2 -> attackDown2;
                    case 3 -> {
                        if (spriteNum == 1) {
                            yield attackDown3;
                        }
                        else {
                            yield attackDown4;
                        }
                    }
                    default -> attackSprite;
                };
            }
            case LEFT -> {
                if (attackNum == 1 || attackNum == 2) {
                    tempScreenPoint.y -= gp.tileSize;
                }
                if (attackNum == 2 || attackNum == 3) {
                    tempScreenPoint.x -= gp.tileSize;
                }
                attackSprite = switch (attackNum) {
                    case 1 -> attackLeft1;
                    case 2 -> attackLeft2;
                    case 3 -> {
                        if (spriteNum == 1) {
                           yield attackLeft3;
                        }
                        else {
                            yield attackLeft4;
                        }
                    }
                    default -> attackSprite;
                };
            }
            case RIGHT -> {
                if (attackNum == 1 || attackNum == 2) {
                    tempScreenPoint.y -= gp.tileSize;
                }
                attackSprite = switch (attackNum) {
                    case 1 -> attackRight1;
                    case 2 -> attackRight2;
                    case 3 -> {
                        if (spriteNum == 1) {
                            yield attackRight3;
                        }
                        else {
                            yield attackRight4;
                        }
                    }
                    default -> attackSprite;
                };
            }
        }

        return attackSprite;
    }
    private BufferedImage getSpinSprite() {
        BufferedImage spinSprite = spinUp1;

        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                tempScreenPoint.y -= gp.tileSize;
                spinSprite = switch (attackNum) {
                    case 1 -> spinUp1;
                    case 2 -> spinUp2;
                    default -> spinSprite;
                };
            }
            case DOWN, DOWNLEFT, DOWNRIGHT-> {
                if (attackNum == 1) {
                    tempScreenPoint.x -= gp.tileSize;
                }
                spinSprite = switch (attackNum) {
                    case 1 -> spinDown1;
                    case 2 -> spinDown2;
                    default -> spinSprite;
                };
            }
            case LEFT -> {
                 tempScreenPoint.x -= gp.tileSize;
                if (attackNum == 1) {
                    tempScreenPoint.y -= gp.tileSize;
                }
                spinSprite = switch (attackNum) {
                    case 1 -> spinLeft1;
                    case 2 -> spinLeft2;
                    default -> spinSprite;
                };
            }
            case RIGHT ->
                    spinSprite = switch (attackNum) {
                        case 1 -> spinRight1;
                        case 2 -> spinRight2;
                        default -> spinSprite;
                     };
        }

        return spinSprite;
    }
    private BufferedImage getThrowSprite() {
        BufferedImage throwSprite;

        if (throwNum == 1) {
            throwSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> throwUp1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> throwDown1;
                case LEFT -> throwLeft1;
                case RIGHT -> throwRight1;
            };
        } else {
            throwSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> throwUp2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> throwDown2;
                case LEFT -> throwLeft2;
                case RIGHT -> throwRight2;
            };
        }

        return throwSprite;
    }
    private BufferedImage getDigSprite() {
        BufferedImage digSprite;

        if (digNum == 1) {
            digSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> digUp1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> digDown1;
                case LEFT -> digLeft1;
                case RIGHT -> digRight1;
            };
        } else {
            digSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> digUp2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> digDown2;
                case LEFT -> digLeft2;
                case RIGHT -> digRight2;
            };
        }

        return digSprite;
    }
    private BufferedImage getAimSprite() {
        BufferedImage aimSprite;

        if (aimNum == 1) {
            aimSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> aimUp1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> aimDown1;
                case LEFT -> aimLeft1;
                case RIGHT -> aimRight1;
            };
        } else {
            aimSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> aimUp2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> aimDown2;
                case LEFT -> aimLeft2;
                case RIGHT -> aimRight2;
            };
        }

        return aimSprite;
    }
    private BufferedImage getJumpSprite(Graphics2D g2) {
        BufferedImage jumpSprite;

        tempScreenPoint.y -= 30;

        if (jumpNum == 1) {
            jumpSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> jumpUp1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> jumpDown1;
                case LEFT -> jumpLeft1;
                case RIGHT -> jumpRight1;
            };
        }
        else if (jumpNum == 2) {
            jumpSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> jumpUp2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> jumpDown2;
                case LEFT -> jumpLeft2;
                case RIGHT -> jumpRight2;
            };
        } else if (jumpNum == 3) {
            jumpSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> jumpUp3;
                case DOWN, DOWNLEFT, DOWNRIGHT -> jumpDown3;
                case LEFT -> jumpLeft3;
                case RIGHT -> jumpRight3;
            };
        } else {
            jumpSprite = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> soarUp1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> soarDown1;
                case LEFT -> soarLeft1;
                case RIGHT -> soarRight1;
            };
        }

        g2.setColor(Color.BLACK);
        g2.fillOval(tempScreenPoint.x + 10, tempScreenPoint.y + 70, 30, 10);

        return jumpSprite;        
    }
    private BufferedImage getFallSprite() {
        BufferedImage fallSprite;

        if (damageNum == 1) {
            fallSprite = fall1;
        }
        else if (damageNum == 2) {
            fallSprite = fall2;
        }
        else if (damageNum == 3) {
            fallSprite = fall3;
        }
        else {
            fallSprite = null;
        }

        return fallSprite;
    }
    private BufferedImage getDrownSprite() {
        return drown1;
    }

    /** GETTERS */
    public Entity getLockedOnTarget() {
        return lockedOnTarget;
    }
}
