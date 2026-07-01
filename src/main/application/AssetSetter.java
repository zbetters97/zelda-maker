package application;

import entity.enemy.EMY_Goblin_Combat;
import entity.enemy.EMY_Keese;
import entity.npc.NPC_OldMan;
import entity.object.OBJ_Chest;

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

        gp.enemy[mapNum][i] = new EMY_Keese(gp, 26, 20);
        i++;
        gp.enemy[mapNum][i] = new EMY_Goblin_Combat(gp, 26, 22);
    }

    private void setObjects() {
        int mapNum = 0;
        int i = 0;

        gp.obj[mapNum][i] = new OBJ_Chest(gp, 12, 21);
    }
}