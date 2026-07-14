package entity.item;

import application.GamePanel;
import entity.Entity;
import entity.object.OBJ_Bomb;
import entity.object.Object;

public class ITM_Bomb extends Item {

    public static final String itmName = "Item Bomb";

    public ITM_Bomb(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.GRABBING);
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_bomb");
    }

    @Override
    protected void use() {

        if (0 < user.getBombs() && user.getGrabbedObject() == null) {

            Object bomb = new OBJ_Bomb(gp, user.getWorldPoint().x, user.getWorldPoint().y);
            addBomb(bomb);

            user.addBombs(-1);
            user.setGrabbedObject(bomb);

            super.use();
        }
    }

    private void addBomb(Object bomb) {
        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) {
                gp.obj[i] = bomb;
                break;
            }
        }
    }
}