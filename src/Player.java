import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player {

    private int x;
    private int y;
    private int size;
    private int speed;
    private int health;
    private int stamina;
    private final int MAX_STAMINA = 100;
    private BufferedImage sprite;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 32;
        this.speed = 4;
        this.health = 100;
        this.stamina = 100;

        try {
            sprite = ImageIO.read(getClass().getResource("/assets/sprite_hero.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(boolean up, boolean down, boolean left, boolean right, boolean sprinting) {
        boolean moving = up || down || left || right;

        int currentSpeed = speed;

        if (sprinting && moving && stamina > 0) {
            currentSpeed = speed + 3;
            stamina--;
        } else {
            if (stamina < MAX_STAMINA) {
                stamina++;
            }
        }

        if (up) y -= currentSpeed;
        if (down) y += currentSpeed;
        if (left) x -= currentSpeed;
        if (right) x += currentSpeed;

        keepOnScreen();
    }

    private void keepOnScreen() {
        if (x < 0) x = 0;
        if (y < 0) y = 0;

        if (x > GamePanel.WIDTH - size) {
            x = GamePanel.WIDTH - size;
        }

        if (y > GamePanel.HEIGHT - size) {
            y = GamePanel.HEIGHT - size;
        }
    }

    public void draw(Graphics g) {
       if (sprite != null) {
           g.drawImage(sprite, x, y, size, size, null);
       } else {
           g.setColor(Color.GREEN);
           g.fillRect(x, y, size, size);
       }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public void takeDamage(int amount) {
        health -= amount;

        if (health < 0) {
            health = 0;
        }
    }

    public void dash(boolean up, boolean down, boolean left, boolean right) {
        int dashDistance = 60;

        if (up) y -= dashDistance;
        if (down) y += dashDistance;
        if (left) x -= dashDistance;
        if (right) x += dashDistance;

        keepOnScreen();
    }

    public void heal(int amount) {
        health += amount;

        if (health > 100) {
            health = 100;
        }
    }

    public int getHealth() {
        return health;
    }

    public int getStamina() {
        return stamina;
    }


}
