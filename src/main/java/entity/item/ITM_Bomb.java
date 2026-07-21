package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.object.OBJ_Bomb;
import entity.object.Object;

public class ITM_Bomb extends Item {

    public static final String itmName = "Bomb";

    public ITM_Bomb(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.GRABBING);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_bomb");
    }

    @Override
    public void use() {

        if (0 < user.getBombs() && user.getGrabbedObject() == null) {

            Object bomb = new OBJ_Bomb(gp, -gp.tileSize, -gp.tileSize);
            gp.objects.add(bomb);

            user.addBombs(-1);
            user.setGrabbedObject(bomb);

            super.use();
        }
    }
}