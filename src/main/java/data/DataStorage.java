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
    int health;

    // ENEMIES
    String[] enemyNames;
    int[] enemyWorldX, enemyWorldY, enemyHealth;

    // OBJECTS
    String[] objectNames, objectDirections;
    int[] objectWorldX, objectWorldY;

    // TILES
    int[] tileNums;
}