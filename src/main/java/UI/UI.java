package UI;

import application.GamePanel;
import application.UtilityTool;
import entity.Entity;
import entity.collectable.Collectable;
import entity.item.ITM_Bomb;
import entity.item.ITM_Bow;
import tile.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Supplier;

public class UI {

    /** CONFIG */
    private final GamePanel gp;
    private Graphics2D g2;
    private Font PK_DS;

    public Cursor cursor;

    /** UI COLORS */
    private final Color itm_brown_1 = new Color(168, 127, 89);
    private final Color itm_brown_2 = new Color(247, 219, 167);
    private final Color itm_green = new Color(95, 190, 80);

    /** HUD HANDLERS */
    private int rupeeChange;
    private int rupeeCounter = 0;

    /** Z-TARGETING */
    private int zTargetCounter = 0;
    private int zTargetDirection = 0;
    private int zTargetRotation = 0;

    /** EDITING HANDLERS */
    private boolean wasYPressed = false;
    private boolean editingTiles = false;

    /** ENTITY EDITING */
    private final ArrayList<ArrayList<UIEntity>> entityLibrary = new ArrayList<>();
    private int entityListIndex = 0;
    private int entityIndex = 0;
    private Entity currentEntity;
    private Entity selectedEntity;

    /** TILE EDITING */
    private final ArrayList<UITile> tileLibrary = new ArrayList<>();
    private int tileIndex = 0;

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

        cursor = new Cursor(gp);

