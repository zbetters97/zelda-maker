package entity.npc;

import ai.EntityAI;
import application.GamePanel;
import entity.Entity;

import java.awt.*;

public class NPC extends Entity {

    public NPC(GamePanel gp, int worldX, int worldY, String npcName) {
        super(gp, worldX, worldY, npcName);

        animationSpeed = 15;

        defaultSpeed = 1;
        speed = defaultSpeed;

        ai = new EntityAI(gp, this);

        hitbox = new Rectangle(8, 16, 32, 32);
        hitboxDefaultPoint.setLocation(hitbox.x, hitbox.y);
        hitboxDefaultWidth = hitbox.width;
        hitboxDefaultHeight = hitbox.height;
    }

    public void interact(Entity user) {
        gp.keyH.aPressed = false;
        direction = getOppositeDirection(user.getDirection());
    }
}
