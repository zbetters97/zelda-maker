package entity;

import application.GamePanel;
import application.GamePanel.Direction;
import entity.item.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static application.GamePanel.Direction.*;

public class Player extends Entity {

    /* X/Y VALUES */
    public int safeWorldX = 0;
    public int safeWorldY = 0;

    /** ANIMATION HANDLERS */
    private int spinCharge = 0;
    private int coolDownCounter = 0;
    private int
            attackNum = 1, attackCounter = 0,
            digNum = 1, digCounter = 0,
            aimNum = 1, aimCounter = 0,
            throwNum = 1, throwCounter = 0,
            jumpNum = 1, jumpCounter = 0,
            damageNum = 1, damageCounter = 0;

    /** SPRITE IMAGES */
    private BufferedImage
            spinUp1, spinUp2, spinDown1, spinDown2,
            spinLeft1, spinLeft2, spinRight1, spinRight2,

            rollUp1, rollUp2, rollUp3, rollUp4, rollDown1, rollDown2, rollDown3, rollDown4,
            rollLeft1, rollLeft2, rollLeft3, rollLeft4, rollRight1, rollRight2, rollRight3, rollRight4,

            guardUp1, guardUp2, guardDown1, guardDown2,
            guardLeft1, guardLeft2, guardRight1, guardRight2,

            digUp1, digUp2, digDown1, digDown2,
            digLeft1, digLeft2, digRight1, digRight2,

            aimUp1, aimUp2, aimDown1, aimDown2,
            aimLeft1, aimLeft2, aimRight1, aimRight2,
    
            throwUp1, throwUp2, throwDown1, throwDown2,
            throwLeft1, throwLeft2, throwRight1, throwRight2,

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
        super(gp);

        name = "Link";
        health = 16;
        maxHealth = 16;

        // Player position locked to center of screen
        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        // Hitbox
        hitbox = new Rectangle(8, 12, 32, 34);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        // Attack box
        attackBox.width = 44;
        attackBox.height = 42;

        attack = 1;

        // Attack speed
        swingSpeed1 = 4;
        swingSpeed2 = 7;
        swingSpeed3 = 15;
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
        getAttackImages();
        getSpinImages();
        getRollImages();
        getGuardImages();
        getDigImages();
        getAimImages();
        getThrowImages();
        getJumpImages();
        getSoarImages();
        getFallImages();
        getDrownImages();
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
        setDefaultPosition();

