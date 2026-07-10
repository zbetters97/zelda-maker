package data;

import application.GamePanel;
import entity.Entity;
import entity.collectable.*;
import entity.enemy.*;
import entity.npc.*;
import entity.object.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EntityGenerator {

    public final Map<String, Supplier<Entity>> npcFactory = new LinkedHashMap<>();
    public final Map<String, Supplier<Entity>> enemyFactory = new LinkedHashMap<>();
    public final Map<String, Supplier<Entity>> objectFactory = new LinkedHashMap<>();
    public final Map<String, Supplier<Entity>> collectableFactory = new LinkedHashMap<>();

    public EntityGenerator(GamePanel gp) {

        // NPCs
        npcFactory.put(NPC_OldMan.npcName, () -> new NPC_OldMan(gp, 0, 0));

        // Enemies
        enemyFactory.put(EMY_Beamos.emyName, () -> new EMY_Beamos(gp, 0, 0));
        enemyFactory.put(EMY_Beetle.emyName, () -> new EMY_Beetle(gp, 0, 0));
        enemyFactory.put(EMY_Buzzblob.emyName, () -> new EMY_Buzzblob(gp, 0, 0));

        // Objects
        objectFactory.put(OBJ_Block_Blue.objName, () -> new OBJ_Block_Blue(gp, 0, 0));
        objectFactory.put(OBJ_Block_Red.objName, () -> new OBJ_Block_Red(gp, 0, 0));
        objectFactory.put(OBJ_Chest.objName, () -> new OBJ_Chest(gp, 0, 0));

        // Collectables
        collectableFactory.put(COL_Heart.colName, () -> new COL_Heart(gp));
        collectableFactory.put(COL_Rupee_Blue.colName, () -> new COL_Rupee_Blue(gp));
    }

    public Entity getEntity(String eName) {

        Entity entity;

        entity = getFromFactory(npcFactory, eName);
        if (entity != null) return entity;

        entity = getFromFactory(enemyFactory, eName);
        if (entity != null) return entity;

        entity = getFromFactory(objectFactory, eName);
        if (entity != null) return entity;

        entity = getFromFactory(collectableFactory, eName);
        return entity;
    }

    private Entity getFromFactory(Map<String, Supplier<Entity>> factory, String name) {
        Supplier<Entity> supplier = factory.get(name);
        return supplier == null ? null : supplier.get();
    }
}
