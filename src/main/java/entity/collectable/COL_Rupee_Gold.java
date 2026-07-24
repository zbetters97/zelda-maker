package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Rupee_Gold extends Collectable {

    public static final String colName = "Rupee_Gold";

    public COL_Rupee_Gold(GamePanel gp) {
        super(gp, colName);
        formattedName = "a gold rupee";
        value = 300;
        description = "That's worth " + value + " rupees! YOU'RE RICH!!!";
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_rupee_gold", 38, 38);
    }

    @Override
    public void use(Entity user) {
        gp.ui.addRupees(value);
        alive = false;
    }
}
