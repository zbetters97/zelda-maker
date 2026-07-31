package entity.collectable;

import application.GamePanel;

public class COL_Rupee_Red extends Rupee {

    public static final String colName = "Rupee_Red";

    public COL_Rupee_Red(GamePanel gp) {
        super(gp, colName, 20, "Nice!");
        formattedName = "a red rupee";
    }
}
