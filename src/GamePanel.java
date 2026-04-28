import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Thread gameThread;
    private boolean running = false;

    private Player player;
    private Enemy enemy;
    private ArrayList<Bullet> bullets;

    private boolean up, down, left, right;

    private int damageCooldown = 0;
    private final int DAMAGE_COOLDOWN_MAX = 60;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        player = new Player(400, 300);
        enemy = new Enemy(100, 100);
        bullets = new ArrayList<>();
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
        player.update(up, down, left, right);
        enemy.update(player);

        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update();
            if (bullets.get(i).isOffScreen()) {
                bullets.remove(i);
                i--;
            }
        }

        if (damageCooldown > 0) {
            damageCooldown--;
        }

        if (enemy.isTouchingPlayer(player) && damageCooldown == 0) {
            player.takeDamage(10);
            damageCooldown = DAMAGE_COOLDOWN_MAX;

            System.out.println("Health: " + player.getHealth());
        }

        if (player.getHealth() <= 0) {
            running = false;
            System.out.println("GAME OVER");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        player.draw(g);
        enemy.draw(g);

        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }

        g.setColor(Color.WHITE);
        g.drawString("Health: " + player.getHealth(), 20, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) up = true;
        if (e.getKeyCode() == KeyEvent.VK_S) down = true;
        if (e.getKeyCode() == KeyEvent.VK_A) left = true;
        if (e.getKeyCode() == KeyEvent.VK_D) right = true;

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            bullets.add(new Bullet(player.getX() + player.getSize() / 2, player.getY(), 0, -1));
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            bullets.add(new Bullet(player.getX() + player.getSize() / 2, player.getY() + player.getSize(), 0, 1 ));
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            bullets.add(new Bullet(player.getX(), player.getY() + player.getSize() / 2, -1, 0 ));
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            bullets.add(new Bullet(player.getX() + player.getSize(), player.getY() + player.getSize() / 2, 1, 0 ));
        }
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
}
