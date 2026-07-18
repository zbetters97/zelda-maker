package entity.object;

import application.GamePanel;
import entity.Entity;
import entity.enemy.Enemy;
import entity.npc.NPC;

import java.awt.*;

public class OBJ_Block_Blue extends Object {

    public static final String objName = "Block_Blue";

    public OBJ_Block_Blue(GamePanel gp, int col, int row) {
        super(gp, col, row, objName);
        collisionOn = false;
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/objects/obj_block_blue_on");
        sprite = up2 = setupImage("/objects/obj_block_blue_off");
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

        for (Object object : gp.objects) {

            // Find switch in objects list
            boolean isSwitch = object != null && object.getName().equals(OBJ_Switch.objName);
            if (isSwitch) {
                // Turn switch off if on
                if (object.getOpened()) {
                    opened = false;
                    collisionOn = false;
                    return;
                }
                // Turn on if no collision
                else if (noOverlap()) {
                    opened = true;
                    collisionOn = true;
                    return;
                }
            }
        }
    }

    private boolean noOverlap() {
        NPC npc = gp.cChecker.checkOverlapCollision(this, gp.npcs);
        Enemy enemy = gp.cChecker.checkOverlapCollision(this, gp.enemies);
        boolean contactPlayer = gp.cChecker.checkPlayer(this);

        return npc == null && enemy == null && !contactPlayer;
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
        image = opened ? up1 : up2;
    }

    @Override
    public DrawLayer getDrawLayer() {
        return collisionOn ? DrawLayer.ENTITY : DrawLayer.GROUND;
    }
}