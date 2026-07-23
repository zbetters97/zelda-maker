package entity.npc;

import application.GamePanel;
import entity.Entity;

public class NPC_Merchant extends NPC {

    public static final String npcName = "Merchant";

    public NPC_Merchant(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, npcName);

        dialogue = "Buy somethin', will ya?";

        animationSpeed = 25;

        defaultSpeed = 0;
        speed = defaultSpeed;

        availableAction = "TALK";
    }

    @Override
    protected void getImages() {
        sprite = down1 = setupImage("/npc/merchant_down_1");
        down2 = setupImage("/npc/merchant_down_2");
    }

    @Override
    public void update() {
        setAction();
    }

    @Override
    protected void setAction() {
        cycleSprites();
    }

    @Override
    public void interact(Entity user) {
        super.interact(user);
    }

    @Override
    protected void getSpriteImage() {
        image = spriteNum == 1 ? down1 : down2;
    }
}