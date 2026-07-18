package application;

import entity.object.OBJ_Pot;

public record AssetSetter(GamePanel gp) {

    public void setup() {
        gp.objects.add(new OBJ_Pot(gp, 1, 3));
    }
}