package Engine.Input;

import Core.Input.MoveInput;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

public class KeyHandler implements KeyListener, MoveInput {
	 // Flags pour savoir quelles touches de direction sont pressées
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private Consumer<Void> tabCallback;
    private final boolean isAzerty;
    
    public KeyHandler() {
        this.isAzerty = KeyboardLayoutDetector.isAzerty();// Détection du type de clavier
    }
    
    public void setTabCallback(Consumer<Void> callback) {
        this.tabCallback = callback;
    }
 // Accesseurs pour savoir quelles touches sont pressées
    public boolean isUpPressed() {
        return upPressed;
    }
    
    public boolean isDownPressed() {
        return downPressed;
    }
    
    public boolean isLeftPressed() {
        return leftPressed;
    }
    
    public boolean isRightPressed() {
        return rightPressed;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        // Détermination des touches de mouvement selon qwerty par défaut
        boolean isUp = (keyCode == KeyEvent.VK_W) || (keyCode == KeyEvent.VK_Z);
        boolean isDown = (keyCode == KeyEvent.VK_S);
        boolean isLeft = (keyCode == KeyEvent.VK_A) || (keyCode == KeyEvent.VK_Q);
        boolean isRight = (keyCode == KeyEvent.VK_D);
        
        if (isAzerty) {
            isUp = (keyCode == KeyEvent.VK_Z);
            isLeft = (keyCode == KeyEvent.VK_Q);
        }
        
        if (isUp) upPressed = true;
        if (isDown) downPressed = true;
        if (isLeft) leftPressed = true;
        if (isRight) rightPressed = true;
        // Détection de la touche R 
        if (keyCode == KeyEvent.VK_R) {
            e.consume();
            if (tabCallback != null) {
                tabCallback.accept(null);
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
     // Détermination des touches de mouvement selon qw par défaut
        boolean isUp = (keyCode == KeyEvent.VK_W) || (keyCode == KeyEvent.VK_Z);
        boolean isDown = (keyCode == KeyEvent.VK_S);
        boolean isLeft = (keyCode == KeyEvent.VK_A) || (keyCode == KeyEvent.VK_Q);
        boolean isRight = (keyCode == KeyEvent.VK_D);
        
        if (isAzerty) {
            isUp = (keyCode == KeyEvent.VK_Z);
            isLeft = (keyCode == KeyEvent.VK_Q);
        }
     
        if (isUp) upPressed = false;
        if (isDown) downPressed = false;
        if (isLeft) leftPressed = false;
        if (isRight) rightPressed = false;
    }
}

