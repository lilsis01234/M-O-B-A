package Engine.Input;

import Core.Input.TargetInput;
import Engine.Render.Camera;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseHandler extends MouseAdapter implements TargetInput {
    
    private int targetX = -1;
    private int targetY = -1;
    private boolean hasTarget = false;
    private boolean targetIsWorldCoords = false;

    private int currentX = -1;
    private int currentY = -1;
    private int wheelRotation = 0;

    private Camera camera;
    private int lastClickX = -1;
    private int lastClickY = -1;
    private boolean clickTriggered = false;
    private java.util.function.Consumer<java.awt.Point> leftClickCallback;
    private java.util.function.Function<java.awt.Point, Boolean> rightClickCallback;

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public void setLeftClickCallback(java.util.function.Consumer<java.awt.Point> callback) {
        this.leftClickCallback = callback;
    }

    public void setRightClickCallback(java.util.function.Function<java.awt.Point, Boolean> callback) {
        this.rightClickCallback = callback;
    }
    
    public int getTargetX() {
        if (camera != null && hasTarget && !targetIsWorldCoords) {
            return camera.screenToWorldX(targetX);
        }
        return targetX;
    }
    
    public int getTargetY() {
        if (camera != null && hasTarget && !targetIsWorldCoords) {
            return camera.screenToWorldY(targetY);
        }
        return targetY;
    }

    public boolean hasNewClick() {
        return clickTriggered;
    }

    public int getLastClickWorldX() {
        return camera != null ? camera.screenToWorldX(lastClickX) : lastClickX;
    }

    public int getLastClickWorldY() {
        return camera != null ? camera.screenToWorldY(lastClickY) : lastClickY;
    }

    public void clearNewClick() {
        clickTriggered = false;
    }
    
    public boolean hasTarget() {
        return hasTarget;
    }

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public int getWheelRotation() {
        int rotation = wheelRotation;
        wheelRotation = 0; // Reset after reading
        return rotation;
    }
    
    public void clearTarget() {
        hasTarget = false;
        targetX = -1;
        targetY = -1;
        targetIsWorldCoords = false;
    }

    public void setTarget(int x, int y) {
        targetX = x;
        targetY = y;
        hasTarget = true;
        targetIsWorldCoords = true;
        clickTriggered = true;
        lastClickX = x;
        lastClickY = y;
    }

    public void setTargetFromScreen(int x, int y) {
        targetX = x;
        targetY = y;
        hasTarget = true;
        targetIsWorldCoords = false;
        clickTriggered = true;
        lastClickX = x;
        lastClickY = y;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && leftClickCallback != null) {
            leftClickCallback.accept(new java.awt.Point(e.getX(), e.getY()));
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            boolean handled = rightClickCallback != null && rightClickCallback.apply(new java.awt.Point(e.getX(), e.getY()));
            if (!handled) {
                setTargetFromScreen(e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        wheelRotation = e.getWheelRotation();
    }
}
