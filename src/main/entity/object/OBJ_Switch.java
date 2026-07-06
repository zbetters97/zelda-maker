package entity.object;

import application.GamePanel;

public class OBJ_Switch extends Object {

    public static final String objName = "Switch";

    public OBJ_Switch(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/objects/obj_switch_off");
        up2 = setupImage("/objects/obj_switch_on");
    }

    @Override
    public void interact() {
        opened = !opened;
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
