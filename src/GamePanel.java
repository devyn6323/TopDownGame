import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Thread gameThread;
    private boolean running = false;
    private Random random = new Random();
    private Background background;

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<FloatingText> floatingTexts;
    private ArrayList<Obstacle> obstacles;

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

    private int highScore = 0;
    private final String HIGH_SCORE_FILE = "highscore.txt";

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
        background = new Background(WIDTH, HEIGHT);
        obstacles = new ArrayList<>();

        loadHighScore();

        obstacles.add(new Obstacle(100, 150, 120, 30));
        obstacles.add(new Obstacle(500, 150, 120, 30));
        obstacles.add(new Obstacle(300, 400, 200, 30));
        obstacles.add(new Obstacle(100, 300, 30, 120));
        obstacles.add(new Obstacle(650, 300, 30, 120));

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

        boolean moving = up || down || left || right;

        int oldPlayerx = player.getX();

        player.moveX(left, right, sprinting);

        for (Obstacle obstacle : obstacles) {
            if (obstacle.isTouchingPlayer(player)) {
                player.setX(oldPlayerx);
                break;
            }
        }

        int oldPlayerY = player.getY();

        player.moveY(up, down, sprinting);

        for (Obstacle obstacle : obstacles) {
            if (obstacle.isTouchingPlayer(player)) {
                player.setY(oldPlayerY);
                break;
            }
        }

        player.recoverStamina(moving, sprinting);

        player.updateDamageFlash();

        for (Enemy enemy : enemies) {
            int oldEnemyX = enemy.getX();

            enemy.moveX(player);



            for (Obstacle obstacle : obstacles) {
                if (obstacle.isTouchingEnemy(enemy)) {
                    enemy.setX(oldEnemyX);
                    break;
                }
            }

            int oldEnemyY = enemy.getY();

            enemy.moveY(player);

            for (Obstacle obstacle : obstacles) {
                if (obstacle.isTouchingEnemy(enemy)) {
                    enemy.setY(oldEnemyY);
                    break;
                }
            }
            enemy.updateHitFlash();
        }

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            boolean hitObstacle = false;

            for (Obstacle obstacle : obstacles) {
                if (obstacle.isTouchingBullet(bullet)) {
                    bullets.remove(i);
                    i--;
                    hitObstacle = true;
                    break;
                }
            }

            if (hitObstacle) {
                continue;
            }

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
            if (score > highScore) {
                highScore = score;
                saveHighScore();
            }
            gameState = GAME_OVER;
            System.out.println("GAME OVER");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        background.draw(g);

        for (Obstacle obstacle : obstacles) {
            obstacle.draw(g);
        }

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
        g.drawString("Health", 20, 20);
        drawBar(g, 80, 10, 150, 15, player.getHealth(), 100, Color.RED);

        g.setColor(Color.WHITE);
        g.drawString("Stamina", 20, 45);
        drawBar(g, 80, 35, 150, 15, player.getStamina(), 100, Color.GREEN);

        g.setColor(Color.WHITE);
        g.drawString("Enemies: " + enemies.size(), 20, 60);
        g.drawString("Score: " + score, 20, 80);
        g.drawString("Wave: " + wave, 20, 100);
        g.drawString("High Score: " + highScore, 20, 135);

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
        enemies.add(createRandomEnemy());
        enemies.add(createRandomEnemy());
        enemies.add(createRandomEnemy());

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
            enemies.add(createRandomEnemy());
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

    private boolean isSafeSpawn(int x, int y, int size) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isTouchingRect(x, y, size)) {
                return false;
            }
        }
        int buffer = 40;

        if (Math.abs(x - player.getX()) < buffer && Math.abs(y - player.getY()) < buffer) {
            return false;
        }
        return true;
    }

    private Enemy createRandomEnemy() {
        int size = 32;

        for (int attempt = 0; attempt < 100; attempt++) {
            int x = random.nextInt(WIDTH - size);
            int y = random.nextInt(HEIGHT - size);

            if (isSafeSpawn(x, y, size)) {
                return new Enemy(x, y, wave);
            }
        }
        return new Enemy(100, 100, wave);
    }

    private void loadHighScore() {
        try {
            File file = new File(HIGH_SCORE_FILE);

            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                highScore = scanner.nextInt();
                scanner.close();
            }
        } catch (Exception e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try {
            PrintWriter writer = new PrintWriter(HIGH_SCORE_FILE);
            writer.println(highScore);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawMenu(Graphics g) {
        g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 52));
        g.drawString("TOP DOWN SURVIVAL", 140, 180);

        g.setFont(new Font("Arial", Font.PLAIN, 26));
        g.drawString("WASD = Move", 310, 260);
        g.drawString("Arrow Keys = Shoot", 285, 300);
        g.drawString("Survive waves and collect power-ups", 200, 340);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("High Score: " + highScore, 315, 390);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Press ENTER to Start", 260, 455);
    }

    public void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 52));
        g.drawString("GAME OVER", 245, 240);

        g.setFont(new Font("Arial", Font.PLAIN, 26));
        g.drawString("Final Score: " + score, 310, 300);
        g.drawString("High Score: " + highScore, 300, 340);
        g.drawString("Wave Reached: " + wave, 300, 380);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Press R to Restart", 280, 450);
    }


    private void drawBar(Graphics g, int x, int y, int width, int height, int current, int max, Color fillColor) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, width, height);

        g.setColor(fillColor);
        int fillWidth = (int)((double) current / max * width);
        g.fillRect(x, y, fillWidth, height);

        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }
}
