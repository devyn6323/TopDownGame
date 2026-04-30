import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Enemy {

    private int x;
    private int y;
    private int size;
    private int speed;
    private int health;
    private int maxHealth;
    private BufferedImage sprite;
    private String type;
    private int hitFlashTimer = 0;



    public Enemy(int x, int y, int wave) {
        this.x = x;
        this.y = y;
        this.size = 32;
        this.speed = 2 + wave / 3;

        int randomType = (int)(Math.random() * 100);

        if (randomType < 60) {
            type = "NORMAL";
            speed = 2 + wave / 3;
            maxHealth = 30 + wave * 10;
        } else if (randomType < 85) {
            type = "FAST";
            speed = 4 + wave / 3;
            maxHealth = 20 + wave * 5;
        } else {
            type = "TANK";
            speed = 1 + wave / 4;
            maxHealth = 70 + wave * 15;
        }
        health = maxHealth;

        loadSprite();


    }

    public void update(Player player ) {
        if (x < player.getX()) x += speed;
        if (x > player.getX()) x -= speed;

        if (y < player.getY()) y += speed;
        if (y > player.getY()) y -= speed;
    }

    public void draw(Graphics g) {
       if (hitFlashTimer > 0) {
           g.setColor(Color.WHITE);
           g.fillRect(x, y, size, size);
       }

        if (sprite != null) {
            g.drawImage(sprite, x, y, size, size, null);
        } else {
            if (type.equals("FAST")) {
                g.setColor(Color.ORANGE);
            } else if (type.equals("TANK")) {
                g.setColor(Color.MAGENTA);
            } else {
                g.setColor(Color.RED);
            }
            g.fillRect(x, y, size, size);
        }

        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y - 8, size, 5);

        g.setColor(Color.GREEN);
        int healthBarWidth = (int)((double) health / maxHealth * size);
        g.fillRect(x, y -8, healthBarWidth, 5);
    }

    public boolean isTouchingPlayer(Player player) {
        return x < player.getX() + player.getSize()
                && x + size > player.getX()
                && y < player.getY() + player.getSize()
                && y + size > player.getY();
    }

    public void knockBackFrom(Player player) {
        int knockBackDistance = 20;

        if (x < player.getX()) {
            x -= knockBackDistance;
        } else if (x > player.getX()) {
            x += knockBackDistance;
        }

        if (y < player.getY()) {
            y -= knockBackDistance;
        } else if (y > player.getY()) {
            y += knockBackDistance;
        }
    }

    public void loadSprite() {
        try {
            if (type.equals("FAST")) {
                sprite = ImageIO.read(getClass().getResource("/assets/sprite_enemy_fast.png"));
            } else if (type.equals("TANK")) {
                sprite = ImageIO.read(getClass().getResource("/assets/sprite_enemy_heavy.png"));
            } else {
                sprite = ImageIO.read(getClass().getResource("/assets/sprite_enemy_normal.png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takeDamage(int amount) {
        health -= amount;
        hitFlashTimer = 6;

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

    public void increaseSpeed() {
        speed++;
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

    public int getSpeed() {return speed;}

    public void updateHitFlash() {
        if (hitFlashTimer > 0) {
            hitFlashTimer--;
        }
    }

    public void moveX(Player player) {
        if (x < player.getX()) x += speed;
        if (x > player.getX()) x -= speed;
    }

    public void moveY(Player player) {
        if (y < player.getY()) y += speed;
        if (y > player.getY()) y -= speed;
    }
}
