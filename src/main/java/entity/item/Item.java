package entity.item;

import application.GamePanel;
import entity.Entity;

public class Item extends Entity {

    public Item(GamePanel gp, String itmName, Action action) {
        super(gp, itmName);
        this.action = action;
    }
    public Item(GamePanel gp, String itmName, Entity owner, Action action) {
        super(gp, itmName, owner);
        this.action = action;
    }

    public void use() {
        user.setAction(action);
        playSoundEffect();
    }

    @Override
    protected void getSpriteImage() {
        image = sprite;
    }

    protected void playSoundEffect() {

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Item other)) return false;
        return this.getName().equals(other.getName());
    }
}
