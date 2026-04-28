import java.awt.*;

public class Bullet {

    private int x;
    private int y;
    private int size;
    private int speed;
    private int dx;
    private int dy;

    public Bullet(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.size = 8;
        this.speed = 8;
        this.dx = dx;
        this.dy = dy;
    }

    public void update() {
        x += dx * speed;
        y += dy * speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, size, size);
    }

    public boolean isOffScreen() {
        return x < 0 || x > GamePanel.WIDTH || y < 0 || y > GamePanel.HEIGHT;
    }
}
