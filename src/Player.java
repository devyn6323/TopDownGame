import java.awt.*;

public class Player {

    private int x;
    private int y;
    private int size;
    private int speed;
    private int health;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 32;
        this.speed = 4;
        this.health = 100;
    }

    public void update(boolean up, boolean down, boolean left, boolean right) {
        if (up) y -= speed;
        if (down) y += speed;
        if (left) x -= speed;
        if (right) x += speed;

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
        g.setColor(Color.GREEN);
        g.fillRect(x,y,size,size);
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

    public void heal(int amount) {
        health += amount;

        if (health > 100) {
            health = 100;
        }
    }

    public int getHealth() {
        return health;
    }


}
