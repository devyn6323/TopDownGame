import java.awt.*;

public class PowerUp {

    private int x;
    private int y;
    private int size;
    private String type;

    public PowerUp(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.size = 20;
        this.type = type;
    }

    public void draw(Graphics g) {
        if (type.equals("HEALTH")) {
            g.setColor(Color.PINK);
        } else if (type.equals("RAPID_FIRE")) {
            g.setColor(Color.CYAN);
        }

        g.fillOval(x, y, size, size);
    }

    public boolean isTouchingPlayer(Player player) {
        return x < player.getX() + player.getSize()
                && x + size > player.getX()
                && y < player.getY() + player.getSize()
                &&y + size > player.getY();
    }

    public String getType() {
        return type;
    }
}
