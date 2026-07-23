package entity.object;

import application.GamePanel;
import entity.Entity;
import entity.item.ITM_Bow;

import java.awt.*;

import static application.GamePanel.Direction.*;

public class OBJ_Chest extends Object {

    public static final String objName = "Chest";

    public OBJ_Chest(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
        latchable = true;

        item = new ITM_Bow(gp, this);

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

        if (opened || !gp.keyH.aPressed) return;
        gp.keyH.aPressed = false;

        // User must be looking up at chest
        boolean facingDirection = user.getDirection() == UP || user.getDirection() == UPLEFT || user.getDirection() == UPRIGHT;
        if (facingDirection) {
            opened = true;
            availableAction = "";
            user.receiveLoot(loot);
            user.showReward(loot);
        }
    }

    @Override
    public boolean canHoldLoot(Entity loot) {
        return true;
    }

    @Override
    protected void getSpriteImage() {
        image = opened ? up2 : up1;
    }

    @Override
    public String getAvailableAction(Entity user) {

        if (user.getDirection() != UP) {
            return "";
        }

        return availableAction;
    }
}
