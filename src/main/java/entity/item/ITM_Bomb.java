package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.object.OBJ_Bomb;
import entity.object.Object;

public class ITM_Bomb extends Item {

    public static final String itmName = "Bomb";

    public ITM_Bomb(GamePanel gp, Entity owner) {
        super(gp, itmName, owner, Action.GRABBING);
        setUser(owner);
        formattedName = "a Bomb Bag";
        description = "Press X to grab a bomb.\nPress A to throw or pick one up.";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_bomb");
    }

    @Override
    public void setUser(Entity user) {
        if (user == null) return;

        super.setUser(user);
        user.setMaxBombs(30);
        user.setBombs(30);
    }

    @Override
    public void use() {

        if (user.getBombs() <= 0) return;

        Object bomb = new OBJ_Bomb(gp, -gp.tileSize, -gp.tileSize);
        gp.objects.add(bomb);

        user.addBombs(-1);
        user.grab(bomb);

        super.use();
    }
}