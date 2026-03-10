package Main;

import javax.swing.*;

public class Main {
    
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setTitle("Moba");
        
        Engine.GamePanel gamePanel = new Engine.GamePanel();
        window.add(gamePanel);
        
        window.pack();
        window.setLocationRelativeTo(null);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setVisible(true);
        
        gamePanel.requestFocusInWindow();
        gamePanel.startGameThread();
    }
}
