import java.awt.*;

public class Obstacle {

    private int x;
    private int y;
    private int width;
    private int height;

    public Obstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        g.setColor(new Color(70, 70, 80));
        g.fillRect(x, y, width, height);

        g.setColor(new Color(110, 110, 120));
        g.drawRect(x, y, width, height);
    }

    public boolean isTouchingPlayer(Player player) {
        return x < player.getX() + player.getSize()
                && x + width > player.getX()
                && y < player.getY() + player.getSize()
                && y + height > player.getY();
    }

    public boolean isTouchingBullet(Bullet bullet) {
        return x < bullet.getX() + bullet.getSize()
                && x + width > bullet.getX()
                && y < bullet.getY() + bullet.getSize()
                && y + height > bullet.getY();
    }

    public boolean isTouchingEnemy(Enemy enemy) {
        return x < enemy.getX() + enemy.getSize()
                && x + width > enemy.getX()
                && y < enemy.getY() + enemy.getSize()
                && y + height > enemy.getY();
    }
}
