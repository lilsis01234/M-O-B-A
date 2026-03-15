package Core.Database.model;
<<<<<<< HEAD
/**
 * Classe de données du joueur 
 * @author RAHARIMANANA Tianantenaina BOUKIRAT Thafat ZEGHBIB Sonia
 * */
=======

>>>>>>> 32752d2db8a08142a371d5701f5cd1a15d2955be
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
