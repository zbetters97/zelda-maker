package entity.object;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

import static application.GamePanel.Direction.*;

public class OBJ_Chest extends Entity {

    public static final String objName = "Chest";

    public OBJ_Chest(GamePanel gp, int worldX, int worldY) {
        super(gp);
        this.worldX = worldX * gp.tileSize;
        this.worldY = worldY * gp.tileSize;

        entity_type = type_object;
        name = objName;

        grabbable = true;
        direction = GamePanel.Direction.DOWN;

        hitbox = new Rectangle(4, 16, 40, 32);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/objects/obj_chest_closed");
        up2 = setupImage("/objects/obj_chest_opened");
    }

    @Override
    protected void interact(Entity user) {
        if (opened) {
            return;
        }

        if (user.getDirection() == UP || user.getDirection() == UPLEFT || user.getDirection() == UPRIGHT) {
            opened = true;
        }
    }

    @Override
    protected void getSpriteImage() {
        if (opened) {
            image = up2;
        }
        else {
            image = up1;
        }
    }
}
