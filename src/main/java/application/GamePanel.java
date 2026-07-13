package application;

import UI.UI;
import UI.Camera;
import ai.PathFinder;
import data.EntityGenerator;
import entity.Entity;
import entity.Player;
import entity.collectable.Collectable;
import entity.enemy.Enemy;
import entity.npc.NPC;
import entity.object.Object;
import entity.projectile.Projectile;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

public class GamePanel extends JPanel implements Runnable {

    public enum Direction {
        UP,
        UPLEFT,
        UPRIGHT,
        DOWN,
        DOWNLEFT,
        DOWNRIGHT,
        LEFT,
        RIGHT
    }

    /** GENERAL CONFIG */
    private Graphics2D g2;
    private Thread gameThread;
    public static UtilityTool utility = new UtilityTool();

    /** DATA */
    public final EntityGenerator eGenerator = new EntityGenerator(this);

    /** CONTROLS / SOUND / UI */
    public KeyHandler keyH = new KeyHandler();

    public UI ui = new UI(this);
    public Camera camera = new Camera(this);

    /** SCREEN SETTINGS */
    private final int originalTileSize = 16; // 16x16 tile
    private final int scale = 3; // scale rate to accommodate for large screen
    public final int tileSize = originalTileSize * scale; // scaled tile (16*3 = 48px)
    public final int maxScreenCol = 17; // columns (width)
    public final int maxScreenRow = 13; // rows (height)
    public final int screenWidth = tileSize * maxScreenCol; // screen width (in tiles) 768px
    public final int screenHeight = tileSize * maxScreenRow;

    /** WORLD SIZE */
    public int maxWorldCol = 50;
    public int maxWorldRow = 50;
    public int worldWidth = tileSize * maxWorldCol;
    public int worldHeight  = tileSize * maxWorldRow;

    /** MAPS */
    public final String mapFile = "map_default.txt";

    /** FULL SCREEN SETTINGS */
    public boolean fullScreenOn = false;
    private int screenWidth2 = screenWidth;
    private int screenHeight2 = screenHeight;
    private BufferedImage tempScreen;

    /** GAME STATES */
    public int gameState;
    public final int playState = 1;
    public final int editState = 2;

    /** HANDLERS */
    public TileManager tileM = new TileManager(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public PathFinder pFinder = new PathFinder(this);

    /** ENTITIES */
    private final ArrayList<Entity> entityList = new ArrayList<>();
    public final Player player = new Player(this);
    public final Entity[] npc = new Entity[10];
    public final Enemy[] enemy = new Enemy[25];
    public final Object[] obj = new Object[25];
    public final Collectable[] col = new Collectable[25];
    public final Projectile[] proj = new Projectile[50];

    /**
     * CONSTRUCTOR
     */
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // screen size
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // improves rendering performance

        this.addKeyListener(keyH);
        this.setFocusable(true); // GamePanel in focus to receive input
    }

    /**
     * SETUP GAME
     * Prepares the game with default settings
     * Called by Driver
     */
    protected void setupGame() {

        gameState = editState;

        // Temp game window (before drawing to window)
        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) tempScreen.getGraphics();

        tileM.loadMap();
        aSetter.setup();

        player.setDefaultValues();

