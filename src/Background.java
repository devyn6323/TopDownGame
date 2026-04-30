import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Background {
    private int width;
    private int height;
    private int tileSize;
    private ArrayList<FloorDetail> details;
    private Random random;

    public Background(int width, int height) {
        this.width = width;
        this.height = height;
        this.tileSize = 40;
        details = new ArrayList<>();
        random = new Random();

        generateDetails();
    }

    public void draw(Graphics g) {
        //base floor
        g.setColor(new Color(20, 20, 25));
        g.fillRect(0, 0, width, height);

        //tile grid
        g.setColor(new Color(35, 35, 45));

        for (int x = 0; x < width; x += tileSize) {
            g.drawLine(x, 0, x, height);
        }

        for (int y = 0; y < height; y +=tileSize) {
            g.drawLine(0, y, width, y);
        }

        //small floor cracks and details
        g.setColor(new Color(50, 50, 60));

        for (FloorDetail detail : details) {
            detail.draw(g);
        }


        //darker edge border
        g.setColor(new Color(10, 10, 15));
        g.fillRect(0, 0, width, 20);
        g.fillRect(0, height - 20, width, 20);
        g.fillRect(0, 0, 20, height);
        g.fillRect(width - 20, 0, 20, height);
    }

    private void generateDetails() {
        for (int i = 0; i < 60; i++) {
             int x = random.nextInt(width);
             int y = random.nextInt(height);
             int type = random.nextInt(3);

             details.add(new FloorDetail(x, y, type));
        }
    }
}
