package data;

import application.GamePanel;
import entity.Entity;
import entity.enemy.Enemy;
import entity.item.Item;
import entity.npc.NPC;
import entity.object.Object;

import java.awt.*;
import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class SaveLoad {

    private final GamePanel gp;

    public File saveDir = new File(System.getProperty("user.home") + "/zmaker-conf/save_1.dat");

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
    }

    public void save()  {

        try {

            // SAVE DATA TO DS
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveDir));
            DataStorage ds = new DataStorage();

            // CURRENT DATE/TIME
            ds.file_date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));

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

            // TILES
            ds.tileNums = new int[gp.maxWorldCol * gp.maxWorldRow];

            int t = 0;
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {

                    int tileNum = gp.tileM.mapTileNum[col][row];
                    ds.tileNums[t] = tileNum;

                    t++;
                }
            }

            // NPCs
            for (int i = 0; i < gp.npcs.size(); i++) {

                NPC npc = gp.npcs.get(i);
                if (npc == null) continue;

                ds.npcNames[i] = npc.getName();
                ds.npcWorldX[i] = npc.getWorldPoint().x;
                ds.npcWorldY[i] = npc.getWorldPoint().y;
                ds.npcDirections[i] = npc.getDirection().toString();
                ds.npcLoot[i] = npc.getLoot() == null ? "NULL" : npc.getLoot().getName();
            }

            // ENEMIES
            for (int i = 0; i < gp.enemies.size(); i++) {

                Enemy enemy = gp.enemies.get(i);
                if (enemy == null) continue;

                ds.enemyNames[i] = enemy.getName();
                ds.enemyWorldX[i] = enemy.getWorldPoint().x;
                ds.enemyWorldY[i] = enemy.getWorldPoint().y;
                ds.enemyDirections[i] = enemy.getDirection().toString();
                ds.enemyHealth[i] = enemy.getHealth();
                ds.enemyLoot[i] = enemy.getLoot() == null ? "NULL" : enemy.getLoot().getName();
            }

            // OBJECTS
            for (int i = 0; i < gp.objects.size(); i++) {

                Object object = gp.objects.get(i);
                if (object == null) continue;

                ds.objectNames[i] = object.getName();
                ds.objectWorldX[i] = object.getWorldPoint().x;
                ds.objectWorldY[i] = object.getWorldPoint().y;
                ds.objectDirections[i] = object.getDirection().toString();
                ds.objectLoot[i] = object.getLoot() == null ? "NULL" : object.getLoot().getName();
            }

            oos.writeObject(ds);
            oos.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void load() {
        try {

            // LOAD DATA FROM DS
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveDir));
            DataStorage ds = (DataStorage) ois.readObject();

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

            // TILES
            int t = 0;
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {

                    int tileNum = ds.tileNums[t];
                    gp.tileM.mapTileNum[col][row] = tileNum;

                    t++;
                }
            }

            // NPCs
            for (int i = 0; i < ds.npcNames.length; i++) {

                NPC npc = (NPC) gp.eGenerator.getEntity(ds.npcNames[i]);
                if (npc == null) continue;

                npc.setWorldPoint(new Point(ds.npcWorldX[i], ds.npcWorldY[i]));
                npc.setDirection(GamePanel.Direction.valueOf(ds.npcDirections[i]));

                Entity loot = gp.eGenerator.getEntity(ds.npcLoot[i]);
                if (loot != null) npc.setLoot(loot);

                gp.npcs.add(npc);
            }

            // ENEMIES
            for (int i = 0; i < ds.enemyNames.length; i++) {

                Enemy enemy = (Enemy) gp.eGenerator.getEntity(ds.enemyNames[i]);
                if (enemy == null) continue;

                enemy.setWorldPoint(new Point(ds.enemyWorldX[i], ds.enemyWorldY[i]));
                enemy.setDirection(GamePanel.Direction.valueOf(ds.enemyDirections[i]));
                enemy.setHealth(ds.enemyHealth[i]);

                Entity loot = gp.eGenerator.getEntity(ds.enemyLoot[i]);
                if (loot != null) enemy.setLoot(loot);

                gp.enemies.add(enemy);
            }

            // OBJECTS
            for (int i = 0; i < ds.objectNames.length; i++) {

                Object object = (Object) gp.eGenerator.getEntity(ds.objectNames[i]);
                if (object == null) continue;

                object.setWorldPoint(new Point(ds.objectWorldX[i], ds.objectWorldY[i]));
                object.setDirection(GamePanel.Direction.valueOf(ds.objectDirections[i]));

                Entity loot = gp.eGenerator.getEntity(ds.objectLoot[i]);
                if (loot != null) object.setLoot(loot);

                gp.objects.add(object);
            }

            ois.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}