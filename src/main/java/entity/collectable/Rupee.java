package entity.collectable;

import application.GamePanel;
import entity.Entity;

public class Rupee extends Collectable {

    public Rupee(GamePanel gp, String colName, int value, String description) {
        super(gp, colName);

        this.value = value;
        this.description = value == 1 ?
                "That's worth " + value + " rupee." :
                "That's worth " + value + " rupees! " + description;
    }

    @Override
    protected void getImages() {
        sprite = setupImage("/collectables/col_" + name, 38, 38);
    }

    @Override
    public void use(Entity user) {
        add(user);
        playPickup();
    }

    @Override
    public void add(Entity user) {
        gp.ui.addRupees(value);
        alive = false;
    }

    private void playPickup() {
        gp.playSE(6, 1);
    }
}
