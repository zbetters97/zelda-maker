package entity.enemy;

import application.GamePanel;

import java.util.Random;

public class EMY_Keese extends Enemy {

    public static final String emyName = "Keese";

    public EMY_Keese(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 5;

        maxHealth = 4;
        health = maxHealth;

        defaultSpeed = 2;
        speed = defaultSpeed;

        knockbackPower = 0;
    }

    @Override
    protected void getImages() {
        sprite = up1 = setupImage("/enemy/keese_down_1");
        up2 = setupImage("/enemy/keese_down_2");
    }

    @Override
    protected void setAction() {
        setDirection(25);

        // Squeak randomly every 2 seconds
        int i = new Random().nextInt(120);
        if (i == 0) playSqueak();
    }

    @Override
    protected void getSpriteImage() {
        image = spriteNum == 1 ? up1 : up2;
    }

    private void playSqueak() {
        gp.playSE(4, 7);
    }
}