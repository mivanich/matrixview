package matrixchar.mvc.controller;

import matrixchar.mvc.model.Model;
import matrixchar.mvc.view.View;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

public class Controller {
    private final static int UPDATE_DELAY_MS = 90;

    private final Model model;
    private final View view;

    private boolean escKeyPressed = false;
    private boolean pauseKeyPressed = false;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this::dispatchKeyEvent);
    }

    public void start() {
        try (view) {
            while (!escKeyPressed) {
                if (!pauseKeyPressed) {
                    model.update();
                    view.draw();
                }
                sleep();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getID() == KeyEvent.KEY_RELEASED) {
            if (event.getKeyCode() == KeyEvent.VK_PAUSE) {
                pauseKeyPressed = !pauseKeyPressed;
                return true;
            }
            escKeyPressed = true;
            return true;
        }
        return false;
    }

    private static void sleep() throws InterruptedException {
        Thread.sleep(UPDATE_DELAY_MS);
    }
}