package data;

import application.GamePanel;
import entity.Entity;
import entity.item.Item;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.UUID;

public class SaveLoad {

    private final GamePanel gp;

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
    }

    public void save(String worldName, String fileName) {
        saveSnapshot(worldName);
        saveToFile(fileName);
    }

    public void saveSnapshot(String worldName) {

        DataStorage ds = new DataStorage();

        ds.world_name = worldName;
        ds.world_song = gp.song;

        // 01/31/2026 format
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        ds.file_date = sdf.format(new Date(System.currentTimeMillis()));

        // PLAYER DATA
        ds.pWorldX = gp.player.getWorldPoint().x;
        ds.pWorldY = gp.player.getWorldPoint().y;
        ds.direction = gp.player.getDirection().toString();
        ds.maxHealth = gp.player.getHealth();
        ds.health = gp.player.getHealth();
        ds.maxRupees = gp.player.getMaxRupees();
        ds.rupees = gp.player.getRupees();
        ds.maxArrows = gp.player.getMaxArrows();
        ds.arrows = gp.player.getArrows();
        ds.maxBombs = gp.player.getMaxBombs();
        ds.bombs = gp.player.getBombs();
        ds.keys = gp.player.getKeys();
        ds.hasBossKey = gp.player.getHasBossKey();

        ds.currentItemSlot = gp.player.getCurrentItemSlot();
        ds.items = new String[gp.player.getItems().size()];
        for (int i = 0; i < gp.player.getItems().size(); i++) {

            Item item = gp.player.getItems().get(i);

            if (item != null) {
                ds.items[i] = item.getName();
            }
        }

        // NPCs
        int npcLength = gp.npcs.size();
        ds.npcNames = new String[npcLength];
        ds.npcWorldX = new int[npcLength];
        ds.npcWorldY = new int[npcLength];
        ds.npcDirections = new String[npcLength];
        ds.npcLoot = new String[npcLength];

        // ENEMIES
        int enemyLength = gp.enemies.size();
        ds.enemyNames = new String[enemyLength];
        ds.enemyWorldX = new int[enemyLength];
        ds.enemyWorldY = new int[enemyLength];
        ds.enemyDirections = new String[enemyLength];
        ds.enemyHealth = new int[enemyLength];
        ds.enemyLoot = new String[enemyLength];

        // OBJECTS
        int objectLength = gp.objects.size();
        ds.objectWorldX = new int[objectLength];
        ds.objectWorldY = new int[objectLength];
        ds.objectNames = new String[objectLength];
        ds.objectDirections = new String[objectLength];
        ds.objectLoot = new String[objectLength];

        // COLLECTABLES
        int collectableLength = gp.collectables.size();
        ds.collectableNames = new String[collectableLength];
        ds.collectableWorldX = new int[collectableLength];
        ds.collectableWorldY = new int[collectableLength];

        // TILES
        ds.tileNumbers = new int[gp.maxWorldCol * gp.maxWorldRow];

        int t = 0;
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {

                int tileNum = gp.tileM.mapTileNum[col][row];
                ds.tileNumbers[t] = tileNum;

                t++;
            }
        }

        saveEntityList(gp.npcs, ds.npcNames, ds.npcWorldX, ds.npcWorldY, ds.npcDirections, null, ds.npcLoot);
        saveEntityList(gp.enemies, ds.enemyNames, ds.enemyWorldX, ds.enemyWorldY, ds.enemyDirections, ds.enemyHealth, ds.enemyLoot);
        saveEntityList(gp.objects, ds.objectNames, ds.objectWorldX, ds.objectWorldY, ds.objectDirections, null, ds.objectLoot);
        saveEntityList(gp.collectables, ds.collectableNames, ds.collectableWorldX, ds.collectableWorldY, null, null, null);

        gp.snapshot = ds;
    }
    private void saveEntityList(ArrayList<? extends Entity> entities, String[] entityNames,
                                int[] entityWorldX, int[] entityWorldY, String[] entityDirections,
                                int[] entityHealth, String[] entityLoot) {

        for (int i = 0; i < entities.size(); i++) {

            Entity entity = entities.get(i);
            if (entity == null) continue;

            entityNames[i] = entity.getName();
            entityWorldX[i] = entity.getWorldPoint().x;
            entityWorldY[i] = entity.getWorldPoint().y;

            if (entityDirections != null) {
                entityDirections[i] = entity.getDirection().toString();
            }
            if (entityHealth != null) {
                entityHealth[i] = entity.getHealth();
            }
            if (entityLoot != null) {
                entityLoot[i] = entity.getLoot() == null ? "NULL" : entity.getLoot().getName();
            }
        }
    }
    private void saveToFile(String fileName) {
        if (gp.dbNotConnected()) return;

        try {
            // Create new save or overwrite existing one
            String fileID = fileName.isEmpty() ? UUID.randomUUID() + ".dat" : fileName;
            Path tempFile = Path.of(fileID);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile.toFile()));

            // Write to the DS object
            oos.writeObject(gp.snapshot);
            oos.close();

            // Upload to Firebase storage
            gp.db.uploadWorld(tempFile);

            // Update stored save files
            gp.saveFiles = gp.db.getUserWorlds(gp.auth.getUserId());
        }
        catch (Exception e) {
            System.out.println("Error saving world: " + e.getMessage());
        }
    }

    public void load(String fileName) {
        loadWorld(fileName);
        loadSnapshot();
    }
    private void loadWorld(String fileName) {
        if (gp.dbNotConnected()) return;

        try {
            // Get save file from Firebase storage
            byte[] data = gp.db.downloadWorld(fileName);
            if (data == null) return;

            // Read saved file
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));

            // Load data to the DS object
            gp.snapshot = (DataStorage) ois.readObject();

            // Close file
            ois.close();
        }
        catch (Exception e) {
            System.out.println("Error loading world: " + e.getMessage());
        }
    }
    public void loadSnapshot() {

        DataStorage ds = gp.snapshot;

        gp.song = ds.world_song;

        // PLAYER DATA
        gp.player.setWorldPoint(new Point(ds.pWorldX, ds.pWorldY));
        gp.player.setDirection(GamePanel.Direction.valueOf(ds.direction));
        gp.player.setMaxHealth(ds.maxHealth);
        gp.player.setHealth(ds.health);
        gp.player.setMaxRupees(ds.maxRupees);
        gp.player.setRupees(ds.rupees);
        gp.ui.setRupeeChange(ds.rupees);
        gp.player.setMaxArrows(ds.maxArrows);
        gp.player.setArrows(ds.arrows);
        gp.player.setMaxBombs(ds.maxBombs);
        gp.player.setBombs(ds.bombs);
        gp.player.setKeys(ds.keys);
        gp.player.setHasBossKey(ds.hasBossKey);

        gp.player.setCurrentItemSlot(ds.currentItemSlot);

        // PLAYER ITEMS
        if (ds.items != null) {
            for (int i = 0; i < ds.items.length; i++) {

                String itemName = ds.items[i];
                if (itemName == null) continue;

                // Entity found is not an Item
                Entity item = gp.eGenerator.getEntity(itemName);
                if (!(item instanceof Item)) continue;

                gp.player.addItem((Item) item);

                // Equip item if current one
                if (i == ds.currentItemSlot) {
                    gp.player.setItem((Item) item);
                }
            }
        }

        // TILES
        int t = 0;
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {

                int tileNum = ds.tileNumbers[t];
                gp.tileM.mapTileNum[col][row] = tileNum;

                t++;
            }
        }

        // NPCs
        if (ds.npcNames != null) {
            loadEntityList(ds.npcNames, ds.npcWorldX, ds.npcWorldY, ds.npcDirections, null, ds.npcLoot);
        }

        if (ds.enemyNames != null) {
            loadEntityList(ds.enemyNames, ds.enemyWorldX, ds.enemyWorldY, ds.enemyDirections, ds.enemyHealth, ds.enemyLoot);
        }

        if (ds.objectNames != null) {
            loadEntityList(ds.objectNames, ds.objectWorldX, ds.objectWorldY, ds.objectDirections, null, ds.objectLoot);
        }

        if (ds.collectableNames != null) {
            loadEntityList(ds.collectableNames, ds.collectableWorldX, ds.collectableWorldY, null, null, null);
        }
    }
    private void loadEntityList(String[] names, int[] worldX, int[] worldY,
                                String[] directions, int[] health, String[] loots) {
        for (int i = 0; i < names.length; i++) {

            Entity entity =  gp.eGenerator.getEntity(names[i]);
            if (entity == null) continue;

            entity.setWorldPoint(new Point(worldX[i], worldY[i]));

            if (directions != null) {
                entity.setDirection(GamePanel.Direction.valueOf(directions[i]));
            }

            if (health != null) {
                entity.setHealth(health[i]);
            }

            if (loots != null) {
                Entity loot = gp.eGenerator.getEntity(loots[i]);
                if (loot != null) entity.setLoot(loot);
            }

            gp.addEntity(entity);
        }
    }

    public void delete(String fileName) {
        if (gp.dbNotConnected()) return;

        if (gp.db.deleteWorld(fileName)) {
            gp.saveFiles = gp.db.getUserWorlds(gp.auth.getUserId());
        }
    }
}