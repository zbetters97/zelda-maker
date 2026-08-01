package UI;

import application.GamePanel;
import application.UtilityTool;
import entity.Entity;
import entity.collectable.Collectable;
import entity.item.ITM_Bomb;
import entity.item.ITM_Bow;
import entity.item.Item;
import tile.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

public class UI {

    /** CONFIG */
    private final GamePanel gp;
    private Graphics2D g2;
    private Font PK_DS;

    /** PAUSE COLORS */
    private final Color pause_brown_1 = new Color(62, 42, 19, 255);
    private final Color pause_brown_2 = new Color(62, 42, 19, 205);
    private final Color pause_text =  new Color(239, 231, 207, 255);

    /** PAUSE HANDLERS */
    private int subState = 0;
    private int commandNum = 0;
    private Map<String, String> usersList = new HashMap<>();
    private boolean viewingUserLevels;

    private final ArrayList<String> menuOptions = new ArrayList<>(Arrays.asList(
            "Play", "New", "Browse", "Login", "Settings"
    ));
    private final ArrayList<String> menuAuthOptions = new ArrayList<>(Arrays.asList(
            "Play", "New", "Load", "Save", "Delete", "Upload", "Browse", "Logout", "Settings"
    ));

    private final ArrayList<String> controls = new ArrayList<>(Arrays.asList(
            "Action", "Attack", "Use Item", "Cycle Items", "Z-Target", "Shield"
    ));
    private int controlToEdit = -1;
    private int buffer;

    /** SAVE/LOAD HANDLERS */
    private final Map<Integer, String> keyboard = new LinkedHashMap<>();
    private boolean capital = true;
    private String textInput = "";
    private boolean isSaving;
    private boolean isLoading;

    /** EDITING HANDLERS */
    public Cursor cursor;
    private boolean wasYPressed ;
    private boolean editingTiles = true;

    /** ENTITY EDITING */
    private final ArrayList<ArrayList<UIEntity>> entityLibrary = new ArrayList<>();
    private int entityListIndex = 0;
    private int entityIndex = 0;

    /** TILE EDITING */
    private final ArrayList<ArrayList<UIEntity>> tileLibrary = new ArrayList<>();
    private static final int[][] TILE_GROUPS = {
            {4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 86, 87, 88, 89}, // Land 1
            {0, 1, 2, 3, 9, 38, 21}, // Hazard
            {90, 91, 92, 93, 94, 95, 96, 97}, // House 1
            {20, 78, 19, 64, 65, 74, 75, 76, 77, 79, 80, }, // Inside 1
            {66, 67, 68, 69, 70, 71, 72, 73, 85, 81, 82, 83, 84}, // Inside 2
            {22, 23, 24, 37, 25, 36, 35, 26, 27, 28, 29, 30, 31, 32, 33, 34}, // Dungeon 1
            {39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63} // Dungeon 2
    };
    public Point selectedTile;
    private final Map<Point, Integer> copiedTiles = new HashMap<>();

    /** ITEM COLORS */
    private final Color itm_brown_1 = new Color(168, 127, 89);
    private final Color itm_brown_2 = new Color(247, 219, 167);
    private final Color itm_green = new Color(95, 190, 80);

    /** HUD SPRITES */
    private BufferedImage
            heart_0, heart_1, heart_2, heart_3, heart_4,
            rupee, key, bossKey,
            zTarget_arrow, zTarget_circle;

    /** RUPEE HANDLERS */
    private int rupeeChange;
    private int rupeeCounter = 0;

    /** Z-TARGETING */
    private int zTargetCounter = 0;
    private int zTargetDirection = 0;
    private int zTargetRotation = 0;

