package Main;

import Engine.GamePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    
    private static JFrame window;
    private static GamePanel gamePanel;
    private static boolean isFullscreen = false;
    private static GraphicsDevice gd;
    
    public static void main(String[] args) {
        gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setTitle("Moba");
        
        gamePanel = new Engine.GamePanel();
        gamePanel.setFocusable(true);
        window.add(gamePanel);
        
        window.pack();
        window.setLocationRelativeTo(null);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F11) {
                    e.consume();
                    toggleFullscreen();
                }
            }
        });
        
        window.setVisible(true);
        
        gamePanel.requestFocusInWindow();
    }
    
    private static void toggleFullscreen() {
        System.out.println("Toggle fullscreen: " + isFullscreen);
        if (isFullscreen) {
            gd.setFullScreenWindow(null);
            window.setVisible(false);
            window.dispose();
            window.setUndecorated(false);
            window.setVisible(true);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            window.setVisible(false);
            window.dispose();
            window.setUndecorated(true);
            gd.setFullScreenWindow(window);
            window.setVisible(true);
        }
        
        isFullscreen = !isFullscreen;
        gamePanel.requestFocusInWindow();
    }
}
