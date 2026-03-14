package Engine.Tile;

import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;

public class Tile {

    private final int id;
    private final String name;
    private final List<BufferedImage> images = new ArrayList<>();
    private final boolean collision;
    private java.awt.Color color;
    private Object userData; // For caching sprite frames, etc.

    public Tile(int id, String name, BufferedImage image, boolean collision) {
        this.id = id;
        this.name = name;
        if (image != null) {
            this.images.add(image);
        }
        this.collision = collision;
    }

    public void setColor(java.awt.Color color) {
        this.color = color;
    }

    public java.awt.Color getColor() {
        return color;
    }

    public void addImage(BufferedImage img) {
        if (img != null) {
            images.add(img);
        }
    }

    public List<BufferedImage> getImages() {
        return images;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BufferedImage getImage() {
        return images.isEmpty() ? null : images.get(0);
    }

    public boolean isCollision() {
        return collision;
    }
    
    public void setUserData(Object data) {
        this.userData = data;
    }
    
    public Object getUserData() {
        return userData;
    }
}

