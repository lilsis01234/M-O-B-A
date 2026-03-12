package Engine.Render;

import Core.Entity.MathUtils;
import Core.Config;

public class Camera {
    
    private float x;
    private float y;
    private float zoom = 1.0f;
    private int viewportWidth;
    private int viewportHeight;
    private int worldWidth;
    private int worldHeight;
    private float dynamicMinZoom = 1.0f;

    public Camera(int viewportWidth, int viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.x = 0;
        this.y = 0;
        updateDynamicMinZoom();
    }

    public void setWorldSize(int width, int height) {
        this.worldWidth = width;
        this.worldHeight = height;
        updateDynamicMinZoom();
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setViewportSize(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        updateDynamicMinZoom();
    }

    private void updateDynamicMinZoom() {
        if (worldWidth > 0 && worldHeight > 0 && viewportWidth > 0 && viewportHeight > 0) {
            float zoomX = (float) viewportWidth / worldWidth;
            float zoomY = (float) viewportHeight / worldHeight;
            dynamicMinZoom = Math.max(zoomX, zoomY);
            
            if (zoom < dynamicMinZoom) {
                zoom = dynamicMinZoom;
                clamp();
            }
        }
    }

    public void update(int mouseX, int mouseY) {
        if (!isMouseInBounds(mouseX, mouseY)) {
            return;
        }

        float maxCameraX = getMaxCameraX();
        float maxCameraY = getMaxCameraY();

        handleEdgeScrolling(mouseX, mouseY, maxCameraX, maxCameraY);
        clamp();
    }

    private boolean isMouseInBounds(int mouseX, int mouseY) {
        return mouseX >= 0 && mouseY >= 0 && 
               mouseX <= viewportWidth && mouseY <= viewportHeight;
    }

    private float getMaxCameraX() {
        return Math.max(0, worldWidth - (viewportWidth / zoom));
    }

    private float getMaxCameraY() {
        return Math.max(0, worldHeight - (viewportHeight / zoom));
    }

    private void handleEdgeScrolling(int mouseX, int mouseY, float maxCameraX, float maxCameraY) {
        float speed = Config.getCameraSpeed();
        float threshold = Config.getCameraEdgeThreshold();

        if (mouseX < threshold && x > 0) {
            x -= speed;
        } else if (mouseX > viewportWidth - threshold && x < maxCameraX) {
            x += speed;
        }

        if (mouseY < threshold && y > 0) {
            y -= speed;
        } else if (mouseY > viewportHeight - threshold && y < maxCameraY) {
            y += speed;
        }
    }

    private void clamp() {
        x = MathUtils.clamp(x, 0, getMaxCameraX());
        y = MathUtils.clamp(y, 0, getMaxCameraY());
    }

    public void zoom(int wheelRotation) {
        if (wheelRotation == 0) return;

        float oldZoom = zoom;
        zoom += (wheelRotation < 0 ? 1 : -1) * Config.getZoomStep();
        zoom = MathUtils.clamp(zoom, dynamicMinZoom, Config.getMaxZoom());

        if (oldZoom != zoom) {
            adjustPositionForZoom(oldZoom);
            clamp();
        }
    }

    private void adjustPositionForZoom(float oldZoom) {
        float viewportCenterX = viewportWidth / 2f;
        float viewportCenterY = viewportHeight / 2f;

        float worldCenterX = x + (viewportCenterX / oldZoom);
        float worldCenterY = y + (viewportCenterY / oldZoom);

        x = worldCenterX - (viewportCenterX / zoom);
        y = worldCenterY - (viewportCenterY / zoom);
    }

    public int screenToWorldX(int screenX) {
        return (int) (screenX / zoom + x);
    }

    public int screenToWorldY(int screenY) {
        return (int) (screenY / zoom + y);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZoom() { return zoom; }
    public int getWorldWidth() { return worldWidth; }
    public int getWorldHeight() { return worldHeight; }
    public int getViewportWidth() { return viewportWidth; }
    public int getViewportHeight() { return viewportHeight; }
}
