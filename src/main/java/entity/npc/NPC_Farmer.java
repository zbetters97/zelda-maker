package entity.npc;

import application.GamePanel;
import entity.Entity;

public class NPC_Farmer extends NPC {

    public static final String npcName = "Farmer";

    public NPC_Farmer(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, npcName);
        availableAction = "TALK";
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/npc/farmer_up_1");
        up2 = setupImage("/npc/farmer_up_2");
        sprite = down1 = setupImage("/npc/farmer_down_1");
        down2 = setupImage("/npc/farmer_down_2");
        left1 = setupImage("/npc/farmer_left_1");
        left2 = setupImage("/npc/farmer_left_2");
        right1 = setupImage("/npc/farmer_right_1");
        right2 = setupImage("/npc/farmer_right_2");
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
        System.out.println("Have you seen my Cucco, " + user.getName() + "?!");
    }
}