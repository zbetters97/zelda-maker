package entity.collectable;

import application.GamePanel;

public class COL_Rupee_Purple extends Rupee {

    public static final String colName = "Rupee_Purple";

    public COL_Rupee_Purple(GamePanel gp) {
        super(gp, colName, 50, "Wow!");
        formattedName = "a purple rupee";
    }
}
