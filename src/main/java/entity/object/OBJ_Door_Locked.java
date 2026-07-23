package entity.object;

import application.GamePanel;
import entity.Entity;

public class OBJ_Door_Locked extends Object {

    public static final String objName = "Door_Locked";

    public OBJ_Door_Locked(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
        animationSpeed = 10;
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/objects/obj_door_locked_up_1");
        up2 = setupImage("/objects/obj_door_locked_up_2");
        sprite = down1 = setupImage("/objects/obj_door_locked_down_1");
        down2 = setupImage("/objects/obj_door_locked_down_2");
        left1 = setupImage("/objects/obj_door_locked_left_1");
        left2 = setupImage("/objects/obj_door_locked_left_2");
        right1 = setupImage("/objects/obj_door_locked_right_1");
        right2 = setupImage("/objects/obj_door_locked_right_2");
    }

    @Override
    public void update() {
        if (opened) {
            cycleSprites();
        }
    }

    @Override
    public void interact(Entity user) {
        if (opened || !gp.keyH.aPressed) return;
        gp.keyH.aPressed = false;

        boolean userFacing = user.getDirection() == getOppositeDirection(direction);
        boolean userHasKey = user.getKeys() > 0;

        if (userFacing && userHasKey) {
            user.addKeys(-1);
            opened = true;
        }
    }

    @Override
    protected void cycleSprites() {

        if (animationSpeed < ++spriteCounter) {

            if (spriteNum == 1) {
                spriteNum = 2;
            }
            else if (spriteNum == 2) {
                alive = false;
            }

            spriteCounter = 0;
        }
    }
}