    /** DIALOGUE VALUES */
    private String dialogue = "";
    private String currentDialogue = "";
    private int dialogueCounter = 0;
    private int charIndex = 0;
    private String combinedText = "";
    private boolean canSkip;
    private Entity dialogueReward;

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
        zTarget_arrow = setupImage("/ui/ui_ztarget_arrow", 48 + 20, 48 + 20);
        zTarget_circle = setupImage("/ui/ui_ztarget_circle", 48 + 20, 48 + 20);
    }
    private void getHUDImages() {

        heart_0 = setupImage("/ui/ui_heart_0", 23, 23);
        heart_1 = setupImage("/ui/ui_heart_1", 23, 23);
        heart_2 = setupImage("/ui/ui_heart_2", 23, 23);
        heart_3 = setupImage("/ui/ui_heart_3", 23, 23);
        heart_4 = setupImage("/ui/ui_heart_4", 23, 23);

        rupee = setupImage("/ui/ui_rupee");
        key = setupImage("/ui/ui_key");
        bossKey = setupImage("/ui/ui_key_boss");
    }

    private void fillTileLibrary() {

        for (int[] group : TILE_GROUPS) {

            ArrayList<UIEntity> category = new ArrayList<>();

            for (int tileNum : group) {
                category.add(new UIEntity(tileNum));
            }

            tileLibrary.add(category);
        }
    }

    private void fillEntityLibrary() {
        entityLibrary.addAll(Arrays.asList(
                buildFromFactory("npc", gp.eGenerator.npcFactory),
                buildFromFactory("enemy", gp.eGenerator.enemyFactory),
                buildFromFactory("object", gp.eGenerator.objectFactory),
                buildFromFactory("collectable", gp.eGenerator.collectableFactory),
                buildFromFactory("item", gp.eGenerator.itemFactory)
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
     * @param g2 Graphics2D engine
     */
    public void draw(Graphics2D g2) {

        this.g2 = g2;

        g2.setFont(PK_DS);
        g2.setColor(Color.white);

        if (gp.GAME_STATE == gp.PAUSE_STATE) {
            drawPauseState();
        }
        else if (gp.GAME_STATE == gp.EDIT_STATE) {
            drawEditState();
        }
        else if (gp.GAME_STATE == gp.PLAY_STATE) {
            drawPlayState();
        }
        else if (gp.GAME_STATE == gp.DIALOGUE_STATE) {
            drawDialogueState();
        }
    }

    /** PAUSED */
    private void drawPauseState() {

        if (subState == 0) {
            if (gp.dbNotConnected() || !gp.auth.isLoggedIn()) {
                drawPause_Menu();
            }
            else {
                drawPause_Menu_Auth();
            }

            pauseMenu_Input_Back();
        }
        else if (subState == 1) {
            drawPause_Users();
        }
        else if (subState == 2) {
            drawPause_Levels();
        }
        else if (subState == 3) {
            drawPause_Auth();
        }
        else if (subState == 4) {
            drawPause_Name();
        }
        else if (subState == 5) {
            drawPause_Settings();
        }
        else if (subState == 6) {
            drawPause_Controls();
        }
    }

    private void drawPause_Menu() {

        int x = gp.tileSize;
        int y = gp.tileSize;
        int width = gp.tileSize * 4;
        int height = (int) (gp.tileSize * 5.75);
        drawPauseWindow(x, y, width, height);

        g2.setColor(pause_text);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        x = gp.tileSize * 2;
        y = gp.tileSize * 2;
        int index = 0;

        for (String option : menuOptions) {

            g2.drawString(option, x, y);
            if (commandNum == index) {
                g2.drawString(">", x - 25, y);
            }

            y += gp.tileSize;
            index++;
        }

        pauseMenu_Input_A();
        pauseMenu_Input_Dir();
    }
    private void pauseMenu_Input_A() {
        if (!gp.keyH.uiAPressed) return;
        gp.keyH.uiAPressed = false;

        // PLAY
        if (commandNum == 0) {
            gp.saveLoad.saveSnapshot("temp");
            gp.GAME_STATE = gp.PLAY_STATE;
            subState = 0;
        }
        // NEW
        else if (commandNum == 1) {
            subState = 0;
        }
        // BROWSE
        else if (commandNum == 2) {
            usersList = gp.db.getAllUsers();
            if (usersList == null || usersList.isEmpty()) {
                playMenuError();
                return;
            }

            subState = 1;
        }
        // LOGIN
        else if (commandNum == 3) {
            subState = 3;
        }
        // SETTINGS
        else if (commandNum == 4) {
            subState = 5;
        }

        if (0 < commandNum && commandNum < 5) {
            playMenuSelect();
        }

        commandNum = 0;
    }
    private void pauseMenu_Input_Dir() {
        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;

            if (0 < commandNum) {
                commandNum--;
                playMenuCursor();
            }
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;

            if (commandNum < 4) {
                commandNum++;
                playMenuCursor();
            }
        }
    }

    private void drawPause_Auth() {

        int width = (int) (gp.tileSize * 11.5);
        int height = gp.tileSize * 3;
        int x = (gp.screenWidth - width) / 2;
        int y = (int) (gp.tileSize * 4.5);
        drawPauseWindow(x, y, width, height);

        g2.setColor(pause_text);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        String text = "Please login from your browser to continue...";
        x = getXForCenteredTextOnWidth(text, width, x);
        y += (int) (gp.tileSize * 1.5);
        g2.drawString(text, x, y);

        // Run one frame to freeze window into place
        if (commandNum == 1) gp.changeLogin();
        commandNum++;

        // User logged in or timed out, return to screen
        if (commandNum == 2) {
            commandNum = 0;
            subState = 0;
        }
    }

    private void drawPause_Menu_Auth() {

        int x = gp.tileSize;
        int y = gp.tileSize;
        int width = gp.tileSize * 4;
        int height = (int) (gp.tileSize * 9.75);
        drawPauseWindow(x, y, width, height);

        g2.setColor(pause_text);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        x = gp.tileSize * 2;
        y = gp.tileSize * 2;
        int index = 0;

        for (String option : menuAuthOptions) {

            g2.drawString(option, x, y);
            if (commandNum == index) {
                g2.drawString(">", x - 25, y);
            }

            y += gp.tileSize;
            index++;
        }

        pauseMenu_Auth_Input_A();
        pauseMenu_Auth_Input_Dir();
    }
    private void pauseMenu_Auth_Input_A() {
        if (!gp.keyH.uiAPressed) return;
        gp.keyH.uiAPressed = false;

        // PLAY
        if (commandNum == 0) {
            gp.saveLoad.saveSnapshot("temp");
            gp.GAME_STATE = gp.PLAY_STATE;
            subState = 0;
        }
        // NEW
        else  if (commandNum == 1) {
            subState = 0;
        }
        // LOAD
        else if (commandNum == 2) {
            gp.saveFiles = gp.db.getUserWorlds(gp.auth.getUserId());
            if (gp.saveFiles == null || gp.saveFiles.isEmpty()) {
                playMenuError();
                return;
            }

            isSaving = false; isLoading = true;
            subState = 2;
        }
        // SAVE
        else if (commandNum == 3) {
            gp.saveFiles = gp.db.getUserWorlds(gp.auth.getUserId());

            isSaving = true; isLoading = false;
            subState = 2;
        }
        // DELETE
        else if (commandNum == 4) {
            gp.saveFiles = gp.db.getUserWorlds(gp.auth.getUserId());
            if (gp.saveFiles == null || gp.saveFiles.isEmpty()) {
                playMenuError();
                return;
            }

            isSaving = false; isLoading = false;
            subState = 2;
        }
        // UPLOAD
        else if (commandNum == 5) {
            subState = 4;
        }
        // BROWSE
        else if (commandNum == 6) {
            usersList = gp.db.getAllUsers();
            if (usersList == null || usersList.isEmpty()) {
                playMenuError();
                return;
            }

            subState = 1;
        }
        // LOGOUT
        else if (commandNum == 7) {
            gp.changeLogin();
            subState = 0;
        }
        // SETTINGS
        else if (commandNum == 8) {
            subState = 5;
        }

        if (0 < commandNum && commandNum < 9) {
            playMenuSelect();
        }

        commandNum = 0;
    }
    private void pauseMenu_Auth_Input_Dir() {
        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;

            if (0 < commandNum) {
                commandNum--;
                playMenuCursor();
            }
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;

            if (commandNum < 8) {
                commandNum++;
                playMenuCursor();
            }
        }
    }

    private void pauseMenu_Input_Back() {
        if (!gp.keyH.startPressed && !gp.keyH.uiBPressed) return;
        gp.keyH.startPressed = false;
        gp.keyH.uiBPressed = false;

        commandNum = 0;
        subState = 0;

        gp.GAME_STATE = gp.EDIT_STATE;
        playMenuClose();
    }

    private void drawPause_Users() {
        if (usersList == null || usersList.isEmpty()) return;

        int x = gp.tileSize * 3;
        int y = gp.tileSize * 2;
        int width = gp.tileSize * 11;
        int height = (int) ((gp.tileSize * .95) * (usersList.size() + 1));
        drawPauseWindow(x, y, width, height);

        g2.setColor(pause_text);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        drawPause_UsersList();

        pauseUsers_Input_B();
        pauseUsers_Input_Dir();
    }
    private void drawPause_UsersList() {

        String text;
        int index = 0;

        int x = gp.tileSize * 4;
        int y = gp.tileSize * 3;

        for (Map.Entry<String, String> entry : usersList.entrySet()) {

            text = index + 1 + ")  " + entry.getValue();
            g2.drawString(text, x, y);

            if (commandNum == index) {
                g2.drawString(">", x - 25, y);
                boolean uiAPressed = pauseUsers_Input_A(entry.getKey());
                if (uiAPressed) break;
            }

            index++;
            y += gp.tileSize;
        }
    }
    private boolean pauseUsers_Input_A(String userId) {
        if (!gp.keyH.uiAPressed) return false;
        gp.keyH.uiAPressed = false;

        gp.saveFiles = gp.db.getUserWorlds(userId);

        viewingUserLevels = true;
        isSaving = false; isLoading = true;
        commandNum = 0;
        subState = 2;
        playMenuSelect();

        return true;
    }
    private void pauseUsers_Input_B() {
        if (!gp.keyH.uiBPressed) return;
        gp.keyH.uiBPressed = false;

        commandNum = 0;
        subState = 0;
        playMenuClose();
    }
    private void pauseUsers_Input_Dir() {
        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;

            if (0 < commandNum) {
                commandNum--;
                playMenuCursor();
            }
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;

            if (commandNum < usersList.size() - 1) {
                commandNum++;
                playMenuCursor();
            }
        }
    }

    private void drawPause_Levels() {

        // Trying to load/delete a file from a blank list
        if (gp.saveFiles.isEmpty() && !isSaving) return;

        int x = gp.tileSize * 3;
        int y = gp.tileSize * 2;
        int width = gp.tileSize * 11;
        int offset = isSaving ? 2 : 1;
        int height = (int) ((gp.tileSize * 0.95) * (gp.saveFiles.size() + offset));
        drawPauseWindow(x, y, width, height);

        g2.setColor(pause_text);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        x += gp.tileSize;
        y += gp.tileSize;
        int index = 0;

        if (isSaving) {
            drawPause_SaveLevel(x, y);
            index++;
            y += gp.tileSize;
        }

        drawPause_ListLevels(x, y, index);

        pauseLevel_Input_B();
        pauseLevel_Input_Dir();
    }
    private void drawPause_SaveLevel(int x, int y) {

        String text = "1)  NEW";
        g2.drawString(text, x, y);

        if (commandNum == 0) {
            g2.drawString(">", x - 25, y);

            if (gp.keyH.uiAPressed) {
                gp.keyH.uiAPressed = false;
                commandNum = 0;
                subState = 4;
            }
        }
    }
    private void drawPause_ListLevels(int x, int y, int index) {

        String text;
        for (Map.Entry<String, String> entry : gp.saveFiles.entrySet()) {

            text = index + 1 + ")  " + entry.getValue();
            g2.drawString(text, x, y);

            if (commandNum == index) {
                g2.drawString(">", x - 25, y);
                boolean uiAPressed = pauseLevel_Input_A(entry.getKey(), entry.getValue());
                if (uiAPressed) break;
            }

            index++;
            y += gp.tileSize;
        }
    }
    private boolean pauseLevel_Input_A(String fileName, String levelName) {
        if (!gp.keyH.uiAPressed) return false;
        gp.keyH.uiAPressed = false;

        if (isSaving) {
            // Chop off date from level name
            String lvlName = levelName.contains(" [") ?
                    levelName.substring(0, levelName.indexOf(" [")) :
                    levelName;

            gp.saveLoad.save(lvlName, fileName);
        }
        else if (isLoading) {
            gp.resetGame();

            gp.saveLoad.load(fileName);

            cursor.setWorldPoint(gp.player.getWorldPoint());
            gp.camera.follow(cursor.getWorldPoint());
        }
        else {
            gp.saveLoad.delete(fileName);
        }

        gp.saveFiles.clear();
        viewingUserLevels = false;
        commandNum = 0;
        subState = 0;
        playMenuSelect();

        return true;
    }
    private void pauseLevel_Input_B() {
        if (!gp.keyH.uiBPressed) return;
        gp.keyH.uiBPressed = false;

        commandNum = 0;
        subState = 0;
        playMenuClose();

        if (viewingUserLevels) {
            viewingUserLevels = false;
            subState = 1;
        }

        gp.saveFiles.clear();
    }
    private void pauseLevel_Input_Dir() {
        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;

            if (0 < commandNum) {
                commandNum--;
                playMenuCursor();
            }
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;

            int maxSize = isSaving ? gp.saveFiles.size() : gp.saveFiles.size() - 1;
            if (commandNum < maxSize) {
                commandNum++;
                playMenuCursor();
            }
        }
    }

    private void drawPause_Name() {

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 40F));

        drawPause_Keyboard();
    }
    private void drawPause_Keyboard() {

        String keyboardLetters = (capital) ? "QWERTYUIOPASDFGHJKLZXCVBNM_" : "qwertyuiopasdfghjklzxcvbnm_";

        int defaultX = gp.tileSize * 2;
        int x = defaultX;
        int y = (int) (gp.tileSize * 1.5);
        int width = gp.tileSize * 13;
        int height = gp.tileSize * 9;
        drawSubWindow(x, y, width, height);

        x = getXForCenteredTextOnWidth("Please name your level", width, x);
        y += (int) (gp.tileSize * 1.25);
        g2.drawString("Please name your level", x, y);

        String text = textInput.length() <= 21 ?
                "-> " + textInput + "_" :
                "-> " + textInput;
        defaultX += gp.tileSize;
        x = defaultX;
        y += (int) (gp.tileSize * 1.5);
        g2.drawString(text, x, y);

        y += gp.tileSize * 2;
        int index = 0;
        for (char key : keyboardLetters.toCharArray()) {
            if (key == 'A' || key == 'a' || key == 'Z' || key == 'z') {
                x = defaultX;
                y += gp.tileSize;
            }

            text = commandNum == index ?  "[" + key + "]" : " " + key + " ";
            g2.drawString(text, x, y);

            x += gp.tileSize;
            index++;
        }

        text = commandNum == keyboardLetters.length() ? "[DEL]" : " DEL ";
        g2.drawString(text, x, y);

        x += (int) (gp.tileSize * 1.75);
        text = commandNum == keyboardLetters.length() + 1 ? "[CAP]" : " CAP ";
        g2.drawString(text, x, y);

        x = defaultX + gp.tileSize * 2;
        y += (int) (gp.tileSize * 1.5);
        g2.drawString("GO BACK", x, y);
        if (commandNum == keyboardLetters.length() + 2) {
            g2.drawString(">", x - gp.tileSize / 2, y);
        }

        x += gp.tileSize * 5;
        g2.drawString("SUBMIT", x, y);
        if (commandNum == keyboardLetters.length() + 3) {
            g2.drawString(">", x - gp.tileSize / 2, y);
        }

        for (int i = 0; i < keyboardLetters.length(); i++) {
            keyboard.put(i, String.valueOf(keyboardLetters.charAt(i)));
        }

        pauseKeyboard_Input_A(keyboardLetters);
        pauseKeyboard_Input_B();
        pauseKeyboard_Input_Dir(keyboardLetters);
    }
    private void pauseKeyboard_Input_A(String keyboardLetters) {
        if (!gp.keyH.uiAPressed) return;
        gp.keyH.uiAPressed = false;

        int MAX_WORLD_NAME = 20;

        // LETTER SELECT
        if (commandNum < keyboardLetters.length()) {
            if (textInput.length() > MAX_WORLD_NAME) {
                playMenuError();
                return;
            }

            // SPACE BUTTON
            if (commandNum == keyboardLetters.length() - 1) {
                textInput += " ";
                playMenuSelect();
            }
            // LETTER
            else {
                // Get char in map via corresponding key (EX: 0 -> Q, 10 -> A)
                textInput += keyboard.get(commandNum);
                playMenuSelect();
            }
        }
        // DEL BUTTON
        else if (commandNum == keyboardLetters.length()) {
            if (textInput.isEmpty()) {
                playMenuError();
                return;
            }

            textInput = textInput.substring(0, textInput.length() - 1);
            playMenuSelect();
        }
        // CAPS BUTTON
        else if (commandNum == keyboardLetters.length() + 1) {
            capital = !capital;
            playMenuSelect();
        }
        // BACK BUTTON
        else if (commandNum == keyboardLetters.length() + 2) {
            textInput = "";
            capital = true;
            commandNum = 0;
            subState = 0;
            playMenuClose();
        }
        // SUBMIT BUTTON
        else if (commandNum == keyboardLetters.length() + 3) {
            if (textInput.length() < 3 || textInput.length() > MAX_WORLD_NAME) {
                playMenuError();
                return;
            }

            gp.saveLoad.save(textInput, "");
            commandNum = 0;
            subState = 0;

            textInput = "";
            capital = true;
            playMenuSelect();
        }
    }
    private void pauseKeyboard_Input_B() {
        if (!gp.keyH.uiBPressed || textInput.isEmpty()) return;
        gp.keyH.uiBPressed = false;

        textInput = textInput.substring(0, textInput.length() - 1);
        playMenuSelect();
    }
    private void pauseKeyboard_Input_Dir(String keyboardLetters) {

        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;

            if (10 <= commandNum && commandNum <= 18) {
                commandNum -= 10;
                playMenuSelect();
            }
            else if (19 <= commandNum && commandNum <= 25) {
                commandNum -= 9;
                playMenuSelect();
            }
            else if (commandNum == 26) {
                commandNum = 17;
                playMenuSelect();
            }
            else if (commandNum == 27) {
                commandNum = 18;
                playMenuSelect();
            }
            else if (28 <= commandNum) {
                commandNum = 19;
                playMenuSelect();
            }
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;

            if (0 <= commandNum && commandNum <= 8) {
                commandNum += 10;
                playMenuSelect();
            }
            else if (9 <= commandNum && commandNum <= 17) {
                commandNum += 9;
                playMenuSelect();
            }
            else if (commandNum == 18) {
                commandNum += 9;
                playMenuSelect();
            }
            else if (19 <= commandNum && commandNum <= keyboardLetters.length()) {
                commandNum = keyboardLetters.length() + 2;
                playMenuSelect();
            }
            else if (commandNum < keyboardLetters.length() + 2) {
                commandNum = keyboardLetters.length() + 2;
                playMenuSelect();
            }
        }
        else if (gp.keyH.leftPressed) {
            gp.keyH.leftPressed = false;

            if (0 < commandNum) {
                commandNum--;
                playMenuSelect();
            }
        }
        else if (gp.keyH.rightPressed) {
            gp.keyH.rightPressed = false;

            if (commandNum < keyboardLetters.length() + 3) {
                commandNum++;
                playMenuSelect();
            }
        }
    }

    private void drawPause_Settings() {

        int x = gp.tileSize;
        int y = gp.tileSize;
        int width = gp.tileSize * 9;
        int height = gp.tileSize * 7;
        drawPauseWindow(x, y, width, height);

        x += gp.tileSize;
        y += gp.tileSize;
        g2.setColor(pause_text);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        drawPause_Settings_Labels(x, y);

        x += (int) (gp.tileSize * 4.5);
        y -= (int) (gp.tileSize * 0.4);
        drawPause_Settings_Toggles(x, y);

        pauseSettings_Input_A();
        pauseSettings_Input_Back();
        pauseSettings_Input_Dir();
    }
    private void drawPause_Settings_Labels(int x, int y) {

        g2.drawString("Full Screen", x, y);
        if (commandNum == 0) {
            g2.drawString(">", x - 25, y);
        }

        y += gp.tileSize;
        g2.drawString("Music", x, y);
        if (commandNum == 1) {
            g2.drawString(">", x - 25, y);
        }

        // SOUND EFFECTS VOLUME
        y += gp.tileSize;
        g2.drawString("Sound Effects", x, y);
        if (commandNum == 2) {
            g2.drawString(">", x - 25, y);
        }

        // LEVEL SONG
        y += gp.tileSize;
        g2.drawString("Music", x, y);
        if (commandNum == 3) {
            g2.drawString(">", x - 25, y);
        }

        // CONTROLS
        y += gp.tileSize;
        g2.drawString("Controls", x, y);
        if (commandNum == 4) {
            g2.drawString(">", x - 25, y);
        }

        y += (int) (gp.tileSize * 1.5);
        g2.drawString("Back", x, y);
        if (commandNum == 5) {
            g2.drawString(">", x - 25, y);
        }
    }
    private void drawPause_Settings_Toggles(int x, int y) {

        // FULL SCREEN CHECK BOX
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x, y, 24, 24);
        if (gp.fullScreenOn) {
            g2.fillRect(x, y, 24, 24);
        }

        // MUSIC SLIDER
        y += gp.tileSize;
        g2.drawRect(x, y, 120, 24); // 120/5 = 24
        int volumeWidth = 24 * gp.music.volumeScale;
        g2.fillRect(x, y, volumeWidth, 24);

        // SOUND EFFECTS SLIDER
        y += gp.tileSize;
        g2.drawRect(x, y, 120, 24);
        g2.drawRect(x, y, 120, 24); // 120/5 = 24
        volumeWidth = 24 * gp.se.volumeScale;
        g2.fillRect(x, y, volumeWidth, 24);

        // SONG NUMBER
        y += (int) (gp.tileSize * 1.5);
        g2.drawString(gp.se.getSongName(gp.song), x, y);
    }
    private void pauseSettings_Input_A() {
        if (!gp.keyH.uiAPressed) return;
        gp.keyH.uiAPressed = false;

        // Toggle Fullscreen
        if (commandNum == 0) {
            gp.fullScreenOn = !gp.fullScreenOn;
            playMenuSelect();
        }
        // Controls
        else if (commandNum == 4) {
            commandNum = 0;
            subState = 6;
            playMenuSelect();
        }
        // Back
        else if (commandNum == 5) {
            gp.config.saveConfig();
            commandNum = 0;
            subState = 0;
            playMenuClose();
        }
    }
    private void pauseSettings_Input_Back() {
        if (!gp.keyH.uiBPressed && !gp.keyH.startPressed) return;
        gp.keyH.uiBPressed = false;
        gp.keyH.startPressed = false;

        gp.config.saveConfig();
        commandNum = 0;
        subState = 0;
        playMenuClose();
    }
    private void pauseSettings_Input_Dir() {

        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;

            if (0 < commandNum) {
                commandNum--;
                playMenuCursor();
            }
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;

            if (commandNum < 5) {
                commandNum++;
                playMenuCursor();
            }
        }
        else if (gp.keyH.leftPressed) {
            gp.keyH.leftPressed = false;

            if (commandNum == 1 && 0 < gp.music.volumeScale) {
                gp.music.volumeScale--;
                gp.music.checkVolume();
            }
            else if (commandNum == 2 && 0 < gp.se.volumeScale) {
                gp.se.volumeScale--;
                gp.music.checkVolume();
                playMenuCursor();
            }
            else if (commandNum == 3 && 0 < gp.song) {
                gp.song--;
                gp.playMusic(gp.song);
            }
        }
        else if (gp.keyH.rightPressed) {
            gp.keyH.rightPressed = false;

            if (commandNum == 1 && gp.music.volumeScale < 5) {
                gp.music.volumeScale++;
                gp.music.checkVolume();
            }
            else if (commandNum == 2 && gp.se.volumeScale < 5) {
                gp.se.volumeScale++;
                gp.music.checkVolume();
                playMenuCursor();
            }
            else if (commandNum == 3 && gp.song < gp.se.maxSongs) {
                gp.song++;
                gp.playMusic(gp.song);
            }
        }
    }

    private void drawPause_Controls() {

        int x = gp.tileSize;
        int y = gp.tileSize;
        int width = gp.tileSize * 8;
        int height = (int) (gp.tileSize * 10.5);
        drawPauseWindow(x, y, width, height);

        x += gp.tileSize;
        y += gp.tileSize;
        g2.setColor(pause_text);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        drawPause_Controls_Labels(x, y);

        pauseControls_Input_New();

        if (controlToEdit == -1) {
            pauseControls_Input_A();
            pauseControls_Input_B();
            pauseControls_Input_Dir();
        }
    }
    private void drawPause_Controls_Labels(int labelX, int y) {

        int inputX = labelX + (gp.tileSize * 5);

        g2.drawString("COMMAND", labelX, y);
        g2.drawString("INPUT", inputX, y);
        y += (int) (gp.tileSize * 1.25);
        inputX += (int) (gp.tileSize * 0.5);

        int index = 0;
        for (String label : controls) {

            int key = getKeyFromIndex(index);

            g2.drawString(label, labelX, y);
            if (commandNum == index) {
                g2.drawString(">", labelX - 25, y);
            }

            String input = controlToEdit == key ? "[]" : KeyEvent.getKeyText(key);
            g2.drawString(input, inputX, y);

            y += gp.tileSize;
            index++;
        }

        g2.drawString("Restore Defaults", labelX, y);
        if (commandNum == index) {
            g2.drawString(">", labelX - 25, y);
        }

        y += (int) (gp.tileSize * 1.5);
        g2.drawString("Back", labelX, y);
        if (commandNum == index + 1) {
            g2.drawString(">", labelX - 25, y);
        }
    }
    private int getKeyFromIndex(int index) {

        if (index == 0) return gp.keyH.btn_A;
        else if (index == 1) return gp.keyH.btn_B;
        else if (index == 2) return gp.keyH.btn_X;
        else if (index == 3) return gp.keyH.btn_Y;
        else if (index == 4) return gp.keyH.btn_L;
        else if (index == 5) return gp.keyH.btn_R;

        return 0;
    }
    private void pauseControls_Input_New() {
        if (controlToEdit == -1) return;

        // Get user's most recent key press
        int newKey = gp.keyH.getLastKeyPressed();

        // Skip one frame for buffer
        buffer++;
        if (buffer == 1) return;

        // User didn't press a new key
        if (newKey == -1) return;

        // User must input a letter key
        if (newKey < 65 || 90 < newKey) {
            playMenuError();
            return;
        }

        boolean isExistingControl = gp.keyH.isExistingButton(newKey);

        // New key entered is already mapped, swap mappings with new control
        // Must use -1 as placeholder when swapping
        if (isExistingControl) {
            gp.keyH.updateButton(controlToEdit, -1);
            gp.keyH.updateButton(newKey, controlToEdit);
            gp.keyH.updateButton(-1, newKey);
        }
        // Replace selected control with new key
        else {
            gp.keyH.updateButton(controlToEdit, newKey);
        }

        controlToEdit = -1;
        buffer = 0;
        gp.keyH.stopAllKeys();
        playMenuSelect();
    }
    private void pauseControls_Input_A() {
        if (!gp.keyH.uiAPressed) return;
        gp.keyH.uiAPressed = false;

        // Select key to edit
        if (0 <= commandNum && commandNum < controls.size()) {
            controlToEdit = getKeyFromIndex(commandNum);
            playMenuSelect();
        }
        // Restore keys to defaults
        else if (commandNum == controls.size()) {
            gp.keyH.resetDefaults();
            playMenuSelect();
        }
        // Back
        else if (commandNum == controls.size() + 1) {
            commandNum = 0;
            subState = 5;
            gp.config.saveConfig();
            playMenuClose();
        }
    }
    private void pauseControls_Input_B() {
        if (!gp.keyH.uiBPressed) return;
        gp.keyH.uiBPressed = false;

        commandNum = 0;
        subState = 5;
        gp.config.saveConfig();
        playMenuClose();
    }
    private void pauseControls_Input_Dir() {
        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;

            if (0 < commandNum) {
                commandNum--;
                playMenuCursor();
            }
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;

            if (commandNum < controls.size() + 1) {
                commandNum++;
                playMenuCursor();
            }
        }
    }

    /** EDITING */
    private void drawEditState() {

        drawDebug();

        // User holding down Y
        if (gp.keyH.yPressed) {
            drawEditing_Menu();
        }
        // User let go of Y, run once
        else if (wasYPressed) {
            if (!editingTiles) {
                editing_GetEntity();
            }
        }
        else {
            drawEditing_Map();
            drawCursor();
        }

        // Detect if Y is pressed
        wasYPressed = gp.keyH.yPressed;

        // Switch tile editing on/off (prevent when grabbing entity)
        if (gp.keyH.lPressed) {
            gp.keyH.lPressed = false;

            if (cursor.hasSelectedEntity()) {
                playMenuError();
                return;
            }

            editingTiles = !editingTiles;

            entityListIndex = 0;
            entityIndex = 0;
            selectedTile = null;
            copiedTiles.clear();
        }
    }

    private void drawCursor() {

        Point screenPoint = new Point();
        gp.camera.worldToScreen(cursor.getWorldPoint(), screenPoint);

        // Entity currently selected, draw sprite under cursor
        if (!editingTiles && cursor.hasSelectedEntity()) {
            g2.drawImage(cursor.getSelectedEntity().getSprite(), screenPoint.x, screenPoint.y, gp.tileSize, gp.tileSize, null);
            g2.drawImage(cursor.getSelect(), screenPoint.x - 6, screenPoint.y - 6, gp.tileSize + 13, gp.tileSize + 13,null);
        }
        else {
            BufferedImage sprite = editingTiles && gp.keyH.rPressed ?
                    cursor.getSelect() :
                    cursor.getCursor();

            UIEntity uiEntity = editingTiles ?
                    tileLibrary.get(entityListIndex).get(entityIndex) :
                    entityLibrary.get(entityListIndex).get(entityIndex);

            float alpha = editingTiles ? 0.9f : 0.4f;

            drawCurrentSprite(screenPoint, alpha, uiEntity.getSprite());
            g2.drawImage(sprite, screenPoint.x, screenPoint.y, gp.tileSize, gp.tileSize,null);
        }
    }
    private void drawCurrentSprite(Point screenPoint, float alpha, BufferedImage sprite) {

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.drawImage(sprite, screenPoint.x + 7, screenPoint.y + 7, gp.tileSize - 14, gp.tileSize - 14, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawEditing_Menu() {

        if (editingTiles) {
            editing_Entity_Menu(tileLibrary);
            editing_Menu_Input_Dir(tileLibrary);
        }
        else {
            editing_Entity_Menu(entityLibrary);
            editing_Menu_Input_Dir(entityLibrary);
        }
    }
    private void editing_Entity_Menu(ArrayList<ArrayList<UIEntity>> library) {

        int listSpacingX = (int) (gp.tileSize * 1.50);
        int padding = 25;

        int width = (library.size() - 1) * listSpacingX + gp.tileSize + padding * 2;
        int height = gp.tileSize * 2;
        int baseX = (gp.screenWidth - width) / 2;
        int baseY = gp.tileSize * 5;
        g2.setColor(pause_brown_1);
        g2.fillRoundRect(baseX, baseY, width, height, 0, 0);

        int cursorX = baseX + (entityListIndex * listSpacingX + padding);
        int cursorY = (gp.screenHeight / 2) - gp.tileSize;

        int entitySpacingY = (int) (gp.tileSize * 1.75);
        int scrollOffsetY = cursorY - (entityIndex * entitySpacingY);

        for (int i = 0; i < library.size(); i++) {

            int x = baseX + (i * listSpacingX + padding);
            int y = (i == entityListIndex) ? scrollOffsetY : cursorY;

            for (int c = 0; c < library.get(i).size(); c++) {

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
                    g2.setColor(pause_brown_2);
                    g2.fillRoundRect(x - 10, y - 10,gp.tileSize + 20, gp.tileSize + 20,0, 0);
                }

                if (i != entityListIndex || c != entityIndex) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                }
                g2.drawImage(library.get(i).get(c).getSprite(), x, y, gp.tileSize, gp.tileSize,null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                y += entitySpacingY;
            }
        }
    }
    private void editing_Menu_Input_Dir(ArrayList<ArrayList<UIEntity>> library) {
        if (gp.keyH.upPressed) {
            gp.keyH.upPressed = false;

            entityIndex--;
            if (entityIndex < 0) {
                entityIndex = library.get(entityListIndex).size() - 1;
            }
            playMenuCursor();
        }
        else if (gp.keyH.downPressed) {
            gp.keyH.downPressed = false;

            entityIndex++;
            if (entityIndex > library.get(entityListIndex).size() - 1) {
                entityIndex = 0;
            }
            playMenuCursor();
        }
        else if (gp.keyH.leftPressed) {
            gp.keyH.leftPressed = false;

            entityListIndex--;
            entityIndex = 0;
            if (entityListIndex < 0) {
                entityListIndex = library.size() - 1;
            }
            playMenuCursor();
        }
        else if (gp.keyH.rightPressed) {
            gp.keyH.rightPressed = false;

            entityListIndex++;
            entityIndex = 0;
            if (entityListIndex > library.size() - 1) {
                entityListIndex = 0;
            }
            playMenuCursor();
        }
    }

    private void drawEditing_Map() {

        if (editingTiles) {

            // Unhighlight tiles if not pressing R
            if (!gp.keyH.rPressed) {
                selectedTile = null;
            }

            editing_Map_Tile_Input_A();
            editing_Map_Tile_Input_B();
        }
        else {
            editing_Map_Entity_Input_A();
            editing_Map_Entity_Input_B();
            editing_Map_Entity_Input_X();
        }

        editing_Map_Input_Dir();
    }

    private void editing_Map_Tile_Input_A() {
        if (!gp.keyH.uiAPressed) return;
        gp.keyH.uiAPressed = false;

        UIEntity currentTile = tileLibrary.get(entityListIndex).get(entityIndex);
        int tileNum = Integer.parseInt(currentTile.getName());

        if (gp.keyH.rPressed) {

            // Set highlighted start point
            if (selectedTile == null) {
                selectedTile = new Point(cursor.getWorldPoint());
            }
            else {
                editing_FillTiles(tileNum);
            }

            copiedTiles.clear();
        }
        else {
            editing_PlaceTile(tileNum);
        }
    }
    private void editing_Map_Tile_Input_B() {
        if (!gp.keyH.uiBPressed) return;
        gp.keyH.uiBPressed = false;

        if (gp.keyH.rPressed) {

            // Must be highlighted to copy
            if (copiedTiles.isEmpty() && selectedTile != null) {
                editing_CopyTiles();
            }
            // Selected tile not needed for paste
            else if (!copiedTiles.isEmpty()) {
                editing_PasteTiles();
            }
        }
    }
    private void editing_FillTiles(int tileNum) {

        // Detect start and end point on highlighted square
        int startCol = Math.min(selectedTile.x, cursor.getWorldX()) / gp.tileSize;
        int endCol = Math.max(selectedTile.x, cursor.getWorldX()) / gp.tileSize;

        int startRow = Math.min(selectedTile.y, cursor.getWorldY()) / gp.tileSize;
        int endRow = Math.max(selectedTile.y, cursor.getWorldY()) / gp.tileSize;

        // Loop over each tile highlighted
        for (int col = startCol; col <= endCol; col++) {
            for (int row = startRow; row <= endRow; row++) {

                // Fill with current tile
                gp.tileM.mapTileNum[col][row] = tileNum;
            }
        }
    }
    private void editing_CopyTiles() {

        // Detect start and end point on highlighted square
        int startCol = Math.min(selectedTile.x, cursor.getWorldX()) / gp.tileSize;
        int endCol = Math.max(selectedTile.x, cursor.getWorldX()) / gp.tileSize;

        int startRow = Math.min(selectedTile.y, cursor.getWorldY()) / gp.tileSize;
        int endRow = Math.max(selectedTile.y, cursor.getWorldY()) / gp.tileSize;

        // Loop over each tile highlighted
        for (int col = startCol; col <= endCol; col++) {
            for (int row = startRow; row <= endRow; row++) {

                // Offset tile from cursor's position
                int dx = col - cursor.getWorldX() / gp.tileSize;
                int dy = row - cursor.getWorldY() / gp.tileSize;

                // Store in copied tiles
                copiedTiles.put(new Point(dx, dy), gp.tileM.mapTileNum[col][row]);
            }
        }
    }
    private void editing_PasteTiles() {

        int startCol = cursor.getWorldX() / gp.tileSize;
        int startRow = cursor.getWorldY() / gp.tileSize;

        // Loop over stored tile copies
        for (Map.Entry<Point, Integer> entry : copiedTiles.entrySet()) {

            int col = startCol + entry.getKey().x;
            int row = startRow + entry.getKey().y;
            if (col < 0 || gp.maxWorldCol < col || row < 0 || gp.maxWorldRow < row) {
                continue;
            }

            int tileNum = entry.getValue();

            // Paste copied area onto map
            gp.tileM.mapTileNum[col][row] = tileNum;
        }
    }
    private void editing_PlaceTile(int tileNum) {

        int col = cursor.getWorldX() / gp.tileSize;
        int row = cursor.getWorldY() / gp.tileSize;

        gp.tileM.mapTileNum[col][row] = tileNum;
    }

    private void editing_Map_Entity_Input_A() {
        if (!gp.keyH.uiAPressed) return;
        gp.keyH.uiAPressed = false;

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
        else if (cursor.hasSelectedEntity()) {
            editing_HandleEntityAPress(cursor.getSelectedEntity());
        }
        // Not currently holding an entity over an empty spot
        else {
            editing_HandleEntityAPress(cursor.getCurrentEntity());
        }
    }

    private void editing_HandleMapEntityAPress(Entity mapEntity) {

        // Attempt to give loot
        if (editing_GiveLoot(mapEntity, cursor.getSelectedEntity()) || editing_GiveLoot(mapEntity, cursor.getCurrentEntity())) {
            cursor.setSelectedEntity(null);
            return;
        }

        // Grab map entity if not currently holding an entity
        if (!cursor.hasSelectedEntity()) {

            cursor.setSelectedEntity(mapEntity);

            // Move player offscreen when selected
            if (mapEntity == gp.player) {
                gp.player.setWorldPoint(new Point(-48, -48));
            }
            else {
                gp.removeEntity(mapEntity);
            }
        }
        // Trying to place selected entity on top of another
        else {
            playMenuError();
        }
    }
    private void editing_HandleEntityAPress(Entity entity) {

        // Cannot place on current tile
        if (cannotPlaceEntity(entity)) return;

        // Place on map
        editing_PlaceEntity(entity);
        cursor.setSelectedEntity(null);
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

    private boolean editing_GiveLoot(Entity target, Entity loot) {

        // Not valid
        if (target == null || !target.canHoldLoot(loot) || loot == null || target == gp.player) return false;

        if (loot instanceof Collectable || loot instanceof Item) {
            target.setLoot(loot);
            return true;
        }

        return false;
    }

    private void editing_GetEntity() {

        UIEntity uiEntity = entityLibrary.get(entityListIndex).get(entityIndex);

        Entity currentEntity = gp.eGenerator.getEntity(uiEntity.getName());
        cursor.setCurrentEntity(currentEntity);
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
        if (!gp.keyH.uiBPressed) return;
        gp.keyH.uiBPressed = false;

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

    private void editing_Map_Entity_Input_X() {
        if (!gp.keyH.xPressed) return;
        gp.keyH.xPressed = false;

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
     * DRAW HUD
     * Draws the HUD during Play State
     * called by draw()
     */
    private void drawPlayState() {
        drawHUD();
        drawDebug();
    }

    private void drawHUD() {
        drawZTarget();
        drawChargeBar();
        drawPlayerHealth();
        drawPlayerItem();
        drawKeys();
        drawBossKey();
        drawRupeeCount();
        drawAvailableAction();
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

        // User can't use item when not IDLE
        if (gp.player.getAction() != Entity.Action.IDLE) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        }

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

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
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

    private void drawKeys() {

        // Draw key image
        int x = gp.tileSize * 14 + 30;
        int y = gp.tileSize * 10 + 20;
        g2.drawImage(key, x, y, gp.tileSize - 5, gp.tileSize - 5, null);

        x += gp.tileSize - 8;
        y += gp.tileSize - 12;

        // Draw key count
        String keyCount = Integer.toString(gp.player.getKeys());
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 45F));
        g2.drawString(keyCount, x, y);
    }
    private void drawBossKey() {

        if (!gp.player.getHasBossKey()) return;

        // Draw boss key image
        int x = gp.tileSize * 15 + 40;
        int y = gp.tileSize * 10 + 20;
        g2.drawImage(bossKey, x, y, gp.tileSize - 5, gp.tileSize - 5, null);
    }

    /**
     * DRAW RUPEE COUNT
     * Draws the current player's rupee count in the bottom-right corner of the screen
     * Called by drawHUD()
     */
    private void drawRupeeCount() {

        // Draw rupee image
        int x = gp.tileSize * 14 + 30;
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
            rupeeCounter = 0;
            gp.player.addRupees(count);
            playWallet();
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
    public void addRupees(int rupees) {
        rupeeChange += rupees;
    }
    public void setRupeeChange(int rupees) {
        rupeeChange = rupees;
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

            if (enemy != null && enemy.canBeTargeted()) {

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

        g2.drawImage(zTarget_arrow, x, y, null);
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

        BufferedImage img = rotateImage(zTarget_circle, zTargetRotation);

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

    /** DIALOGUE */
    private void drawDialogueState() {
        drawDialogueWindow();
        handleFinishDialogue();
    }
    private void drawDialogueWindow() {

        int x = gp.tileSize * 2;
        int y = (gp.screenWidth / 2) - gp.tileSize;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4;

        // Black rectangle
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRoundRect(x, y, width, height, 25, 25);

        // White border
        g2.setColor(new Color(255, 255, 255));
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 15, 15);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 37F));
        x += gp.tileSize;
        y += gp.tileSize;

        int textSpeed = 1;
        if (dialogueCounter == textSpeed) {
            char[] characters = dialogue.toCharArray();

            if (charIndex < characters.length) {
                String text = String.valueOf(characters[charIndex]);
                combinedText += text;
                currentDialogue = combinedText;
                charIndex++;
                playDialogueText();
            }
            else {
                canSkip = true;
            }

            dialogueCounter = 0;
        }
        else {
            dialogueCounter++;
        }

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += 40;
        }
    }
    private void handleFinishDialogue() {
        if (!gp.keyH.uiAPressed || !canSkip) return;
        gp.keyH.uiAPressed = false;

        resetDialogue();
        playDialogueNext();

        // If dialogue has reward, set reward dialogue
        gp.player.showReward(dialogueReward);

        // Item gifted to player, show reward dialogue
        if (dialogueReward != null) {
            gp.player.receiveLoot(dialogueReward);
            dialogueReward = null;
        }
        // No reward, continue to play state
        else {
            gp.player.resetState();
            gp.GAME_STATE = gp.PLAY_STATE;
        }
    }

    private void resetDialogue() {
        dialogue = "";
        currentDialogue = "";
        dialogueCounter = 0;
        charIndex = 0;
        combinedText = "";
        canSkip = false;
    }

    public void setupDialogue(String dialogue, Entity reward) {
        this.dialogue = dialogue;
        this.dialogueReward = reward;
        gp.GAME_STATE = gp.DIALOGUE_STATE;
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }

        return image;
    }

    public void setDialogue(String dialogue) {
        this.dialogue = dialogue;
    }

    private void drawSubWindow(int x, int y, int width, int height) {

        // Black (RGB, Transparency)
        Color c = new Color(0, 0, 0, 220);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 25, 25);

        // White (RGB)
        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 15, 15);
    }
    private void drawPauseWindow(int x, int y, int width, int height) {

        g2.setColor(pause_brown_1);
        g2.fillRoundRect(x, y, width, height, 25, 10);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(x, y, width, height, 25, 10);
    }

    /** SOUND EFFECTS */
    public void playMenuOpen() {
        gp.playSE(1, 0);
    }
    private void playMenuCursor() {
        gp.playSE(1, 1);
    }
    private void playMenuSelect() {
        gp.playSE(1, 2);
    }
    public void playMenuError() {
        gp.playSE(1, 3);
    }
    public void playMenuClose() {
        gp.playSE(1, 4);
    }
    private void playDialogueText() {
        gp.playSE(1, 5);
    }
    private void playDialogueNext() {
        gp.playSE(1, 6);
    }
    private void playWallet() {
        gp.playSE(1, 7);
    }
}
