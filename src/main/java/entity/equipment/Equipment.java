package entity.equipment;

import application.GamePanel;
import entity.Entity;

public class Equipment extends Entity {

    public Equipment(GamePanel gp, String eqpName) {
        super(gp, eqpName);
    }

    @Override
    protected void getSpriteImage() {
        image = sprite;
    }

    public void use(Entity user) {

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Equipment other)) return false;
        return this.getName().equals(other.getName());
    }
}
