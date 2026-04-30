import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Thread gameThread;
    private boolean running = false;
    private Random random = new Random();

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<FloatingText> floatingTexts;

    private boolean up, down, left, right, sprinting;

    private int damageCooldown = 0;
    private final int DAMAGE_COOLDOWN_MAX = 60;
    private int fireCooldown = 0;
    private final int NORMAL_FIRE_COOLDOWN_MAX = 15;
    private final int RAPID_FIRE_COOLDOWN_MAX = 5;
    private int dashCooldown = 0;
    private final int MAX_DASH_COOLDOWN = 90;

    private int currentFireCooldownMax = NORMAL_FIRE_COOLDOWN_MAX;
    private int rapidFireTimer = 0;

    private int score;
    private final int MENU = 0;
    private final int PLAYING = 1;
    private final int GAME_OVER = 2;

    private boolean paused = false;

    private int gameState = MENU;

    private int wave = 1;
    private int enemiesDefeatedThisWave = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        player = new Player(400, 300);
        enemies = new ArrayList<>();
        enemies.add(new Enemy(100, 100, wave));
        enemies.add(new Enemy(650, 100, wave));
        enemies.add(new Enemy(100, 450, wave));
        bullets = new ArrayList<>();
        powerUps = new ArrayList<>();
        floatingTexts = new ArrayList<>();
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
        if (gameState != PLAYING || paused) {
            return;
        }

        player.update(up, down, left, right, sprinting);

        for (Enemy enemy : enemies) {
            enemy.update(player);
        }

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            boolean bulletRemoved = false;

            for (int j = 0; j < enemies.size(); j++) {
                Enemy enemy = enemies.get(j);

                if (bullet.isTouchingEnemy(enemy)) {
                    enemy.takeDamage(10);
                    enemy.knockBackFrom(player);

                    bullets.remove(i);
                    i--;
                    bulletRemoved = true;

                    System.out.println("Enemy Health: " + enemy.getHealth());

                    if (enemy.isDead()) {
                        score += 100;
                        enemiesDefeatedThisWave++;
                        floatingTexts.add(new FloatingText(enemy.getX(), enemy.getY(), "+100"));

                        int dropX = enemy.getX();
                        int dropY = enemy.getY();

                        if (random.nextInt(100) < 30) {
                            if (random.nextBoolean()) {
                                powerUps.add(new PowerUp(dropX, dropY, "HEALTH"));
                            } else {
                                powerUps.add(new PowerUp(dropX, dropY, "RAPID_FIRE"));
                            }
                        }

                        enemies.remove(j);

                        System.out.println("Enemy defeated!");

                        if (enemies.isEmpty()) {
                            startNextWave();
                        }
                    }

                    break;
                }
            }

            if (bulletRemoved) {
                continue;
            }

            if (bullet.isOffScreen()) {
                bullets.remove(i);
                i--;
            }
        }

        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp powerUp = powerUps.get(i);

            if (powerUp.isTouchingPlayer(player)) {
                if (powerUp.getType().equals("HEALTH")) {
                    player.heal(20);
                    System.out.println("Picked up health!");
                } else if (powerUp.getType().equals("RAPID_FIRE")) {
                    currentFireCooldownMax = RAPID_FIRE_COOLDOWN_MAX;
                    rapidFireTimer = 300;
                    System.out.println("Picked up rapid fire!");
                }

                powerUps.remove(i);
                i--;
            }
        }

        for (int i = 0; i < floatingTexts.size(); i++) {
            FloatingText text = floatingTexts.get(i);
            text.update();

            if (text.isDone()) {
                floatingTexts.remove(i);
                i--;
            }
        }

        if (damageCooldown > 0) {
            damageCooldown--;
        }

        if (dashCooldown > 0) {
            damageCooldown--;
        }

        if (fireCooldown > 0) {
            fireCooldown--;
        }

        if (rapidFireTimer > 0) {
            rapidFireTimer--;

            if (rapidFireTimer == 0) {
                currentFireCooldownMax = NORMAL_FIRE_COOLDOWN_MAX;
                System.out.println("Rapid fire ended!");
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy.isTouchingPlayer(player) && damageCooldown == 0) {
                player.takeDamage(10);
                damageCooldown = DAMAGE_COOLDOWN_MAX;

                System.out.println("Health: " + player.getHealth());
            }
        }

        if (player.getHealth() <= 0) {
            gameState = GAME_OVER;
            System.out.println("GAME OVER");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);

        if (gameState == MENU) {
            drawMenu(g);
            return;
        }

        player.draw(g);

        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }

        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g);
        }

        for (FloatingText text : floatingTexts) {
            text.draw(g);
        }

        //draw the player health on the screen
        g.setColor(Color.WHITE);
        g.drawString("Health: " + player.getHealth(), 20, 20);
        g.drawString("Stamina: " + player.getStamina(), 20, 40);
        g.drawString("Enemies: " + enemies.size(), 20, 60);
        g.drawString("Score: " + score, 20, 80);
        g.drawString("Wave: " + wave, 20, 100);
        //g.drawString("Dash Cooldown" + dashCooldown / 60 + "s", 20, 140);

        if (rapidFireTimer > 0) {
            g.drawString("Rapid Fire: " + rapidFireTimer / 60 + "s", 20, 120);
        }

        if (paused) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 52));
            g.drawString("PAUSED", 300, 300);

            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Press P to Resume", 300, 340);
        }

        if (gameState == GAME_OVER) {
            drawGameOver(g);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameState == MENU && e.getKeyCode() == KeyEvent.VK_ENTER) {
            restartGame();
            return;
        }

        if (gameState == GAME_OVER && e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
            return;
        }

        if (gameState != PLAYING) {
            return;
        }

        if (gameState == PLAYING && e.getKeyCode() == KeyEvent.VK_P) {
            paused = !paused;
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_W) up = true;
        if (e.getKeyCode() == KeyEvent.VK_S) down = true;
        if (e.getKeyCode() == KeyEvent.VK_A) left = true;
        if (e.getKeyCode() == KeyEvent.VK_D) right = true;
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) sprinting = true;

        if (e.getKeyCode() == KeyEvent.VK_SPACE && damageCooldown == 0) {
            player.dash(up, down, left, right);
            damageCooldown = MAX_DASH_COOLDOWN;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            shoot(0, -1);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            shoot(0, 1);
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            shoot(-1, 0);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            shoot(1, 0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameState != PLAYING) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_W) up = false;
        if (e.getKeyCode() == KeyEvent.VK_S) down = false;
        if (e.getKeyCode() == KeyEvent.VK_A) left = false;
        if (e.getKeyCode() == KeyEvent.VK_D) right = false;
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) sprinting = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private void restartGame() {
        player = new Player(400, 300);

        wave = 1;
        enemiesDefeatedThisWave = 0;

        enemies.clear();
        enemies.add(new Enemy(100, 100, wave));
        enemies.add(new Enemy(650, 100, wave));
        enemies.add(new Enemy(100, 450, wave));

        bullets.clear();
        powerUps.clear();
        floatingTexts.clear();
        paused = false;

        score = 0;
        fireCooldown = 0;
        damageCooldown = 0;
        dashCooldown = 0;
        rapidFireTimer = 0;
        currentFireCooldownMax = NORMAL_FIRE_COOLDOWN_MAX;

        up = false;
        down = false;
        left = false;
        right = false;
        sprinting = false;

        gameState = PLAYING;

    }

    private void startNextWave() {
        wave++;
        enemiesDefeatedThisWave = 0;

        int enemyCount = 2 + wave;

        for (int i = 0; i < enemyCount; i++) {
            int x = 50 + (i * 120) % (WIDTH - 100);
            int y = 50 + (i * 90) % (HEIGHT - 100);

            enemies.add(new Enemy(x, y, wave));
        }
        System.out.println("Wave " + wave + " started!");
    }

    private void shoot(int dx, int dy) {
        if (gameState != PLAYING) {
            return;
        }

        if (fireCooldown > 0) {
            return;
        }

        int bulletX = player.getX() + player.getSize() / 2;
        int bulletY = player.getY() + player.getSize() / 2;

        if (dx == -1) {
            bulletX = player.getX();
        } else if (dx == 1) {
            bulletX = player.getX() + player.getSize();
        }

        if (dy == -1) {
            bulletY = player.getY();
        } else if (dy == 1) {
            bulletY = player.getY() + player.getSize();
        }

        bullets.add(new Bullet(bulletX,bulletY, dx, dy));
        fireCooldown = currentFireCooldownMax;
    }

    public void drawMenu(Graphics g) {
        g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 52));
        g.drawString("TOP DOWN SURVIVAL", 140, 180);

        g.setFont(new Font("Arial", Font.PLAIN, 26));
        g.drawString("WASD = Move", 310, 260);
        g.drawString("Arrow Keys = Shoot", 285, 300);
        g.drawString("Survive waves and collect power-ups", 200, 340);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Press ENTER to Start", 260, 430);
    }

    public void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 52));
        g.drawString("GAME OVER", 245, 240);

        g.setFont(new Font("Arial", Font.PLAIN, 26));
        g.drawString("Final Score: " + score, 310, 300);
        g.drawString("Wave Reached: " + wave, 300, 340);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Press R to Restart", 280, 430);
    }

    public void drawBackground(Graphics g) {
        g.setColor(new Color(25, 25, 25));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(new Color(40, 40, 40));

        int tileSize = 40;

        for (int x = 0; x < WIDTH; x += tileSize) {
            g.drawLine(x, 0, x, HEIGHT);
        }

        for (int y = 0; y < HEIGHT; y += tileSize) {
            g.drawLine(0, y, WIDTH, y);
        }
    }
}
