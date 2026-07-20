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
        sprite = up1 = setupImage("/objects/obj_switch_off");
        up2 = setupImage("/objects/obj_switch_on");
    }

    @Override
    public void update() {
        manageValues();
    }

    @Override
    public boolean canCollideWith(Entity target) {
        return !target.getElevated();
    }

    @Override
    public void interact() {

        // Hit by entity, activate
        if (!invincible) {
            opened = !opened;
            invincible = true;
            flipSwitches();
        }
    }
    
    private void flipSwitches() {

        // Find other switches in object list
        for (Object object : gp.objects) {

            boolean isSwitch = object != null && object.getName().equals(name);
            if (isSwitch) {
                object.setOpened(opened);
                object.setInvincible(true);
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
        image = opened ? up2 : up1;
    }
}
