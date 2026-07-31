package entity.collectable;

import application.GamePanel;

public class COL_Rupee_Blue extends Rupee {

    public static final String colName = "Rupee_Blue";

    public COL_Rupee_Blue(GamePanel gp) {
        super(gp, colName, 5, "Not too shabby.");
        formattedName = "a blue rupee";
    }
}
