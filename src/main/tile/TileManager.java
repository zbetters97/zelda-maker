package tile;

import application.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class TileManager {

    private final GamePanel gp;
    public Tile[] tiles;

    /* [MAP NUMBER][ROW][COL] */
    public final int[][][] mapTileNum;

    /* TILE NUMBERS */
    private final int oceanTile1 = 19;
    private final int oceanTile2 = 39;

    /* WATER ANIMATION COUNTER */
    private int waterNum = 1;
    private int waterCounter = 0;
    private final int waterCounterMax = 45;

    /**
     * CONSTRUCTOR
     * @param gp GamePanel
     */
    public TileManager(GamePanel gp) {
        this.gp = gp;
        mapTileNum = new int[gp.maxMap][100][100];
        loadTileData();
    }

    /**
     * LOAD MAP
     * Loads current map data
     */
    public void loadMap() {

        // Import current map
        InputStream inputStream = getClass().getResourceAsStream("/maps/" + gp.mapFiles[gp.currentMap]);
        int mapLength = 0;

        try {
            Scanner sc = new Scanner(Objects.requireNonNull(inputStream));

            for (int row = 0; sc.hasNextLine(); row++) {
                String line = sc.nextLine();
                String[] numbers = line.split(" ");
                mapLength = numbers.length;

                for (int col = 0; col < numbers.length; col++) {
                    int tileNum = Integer.parseInt(numbers[col]);
                    mapTileNum[gp.currentMap][col][row] = tileNum;
                }
            }

            sc.close();

            // Assign new world dimensions
            gp.maxWorldCol = mapLength;
            gp.maxWorldRow = mapLength;
            gp.worldWidth = gp.tileSize * mapLength;
            gp.worldHeight = gp.tileSize * mapLength;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * LOAD TILE DATA
     * Loads the tile data from a text document
     */
    private void loadTileData() {
        // Arrays to hold tile attributes
        ArrayList<String> tileNumbers = new ArrayList<>();
        ArrayList<String> collisionStatus = new ArrayList<>();
        ArrayList<String> waterStatus = new ArrayList<>();
        ArrayList<String> pitStatus = new ArrayList<>();

        // Import tile data
        InputStream is = getClass().getResourceAsStream("/maps/map_tile_data.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));

        // Add tile data to arrays
        try {
            String line;
            while ((line = br.readLine()) != null) {
                tileNumbers.add(line);
                collisionStatus.add(br.readLine());
                waterStatus.add(br.readLine());
                pitStatus.add(br.readLine());
            }
            br.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // Assign tiles array
        tiles = new Tile[tileNumbers.size()];

        String tileNumber;
        boolean hasCollision, isWater, isPit;

        // Loop through all tile data in fileNames
        for (int i = 0; i < tileNumbers.size(); i++) {

            // Assign each name to fileName
            tileNumber = tileNumbers.get(i);

            // Retrieve status for each tile
            hasCollision = collisionStatus.get(i).equals("true");
            isWater = waterStatus.get(i).equals("true");
            isPit = pitStatus.get(i).equals("true");

            createTile(i, tileNumber, hasCollision, isWater, isPit);
        }
    }

    /**
     * CREATE TILE
     * Assigns tile attributes to the tiles array
     * @param index Array index
     * @param tileNumber Tile number
     * @param hasCollision If tile has collision
     * @param isWater If tile is water
     * @param isPit If tile is a pit
     */
    private void createTile(int index, String tileNumber, boolean hasCollision, boolean isWater, boolean isPit) {
        try {
            tiles[index] = new Tile();

            tiles[index].image = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/tiles/" + tileNumber)
            ));

            tiles[index].image = GamePanel.utility.scaleImage(tiles[index].image, gp.tileSize, gp.tileSize);

            tiles[index].hasCollision = hasCollision;
            tiles[index].isWater = isWater;
            tiles[index].isPit = isPit;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * DRAW
     * Draws the tile data
     * @param g2 GamePanel
     */
    public void draw(Graphics2D g2) {

        // Animate water
        waterCounter++;
        if (waterCounter >= waterCounterMax) {
            waterCounter = 0;
            if (waterNum == 1) {
                waterNum = 2;
            }
            else {
                waterNum = 1;
            }
        }

        int worldCol = 0;
        int worldRow = 0;
        boolean offCenter = false;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

            // Tile number from map txt document
            int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];

            // World X,Y
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;

            // Player screen position X, Y Offset to Center
            int screenX = worldX - gp.player.getWorldPoint().x + gp.player.getScreenPoint().x;
            int screenY = worldY - gp.player.getWorldPoint().y + gp.player.getScreenPoint().y;

            // Stop camera movement at world boundary
            if (gp.player.getScreenPoint().x > gp.player.getWorldPoint().x) {
                screenX = worldX;
                offCenter = true;
            }
            if (gp.player.getScreenPoint().y > gp.player.getWorldPoint().y) {
                screenY = worldY;
                offCenter = true;
            }

            // From player right-edge to screen
            int rightOffset = gp.screenWidth - gp.player.getScreenPoint().x;

            // From player to right-edge of world
            if (rightOffset > gp.worldWidth - gp.player.getWorldPoint().x) {
                screenX = gp.screenWidth - (gp.worldWidth - worldX);
                offCenter = true;
            }

            // From player to bottom-edge of screen
            int bottomOffSet = gp.screenHeight - gp.player.getScreenPoint().y;

            // From player to bottom-edge of world
            if (bottomOffSet > gp.worldHeight - gp.player.getWorldPoint().y) {
                screenY = gp.screenHeight - (gp.worldHeight - worldY);
                offCenter = true;
            }

            // Draw tiles within player boundary
            if (worldX + gp.tileSize > gp.player.getWorldPoint().x - gp.player.getScreenPoint().x &&
                    worldX - gp.tileSize < gp.player.getWorldPoint().x + gp.player.getScreenPoint().x &&
                    worldY + gp.tileSize > gp.player.getWorldPoint().y - gp.player.getScreenPoint().y &&
                    worldY - gp.tileSize < gp.player.getWorldPoint().y + gp.player.getScreenPoint().y) {

                if (tileNum == oceanTile1) {
                    if (waterNum == 2) {
                        tileNum = oceanTile2;
                    }
                }

                g2.drawImage(tiles[tileNum].image, screenX, screenY, null);
            }
            else if (offCenter) {
                if (tileNum == oceanTile1) {
                    if (waterNum == 2) {
                        tileNum = oceanTile2;
                    }
                }
                g2.drawImage(tiles[tileNum].image, screenX, screenY, null);
            }

            // To next column
            worldCol++;

            // To next row
            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
