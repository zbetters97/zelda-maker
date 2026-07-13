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
    int maxHealth, health, attack;

    // ENEMIES
    int[] enemyWorldX, enemyWorldY, enemyHealth;
    boolean[] enemyAlive;

    // MAP OBJECTS
    String[] mapObjectNames, mapObjectDirections;
    int[] mapObjectWorldX, mapObjectWorldY;
}