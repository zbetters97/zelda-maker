package entity.item;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class Item extends Entity {

    public Item(GamePanel gp, String itmName, Entity user, Action action) {
        super(gp, itmName, user);
        this.action = action;
    }

    public void use() {
        user.setAction(action);
    }

    @Override
    protected void getSpriteImage() {
        image = sprite;
    }
}
