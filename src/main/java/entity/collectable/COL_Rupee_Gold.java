package entity.collectable;

import application.GamePanel;

public class COL_Rupee_Gold extends Rupee {

    public static final String colName = "Rupee_Gold";

    public COL_Rupee_Gold(GamePanel gp) {
        super(gp, colName, 300, "I'm jealous!");
        formattedName = "a gold rupee";
    }
}
