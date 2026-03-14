package Core.Database.model;

import java.time.LocalDateTime;

public class Player {
    private int id;
    private String username;
    private LocalDateTime createdAt;
    
    public Player() {}
    
    public Player(String username) {
        this.username = username;
        this.createdAt = LocalDateTime.now();
    }
    
    public Player(int id, String username, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
