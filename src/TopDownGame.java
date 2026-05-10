import javax.swing.*;
import java.awt.*;

public class TopDownGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Top Down Game");
        GamePanel gamePanel = new GamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.add(gamePanel);

        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        device.setFullScreenWindow(frame);


        gamePanel.start();
    }
}