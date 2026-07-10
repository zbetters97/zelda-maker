package entity.object;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class Object extends Entity {

    public Object(GamePanel gp, int worldX, int worldY, String objName) {
        super(gp, worldX, worldY, objName);

        hitbox = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    public void interact(Entity user) {

    }

    public void interact() {

    }
}
