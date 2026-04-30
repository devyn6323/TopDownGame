import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class PowerUp {

    private int x;
    private int y;
    private int size;
    private String type;
    private BufferedImage sprite;

    public PowerUp(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.size = 20;
        this.type = type;

        loadSprite();
    }

    public void draw(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, size, size, null);
        } else {

            if (type.equals("HEALTH")) {
                g.setColor(Color.PINK);
            } else if (type.equals("RAPID_FIRE")) {
                g.setColor(Color.CYAN);
            }
            g.fillOval(x, y, size, size);
        }
    }

    public boolean isTouchingPlayer(Player player) {
        return x < player.getX() + player.getSize()
                && x + size > player.getX()
                && y < player.getY() + player.getSize()
                &&y + size > player.getY();
    }

    private void loadSprite() {
        try {
            if (type.equals("HEALTH")) {
                sprite = ImageIO.read(getClass().getResource("/assets/sprite_health_powerup.png"));
            } else if (type.equals("RAPID_FIRE")) {
                sprite = ImageIO.read(getClass().getResource("/assets/sprite_rapid_fire.png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getType() {
        return type;
    }
}
