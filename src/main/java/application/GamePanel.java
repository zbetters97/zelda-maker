package application;

import UI.UI;
import UI.Camera;
import ai.PathFinder;
import data.EntityGenerator;
import data.SaveLoad;
import entity.Entity;
import entity.Player;
import entity.collectable.Collectable;
import entity.enemy.Enemy;
import entity.npc.NPC;
import entity.object.Object;
import entity.object.Particle;
import entity.projectile.Projectile;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

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
    public final SaveLoad saveLoad = new SaveLoad(this);
    public TileManager tileM = new TileManager(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public PathFinder pFinder = new PathFinder(this);

    /** ENTITIES */
    private final ArrayList<Entity> entityList = new ArrayList<>();
    private final ArrayList<ArrayList<? extends Entity>> entities = new ArrayList<>();
    public final Player player = new Player(this);
    public final ArrayList<NPC> npcs = new ArrayList<>();
    public final ArrayList<Enemy> enemies = new ArrayList<>();
    public final ArrayList<Object> objects = new ArrayList<>();
    public final ArrayList<Collectable> collectables = new ArrayList<>();
    public final ArrayList<Projectile> projectiles = new ArrayList<>();
    public final ArrayList<Particle> particles = new ArrayList<>();

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

        gameState = playState;

        // Temp game window (before drawing to window)
        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) tempScreen.getGraphics();

        tileM.loadMap();

        player.setDefaultValues();

        if (fullScreenOn) {
            setFullScreen();
        }

        entities.addAll(Arrays.asList(npcs, enemies, objects, projectiles, collectables, particles));
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
            updateEntities();

            if (keyH.startPressed) {
                keyH.startPressed = false;
                // saveLoad.load();
                ui.cursor.setWorldPoint(player.getWorldPoint());
                gameState = editState;
            }
        }
        else if (gameState == editState) {

            camera.follow(ui.cursor.getWorldPoint());

            if (keyH.startPressed) {
                keyH.startPressed = false;
                // saveLoad.save();
                gameState = playState;
            }
        }
    }

    /** UPDATERS **/
    private void updateEntities() {

        for (ArrayList<? extends Entity> entityList : entities) {

            Iterator<? extends Entity> iterator = entityList.iterator();
            while (iterator.hasNext()) {

                Entity e = iterator.next();

                // Release captured spell before removing
                if (!e.getAlive()) {
                    e.breakCapture();
                    iterator.remove();
                    continue;
                }

                // Don't update dying enemies
                if ((e instanceof Enemy) && e.getDying()) continue;

                e.update();

                // Entity died during update
                if (!e.getAlive()) {
                    e.breakCapture();
                    iterator.remove();
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
        drawEntities();

        if (gameState == editState) {
            drawGrid();
        }

        ui.draw(g2);
    }

    /** DRAW METHODS **/
    private void drawTiles() {
        tileM.draw(g2);
    }
    private void drawEntities() {

        entityList.clear();

        entityList.add(player);
        for (ArrayList<? extends Entity> list : entities) {
            entityList.addAll(list);
        }

        drawLayer(Entity.DrawLayer.GROUND, false);
        drawLayer(Entity.DrawLayer.ENTITY, true);
        drawLayer(Entity.DrawLayer.ABOVE, false);
    }
    private void drawLayer(Entity.DrawLayer layer, boolean sort) {

        Stream<Entity> stream = entityList.stream()
                .filter(e -> e.getDrawLayer() == layer);

        if (sort) {
            stream = stream.sorted(
                    Comparator.comparing(Entity::getIsGrabbed)
                            .thenComparingInt(Entity::getWorldPointY)
            );
        }

        stream.forEach(e -> e.draw(g2));
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

    public ArrayList<ArrayList<? extends Entity>> getAllEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {

        switch (entity) {
            case NPC npc -> npcs.add(npc);
            case Enemy enemy -> enemies.add(enemy);
            case Particle particle -> particles.add(particle);
            case Object object -> objects.add(object);
            case Collectable collectable -> collectables.add(collectable);
            case Projectile projectile -> projectiles.add(projectile);
            default -> throw new IllegalArgumentException();
        }
    }
}
