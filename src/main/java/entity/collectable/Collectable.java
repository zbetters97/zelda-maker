package entity.collectable;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class Collectable extends Entity {

    public Collectable(GamePanel gp, String colName) {
        super(gp, colName);

        entity_type = type_collectable;

        hitbox = new Rectangle(0, 0, 48, 48);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    public void use(Entity user) {

    }

    @Override
    protected void getSpriteImage() {
        image = sprite;
    }
}