        attack = 2;
        arrows = 50;
        currentItem = new ITM_Cape(gp, this);
    }

    /**
     * SET DEFAULT ANIMATION VALUES
     */
    private void setDefaultAnimationValues() {
        speed = 3;
        defaultSpeed = speed;
        animationSpeed = 10;
        action = Action.IDLE;
    }

    /**
     * SET DEFAULT POSITON
     */
    private void setDefaultPosition() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;

        safeWorldX = worldX;
        safeWorldY = worldY;

        gp.currentMap = 0;
        gp.currentArea = gp.outside;
    }

    /**
     * UPDATE
     * Updates player character based on user inputs
     * Called by GamePanel every frame
     */
    @Override
    public void update() {

        // Always check for collision
        checkCollision();

        if (knockback) {
            handleKnockback();
            manageValues();
            return;
        }

        // Allow A press only when Idle
        if (action == Action.IDLE) {
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
        // Swing sword
        else if (gp.keyH.bPressed) {
            action = Action.ATTACKING;
        }
        // Shield guard
        else if (gp.keyH.rPressed) {
            action = Action.GUARDING;
            spriteNum = 1;
            spriteCounter = 0;
        }
        // Use item
        else if (gp.keyH.xPressed) {
            useItem();
        }
        // Z-target
        else if (gp.keyH.lPressed) {
            startZTarget();
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
        if (action == Action.IDLE) {
            // Cooldown needed for rolling
            if (moving && coolDownCounter == 0) {
                startRoll();
            } else {
                interactIObjects();
            }
        }
    }

    private void interactIObjects() {
        int iObject = gp.cChecker.checkEntity(this, gp.obj_i);

        if (iObject != -1) {
            gp.obj_i[gp.currentMap][iObject].interact(this);
        }
    }

    /**
     * START ROLL
     * Initiates roll logic
     * Called by startAction() when player initiates ROLL
     */
    private void startRoll() {
        action = Action.ROLLING;
        spriteNum = 1;
        spriteCounter = 0;
        lockonDirection = direction;
        coolDownCounter = 30;
    }

    /**
     * USE ITEM
     * Initiates the item use function
     * Called by handleActionInput()
     */
    private void useItem() {

        // Item equipped
        if (currentItem != null) {


            switch (currentItem.name) {
                case ITM_Shovel.itmName, ITM_Boomerang.itmName, ITM_Hookshot.itmName -> currentItem.use();
                case ITM_Bow.itmName, ITM_Feather.itmName, ITM_Cape.itmName -> {
                   lockonDirection = direction;
                   currentItem.use();
                }
            }
        }
        // No equipped item
        else {
            gp.keyH.xPressed = false;
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

            if (e != null && e != lockedOnTarget && !e.dying) {

                int enemyDistance = getTileDistance(e);
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
        if (lockedOnTarget != null && getTileDistance(lockedOnTarget) < maxZTargetDistance) {

            // Target alive
            if (lockedOnTarget.alive) {
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
        int px = (worldX + (gp.tileSize / 2));
        int py = (worldY + (gp.tileSize / 2));

        // Target X/Y
        int ex = (target.worldX + (gp.tileSize / 2));
        int ey = (target.worldY + (gp.tileSize / 2));

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
    /** END Z-TARGETING */

    /**
     * UPDATE ACTION
     * Calls the action method based on current player action
     * Called by update()
     */
    private void updateAction() {
        switch (action) {
            case ATTACKING -> attack();
            case SPINCHARGING -> spinCharging();
            case SPINNING -> spinning();
            case ROLLING -> rolling();
            case GUARDING -> guarding();
            case DIGGING -> digging();
            case AIMING -> aiming();
            case THROWING -> throwing();
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
    protected void checkCollision() {
        collisionOn = false;

        // Check tile collision
        gp.cChecker.checkTile(this);

        // Check NPC collision
        gp.cChecker.checkEntity(this, gp.npc);

        // Check enemy collision
        gp.cChecker.checkEntity(this, gp.enemy);

        // Check interactive object collision
        gp.cChecker.checkEntity(this, gp.obj_i);

        // Player contacted enemy, take damage
        Entity enemy = getEnemy(this);
        if (enemy != null && !enemy.invincible) {
            damagePlayer(enemy.attack);
        }

        gp.cChecker.checkHazard(this);
    }

    /**
     * ATTACKING
     * Handles logic for swinging sword
     * Called by updateAction() when action is ATTACKING
     */
    @Override
    protected void attack() {

        // Increase spin charge if player holds B button
        if (gp.keyH.bPressed) {
            spinCharge++;
        }
        else {
            spinCharge = 0;
        }

        attackCounter++;
        if (attackCounter <= swingSpeed1) {
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
            action = Action.IDLE;

            // Spin charge ready for spin attack
            if (swingSpeed3 < spinCharge && gp.keyH.bPressed) {
                action = Action.SPINCHARGING;
                lockonDirection = direction;
            }

            attackCounter = 0;
            spinCharge = 0;
        }
    }

    /**
     * ADJUST SWING HITBOX
     * Modifies hitbox to accommodate for slash attack
     * Called by attacking()
     */
    private void adjustSwingHitbox() {

        // Save X/Y
        int currentWorldX = worldX;
        int currentWorldY = worldY;

        // Reposition X/Y and hitbox for slash
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

        detectEnemyCollision();

        // Restore hitbox
        worldX = currentWorldX;
        worldY = currentWorldY;
        hitbox.width = hitboxDefaultWidth;
        hitbox.height = hitboxDefaultHeight;
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
            action = Action.SPINNING;
        }
        // Player released B, charge not ready, reset to idle
        else {
            charge = 0;
            attackNum = 1;
            attackCounter = 0;
            speed = defaultSpeed;
            action = Action.IDLE;
        }
    }

    /**
     * UPDATE SPIN DIRECTION
     * Assigns new direction using counter-clockwise rotation
     * Called by charging() and spinning()
     */
    private void updateSpinDirection() {
        direction = switch (direction) {
            case UP, UPLEFT, UPRIGHT -> LEFT;
            case DOWN, DOWNLEFT, DOWNRIGHT -> RIGHT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
        };
    }

    /**
     * SPIN ATTACKING
     * Handles sword spin attack logic
     * Called by getAction() when action == SPINNING
     */
    private void spinning() {

        attackCounter++;
        if (attackCounter < 3) {
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
                action = Action.IDLE;
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
        int currentWorldX = worldX;
        int currentWorldY = worldY;

        // Reposition X/Y and hitbox for spinning slash
        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                worldY -= attackBox.height + hitbox.y;
                hitbox.height = attackBox.height;
                hitbox.width *= 2;
            }
            case DOWN, DOWNLEFT, DOWNRIGHT -> {
                worldX -= attackBox.width;
                worldY += attackBox.height - hitbox.y;
                hitbox.height = attackBox.height;
                hitbox.width *= 2;
            }
            case LEFT -> {
                worldX -= attackBox.width;
                worldY -= attackBox.height;
                hitbox.width = attackBox.width;
                hitbox.height *= 2;
            }
            case RIGHT -> {
                worldX += attackBox.width - hitbox.y;
                hitbox.width = attackBox.width;
                hitbox.height *= 2;
            }
        }

        detectEnemyCollision();

        // Reset X/Y and Hitbox
        worldX = currentWorldX;
        worldY = currentWorldY;
        hitbox.width = hitboxDefaultWidth;
        hitbox.height = hitboxDefaultHeight;
    }

    /**
     * DETECT ENEMY COLLISION
     * Checks if an enemy collides with the player's hitbox/attackBox
     */
    private void detectEnemyCollision() {
        // Find enemy that intersects collision box
        Entity enemy = getEnemy(this);

        // Sword collides with enemy, apply damage
        if (enemy != null && !enemy.invincible) {
            damageEnemy(enemy);
        }
    }

    /**
     * ROLLING
     * Handles logic for when the player rolls
     * Called by getAction() when action = ROLLING
     */
    private void rolling() {

        speed = 5;

        spriteCounter++;
        if (spriteCounter < 6) {
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
            action = Action.IDLE;
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
            action = Action.IDLE;
        }
    }

    /**
     * DIGGING
     * Runs digging animation and logic
     * Called by startAction() when player action is DIGGING
     */
    private void digging() {
        digCounter++;

        if (digCounter <= 12) {
            digNum = 1;
        }
        else if (digCounter < 24 ) {
            digNum = 2;
        }
        else {
            digNum = 1;
            digCounter = 0;
            action = Action.IDLE;
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
            aimCounter++;

            if (10 < aimCounter) {
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
            currentItem.use();
        }
        else {
            currentItem.attack();
            speed = defaultSpeed;
        }
    }

    /**
     * THROWING
     * Runs throwing animation and logic
     * Called by startAction() when player action is THROWING
     */
    private void throwing() {

        throwCounter++;

        if (throwCounter <= 6) {
            throwNum = 1;
        }
        else {
            throwNum = 2;
        }

        if (28 < throwCounter && action == Action.IDLE) {
            throwNum = 1;
            throwCounter = 0;
        }
    }

    /**
     * JUMPING
     * Runs jumping animation and logic
     * Called by startAction() when player action is JUMPING
     */
    private void jumping() {

        jumpCounter++;

        if (jumpCounter <= 6) {
            jumpNum = 1;
        }
        else if (12 <= jumpCounter && jumpCounter < 18) {
            jumpNum = 2;
        }
        else if (jumpCounter < 27) {
            jumpNum = 3;
        }
        else if (28 <= jumpCounter) {
            if (action == Action.SOARING) {
                jumpNum = 4;
                soaring();
            }
            else {
                jumpNum = 1;
                jumpCounter = 0;
                isElevated = false;
                action = Action.IDLE;
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
            isElevated = false;
            action = Action.IDLE;

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
            case UP, UPLEFT, UPRIGHT -> worldY--;
            case DOWN, DOWNLEFT, DOWNRIGHT -> worldY++;
            case LEFT -> worldX--;
            case RIGHT -> worldX++;
        }
    }

    private void takingDamage() {

        damageCounter++;

        if (damageCounter <= 6) {
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

            action = Action.IDLE;
            worldX = safeWorldX;
            worldY = safeWorldY;
        }

    }

    /**
     * MANAGE VALUES
     * Resets or reassigns player attributes
     * Called by update() at the end
     */
    @Override
    protected void manageValues() {

        // Decrease cooldown if filled
        if (coolDownCounter > 0) {
            coolDownCounter--;
        }

        // Shield after taking damage
        if (invincible) {
            invincibleCounter++;

            // 1 SECOND REFRESH TIME
            if (invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
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

        g2.drawImage(image, tempScreenX, tempScreenY, null);

        // Reset opacity
        changeAlpha(g2, 1f);
    }

    /**
     * OFF CENTER
     * Adjusts X, Y if near edge
     */
    private void offCenter() {
        tempScreenX = screenX;
        tempScreenY = screenY;

        if (worldX < screenX) {
            tempScreenX = worldX;
        }
        if (worldY < screenY) {
            tempScreenY = worldY;
        }

        // From player to right-edge of screen
        int rightOffset = gp.screenWidth - screenX;

        //  From player to right-edge of world
        if (rightOffset > gp.worldWidth - worldX) {
            tempScreenX = gp.screenWidth - (gp.worldWidth - worldX);
        }

        // From player to bottom-edge of screen
        int bottomOffSet = gp.screenHeight - screenY;

        //  From player to bottom-edge of world
        if (bottomOffSet > gp.worldHeight - worldY) {
            tempScreenY = gp.screenHeight - (gp.worldHeight - worldY);
        }
    }

    /** GET CURRENT SPRITE TO DRAW */
    private void getSpriteImage(Graphics2D g2) {
        image = switch (action) {
            case IDLE -> getIdleSprite();
            case ATTACKING, SPINCHARGING -> getAttackSprite();
            case SPINNING ->  getSpinSprite();
            case ROLLING -> getRollingSprite();
            case GUARDING -> getGuardSprite();
            case DIGGING -> getDigSprite();
            case AIMING -> getAimSprite();
            case THROWING -> getThrowSprite();
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
    private BufferedImage getAttackSprite() {

        BufferedImage attackSprite = attackUp1;

        switch (direction) {
            case UP, UPLEFT, UPRIGHT -> {
                if (attackNum > 1) {
                    tempScreenY -= gp.tileSize;
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
                    tempScreenX -= gp.tileSize;
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
                    tempScreenY -= gp.tileSize;
                }
                if (attackNum == 2 || attackNum == 3) {
                    tempScreenX -= gp.tileSize;
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
                    tempScreenY -= gp.tileSize;
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
                tempScreenY -= gp.tileSize;
                spinSprite = switch (attackNum) {
                    case 1 -> spinUp1;
                    case 2 -> spinUp2;
                    default -> spinSprite;
                };
            }
            case DOWN, DOWNLEFT, DOWNRIGHT-> {
                if (attackNum == 1) {
                    tempScreenX -= gp.tileSize;
                }
                spinSprite = switch (attackNum) {
                    case 1 -> spinDown1;
                    case 2 -> spinDown2;
                    default -> spinSprite;
                };
            }
            case LEFT -> {
                 tempScreenX -= gp.tileSize;
                if (attackNum == 1) {
                    tempScreenY -= gp.tileSize;
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
    private BufferedImage getJumpSprite(Graphics2D g2) {
        BufferedImage jumpSprite;

        tempScreenY -= 30;

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
        g2.fillOval(tempScreenX + 10, tempScreenY + 70, 30, 10);

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
        BufferedImage drownSprite = drown1;
        return drownSprite;
    }

    /** GETTERS */
    public Entity getLockedOnTarget() {
        return lockedOnTarget;
    }
}
