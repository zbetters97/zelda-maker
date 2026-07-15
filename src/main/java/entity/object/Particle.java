package entity.object;

import application.GamePanel;

import java.awt.*;
import java.util.Arrays;

public class Particle extends Object {

    private final Color color;
    private final int size;
    private final int xd;
    private int yd;

    public Particle(GamePanel gp, Point worldPoint, int maxHealth, int speed, Color color, int size, int xd, int yd) {
        super(gp, 0, 0, "");

        this.maxHealth = maxHealth;
        this.speed = speed;
        this.color = color;
        this.size = size;

        this.xd = xd;
        this.yd = yd;

        health = maxHealth;

        int offset = (gp.tileSize / 2) - (size / 2);
        this.worldPoint.setLocation(
                worldPoint.x + offset,
                worldPoint.y + offset
        );
    }

    public static void generateParticles(GamePanel gp, Point worldPoint, int maxHealth, int speed, Color color, int size) {

        Particle p1 = new Particle(gp, worldPoint, maxHealth, speed, color, size, -2, -1);
        Particle p2 = new Particle(gp, worldPoint, maxHealth, speed, color, size, -2, 1);
        Particle p3 = new Particle(gp, worldPoint, maxHealth, speed, color, size, 2, -1);
        Particle p4 = new Particle(gp, worldPoint, maxHealth, speed, color, size, 2, 1);

        gp.particleList.addAll(Arrays.asList(p1, p2, p3, p4));
    }

    @Override
    public void update() {

        if (--health < maxHealth / 3) {
            yd++;
        }

        worldPoint.x += xd * speed;
        worldPoint.y += yd * speed;

        if (health == 0) {
            alive = false;
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        g2.setColor(color);
        gp.camera.worldToScreen(worldPoint, screenPoint);
        g2.fillRect(screenPoint.x, screenPoint.y, size, size);
    }
}