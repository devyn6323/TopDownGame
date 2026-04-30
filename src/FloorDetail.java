import java.awt.*;

public class FloorDetail {

    private int x;
    private int y;
    private int type;

    public FloorDetail(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void draw(Graphics g) {
        if (type == 0) {
            g.drawLine(x, y, x + 12, y + 4);
        } else if (type == 1) {
            g.drawOval(x, y, 6, 4);
        } else {
            g.fillRect(x, y, 3, 3);
        }
    }
}
