package application;

import entity.enemy.*;
import entity.npc.NPC_OldMan;
import entity.object.*;

import java.awt.*;

public record AssetSetter(GamePanel gp) {

    public void setup() {
        setNPCs();
        setEnemies();
        setObjects();
    }

    private void setNPCs() {

        int mapNum = 0;
        int i = 0;

        gp.npc[mapNum][i] = new NPC_OldMan(gp, 30, 26);
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