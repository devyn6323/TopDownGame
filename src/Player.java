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
    private int damageFlashTimer = 0;
    private int maxHealth;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 32;
        this.speed = 4;
        this.maxHealth = 100;
        this.health = maxHealth;
        this.stamina = 100;

        try {
            sprite = ImageIO.read(getClass().getResource("/assets/sprite_hero.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(boolean up, boolean down, boolean left, boolean right, boolean sprinting, int screenWidth, int screenHeight) {
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

        keepOnScreen(screenWidth, screenHeight);
    }

    private void keepOnScreen(int screenWidth, int screenHeight) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;

        if (x > screenWidth - size) {
            x = screenWidth - size;
        }

        if (y > screenHeight - size) {
            y = screenHeight - size;
        }
    }

    public void draw(Graphics g) {
        if (damageFlashTimer > 0) {
            g.setColor(Color.RED);
            g.fillRect(x, y, size, size);
        } else if (sprite != null) {
           g.drawImage(sprite, x, y, size, size, null);
       } else {
           g.setColor(Color.GREEN);
           g.fillRect(x, y, size, size);
       }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public void takeDamage(int amount) {
        health -= amount;
        damageFlashTimer = 10;

        if (health < 0) {
            health = 0;
        }
    }

    public void dash(boolean up, boolean down, boolean left, boolean right, int screenWidth, int screenHeight) {
        int dashDistance = 60;

        if (up) y -= dashDistance;
        if (down) y += dashDistance;
        if (left) x -= dashDistance;
        if (right) x += dashDistance;

        keepOnScreen(screenWidth, screenHeight );
    }

    public void heal(int amount) {
        health += amount;

        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public int getHealth() {
        return health;
    }

    public int getStamina() {
        return stamina;
    }

    public void updateDamageFlash() {
        if (damageFlashTimer > 0) {
            damageFlashTimer--;
        }
    }

    public void moveX(boolean left, boolean right, boolean sprinting, int gameWidth, int gameHeight) {
        int currentSpeed = speed;

        if (sprinting && stamina > 0 && ( left || right )) {
            currentSpeed = speed + 3;
            stamina--;
        }

        if (left) x-= currentSpeed;
        if (right) x += currentSpeed;

        keepOnScreen(gameWidth, gameHeight);
    }

    public void moveY(boolean up, boolean down, boolean sprinting, int screenWidth, int screenHeight) {
        int currentSpeed = speed;

        if (sprinting && stamina > 0 && ( up || down )) {
            currentSpeed = speed + 3;
            stamina--;
        }

        if (up) y -= currentSpeed;
        if (down) y += currentSpeed;

        keepOnScreen(screenWidth, screenHeight);
    }

    public void recoverStamina(boolean moving, boolean sprinting) {
        if (!sprinting || !moving) {
            if (stamina < MAX_STAMINA) {
                stamina++;
            }
        }
    }

    public void increaseMaxHealth(int amount) {
       if (maxHealth < 200) {
           maxHealth += amount;
           health += amount;
       }

       if (maxHealth > 200) {
           maxHealth = 200;
       }

        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public void increaseSpeed(int amount) {
        if (speed < 8) {
            speed += amount;
        }
        if (speed > 8) {
            speed = 8;
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getSpeed() {
        return speed;
    }


}
