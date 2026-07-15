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
        npcFactory.put(NPC_Farmer.npcName, () -> new NPC_Farmer(gp, 0, 0));
        npcFactory.put(NPC_Merchant.npcName, () -> new NPC_Merchant(gp, 0, 0));
        npcFactory.put(NPC_OldMan.npcName, () -> new NPC_OldMan(gp, 0, 0));

        // Enemies
        enemyFactory.put(EMY_Beamos.emyName, () -> new EMY_Beamos(gp, 0, 0));
        enemyFactory.put(EMY_Beetle.emyName, () -> new EMY_Beetle(gp, 0, 0));
        enemyFactory.put(EMY_Buzzblob.emyName, () -> new EMY_Buzzblob(gp, 0, 0));
        enemyFactory.put(EMY_ChuChu_Green.emyName, () -> new EMY_ChuChu_Green(gp, 0, 0));
        enemyFactory.put(EMY_ChuChu_Red.emyName, () -> new EMY_ChuChu_Red(gp, 0, 0));
        enemyFactory.put(EMY_Goblin_Archer.emyName, () -> new EMY_Goblin_Archer(gp, 0, 0));
        enemyFactory.put(EMY_Goblin_Boomerang.emyName, () -> new EMY_Goblin_Boomerang(gp, 0, 0));
        enemyFactory.put(EMY_Goblin_Combat.emyName, () -> new EMY_Goblin_Combat(gp, 0, 0));
        enemyFactory.put(EMY_Keese.emyName, () -> new EMY_Keese(gp, 0, 0));
        enemyFactory.put(EMY_Octorok.emyName, () -> new EMY_Octorok(gp, 0, 0));
        enemyFactory.put(EMY_Stalfos.emyName, () -> new EMY_Stalfos(gp, 0, 0));
        enemyFactory.put(EMY_Tektite.emyName, () -> new EMY_Tektite(gp, 0, 0));
        enemyFactory.put(EMY_Wizrobe.emyName, () -> new EMY_Wizrobe(gp, 0, 0));
        enemyFactory.put(EMY_Zora.emyName, () -> new EMY_Zora(gp, 0, 0));

        // Objects
        objectFactory.put(OBJ_Block_Blue.objName, () -> new OBJ_Block_Blue(gp, 0, 0));
        objectFactory.put(OBJ_Block_Red.objName, () -> new OBJ_Block_Red(gp, 0, 0));
        objectFactory.put(OBJ_Chest.objName, () -> new OBJ_Chest(gp, 0, 0));
        objectFactory.put(OBJ_Cucco.objName, () -> new OBJ_Cucco(gp, 0, 0));
        objectFactory.put(OBJ_Door_Oneway.objName, () -> new OBJ_Door_Oneway(gp, 0, 0));
        objectFactory.put(OBJ_Pot.objName, () -> new OBJ_Pot(gp, 0, 0));
        objectFactory.put(OBJ_Switch.objName, () -> new OBJ_Switch(gp, 0, 0));

        // Collectables
        collectableFactory.put(COL_Arrow.colName, () -> new COL_Arrow(gp));
        collectableFactory.put(COL_Heart.colName, () -> new COL_Heart(gp));
        collectableFactory.put(COL_Rupee_Blue.colName, () -> new COL_Rupee_Blue(gp));
        collectableFactory.put(COL_Rupee_Green.colName, () -> new COL_Rupee_Green(gp));
        collectableFactory.put(COL_Rupee_Red.colName, () -> new COL_Rupee_Red(gp));
    }

    public Entity getEntity(String eName) {

        Entity entity = getFromFactory(npcFactory, eName);
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
