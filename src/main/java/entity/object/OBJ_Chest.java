package entity.object;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

import static application.GamePanel.Direction.*;

public class OBJ_Chest extends Object {

    public static final String objName = "Chest";

    public OBJ_Chest(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
        latchable = true;

        hitbox = new Rectangle(4, 16, 40, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;

        availableAction = "OPEN";
    }

    @Override
    protected void getImages() {
        sprite = up1 = setupImage("/objects/obj_chest_closed");
        up2 = setupImage("/objects/obj_chest_opened");
    }

    @Override
    public boolean canCollideWith(Entity target) {
        return !target.getElevated();
    }

    @Override
    public void interact(Entity user) {
        if (opened || !gp.keyH.aPressed) {
            return;
        }

        if (user.getDirection() == UP || user.getDirection() == UPLEFT || user.getDirection() == UPRIGHT) {
            opened = true;
            availableAction = "";
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

    @Override
    public String getAvailableAction(Entity user) {

        if (user.getDirection() != UP) {
            return "";
        }

        return availableAction;
    }
}
