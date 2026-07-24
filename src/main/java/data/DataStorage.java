package data;

import java.io.Serial;
import java.io.Serializable;

public class DataStorage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // FILE INFO
    public String file_date;

    // PLAYER
    public int pWorldX, pWorldY;
    public String direction;
    public int maxHealth, health, maxRupees, rupees, maxArrows, arrows, maxBombs, bombs, keys;
    public boolean hasBossKey;
    public String[] items;
    public int currentItemSlot;

    // TILES
    public int[] tileNums;

    // NPCs
    public String[] npcNames, npcDirections, npcLoot;
    public int[] npcWorldX, npcWorldY;

    // ENEMIES
    public String[] enemyNames, enemyDirections, enemyLoot;
    public int[] enemyWorldX, enemyWorldY, enemyHealth;

    // OBJECTS
    public String[] objectNames, objectDirections, objectLoot;
    public int[] objectWorldX, objectWorldY;
}