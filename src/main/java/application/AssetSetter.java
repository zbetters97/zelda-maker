package application;

import entity.object.OBJ_Pot;

public record AssetSetter(GamePanel gp) {

    public void setup() {

        gp.obj[0] = new OBJ_Pot(gp, 1, 3);
    }
}