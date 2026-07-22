package entity.npc;

import application.GamePanel;
import entity.Entity;

public class NPC_Kid extends NPC {

    public static final String npcName = "Kid";

    public NPC_Kid(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, npcName);
        availableAction = "TALK";
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/npc/kid_up_1");
        up2 = setupImage("/npc/kid_up_2");
        sprite = down1 = setupImage("/npc/kid_down_1");
        down2 = setupImage("/npc/kid_down_2");
        left1 = setupImage("/npc/kid_left_1");
        left2 = setupImage("/npc/kid_left_2");
        right1 = setupImage("/npc/kid_right_1");
        right2 = setupImage("/npc/kid_right_2");
    }

    @Override
    public void update() {
        setAction();
    }

    @Override
    protected void setAction() {
        setDirection(60);
    }

    @Override
    public void interact(Entity user) {
        super.interact(user);
        System.out.println("You look stupid!");
    }
}