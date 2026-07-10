package data;

import application.GamePanel;
import entity.object.Object;

import java.awt.*;
import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class SaveLoad {

    private final GamePanel gp;

    public File saveDir = new File(System.getProperty("user.home") + "/loz-conf/save_1.dat");

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
    }

    public void save()  {

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveDir));

            // SAVE DATA TO DS
            DataStorage ds = new DataStorage();

            // CURRENT DATE/TIME
            ds.file_date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));

            // PLAYER DATA
            ds.cMap = gp.currentMap;
            ds.pWorldX = gp.player.getWorldPoint().x;
            ds.pWorldY = gp.player.getWorldPoint().y;
            ds.cArea = gp.currentArea;

            ds.direction = gp.player.getDirection().toString();
            ds.maxHealth = gp.player.getMaxHealth();
            ds.health = gp.player.getHealth();
            ds.attack = gp.player.getAttack();

            // ENEMIES
            ds.enemyWorldX = new int[gp.maxMap][gp.enemy[0].length];
            ds.enemyWorldY = new int[gp.maxMap][gp.enemy[0].length];
            ds.enemyHealth = new int[gp.maxMap][gp.enemy[0].length];
            ds.enemyAlive = new boolean[gp.maxMap][gp.enemy[0].length];

            // MAP OBJECTS
            ds.mapObjectNames = new String[gp.maxMap][gp.obj[0].length];
            ds.mapObjectWorldX = new int[gp.maxMap][gp.obj[0].length];
            ds.mapObjectWorldY = new int[gp.maxMap][gp.obj[0].length];
            ds.mapObjectDirections = new String[gp.maxMap][gp.obj[0].length];

            for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {

                // ENEMIES
                for (int i = 0; i < gp.enemy[0].length; i++) {
                    if (gp.enemy[mapNum][i] == null) {
                        ds.enemyAlive[mapNum][i] = false;
                    }
                    else {
                        ds.enemyWorldX[mapNum][i] = gp.enemy[mapNum][i].getWorldPoint().x;
                        ds.enemyWorldY[mapNum][i] = gp.enemy[mapNum][i].getWorldPoint().y;
                        ds.enemyHealth[mapNum][i] = gp.enemy[mapNum][i].getHealth();
                        ds.enemyAlive[mapNum][i] = true;
                    }
                }

                // MAP OBJECTS
                for (int i = 0; i < gp.obj[0].length; i++) {

                    if (gp.obj[mapNum][i] == null) {
                        ds.mapObjectNames[mapNum][i] = "NULL";
                    }
                    else {
                        ds.mapObjectNames[mapNum][i] = gp.obj[mapNum][i].getName();
                        ds.mapObjectWorldX[mapNum][i] = gp.obj[mapNum][i].getWorldPoint().x;
                        ds.mapObjectWorldY[mapNum][i] = gp.obj[mapNum][i].getWorldPoint().y;
                        ds.mapObjectDirections[mapNum][i] = gp.obj[mapNum][i].getDirection().toString();
                    }
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
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveDir));

            // LOAD DATA FROM DS
            DataStorage ds = (DataStorage) ois.readObject();

            // PLAYER DATA
            gp.currentMap = ds.cMap;
            gp.currentArea = ds.cArea;
            gp.player.setWorldPoint(new Point(ds.pWorldX, ds.pWorldY));


            gp.player.setDirection(GamePanel.Direction.valueOf(ds.direction));
            gp.player.setHealth(ds.health);
            gp.player.setAttack(ds.attack);

            for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {

                // ENEMIES
                for (int i = 0; i < gp.enemy[0].length; i++) {
                    if (!ds.enemyAlive[mapNum][i]) {
                        gp.enemy[mapNum][i] = null;
                    }
                    else if (gp.enemy[mapNum][i] != null) {
                        gp.enemy[mapNum][i].setWorldPoint(new Point(ds.enemyWorldX[mapNum][i], ds.enemyWorldY[mapNum][i]));
                        gp.enemy[mapNum][i].setHealth(ds.enemyHealth[mapNum][i]);
                    }
                }

                // MAP OBJECTS
                for (int i = 0; i < gp.obj[0].length; i++) {

                    if (ds.mapObjectNames[mapNum][i].equals("NULL")) {
                        gp.obj[mapNum][i] = null;
                    }
                    else if (gp.obj[mapNum][i] != null) {
                        gp.obj[mapNum][i] = (Object) gp.eGenerator.getEntity(
                                ds.mapObjectNames[mapNum][i]
                        );

                        gp.obj[mapNum][i].setWorldPoint(new Point(
                                ds.mapObjectWorldX[mapNum][i],
                                ds.mapObjectWorldY[mapNum][i])
                        );

                        gp.obj[mapNum][i].setDirection(GamePanel.Direction.valueOf(ds.mapObjectDirections[mapNum][i]));
                    }
                }
            }

            ois.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}