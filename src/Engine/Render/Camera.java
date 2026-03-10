package Engine.Render;

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

    private void updateDynamicMinZoom() {
        if (worldWidth > 0 && worldHeight > 0 && viewportWidth > 0 && viewportHeight > 0) {
            float zoomX = (float) viewportWidth / worldWidth;
            float zoomY = (float) viewportHeight / worldHeight;
            // The user wants to see the entire map at 1:1 scale (meaning fitting the map into the viewport)
            // We use the maximum of the two ratios to ensure the map at least fills one dimension of the viewport
            // Or the minimum if we want the ENTIRE map to be visible.
            // Usually "scaled 1:1" in this context means fitting the map to the screen.
            dynamicMinZoom = Math.max(zoomX, zoomY);
            
            // If the map is smaller than the viewport, min zoom could be > 1.0. 
            // We should probably allow at least 1.0 if the user prefers, but let's follow the requirement.
            
            if (zoom < dynamicMinZoom) {
                zoom = dynamicMinZoom;
                clamp();
            }
        }
    }

    public void update(int mouseX, int mouseY) {
        // Do not update if the mouse is outside the component (usually indicated by -1 from MouseHandler)
        if (mouseX < 0 || mouseY < 0 || mouseX > viewportWidth || mouseY > viewportHeight) {
            return;
        }

        float speed = Config.getCameraSpeed();
        float threshold = Config.getCameraEdgeThreshold();
        float maxCameraX = Math.max(0, worldWidth - (viewportWidth / zoom));
        float maxCameraY = Math.max(0, worldHeight - (viewportHeight / zoom));

        // Edge scrolling logic with boundary checks
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

        clamp();
    }

    private void clamp() {
        float maxCameraX = Math.max(0, worldWidth - (viewportWidth / zoom));
        float maxCameraY = Math.max(0, worldHeight - (viewportHeight / zoom));

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > maxCameraX) x = maxCameraX;
        if (y > maxCameraY) y = maxCameraY;
    }

    public void zoom(int wheelRotation) {
        float oldZoom = zoom;
        if (wheelRotation < 0) {
            zoom += Config.getZoomStep();
        } else if (wheelRotation > 0) {
            zoom -= Config.getZoomStep();
        }

        // Clamp zoom
        if (zoom < dynamicMinZoom) {
            zoom = dynamicMinZoom;
        } else if (zoom > Config.getMaxZoom()) {
            zoom = Config.getMaxZoom();
        }

        // Adjust x, y so we zoom towards the center of the viewport
        if (oldZoom != zoom) {
            float viewportCenterX = viewportWidth / 2f;
            float viewportCenterY = viewportHeight / 2f;

            // Find world center before zoom
            float worldCenterX = x + (viewportCenterX / oldZoom);
            float worldCenterY = y + (viewportCenterY / oldZoom);

            // Set x, y so that world center remains at the viewport center
            x = worldCenterX - (viewportCenterX / zoom);
            y = worldCenterY - (viewportCenterY / zoom);

            clamp();
        }
    }

    public void setViewportSize(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        updateDynamicMinZoom();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZoom() {
        return zoom;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public int screenToWorldX(int screenX) {
        return (int) (screenX / zoom + x);
    }

    public int screenToWorldY(int screenY) {
        return (int) (screenY / zoom + y);
    }
}
