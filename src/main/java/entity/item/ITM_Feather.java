package entity.item;

import application.GamePanel;
import entity.Entity;

public class ITM_Feather extends Item {

    public static final String itmName = "Feather";

    public ITM_Feather(GamePanel gp, Entity user) {
        super(gp, itmName, user, Action.JUMPING);
        formattedName = "the Roc's Feather";
        description = "Press X to hop over obstacles!";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/items/itm_feather");
    }

    @Override
    public void use() {
        super.use();
        user.setElevated(true);
    }
}