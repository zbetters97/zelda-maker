package entity.object;

import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class OBJ_Block_Red extends Object {

    public static final String objName = "Block_Red";

    public OBJ_Block_Red(GamePanel gp, int col, int row) {
        super(gp, col, row, objName);
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/objects/obj_block_red_on");
        sprite = up2 = setupImage("/objects/obj_block_red_off");
    }

    @Override
    public void update() {
        detectSwitch();
    }

    /**
     * DETECT SWITCHES
     * Changes collision based on if a switch is activated on the same map
     */
    private void detectSwitch() {

        for (int i = 0; i < gp.obj.length; i++) {

            // Find switch in objects list
            if (gp.obj[i] != null && gp.obj[i].getName().equals(OBJ_Switch.objName)) {

                // Turn switch on if off
                if (!gp.obj[i].getOpened()) {
                    opened = false;
                    collisionOn = false;
                }
                // Turn off if no collision
                else if (noOverlap()) {
                    opened = true;
                    collisionOn = true;
                }
            }
        }
    }

    private boolean noOverlap() {
        int npc = gp.cChecker.checkOverlapCollision(this, gp.npc);
        int enemy = gp.cChecker.checkOverlapCollision(this, gp.enemy);
        boolean contactPlayer = gp.cChecker.checkPlayer(this);

        return npc == -1 && enemy == -1 && !contactPlayer;
    }

    /**
     * CAN COLLIDE WITH
     * Checks if entity hits block
     * Called by checkCollision()
     * Skips collision if entity is in air
     * @param target Entity interacting with block
     * @return True if entity collides with block
     */
    @Override
    public boolean canCollideWith(Entity target) {
        return collisionOn && !target.getElevated();
    }

    @Override
    protected void getSpriteImage() {
        if (opened) {
            image = up1;
        }
        else {
            image = up2;
        }
    }
}