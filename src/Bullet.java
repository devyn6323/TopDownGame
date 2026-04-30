import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Bullet {

    private int x;
    private int y;
    private int size;
    private int speed;
    private int dx;
    private int dy;
    private BufferedImage sprite;

    public Bullet(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.size = 12;
        this.speed = 8;
        this.dx = dx;
        this.dy = dy;

        try {
            sprite = ImageIO.read(getClass().getResource("/assets/sprite_bullet.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        x += dx * speed;
        y += dy * speed;
    }

    public void draw(Graphics g) {
       if (sprite != null) {
           g.drawImage(sprite, x, y, size, size, null);
       } else {
           g.setColor(Color.YELLOW);
           g.fillOval(x, y, size, size);
       }
    }

    public boolean isTouchingEnemy(Enemy enemy) {
        return x < enemy.getX() + enemy.getSize()
                && x + size > enemy.getX()
                && y < enemy.getY() + enemy.getSize()
                && y + size > enemy.getY();
    }

    public boolean isOffScreen() {
        return x < 0 || x > GamePanel.WIDTH || y < 0 || y > GamePanel.HEIGHT;
    }
}
