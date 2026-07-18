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

    /*
    public void save()  {

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveDir));

            // SAVE DATA TO DS
            DataStorage ds = new DataStorage();

            // CURRENT DATE/TIME
            ds.file_date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));

            // PLAYER DATA
            ds.pWorldX = gp.player.getWorldPoint().x;
            ds.pWorldY = gp.player.getWorldPoint().y;

            ds.direction = gp.player.getDirection().toString();
            ds.maxHealth = gp.player.getMaxHealth();
            ds.health = gp.player.getHealth();
            ds.attack = gp.player.getAttack();

            // ENEMIES
            ds.enemyWorldX = new int[gp.enemy.length];
            ds.enemyWorldY = new int[gp.enemy.length];
            ds.enemyHealth = new int[gp.enemy.length];
            ds.enemyAlive = new boolean[gp.enemy.length];

            // MAP OBJECTS
            ds.mapObjectNames = new String[gp.obj.length];
            ds.mapObjectWorldX = new int[gp.obj.length];
            ds.mapObjectWorldY = new int[gp.obj.length];
            ds.mapObjectDirections = new String[gp.obj.length];

           
            // ENEMIES
            for (int i = 0; i < gp.enemy.length; i++) {
                if (gp.enemy[i] == null) {
                    ds.enemyAlive[i] = false;
                }
                else {
                    ds.enemyWorldX[i] = gp.enemy[i].getWorldPoint().x;
                    ds.enemyWorldY[i] = gp.enemy[i].getWorldPoint().y;
                    ds.enemyHealth[i] = gp.enemy[i].getHealth();
                    ds.enemyAlive[i] = true;
                }
            }

            // MAP OBJECTS
            for (int i = 0; i < gp.obj.length; i++) {

                if (gp.obj[i] == null) {
                    ds.mapObjectNames[i] = "NULL";
                }
                else {
                    ds.mapObjectNames[i] = gp.obj[i].getName();
                    ds.mapObjectWorldX[i] = gp.obj[i].getWorldPoint().x;
                    ds.mapObjectWorldY[i] = gp.obj[i].getWorldPoint().y;
                    ds.mapObjectDirections[i] = gp.obj[i].getDirection().toString();
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
            gp.player.setWorldPoint(new Point(ds.pWorldX, ds.pWorldY));


            gp.player.setDirection(GamePanel.Direction.valueOf(ds.direction));
            gp.player.setHealth(ds.health);
            gp.player.setAttack(ds.attack);


            // ENEMIES
            for (int i = 0; i < gp.enemy.length; i++) {
                if (!ds.enemyAlive[i]) {
                    gp.enemy[i] = null;
                }
                else if (gp.enemy[i] != null) {
                    gp.enemy[i].setWorldPoint(new Point(ds.enemyWorldX[i], ds.enemyWorldY[i]));
                    gp.enemy[i].setHealth(ds.enemyHealth[i]);
                }
            }

            // MAP OBJECTS
            for (int i = 0; i < gp.obj.length; i++) {

                if (ds.mapObjectNames[i].equals("NULL")) {
                    gp.obj[i] = null;
                }
                else if (gp.obj[i] != null) {
                    gp.obj[i] = (Object) gp.eGenerator.getEntity(
                            ds.mapObjectNames[i]
                    );

                    gp.obj[i].setWorldPoint(new Point(
                            ds.mapObjectWorldX[i],
                            ds.mapObjectWorldY[i])
                    );

                    gp.obj[i].setDirection(GamePanel.Direction.valueOf(ds.mapObjectDirections[i]));
                }
            }


            ois.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

     */
}