package entity.collectable;

import application.GamePanel;

public class COL_Rupee_Silver extends Rupee {

    public static final String colName = "Rupee_Silver";

    public COL_Rupee_Silver(GamePanel gp) {
        super(gp, colName, 100, "Jackpot!");
        formattedName = "a silver rupee";
    }
}
