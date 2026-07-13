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

    /* [ROW][COL] */
    public final int[][] mapTileNum;

    private final Point worldPoint = new Point();
    private final Point screenPoint = new Point();

    /**
     * CONSTRUCTOR
     * @param gp GamePanel
     */
    public TileManager(GamePanel gp) {
        this.gp = gp;
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        loadTileData();
    }

    /**
     * LOAD MAP
     * Loads current map data
     */
    public void loadMap() {

        // Import current map
        InputStream inputStream = getClass().getResourceAsStream("/maps/" + gp.mapFile);
        int mapLength = 0;

        try {
            Scanner sc = new Scanner(Objects.requireNonNull(inputStream));

            for (int row = 0; sc.hasNextLine(); row++) {
                String line = sc.nextLine();
                String[] numbers = line.split(" ");
                mapLength = numbers.length;

                for (int col = 0; col < numbers.length; col++) {
                    int tileNum = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = tileNum;
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

            tiles[index].setCollision(hasCollision);
            tiles[index].setWater(isWater);
            tiles[index].setPit(isPit);
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

        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {

                int tileNum = mapTileNum[col][row];

                worldPoint.setLocation(col * gp.tileSize, row * gp.tileSize);

                // Skip tiles outside the camera view
                if (!gp.camera.isVisible(worldPoint, gp.tileSize)) {
                    continue;
                }

                gp.camera.worldToScreen(worldPoint, screenPoint);

                g2.drawImage(tiles[tileNum].image, screenPoint.x, screenPoint.y, null);
            }
        }
    }
}
