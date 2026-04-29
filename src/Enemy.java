import java.awt.*;

public class Enemy {

    private int x;
    private int y;
    private int size;
    private int speed;
    private int health;

    public Enemy (int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 32;
        this.speed = 2;
        this.health = 30;
    }

    public Enemy(int x, int y, int wave) {
        this.x = x;
        this.y = y;
        this.size = 32;
        this.speed = 2 + wave / 3;
        this.health = 30 + wave * 10;
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

    public void takeDamage(int amount) {
        health -= amount;

        if (health < 0) {
            health = 0;
        }
    }

    public int getHealth() {
        return health;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void respawn() {
        x = 100;
        y = 100;
        health = 30;
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
}
