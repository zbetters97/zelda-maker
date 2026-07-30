package application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    /* GENERAL ATTRIBUTES */
    private boolean lock = true;

    /* BUTTON MAPPING */
    public int btn_UP = KeyEvent.VK_UP;
    public int btn_DOWN = KeyEvent.VK_DOWN;
    public int btn_LEFT = KeyEvent.VK_LEFT;
    public int btn_RIGHT = KeyEvent.VK_RIGHT;

    public int btn_START = KeyEvent.VK_SPACE;

    public int btn_UI_A = KeyEvent.VK_A;
    public int btn_UI_B = KeyEvent.VK_S;

    public int btn_A_DEF = KeyEvent.VK_A;
    public int btn_B_DEF = KeyEvent.VK_S;
    public int btn_X_DEF = KeyEvent.VK_D;
    public int btn_Y_DEF = KeyEvent.VK_F;
    public int btn_R_DEF = KeyEvent.VK_E;
    public int btn_L_DEF = KeyEvent.VK_W;

    public int btn_A = btn_A_DEF;
    public int btn_B = btn_B_DEF;
    public int btn_X = btn_X_DEF;
    public int btn_Y = btn_Y_DEF;
    public int btn_L = btn_L_DEF;
    public int btn_R = btn_R_DEF;

    /* CONFIG VALUES */
    public boolean startPressed, upPressed, downPressed, leftPressed, rightPressed,
            uiAPressed, uiBPressed, aPressed, bPressed, xPressed, yPressed, rPressed, lPressed;

    private int lastKeyPressed = -1;

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
        int code = e.getKeyCode();
        lastKeyPressed = code;

        if (code == btn_START && lock) {
            startPressed = true;
            lock = false;
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
        if ((code == btn_UI_A || code == btn_A) && lock) {
            if (code == btn_UI_A) uiAPressed = true;
            if (code == btn_A) aPressed = true;
            lock = false;
        }
        if ((code == btn_UI_B || code == btn_B) && lock) {
            if (code == btn_UI_B) uiBPressed = true;
            if (code == btn_B) bPressed = true;
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
        if (code == btn_R) {
            rPressed = true;
        }
        if (code == btn_L) {
            lPressed = true;
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
            lock = true;
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
        if (code == btn_UI_A) {
            uiAPressed = false;
            lock = true;
        }
        if (code == btn_UI_B) {
            uiBPressed = false;
            lock = true;
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

    public int getLastKeyPressed() {
        int key = lastKeyPressed;
        lastKeyPressed = -1;
        return key;
    }

    public void updateButton(int oldKey, int newKey) {
        if (btn_A == oldKey) btn_A = newKey;
        else if (btn_B == oldKey) btn_B = newKey;
        else if (btn_X == oldKey) btn_X = newKey;
        else if (btn_Y == oldKey) btn_Y = newKey;
        else if (btn_L == oldKey) btn_L = newKey;
        else if (btn_R == oldKey) btn_R = newKey;
    }

    public int getButtonFromKey(int key) {
        if (btn_A == key) return btn_A;
        else if (btn_B == key) return btn_B;
        else if (btn_X == key) return btn_X;
        else if (btn_Y == key) return btn_Y;
        else if (btn_L == key) return btn_L;
        else if (btn_R == key) return btn_R;

        return -1;
    }

    public void stopAllKeys() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;

        startPressed = false;

        uiAPressed = false;
        uiBPressed = false;

        aPressed = false;
        bPressed = false;
        yPressed = false;
        xPressed = false;
    }

    public void resetDefaults() {
        btn_A = btn_A_DEF;
        btn_B = btn_B_DEF;
        btn_X = btn_X_DEF;
        btn_Y = btn_Y_DEF;
        btn_L = btn_L_DEF;
        btn_R = btn_R_DEF;
    }
}
