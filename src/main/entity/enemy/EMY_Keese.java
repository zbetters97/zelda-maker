package entity.enemy;

import application.GamePanel;

public class EMY_Keese extends Enemy {

    public static final String emyName = "Keese";

    public EMY_Keese(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, emyName);

        animationSpeed = 5;

        maxHealth = 4;
        health = maxHealth;

        defaultSpeed = 2;
        speed = defaultSpeed;
    }

    @Override
    protected void getImages() {
        up1 = down1 = left1 = right1 = setupImage("/enemy/keese_down_1");
        up2 = down2 = left2 = right2 = setupImage("/enemy/keese_down_2");
    }

    @Override
    protected void setAction() {
        setDirection(25);
    }
}