package entity.equipment;

import application.GamePanel;
import entity.Entity;

public class EQP_Flippers extends Equipment {

    public static final String eqpName = "Flippers";

    public EQP_Flippers(GamePanel gp) {
        super(gp, eqpName);
        formattedName = "the Zora Flippers";
        description = "Now you can swim like a Zora!\nPress A to dive!";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/equipment/eqp_flippers");
    }

    public void use(Entity user) {
        alive = false;
        user.setCanSwim(true);
    }
}