        importFont();
        getAllImages();
        fillEntityLibrary();
        fillTileLibrary();
    }

    /**
     * IMPORT FONT
     * Called by Constructor
     */
    private void importFont() {

        try (InputStream is = getClass().getResourceAsStream("/font/pokemon-ds.ttf")) {
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

    private void fillTileLibrary() {

        // Import tile data
        InputStream is = getClass().getResourceAsStream("/maps/map_tile_data.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));

        try {
            String line;
            int tileNumber;

            while ((line = br.readLine()) != null) {

                if (!line.contains(".png")) continue;

                tileNumber = Integer.parseInt(line.replace(".png", ""));
                tileLibrary.add(new UITile(tileNumber));
            }

            br.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void fillEntityLibrary() {
        entityLibrary.addAll(Arrays.asList(
                buildFromFactory("npc", gp.eGenerator.npcFactory),
                buildFromFactory("enemy", gp.eGenerator.enemyFactory),
                buildFromFactory("object", gp.eGenerator.objectFactory),
                buildFromFactory("collectable", gp.eGenerator.collectableFactory)
        ));
    }
    private ArrayList<UIEntity> buildFromFactory(String path, Map<String, ? extends Supplier<Entity>> factory) {
        ArrayList<UIEntity> list = new ArrayList<>();

        for (String name : factory.keySet()) {
            list.add(new UIEntity(name, path, gp));
        }

        return list;
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
        else if (gp.gameState == gp.editState) {
            drawEditState();
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
        drawAvailableAction();
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

        int x = gp.tileSize * 15;
        int y = gp.tileSize / 3;
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
            g2.drawImage(gp.player.getItem().getSprite(), x, y, gp.tileSize + 10, gp.tileSize + 10, null);

            // DRAW ARROW COUNT
            x += 45;
            y += 43;
            if (gp.player.getItem().getName().equals(ITM_Bow.itmName)) {
                drawItemCount(x, y, Integer.toString(gp.player.getArrows()));
            }
            // DRAW BOMB COUNT
            else if (gp.player.getItem().getName().equals(ITM_Bomb.itmName)) {
                drawItemCount(x, y, Integer.toString(gp.player.getBombs()));
            }
        }

        // DRAW ITEM BUTTON
        x = gp.tileSize * 16 + 8;
        y = 10;
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

    private void drawItemCount(int x, int y, String text) {

        int width = 28;
        int height = 28;
        g2.setColor(itm_brown_2);
        g2.fillOval(x, y, width, height);

        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
        x = getXForCenteredTextOnWidth(text, width, x);
        y += 24;
        g2.drawString(text, x, y);
    }

    private void drawAvailableAction() {

        String availableAction = gp.player.getAvailableAction(gp.player);

        int x = gp.tileSize * 13;
        int y = gp.tileSize / 3;
        int width = gp.tileSize + 30;
        int height = gp.tileSize + 30;

        g2.setColor(Color.WHITE);
        g2.fillOval(x, y, width, height);

        g2.setStroke(new BasicStroke(4));
        g2.drawOval(x, y, width, height);

        g2.setColor(Color.BLUE);
        g2.fillOval(x, y, width, height);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 33F));
        x = getXForCenteredTextOnWidth(availableAction, width, x);
        y += gp.tileSize;
        g2.drawString(availableAction, x + 2, y);

        g2.setStroke(new BasicStroke(1));
    }

    /**
     * DRAW RUPEE COUNT
     * Draws the current player's rupee count in the bottom-right corner of the screen
     * Called by drawHUD()
     */
    private void drawRupeeCount() {

        // Draw rupee image
        int x = gp.tileSize * 15;
        int y = gp.tileSize * 11 + 20;
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

        for (Entity enemy : gp.enemies) {

            if (enemy != null && enemy.isAvailable()) {

                // Enemy distance from player
                int enemyDistance = enemy.getAI().getTileDistance(gp.player);

                // Find closest enemy distance within 8 tiles
                if (enemyDistance < currentDistance) {
                    currentDistance = enemyDistance;
                    newTarget = enemy;
                }
            }
        }

        return newTarget;
    }
    private void drawZTargetArrow(Entity newTarget) {

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

        Point screen = new Point();
        gp.camera.worldToScreen(newTarget.getWorldPoint(), screen);

        int x = screen.x - 10;
        int y = screen.y - 30 + zTargetCounter;

        g2.drawImage(ztarget_arrow, x, y, null);
    }
    private void drawZTargetCircle(Entity target) {

        zTargetRotation += 3;
        if (zTargetRotation >= 180) {
            zTargetRotation = 0;
        }

        Point screen = new Point();
        gp.camera.worldToScreen(target.getWorldPoint(), screen);

        zTargetRotation += 3;
        if (zTargetRotation >= 180) {
            zTargetRotation = 0;
        }

        BufferedImage img = rotateImage(ztarget_circle, zTargetRotation);

        g2.drawImage(img, screen.x - 10, screen.y - 10, null);
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
        drawCoordinates();
    }
    private void drawCoordinates() {

        int x = 10;
        int y = gp.tileSize * 6;
        int lineHeight = 20;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));

        g2.drawString("World X: " + gp.camera.getWorldPoint().x, x, y);
        y += lineHeight;
        g2.drawString("World Y: " + gp.camera.getWorldPoint().y, x, y);
        y += lineHeight;
        g2.drawString("Column: " + gp.camera.getWorldPoint().x / gp.tileSize, x, y);
        y += lineHeight;
        g2.drawString("Row: " + gp.camera.getWorldPoint().y / gp.tileSize, x, y);
    }

    /** EDITING */
    private void drawEditState() {

        drawDebug();

        // User holding down Y
        if (gp.keyH.yPressed) {
            drawEditing_Menu();
        }
        // User let go of Y when editing entities, run once
        else if (wasYPressed && !editingTiles) {
            editing_GetEntity();
        }
        else {
            drawEditing_Map();
            drawCursor();
        }

        // Detect if Y is pressed
        wasYPressed = gp.keyH.yPressed;

        // Switch tile editing on/off
        if (gp.keyH.xPressed) {
            gp.keyH.xPressed = false;

            editingTiles = !editingTiles;

            tileIndex = 0;
            entityListIndex = 0;
            entityIndex = 0;
        }
    }

    private void drawCursor() {

        Point screenPoint = new Point();
        gp.camera.worldToScreen(cursor.getWorldPoint(), screenPoint);

        // Entity currently selected, draw sprite under cursor
        if (!editingTiles && selectedEntity != null) {
            g2.drawImage(selectedEntity.getSprite(), screenPoint.x, screenPoint.y, gp.tileSize, gp.tileSize, null);
            g2.drawImage(cursor.getSelect(), screenPoint.x - 6, screenPoint.y - 6, gp.tileSize + 13, gp.tileSize + 13,null);
        }
        else {
            if (editingTiles) {
                UITile uiTile = tileLibrary.get(tileIndex);
                drawCurrentSprite(screenPoint, 0.9f, uiTile.sprite());
            }
            else {
                UIEntity uiEntity = entityLibrary.get(entityListIndex).get(entityIndex);
                drawCurrentSprite(screenPoint, 0.4f, uiEntity.getSprite());
            }

            g2.drawImage(cursor.getCursor(), screenPoint.x, screenPoint.y, gp.tileSize, gp.tileSize,null);
        }
    }

    private void drawCurrentSprite(Point screenPoint, float alpha, BufferedImage sprite) {

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.drawImage(sprite, screenPoint.x + 7, screenPoint.y + 7, gp.tileSize - 14, gp.tileSize - 14, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawEditing_Menu() {

        if (editingTiles) {
            editing_Tile_Menu();
            editing_Tile_Menu_Input_Dir();
        }
        else {
            editing_Entity_Menu();
            editing_Entity_Menu_Input_Dir();
        }
    }

    private void editing_Tile_Menu() {

        int baseX = (int) (gp.screenWidth / 2.25);
        int baseY = (int) (gp.screenHeight / 2.75);
        int width = gp.tileSize * 2;
        int height = gp.tileSize * 2;
        g2.setColor(new Color(0, 0, 0, 235));
        g2.fillRoundRect(baseX, baseY, width, height, 0, 0);

        int offset = 25;

        int cursorX = baseX + offset;
        int cursorY = baseY + offset;
        int spacingY = (int) (gp.tileSize * 1.75);
        int x = baseX + offset;
        int y = cursorY - tileIndex * spacingY;

        for (int i = 0; i < tileLibrary.size(); i++) {

            if (Math.abs(i - tileIndex) > 2) {
                y += spacingY;
                continue;
            }

            if (i == tileIndex) {
                g2.drawImage(cursor.getCursor(),cursorX - 10, cursorY - 10,gp.tileSize + 20, gp.tileSize + 20, null);
            }
            else {
                g2.setColor(new Color(28, 28, 28, 200));
                g2.fillRoundRect(x - 10, y - 10, gp.tileSize + 20, gp.tileSize + 20, 0, 0);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
            }

            g2.drawImage(tileLibrary.get(i).sprite(), x, y,gp.tileSize, gp.tileSize, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            y += spacingY;
        }
    }
    private void editing_Tile_Menu_Input_Dir() {
        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;

            tileIndex--;
            if (tileIndex < 0) {
                tileIndex = tileLibrary.size() - 1;
            }
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;

            tileIndex++;
            if (tileIndex > tileLibrary.size() - 1) {
                tileIndex = 0;
            }
        }
    }

    private void editing_Entity_Menu() {

        int baseX = (int) (gp.tileSize * 5.5);
        int baseY = gp.tileSize * 5;
        int width = (int) (gp.tileSize * 6.5);
        int height = gp.tileSize * 2;
        g2.setColor(new Color(0, 0, 0, 235));
        g2.fillRoundRect(baseX, baseY, width, height, 0, 0);

        int listSpacingX = (int) (gp.tileSize * 1.50);
        int entitySpacingY = (int) (gp.tileSize * 1.75);

        int cursorX = baseX + (entityListIndex * listSpacingX + 25);
        int cursorY = (gp.screenHeight / 2) - gp.tileSize;

        int scrollOffsetY = cursorY - (entityIndex * entitySpacingY);

        for (int i = 0; i < entityLibrary.size(); i++) {

            int x = baseX + (i * listSpacingX + 25);
            int y = (i == entityListIndex) ? scrollOffsetY : cursorY;

            for (int c = 0; c < entityLibrary.get(i).size(); c++) {

                if (i == entityListIndex) {
                    if (Math.abs(c - entityIndex) > 2) {
                        y += entitySpacingY;
                        continue;
                    }
                }
                else if (c != 0) {
                    continue;
                }

                if (i == entityListIndex && c == entityIndex) {
                    g2.drawImage(cursor.getCursor(),cursorX - 10, cursorY - 10,gp.tileSize + 20, gp.tileSize + 20,null);
                }

                if (i == entityListIndex && c != entityIndex) {
                    g2.setColor(new Color(28, 28, 28, 200));
                    g2.fillRoundRect(x - 10, y - 10,gp.tileSize + 20, gp.tileSize + 20,0, 0);
                }

                if (i != entityListIndex || c != entityIndex) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                }
                g2.drawImage(entityLibrary.get(i).get(c).getSprite(), x, y, gp.tileSize, gp.tileSize,null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                y += entitySpacingY;
            }
        }
    }
    private void editing_Entity_Menu_Input_Dir() {
        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;

            entityIndex--;
            if (entityIndex < 0) {
                entityIndex = entityLibrary.get(entityListIndex).size() - 1;
            }
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;

            entityIndex++;
            if (entityIndex > entityLibrary.get(entityListIndex).size() - 1) {
                entityIndex = 0;
            }
        }
        else if (gp.keyH.leftPressed) {
            gp.keyH.leftPressed = false;

            entityListIndex--;
            entityIndex = 0;
            if (entityListIndex < 0) {
                entityListIndex = entityLibrary.size() - 1;
            }
        }
        else if (gp.keyH.rightPressed) {
            gp.keyH.rightPressed = false;

            entityListIndex++;
            entityIndex = 0;
            if (entityListIndex > entityLibrary.size() - 1) {
                entityListIndex = 0;
            }
        }
    }

    private void drawEditing_Map() {

        if (editingTiles) {
            editing_Map_Tile_Input_A();
            editing_Map_Tile_Input_B();
        }
        else {
            editing_Map_Entity_Input_A();
            editing_Map_Entity_Input_B();
            editing_Map_Entity_Input_LR();
        }

        editing_Map_Input_Dir();
    }

    private void editing_Map_Tile_Input_A() {
        if (gp.keyH.aPressed) {
            gp.keyH.aPressed = false;

            editing_PlaceTile(tileIndex);
        }
    }
    private void editing_Map_Tile_Input_B() {
        if (gp.keyH.bPressed) {
            gp.keyH.bPressed = false;

            editing_PlaceTile(0);
        }
    }
    private void editing_PlaceTile(int tileNum) {

        int col = cursor.getWorldX() / gp.tileSize;
        int row = cursor.getWorldY() / gp.tileSize;

        gp.tileM.mapTileNum[col][row] = tileNum;
    }

    private void editing_Map_Entity_Input_A() {

        if (!gp.keyH.aPressed) return;

        gp.keyH.aPressed = false;

        editing_GetEntity();

        // Find entity on map where cursor is
        Entity mapEntity = editing_GetEntityAtTile();

        // Find cursor entity
        editing_GetEntity();

        // Hovering over existing entity on map
        if (mapEntity != null) {
            editing_HandleMapEntityAPress(mapEntity);
        }
        // Currently holding an entity over an empty spot
        else if (selectedEntity != null) {
            editing_HandleEntityAPress(selectedEntity);
        }
        // Not currently holding an entity over an empty spot
        else {
            editing_HandleEntityAPress(currentEntity);
        }
    }

    private void editing_HandleMapEntityAPress(Entity mapEntity) {

        // Attempt to give loot
        if (editing_GiveLoot(mapEntity)) {
            selectedEntity = null;
            return;
        }

        // Grab map entity if not currently holding an entity
        if (selectedEntity == null) {
            selectedEntity = mapEntity;

            // Move player offscreen when selected
            if (mapEntity == gp.player) {
                gp.player.setWorldPoint(new Point(-48, -48));
            }
            else {
                gp.removeEntity(mapEntity);
            }
        }
    }
    private void editing_HandleEntityAPress(Entity entity) {

        // Cannot place on current tile
        if (cannotPlaceEntity(entity)) return;

        // Place on map
        editing_PlaceEntity(entity);
        selectedEntity = null;
    }

    private Entity editing_GetEntityAtTile() {

        int cursorCol = cursor.getWorldX() / gp.tileSize;
        int cursorRow = cursor.getWorldY() / gp.tileSize;

        // If player is selected
        if (gp.player.getCol() == cursorCol && gp.player.getRow() == cursorRow) {
            return gp.player;
        }

        for (ArrayList<? extends Entity> list : gp.entities) {

            for (Entity entity : list) {
                if (entity == null) continue;

                if (entity.getCol() == cursorCol && entity.getRow() == cursorRow) {
                    return entity;
                }
            }
        }

        return null;
    }

    private boolean editing_GiveLoot(Entity entity) {

        // Not valid
        if (entity == null || !entity.canTakeLoot() || entity == gp.player) return false;

        if (selectedEntity != null && selectedEntity instanceof Collectable) {
            entity.setLoot(selectedEntity.getName());
            return true;
        }
        else if (currentEntity != null && currentEntity instanceof Collectable) {
            entity.setLoot(currentEntity.getName());
            return true;
        }

        return false;
    }

    private void editing_GetEntity() {

        UIEntity uiEntity = entityLibrary.get(entityListIndex).get(entityIndex);

        currentEntity = gp.eGenerator.getEntity(uiEntity.getName());
        if (currentEntity == null) return;

        currentEntity.setWorldPoint(cursor.getWorldPoint());
    }
    private boolean cannotPlaceEntity(Entity entity) {

        if (entity == null) return true;

        int col = cursor.getWorldPoint().x / gp.tileSize;
        int row = cursor.getWorldPoint().y / gp.tileSize;
        int tileNum = gp.tileM.mapTileNum[col][row];

        Tile tile = gp.tileM.tiles[tileNum];

        return tile.isNotTraversable(entity, tileNum);
    }
    private void editing_PlaceEntity(Entity entity) {

        if (entity == gp.player) {
            gp.player.setWorldPoint(cursor.getWorldPoint());
            return;
        }

        entity.setWorldPoint(cursor.getWorldPoint());
        gp.addEntity(entity);
    }

    private void editing_Map_Entity_Input_B() {

        if (!gp.keyH.bPressed) return;

        gp.keyH.bPressed = false;

        editing_RemoveEntity();
    }
    private void editing_RemoveEntity() {

        int cursorCol = cursor.getWorldX() / gp.tileSize;
        int cursorRow = cursor.getWorldY() / gp.tileSize;

        // Can't delete player
        if (gp.player.getCol() == cursorCol && gp.player.getRow() == cursorRow) {
            return;
        }

        // Find entity at X/Y
        for (ArrayList<? extends Entity> list : gp.entities) {

            Iterator<? extends Entity> it = list.iterator();

            while (it.hasNext()) {

                Entity entity = it.next();

                // Entity found, delete from list
                if (entity.getCol() == cursorCol && entity.getRow() == cursorRow) {
                    it.remove();
                    return;
                }
            }
        }
    }

    private void editing_Map_Entity_Input_LR() {
        if (!gp.keyH.lPressed && !gp.keyH.rPressed) return;
        gp.keyH.lPressed = false;
        gp.keyH.rPressed = false;

        editing_RotateEntity();
    }
    private void editing_RotateEntity() {

        int cursorCol = cursor.getWorldX() / gp.tileSize;
        int cursorRow = cursor.getWorldY() / gp.tileSize;

        // If player is selected
        if (gp.player.getCol() == cursorCol && gp.player.getRow() == cursorRow) {
            gp.player.rotate();
            return;
        }

        for (ArrayList<? extends Entity> list : gp.entities) {

            for (Entity entity : list) {
                if (entity.getCol() == cursorCol && entity.getRow() == cursorRow) {
                    entity.rotate();
                    return;
                }
            }
        }
    }

    private void editing_Map_Input_Dir() {

        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;
            cursor.moveUp();
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;
            cursor.moveDown();
        }
        else if (gp.keyH.leftPressed) {
            gp.keyH.leftPressed = false;
            cursor.moveLeft();
        }
        else if (gp.keyH.rightPressed) {
            gp.keyH.rightPressed = false;
            cursor.moveRight();
        }
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
