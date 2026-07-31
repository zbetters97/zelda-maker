package entity.collectable;

import application.GamePanel;

public class COL_Rupee_Green extends Rupee {

    public static final String colName = "Rupee_Green";

    public COL_Rupee_Green(GamePanel gp) {
        super(gp, colName, 1, "");
        formattedName = "a green rupee";
    }
}
