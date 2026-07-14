package entity.object;

import application.GamePanel;
import entity.Entity;

public class OBJ_Door_Oneway extends Object {

    public static final String objName = "Oneway";

    public OBJ_Door_Oneway(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, objName);
        getTurnImages();
    }

    @Override
    protected void getImages() {
        up1 = setupImage("/objects/obj_oneway_open_up");
        up2 = setupImage("/objects/obj_oneway_closed_up");
        sprite = down1 = setupImage("/objects/obj_oneway_open_down");
        down2 = setupImage("/objects/obj_oneway_closed_down");
    }
    private void getTurnImages() {
        attackUp1 = setupImage("/objects/obj_oneway_open_up_1");
        attackUp2 = setupImage("/objects/obj_oneway_open_up_2");
        attackUp3 = setupImage("/objects/obj_oneway_open_up_3");
        attackDown1 = setupImage("/objects/obj_oneway_open_down_1");
        attackDown2 = setupImage("/objects/obj_oneway_open_down_2");
        attackDown3 = setupImage("/objects/obj_oneway_open_down_3");
    }

    @Override
    public void update() {
        if (opened) {
            cycleSprites();
        }
    }

    @Override
    public void interact(Entity user) {

        // User walks into door
        if (user.getDirection() == getOppositeDirection(direction)) {
            user.setCanMove(false);
            user.setDrawing(false);
            this.user = user;
            opened = true;
        }
    }

    @Override
    protected void cycleSprites() {

        if (++spriteCounter <= 15) {
            spriteNum = 1;
        }
        else if (spriteCounter <= 30) {
            spriteNum = 2;
        }
        else if (spriteCounter <= 45) {
            spriteNum = 3;
        }
        else {
            spriteNum = 1;
            spriteCounter = 0;

            opened = false;
            letUserThrough();
        }
    }

    private void letUserThrough() {

        user.setWorldPointX(worldPoint.x);

        // Shift player past door
        switch (direction) {
            case UP -> user.setWorldPointY(worldPoint.y + gp.tileSize);
            case DOWN -> user.setWorldPointY(worldPoint.y - gp.tileSize);
        }

        resetValues();
    }

    @Override
    public void resetValues() {
        opened = false;
        spriteNum = 1;
        spriteCounter = 0;
        user.setCanMove(true);
        user.setDrawing(true);
    }

    @Override
    protected void getSpriteImage() {
        if (opened) {
            getOpeningSprite();
        }
        else {
            getIdleSprite();
        }
    }

    private void getOpeningSprite() {
        if (spriteNum == 1) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> attackUp1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown1;
                case LEFT -> attackLeft1;
                case RIGHT -> attackRight1;
            };
        }
        else if (spriteNum == 2) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> attackUp2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown2;
                case LEFT -> attackLeft2;
                case RIGHT -> attackRight2;
            };
        }
        else {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> attackUp3;
                case DOWN, DOWNLEFT, DOWNRIGHT -> attackDown3;
                case LEFT -> attackLeft3;
                case RIGHT -> attackRight3;
            };
        }
    }

    private void getIdleSprite() {
        if (spriteNum == 1) {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up1;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down1;
                case LEFT -> left1;
                case RIGHT -> right1;
            };
        }
        else {
            image = switch (direction) {
                case UP, UPLEFT, UPRIGHT -> up2;
                case DOWN, DOWNLEFT, DOWNRIGHT -> down2;
                case LEFT -> left2;
                case RIGHT -> right2;
            };
        }
    }
}
