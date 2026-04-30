import java.awt.*;

public class FloatingText {

    private int x;
    private int y;
    private String text;
    private int timer;

    public FloatingText(int x, int y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.timer = 60;
    }

    public void update() {
        y--;
        timer--;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
    }

    public boolean isDone() {
        return timer <= 0;
    }
}
