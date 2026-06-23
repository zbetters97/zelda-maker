package entity.object_interactive;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

import static application.GamePanel.Direction.*;

public class OBJ_I_Chest extends Entity {

    public static final String objName = "Chest";

    public OBJ_I_Chest(GamePanel gp, int worldX, int worldY) {
        super(gp);
        this.worldX = worldX * gp.tileSize;
        this.worldY = worldY * gp.tileSize;

        type = type_object_i;
        name = objName;

        grabbable = true;
        direction = GamePanel.Direction.DOWN;

        hitbox = new Rectangle(4, 16, 40, 32);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    public void getImages() {
        down1 = setupImage("/objects_interactive/obj_chest_closed");
        down2 = setupImage("/objects_interactive/obj_chest_opened");
    }

    protected void interact(Entity user) {
        if (user.direction == UP || user.direction == UPLEFT || user.direction == UPRIGHT) {
            System.out.println("called!");
        }
    }
}