        if (fullScreenOn) {
            setFullScreen();
        }
    }

    /**
     * SET FULL SCREEN
     * Changes the graphics to full screen mode
     * Called by setupGame()
     */
    private void setFullScreen() {

        // Get system screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Driver.window);

        // Get full screen width and height
        screenWidth2 = Driver.window.getWidth();
        screenHeight2 = Driver.window.getHeight();
    }

    /**
     * START GAME THREAD
     * Runs a new thread
     * Called by Driver
     */
    protected void startGameThread() {

        // New Thread with GamePanel class
        gameThread = new Thread(this);

        // Calls run() endlessly
        gameThread.start();
    }

    /**
     * RUN
     * Draws and updates the game 60 times a second
     * Called using the game thread start() method
     */
    @Override
    public void run() {

        long currentTime;
        long lastTime = System.nanoTime();
        double drawInterval = 1000000000.0 / 60.0; // 1/60th of a second
        double delta = 0;

        // Update and repaint gameThread
        while (gameThread != null) {

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval; // Time passed (1/60th second)
            lastTime = currentTime;

            if (delta >= 1) {

                // Update game information
                update();

                // Draw temp screen with new information
                drawToTempScreen();

                // Send temp screen to monitors
                drawToScreen();

                delta = 0;
            }
        }
    }

    /**
     * UPDATE
     * Runs each time the frame is updated
     * Called by run()
     */
    private void update() {

        if (gameState == playState) {
            camera.follow(player.getWorldPoint());
            player.update();
            updateNPCs();
            updateEnemies();
            updateObjects();
            updateCollectables();
            updateProjectiles();

            if (keyH.startPressed) {
                keyH.startPressed = false;
                ui.cursor.setWorldPoint(player.getWorldPoint());
                gameState = editState;
            }
        }
        else if (gameState == editState) {
            camera.follow(ui.cursor.getWorldPoint());

            if (keyH.startPressed) {
                keyH.startPressed = false;
                gameState = playState;
            }
        }
    }

    /** UPDATERS **/
    private void updateNPCs() {
        for (Entity entity : npc) {
            if (entity != null) {
                entity.update();
            }
        }
    }
    private void updateEnemies() {
        for (int i = 0; i < enemy.length; i++) {
            if (enemy[i] != null) {
                // Only update if enemy is alive and not dying
                if (enemy[i].getAlive() && !enemy[i].getDying()) {
                    enemy[i].update();
                }
                // Delete enemy if dead
                else if (!enemy[i].getAlive()) {
                    enemy[i] = null;
                }
            }
        }
    }
    private void updateObjects() {
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                obj[i].update();
                if (!obj[i].getAlive()) {
                    obj[i] = null;
                }
            }
        }
    }
    private void updateCollectables() {
        for (int i = 0; i < col.length; i++) {
            if (col[i] != null) {
                col[i].update();
                if (!col[i].getAlive()) {
                    col[i] = null;
                }
            }
        }
    }
    private void updateProjectiles() {
        for (int i = 0; i < proj.length; i++) {
            if (proj[i] != null) {
                proj[i].update();
                if (!proj[i].getAlive()) {
                    proj[i] = null;
                }
            }
        }
    }

    /**
     * DRAW TO TEMP SCREEN
     * Draws to temporary screen before drawing to front-end
     * Called by run()
     */
    private void drawToTempScreen() {
        drawTiles();
        drawObjects();
        drawEntities();
        drawProjectiles();

        if (gameState == editState) {
            drawGrid();
        }

        ui.draw(g2);
    }

    /** DRAW METHODS **/
    private void drawTiles() {
        tileM.draw(g2);
    }
    private void drawObjects() {

        for (Object obj : obj) {
            if (obj != null) {
                obj.draw(g2);
            }
        }

        for (Collectable col : col) {
            if (col != null) {
                col.draw(g2);
            }
        }
    }
    private void drawEntities() {

        entityList.add(player);

        // NPCs
        for (Entity n : npc) {
            if (n != null) {
                entityList.add(n);
            }
        }

        // Enemies
        for (Enemy n : enemy) {
            if (n != null) {
                entityList.add(n);
            }
        }

        // Sort draw order by Y coordinate
        entityList.sort(Comparator.comparingInt(Entity::getWorldPointY));

        // Draw all entities
        for (Entity e : entityList) {
            e.draw(g2);
        }

        // Empty list
        entityList.clear();
    }
    private void drawProjectiles() {

        for (Projectile proj : proj) {
            if (proj != null) {
                proj.draw(g2);
            }
        }
    }

    /**
     * DRAW TO SCREEN
     * Draws graphics to screen
     * Called by run()
     */
    private void drawToScreen() {
        Graphics g = getGraphics();
        g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
        g.dispose();
    }

    private void drawGrid() {

        // Semi-transparent white
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(1));

        // Vertical lines
        for (int col = 0; col <= maxWorldCol; col++) {
            int x = col * tileSize;
            g2.drawLine(x, 0, x, maxWorldRow * tileSize);
        }

        // Horizontal lines
        for (int row = 0; row <= maxWorldRow; row++) {
            int y = row * tileSize;
            g2.drawLine(0, y, maxWorldCol * tileSize, y);
        }
    }

    public Entity[][] getAllEntities() {
        return new Entity[][] {
                npc,
                enemy,
                obj,
                col,
                proj
        };
    }
    public Entity[] getEntityList(Entity entity) {

        if (entity instanceof NPC) {
            return npc;
        }
        else if (entity instanceof Enemy) {
            return enemy;
        }
        else if (entity instanceof Object) {
            return obj;
        }
        else if (entity instanceof Collectable) {
            return col;
        }
        else {
            return null;
        }
    }

    public int findOpenSlot(Entity[] list) {

        if (list == null) {
            return -1;
        }

        for (int i = 0; i < list.length; i++) {
            if (list[i] == null) {
                return i;
            }
        }
        return -1;
    }
}
