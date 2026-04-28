import java.awt.*;

public class Enemy {

    private int x;
    private int y;
    private int size;
    private int speed;

    public Enemy (int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 32;
        this.speed = 2;
    }

    public void update(Player player ) {
        if (x < player.getX()) x += speed;
        if (x > player.getX()) x -= speed;

        if (y < player.getY()) y += speed;
        if (y > player.getY()) y -= speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x,y,size,size);
    }

    public boolean isTouchingPlayer(Player player) {
        return x < player.getX() + player.getSize()
                && x + size > player.getX()
                && y < player.getY() + player.getSize()
                && y + size > player.getY();
    }
}
