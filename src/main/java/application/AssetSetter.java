package application;

import entity.enemy.*;
import entity.object.*;

import java.awt.*;

public record AssetSetter(GamePanel gp) {

    public void setup() {
        setEnemies();
        setObjects();
    }

    private void setEnemies() {
        int mapNum = 0;
        int i = 0;

        gp.enemy[mapNum][i] = new EMY_Wizrobe(gp, 24, 20);
    }

    private void setObjects() {

        int mapNum = 0;
        int i = 0;

        gp.obj[mapNum][i] = new OBJ_Chest(gp, 14, 21);
    }
}