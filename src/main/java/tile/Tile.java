package tile;

import entity.Entity;

import java.awt.image.BufferedImage;

/** TILE CLASS **/
public class Tile {

    // Image file
    protected BufferedImage image;

    // Attributes
    private boolean collision = false;
    private boolean water = false;
    private boolean pit = false;

    public BufferedImage getImage() {
        return image;
    }

    public boolean hasCollision() {
        return collision;
    }
    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean isWater() {
        return water;
    }
    public void setWater(boolean water) {
        this.water = water;
    }

    public boolean isPit() {
        return pit;
    }
    public void setPit(boolean pit) {
        this.pit = pit;
    }

    public boolean isNotTraversable(int tileNum) {
        return collision || water || pit || tileNum == TileManager.spikeTile;
    }

    public boolean isNotTraversable(Entity entity, int tileNum) {

        // Entity needs water, false if water
        if (entity.getNeedsWater()) {
            return !water;
        }

        return collision || water || pit || tileNum == TileManager.spikeTile;
    }
}