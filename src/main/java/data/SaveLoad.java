package data;

import application.GamePanel;
import entity.enemy.Enemy;
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
            ds.health = gp.player.getHealth();

            // NPCs
            ds.npcNames = new String[gp.npcs.size()];
            ds.npcWorldX = new int[gp.npcs.size()];
            ds.npcWorldY = new int[gp.npcs.size()];
            ds.npcDirections = new String[gp.npcs.size()];

            // ENEMIES
            ds.enemyNames = new String[gp.enemies.size()];
            ds.enemyWorldX = new int[gp.enemies.size()];
            ds.enemyWorldY = new int[gp.enemies.size()];
            ds.enemyDirections = new String[gp.enemies.size()];
            ds.enemyHealth = new int[gp.enemies.size()];

            // OBJECTS
            ds.objectWorldX = new int[gp.objects.size()];
            ds.objectWorldY = new int[gp.objects.size()];
            ds.objectNames = new String[gp.objects.size()];
            ds.objectDirections = new String[gp.objects.size()];

            // TILES
            ds.tileNums = new int[gp.maxWorldCol * gp.maxWorldRow];

            // NPCs
            for (int i = 0; i < gp.npcs.size(); i++) {

                NPC npc = gp.npcs.get(i);

                if (npc == null) continue;

                ds.npcNames[i] = npc.getName();
                ds.npcWorldX[i] = npc.getWorldPoint().x;
                ds.npcWorldY[i] = npc.getWorldPoint().y;
                ds.npcDirections[i] = npc.getDirection().toString();
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
            }

            // OBJECTS
            for (int i = 0; i < gp.objects.size(); i++) {

                Object object = gp.objects.get(i);

                if (object == null) continue;

                ds.objectNames[i] = object.getName();
                ds.objectWorldX[i] = object.getWorldPoint().x;
                ds.objectWorldY[i] = object.getWorldPoint().y;
                ds.objectDirections[i] = object.getDirection().toString();
            }

            // TILES
            int i = 0;
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {

                    int tileNum = gp.tileM.mapTileNum[col][row];

                    ds.tileNums[i] = tileNum;
                    i++;
                }
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
            gp.player.setHealth(ds.health);

            // TILES
            int c = 0;
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {

                    int tileNum = ds.tileNums[c];

                    gp.tileM.mapTileNum[col][row] = tileNum;

                    c++;
                }
            }

            // NPCs
            for (int i = 0; i < ds.npcNames.length; i++) {

                NPC npc = (NPC) gp.eGenerator.getEntity(ds.npcNames[i]);

                if (npc == null) continue;

                npc.setWorldPoint(new Point(ds.npcWorldX[i], ds.npcWorldY[i]));
                npc.setDirection(GamePanel.Direction.valueOf(ds.npcDirections[i]));

                gp.npcs.add(npc);
            }

            // ENEMIES
            for (int i = 0; i < ds.enemyNames.length; i++) {

                Enemy enemy = (Enemy) gp.eGenerator.getEntity(ds.enemyNames[i]);

                if (enemy == null) continue;

                enemy.setWorldPoint(new Point(ds.enemyWorldX[i], ds.enemyWorldY[i]));
                enemy.setDirection(GamePanel.Direction.valueOf(ds.enemyDirections[i]));
                enemy.setHealth(ds.enemyHealth[i]);

                gp.enemies.add(enemy);
            }

            // OBJECTS
            for (int i = 0; i < ds.objectNames.length; i++) {

                Object object = (Object) gp.eGenerator.getEntity(ds.objectNames[i]);

                if (object == null) continue;

                object.setWorldPoint(new Point(ds.objectWorldX[i], ds.objectWorldY[i]));
                object.setDirection(GamePanel.Direction.valueOf(ds.objectDirections[i]));

                gp.objects.add(object);
            }

            ois.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}