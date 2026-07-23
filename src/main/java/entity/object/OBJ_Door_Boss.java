package entity.object;

import application.GamePanel;
import entity.Entity;

public class OBJ_Door_Boss extends Object {

    public static final String objName = "Door_Boss";

    public OBJ_Door_Boss(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
        animationSpeed = 10;
    }

    @Override
    protected void getImages() {
        sprite = down1 = setupImage("/objects/obj_door_boss_down_1");
        down2 = setupImage("/objects/obj_door_boss_down_2");
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
        boolean userHasKey = user.getHasBossKey();

        if (userFacing && userHasKey) {
            user.setHasBossKey(false);
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

    @Override
    public void rotate() {

    }
}
