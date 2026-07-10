package entity.npc;

import application.GamePanel;

public class NPC_OldMan extends NPC {

    public static final String npcName = "Old_Man";

    public NPC_OldMan(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, npcName);
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/npc/oldman_up_1");
        up2 = setupImage("/npc/oldman_up_2");
        sprite = down1 = setupImage("/npc/oldman_down_1");
        down2 = setupImage("/npc/oldman_down_2");
        left1 = setupImage("/npc/oldman_left_1");
        left2 = setupImage("/npc/oldman_left_2");
        right1 = setupImage("/npc/oldman_right_1");
        right2 = setupImage("/npc/oldman_right_2");
    }

    @Override
    protected void setAction() {
        setDirection(60);
    }
}