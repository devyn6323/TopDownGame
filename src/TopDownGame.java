import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TopDownGame extends JPanel implements Runnable, KeyListener {

    //window size
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    //game loop
    private Thread gameThread;
    private boolean running = false;

    //player
    private int playerX = 400;
    private int playerY = 300;
    private int playerSize = 32;
    private int speed = 4;

    //input
    private boolean up, down, left, right;

    public TopDownGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    public void start() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1000000000.0 / 60.0;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;

            while (delta >= 1) {
                update();
                delta--;
            }

            repaint();
        }
    }

    private void update() {
        if (up) playerY -= speed;
        if (down) playerY += speed;
        if (left) playerX -= speed;
        if (right) playerX += speed;

        //keep player on screen
        if (playerX < 0) playerX = 0;
        if (playerY < 0) playerY = 0;
        if (playerX > WIDTH - playerSize) playerX = WIDTH - playerSize;
        if (playerY > HEIGHT - playerSize) playerY = HEIGHT - playerSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //player
        g.setColor(Color.GREEN);
        g.fillRect(playerX, playerY, playerSize, playerSize);
    }

    //input
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) up = true;
        if (e.getKeyCode() == KeyEvent.VK_S) down = true;
        if (e.getKeyCode() == KeyEvent.VK_A) left = true;
        if (e.getKeyCode() == KeyEvent.VK_D) right = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) up = false;
        if (e.getKeyCode() == KeyEvent.VK_S) down = false;
        if (e.getKeyCode() == KeyEvent.VK_A) left = false;
        if (e.getKeyCode() == KeyEvent.VK_D) right = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Top Down Game");
        TopDownGame game = new TopDownGame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.start();
    }


}