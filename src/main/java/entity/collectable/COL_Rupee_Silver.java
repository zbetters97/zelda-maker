package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Rupee_Silver extends Collectable {

    public static final String colName = "Rupee_Silver";

    public COL_Rupee_Silver(GamePanel gp) {
        super(gp, colName);
        formattedName = "a silver rupee";
        value = 100;
        description = "That's worth " + value + " rupees! Jackpot!";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_rupee_silver", 38, 38);
    }

    @Override
    public void use(Entity user) {
        gp.ui.addRupees(value);
        alive = false;
        playPickup();
    }

    private void playPickup() {
        gp.playSE(6, 1);
    }
}
