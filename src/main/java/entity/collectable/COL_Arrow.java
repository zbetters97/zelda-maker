package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class COL_Arrow extends Collectable {

    public static final String colName = "Arrow";

    public COL_Arrow(GamePanel gp) {
        super(gp, colName);
        formattedName = "an arrow";
        description = "Aim carefully!";
        value = 1;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_arrow", 38, 38);
    }

    @Override
    public void use(Entity user) {
        add(user);
        playPickup();
    }

    @Override
    public void add(Entity user) {
        user.addArrows(value);
        alive = false;
    }

    private void playPickup() {
        gp.playSE(6, 0);
    }
}
