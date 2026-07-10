package application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    /* GENERAL ATTRIBUTES */
    private boolean lock = true;

    /* BUTTON MAPPING */
    public final int btn_UP = KeyEvent.VK_UP;
    public final int btn_DOWN = KeyEvent.VK_DOWN;
    public final int btn_LEFT = KeyEvent.VK_LEFT;
    public final int btn_RIGHT = KeyEvent.VK_RIGHT;
    public final int btn_A = KeyEvent.VK_A;
    public final int btn_B = KeyEvent.VK_S;
    public final int btn_X = KeyEvent.VK_D;
    public final int btn_Y = KeyEvent.VK_F;
    public final int btn_R = KeyEvent.VK_E;
    public final int btn_L = KeyEvent.VK_W;
    public final int btn_START = KeyEvent.VK_SPACE;

    /* CONFIG VALUES */
    public boolean startPressed, upPressed, downPressed, leftPressed, rightPressed,
            aPressed, bPressed, xPressed, yPressed, rPressed, lPressed;

    /**
     * CONSTRUCTOR
     */
    public KeyHandler() {
    }

    /**
     * KEY TYPED
     * Unused method
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * KEY PRESSED
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); // key pressed by user

        if (code == btn_START) {
            startPressed = true;
        }
        if (code == btn_UP) {
            upPressed = true;
        }
        if (code == btn_DOWN) {
            downPressed = true;
        }
        if (code == btn_LEFT) {
            leftPressed = true;
        }
        if (code == btn_RIGHT) {
            rightPressed = true;
        }
        if (code == btn_A && lock) {
            aPressed = true;
            lock = false;
        }
        if (code == btn_B && lock) {
            bPressed = true;
            lock = false;
        }
        if (code == btn_X && lock) {
            xPressed = true;
            lock = false;
        }
        if (code == btn_Y && lock) {
            yPressed = true;
            lock = false;
        }
        if (code == btn_R && lock) {
            rPressed = true;
            lock = false;
        }
        if (code == btn_L && lock) {
            lPressed = true;
            lock = false;
        }
    }

    /**
     * KEY RELEASED
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == btn_START) {
            startPressed = false;
        }
        if (code == btn_UP) {
            upPressed = false;
        }
        if (code == btn_DOWN) {
            downPressed = false;
        }
        if (code == btn_LEFT) {
            leftPressed = false;
        }
        if (code == btn_RIGHT) {
            rightPressed = false;
        }
        if (code == btn_A) {
            aPressed = false;
            lock = true;
        }
        if (code == btn_B) {
            bPressed = false;
            lock = true;
        }
        if (code == btn_X) {
            xPressed = false;
            lock = true;
        }
        if (code == btn_Y) {
            yPressed = false;
            lock = true;
        }
        if (code == btn_R) {
            rPressed = false;
            lock = true;
        }
        if (code == btn_L) {
            lPressed = false;
            lock = true;
        }
    }
}
