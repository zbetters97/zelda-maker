package entity.object;

import application.GamePanel;
import entity.Entity;

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
    public boolean canCollideWith(Entity target) {
        return !target.getElevated();
    }

    @Override
    public void interact() {
        if (!invincible) {
            opened = !opened;
            flipSwitches();
            invincible = true;
        }
    }
    
    private void flipSwitches() {
        for (int i = 0; i < gp.obj.length; i++) {

            // Find other switches in object list
            if (gp.obj[i] != null && gp.obj[i].getName().equals(name)) {
                gp.obj[i].setOpened(opened);
                gp.obj[i].setInvincible(true);
            }
        }
    }

    @Override
    protected void manageValues() {
        if (30 < ++invincibleCounter) {
            invincibleCounter = 0;
            invincible = false;
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
