package application;

import entity.Entity;
import entity.item.ITM_Bow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class UI {

    /** CONFIG */
    private final GamePanel gp;
    private Graphics2D g2;
    private Font PK_DS;

    /** UI COLORS */
    private final Color pause_text = new Color(76, 50, 13, 245);
    private final Color itm_brown_1 = new Color(168, 127, 89);
    private final Color itm_brown_2 = new Color(217, 180, 122);
    private final Color itm_green = new Color(95, 190, 80);
    private final Color pause_brown_1 = new Color(76, 50, 13, 245);
    private final Color pause_brown_2 = new Color(73, 83, 36, 105);
    private final Color pause_brown_3 = new Color(247, 219, 167);
    private final Color pause_yellow = new Color(248, 242, 135);

    /** HUD HANDLERS */
    private int rupeeChange;
    private int rupeeCounter = 0;

    /** Z-TARGETING */
    private int zTargetCounter = 0;
    private int zTargetDirection = 0;
    private int zTargetRotation = 0;

    /** PAUSE SCREEN HANDLERS */
    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    private final int slotSize = 78;
    private final int cursorSize = 93;

    /** SPRITES */
    private BufferedImage
            rupee,
            heart_0, heart_1, heart_2, heart_3, heart_4,
            ztarget_arrow, ztarget_circle;

    /**
     * CONSTRUCTOR
     * Instance created by GamePanel
     * @param gp GamePanel
     */
    public UI(GamePanel gp) {
        this.gp = gp;

        importFont();
        getAllImages();
    }

    /**
     * IMPORT FONT
     * Called by Constructor
     */
    private void importFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/font/pokemon-ds.ttf");
            PK_DS = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(is));
        }
        catch (FontFormatException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getAllImages() {
        getHUDImages();
        getZTargetImages();
    }
    private void getZTargetImages() {
        ztarget_arrow = setupImage("/ui/ui_ztarget_arrow", 48 + 20, 48 + 20);
        ztarget_circle = setupImage("/ui/ui_ztarget_circle", 48 + 20, 48 + 20);
    }
    private void getHUDImages() {
        rupee = setupImage("/ui/ui_rupee");

        heart_0 = setupImage("/ui/ui_heart_0", 23, 23);
        heart_1 = setupImage("/ui/ui_heart_1", 23, 23);
        heart_2 = setupImage("/ui/ui_heart_2", 23, 23);
        heart_3 = setupImage("/ui/ui_heart_3", 23, 23);
        heart_4 = setupImage("/ui/ui_heart_4", 23, 23);
    }

    /**
     * DRAW
     * Draws the UI
     * Called by GamePanel
     * @param g2 Graphics2D enginge
     */
    public void draw(Graphics2D g2) {

        this.g2 = g2;

        g2.setFont(PK_DS);
        g2.setColor(Color.white);

        if (gp.gameState == gp.playState) {
            drawHUD();
        }
        else if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }
    }

    /**
     * DRAW HUD
     * Draws the HUD during playstate
     * called by draw()
     */
    private void drawHUD() {
        drawZTarget();
        drawChargeBar();
        drawPlayerHealth();
        drawPlayerItem();
        drawRupeeCount();
        drawDebug();
    }

    /**
     * DRAW PLAYER HEALTH
     * Draws the current player's health in the top-left corner of the screen
     * Called by drawHUD()
     */
    private void drawPlayerHealth() {

        // Top-left corner of screen
        int x = gp.tileSize / 2;
        int y = gp.tileSize / 2;
        int spacing = (int) (gp.tileSize / 1.7);

        // Get count of whole hearts
        int maxHearts = gp.player.getMaxHealth() / 4;
        int currentHealth = gp.player.getHealth();

        // Iterate through all whole hearts
        for (int i = 0; i < maxHearts; i++) {

            // 4 if currentHealth is above 4, otherwise currentHealth
            int heartHealth = Math.min(4, currentHealth);

            // Find which fraction heart to use
            BufferedImage heart;
            switch (heartHealth) {
                case 4 -> heart = heart_4;
                case 3 -> heart = heart_3;
                case 2 -> heart = heart_2;
                case 1 -> heart = heart_1;
                default -> heart = heart_0;
            }

            g2.drawImage(heart, x, y, null);

            // De-increment health
            currentHealth -= 4;

            // Move right for next heart
            x += spacing;
        }
    }

    /**
     * DRAW PLAYER ITEM
     * Draws the current player's item in the top-right corner of the screen
     * Called by drawHUD()
     */
    private void drawPlayerItem() {
        int x = gp.tileSize * 14 - 15;
        int y = gp.tileSize / 2 - 15;
        int width = gp.tileSize + 30;
        int height = gp.tileSize + 30;

        // DRAW ITEM CIRCLE
        g2.setColor(itm_brown_1);
        g2.fillOval(x, y, width, height);

        g2.setColor(itm_green);
        g2.setStroke(new BasicStroke(4));
        g2.drawOval(x, y, width, height);

        if (gp.player.getItem() != null) {
            x += 10;
            y += 10;
            g2.drawImage(gp.player.getItem().image, x, y, gp.tileSize + 10, gp.tileSize + 10, null);
        }

        // DRAW ITEM BUTTON
        x = gp.tileSize * 13 + 28;
        y = gp.tileSize + 12;
        width = 35;
        height = 35;
        g2.setColor(itm_green);
        g2.fillOval(x, y, width, height);

        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 33F));
        String text = KeyEvent.getKeyText(gp.keyH.btn_X);
        x = getXForCenteredTextOnWidth(text, width, x);
        y += 28;
        g2.drawString(text, x, y);

        g2.setStroke(new BasicStroke(1));
    }

    /**
     * DRAW RUPEE COUNT
     * Draws the current player's rupee count in the bottom-right corner of the screen
     * Called by drawHUD()
     */
    private void drawRupeeCount() {

        // Draw rupee image
        int x = gp.tileSize * 14 - 20;
        int y = gp.tileSize * 10 + 20;
        g2.drawImage(rupee, x, y, gp.tileSize - 5, gp.tileSize - 5, null);

        x += gp.tileSize - 8;
        y += gp.tileSize - 12;

        // Keep new rupees at maximum
        if (rupeeChange >= gp.player.getMaxRupees()) {
            rupeeChange = gp.player.getMaxRupees();
        }

        // Player adds rupees
        if (gp.player.getRupees() < rupeeChange) {
            modifyRupeeCount(1);
        }
        // Player loses rupees
        else if (rupeeChange < gp.player.getRupees()) {
            modifyRupeeCount(-1);
        }

        String formattedCount = formatRupeeCount();

        // Draw rupee count
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 45F));
        g2.drawString(formattedCount, x, y);
    }

    /**
     * MODIFY RUPEE COUNT
     * Changes player rupee count every 2 frames based on given value
     * @param count Amount to add to player rupee count
     */
    private void modifyRupeeCount(int count) {
        if (rupeeCounter == 2) {
            gp.player.addRupees(count);
            rupeeCounter = 0;
        }
        else {
            rupeeCounter++;
        }
    }

    /**
     * FORMAT RUPEE COUNT
     * Formats the player rupee count based on wallet size
     * @return Formatted player rupee count
     */
    private String formatRupeeCount() {

        String rupeeCount = "0";

        if (gp.player.getMaxRupees() == 99) {
            rupeeCount = String.format("%02d", gp.player.getRupees());
        }
        else if (gp.player.getMaxRupees() == 999) {
            rupeeCount = String.format("%03d", gp.player.getRupees());
        }
        else if (gp.player.getMaxRupees() == 9999) {
            rupeeCount = String.format("%04d", gp.player.getRupees());
        }

        return rupeeCount;
    }

    public void setRupeeChange(int rupees) {
        rupeeChange += rupees;
    }

    /**
     * DRAW CHARGE BAR
     * Draws the spin attack charge bar
     * Called by drawHUD()
     */
    private void drawChargeBar() {

        // If player is charging spin attack
        if (gp.player.charge > 0) {

            // Position above player's head
            int x = gp.player.getScreenPoint().x - 7;
            int y = gp.player.getScreenPoint().y - 20;
            int width = 62;
            int height = 10;

            // Draw black bar
            Color barColor = Color.BLACK;
            g2.setColor(barColor);
            g2.fillRect(x, y, width, height);

            // White outline if not ready, green fill if ready
            int charge = gp.player.charge;
            barColor = charge < 120 ? Color.WHITE : new Color(0, 240, 0);

            g2.setColor(barColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(x, y, width, height);

            barColor = getChargeColor(charge);
            g2.setColor(barColor);

            // Bar fill, slowly increase width
            x++;
            y++;
            height -= 2;
            width = charge / 2;
            g2.fillRect(x, y, width, height);
        }
    }

    /**
     * GET CHARGE COLOR
     * Gets the color of the spin attack charge bar based on charge
     * Called by drawChargeBar()
     * @param charge Current player charge value
     * @return The new color of the charge bar
     */
    private Color getChargeColor(int charge) {
        if (charge < 40) return new Color(0, 105, 0);
        if (charge < 80) return new Color(0, 155, 0);
        if (charge < 120) return new Color(0, 205, 0);

        return new Color(0, 240, 0);
    }

    private void drawZTarget() {

        Entity target = gp.player.getLockedOnTarget();

        // No enemy locked on
        if (target == null) {

            // Find closest enemy
            Entity newTarget = getNewTarget();

            // Close enemy found, draw Z-target
            if (newTarget != null) {
                drawZTargetArrow(newTarget);
            }
        }
        // Enemy locked on
        else {
            drawZTargetCircle(target);
        }
    }
    private Entity getNewTarget() {

        Entity newTarget = null;
        int currentDistance = Entity.maxZTargetDistance;

        for (Entity e : gp.enemy[gp.currentMap]) {

            if (e != null) {

                // Enemy distance from player
                int enemyDistance = e.getTileDistance(gp.player);

                // Find closest enemy distance within 8 tiles
                if (enemyDistance < currentDistance) {
                    currentDistance = enemyDistance;
                    newTarget = e;
                }
            }
        }

        return newTarget;
    }
    private void drawZTargetArrow(Entity newTarget) {
        newTarget.adjustOffCenter();

        if (zTargetCounter < 20 && zTargetDirection == 0) {
            zTargetCounter++;
        }
        else if (zTargetCounter < 20 && zTargetDirection == 1) {
            zTargetCounter--;
        }
        if (zTargetCounter == 20) {
            zTargetCounter--;
            zTargetDirection = 1;
        }
        else if (zTargetCounter == 0) {
            zTargetCounter++;
            zTargetDirection = 0;
        }

        int x = newTarget.getTempScreenPoint().x - 10;
        int y = newTarget.getTempScreenPoint().y - 30 + zTargetCounter;

        g2.drawImage(ztarget_arrow, x, y, null);
    }
    private void drawZTargetCircle(Entity target) {
        target.adjustOffCenter();

        zTargetRotation += 3;
        if (zTargetRotation >= 180) {
            zTargetRotation = 0;
        }

        BufferedImage img = rotateImage(ztarget_circle, zTargetRotation);

        g2.drawImage(img, target.getTempScreenPoint().x - 10, target.getTempScreenPoint().y - 10, null);
    }
    private BufferedImage rotateImage(BufferedImage img, int degrees) {

        AffineTransform rotation = AffineTransform.getRotateInstance(
                Math.toRadians(degrees), (double) img.getWidth() / 2, (double) img.getHeight() / 2
        );

        AffineTransformOp op = new AffineTransformOp(rotation, AffineTransformOp.TYPE_BICUBIC);

        return op.filter(img, null);
    }

    /**
     * DRAW DEBUG
     * UI for debug information
     * Called by drawHUD()
     */
    private void drawDebug() {

        int x = 10;
        int y = gp.tileSize * 6;
        int lineHeight = 20;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));

        // Draw coordinates
        g2.drawString("World X: " + gp.player.getWorldPoint().x, x, y);
        y += lineHeight;
        g2.drawString("World Y: " + gp.player.getWorldPoint().y, x, y);
        y += lineHeight;
        g2.drawString("Column: " + gp.player.getCenterX() / gp.tileSize, x, y);
        y += lineHeight;
        g2.drawString("Row: " + gp.player.getCenterY() / gp.tileSize, x, y);

        // Draw player hitbox
        g2.setColor(Color.RED);
        g2.drawRect(
                gp.player.getScreenPoint().x + gp.player.getHitbox().x,
                gp.player.getScreenPoint().y + gp.player.getHitbox().y,
                gp.player.getHitbox().width,
                gp.player.getHitbox().height);
    }

    /**
     * DRAW PAUSE SCREEN
     * UI for pause screen
     * Called by draw()
     */
    private void drawPauseScreen() {

        g2.setColor(pause_brown_1);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setColor(itm_green);
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(-1, gp.tileSize * 2 + 15, gp.screenWidth + 2, gp.tileSize * 8 + 20);
        g2.setColor(pause_brown_2);
        g2.fillRect(-1, gp.tileSize * 2 + 15, gp.screenWidth + 2, gp.tileSize * 8 + 20);

        pause_inventory();
        pause_header("Inventory");
    }
    private void pause_header(String headerText) {

        String text;
        int width = 32;
        int height = 32;
        int textX;
        int textY;
        int lX = 254;
        int rX = 494;

        // Header
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 45F));
        textX = getXForCenteredText(headerText);
        textY = gp.tileSize + 20;
        g2.drawString(headerText, textX, textY);

        // L
        textY -= 5;
        g2.setColor(itm_brown_2);
        g2.fillRoundRect(lX - 10, textY - 25, width, height, 10, 10);

        g2.setColor(pause_text);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
        text = KeyEvent.getKeyText(gp.keyH.btn_L);
        g2.drawString(text, lX, textY);

        changeAlpha(g2, 1f);

        // R
        g2.setColor(itm_brown_2);
        g2.fillRoundRect(rX - 10, textY - 25, width, height, 10, 10);

        g2.setColor(pause_text);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
        text = KeyEvent.getKeyText(gp.keyH.btn_R);
        g2.drawString(text, rX, textY);

        changeAlpha(g2, 1f);
    }

    private void pause_inventory() {
        pause_inventory_items();
    }
    private void pause_inventory_items() {

        int slotXStart = gp.screenWidth / 2 + gp.tileSize + 8;
        int slotYStart = gp.tileSize * 4 + 20;
        int slotX = slotXStart;
        int slotY = slotYStart;

        // Draw item slots
        for (int i = 0; i < 9; i++) {

            Entity currentItem = null;
            if (gp.player.getInventoryItems().size() > i) {
                currentItem = gp.player.getInventoryItems().get(i);
            }

            // Equipped cursor
            boolean isCurrentItem = currentItem != null && currentItem == gp.player.getItem();
            if (isCurrentItem) {
                g2.setColor(itm_green);
            }
            else {
                g2.setColor(itm_brown_2);
            }

            drawItemSlot(slotX, slotY);

            // Draw item image
            if (currentItem != null) {
                g2.drawImage(currentItem.image, slotX, slotY, gp.tileSize + 28, gp.tileSize + 28, null);
            }

            // Draw arrow count
            boolean isBow = currentItem != null && currentItem.getName().equals(ITM_Bow.itmName);
            if (isBow) {
                drawArrowCount(slotX, slotY);
            }

            slotX += (gp.tileSize * 2) + 8;

            // New line
            if (i == 2 || i == 5) {
                slotX = (gp.screenWidth / 2) + (gp.tileSize + 8);
                slotY += (gp.tileSize * 2) + 8;
            }
        }

        drawCursor(slotXStart, slotYStart);
        pause_inventory_Input_Dir();

        if (gp.keyH.aPressed) {
            gp.keyH.aPressed = false;

            int selectedIndex = getItemIndexOnSlot();
            if (selectedIndex < gp.player.getInventoryItems().size()) {
                gp.player.setItem(gp.player.getInventoryItems().get(selectedIndex));
            }
        }
    }
    private void drawArrowCount(int slotX, int slotY) {

        int x = slotX + gp.tileSize + 8;
        int y = slotY + gp.tileSize + 8;
        g2.setColor(pause_brown_3);
        g2.fillOval(x, y, 28, 28);

        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
        String text = Integer.toString(gp.player.getArrows());
        x = getXForCenteredTextOnWidth(text, 28, x);
        y += 24;
        g2.drawString(text, x, y);
    }
    private void pause_inventory_Input_Dir() {
        if (gp.keyH.upPressed && playerSlotRow > 0) {
            gp.keyH.upPressed = false;

            playerSlotRow--;
        }
        else if (gp.keyH.downPressed && playerSlotRow < 2) {
            gp.keyH.downPressed = false;
            playerSlotRow++;
        }
        else if (gp.keyH.leftPressed && playerSlotCol > 0) {
            gp.keyH.leftPressed = false;
            playerSlotCol--;
        }
        else if (gp.keyH.rightPressed && playerSlotCol < 2) {
            gp.keyH.rightPressed = false;
            playerSlotCol++;
        }
    }

    private void drawItemSlot(int x, int y) {
        g2.setStroke(new BasicStroke(10));
        g2.drawOval(x, y, slotSize, slotSize);
        g2.setColor(itm_brown_1);
        g2.fillOval(x, y, slotSize, slotSize);
    }
    private void drawCursor(int slotXStart, int slotYStart) {

        int x = slotXStart - 8 + ((slotSize + 26) * playerSlotCol);
        int y = slotYStart - 8 + ((slotSize + 26) * playerSlotRow);

        g2.setColor(pause_yellow);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x, y, cursorSize, cursorSize);
    }

    /**
     * GET X FOR TEXT CENTERED
     * @param text Text being used
     * @param width Width of element to center text on
     * @param x Starting point of X
     * @return Middle point X
     */
    private int getXForCenteredTextOnWidth(String text, int width, int x) {
        FontMetrics fm = g2.getFontMetrics();
        int stringWidth = fm.stringWidth(text);
        int centeredX = (width - stringWidth) / 2;
        return centeredX + x;
    }
    public int getXForCenteredText(String text) {
        try {
            int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            int x = (gp.screenWidth / 2) - (length / 2);
            return x;
        }
        catch (Exception e) {
            return gp.screenWidth / 2;
        }
    }

    private int getItemIndexOnSlot() {
        int itemIndex = playerSlotCol + (playerSlotRow * 3);
        return itemIndex;
    }

    private void changeAlpha(Graphics2D g2, float alphaValue) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
    }

    /**
     * SETUP IMAGE
     * @param imagePath Path to image file
     * @param width Width of image
     * @param height Height of image
     * @return Scaled image
     */
    private BufferedImage setupImage(String imagePath, int width, int height) {

        UtilityTool utility = new UtilityTool();
        BufferedImage image = null;

        try {
            image = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath + ".png")
            ));
            image = utility.scaleImage(image, width, height);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }
    /**
     * SETUP IMAGE
     * @param imagePath Path to image file
     * @return Scaled image
     */
    private BufferedImage setupImage(String imagePath) {

        UtilityTool utility = new UtilityTool();
        BufferedImage image = null;

        try {
            image = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath + ".png")
            ));
            image = utility.scaleImage(image, gp.tileSize, gp.tileSize);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }
}
