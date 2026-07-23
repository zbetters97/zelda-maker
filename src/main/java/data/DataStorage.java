package data;

import java.io.Serial;
import java.io.Serializable;

public class DataStorage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // FILE INFO
    String file_date;

    // PLAYER DATA
    int pWorldX, pWorldY;
    String direction;
    int maxHealth, health, maxRupees, rupees, maxArrows, arrows, maxBombs, bombs, keys;
    boolean hasBossKey;
    String[] items;
    int currentItemSlot;

    // NPCs
    String[] npcNames, npcDirections;
    int[] npcWorldX, npcWorldY;

    // ENEMIES
    String[] enemyNames, enemyDirections, enemyLoot;
    int[] enemyWorldX, enemyWorldY, enemyHealth;

    // OBJECTS
    String[] objectNames, objectDirections, objectLoot;
    int[] objectWorldX, objectWorldY;

    // TILES
    int[] tileNums;
}