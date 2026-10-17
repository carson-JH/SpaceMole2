import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;

    double playerX = 0;
    double playerY = 0;
    double playerDx = 0;
    double playerDy = 0;

    long time;

    Timer timer;

    GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.white);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        startGame();
    }

    public void startGame() {
        time = System.currentTimeMillis();
        timer = new Timer(16, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {


        g.setColor(Color.black);
        g.fillRect((int) playerX, (int) playerY, UNIT_SIZE, UNIT_SIZE);

        //Variables for the column
        int xOneColumn = 25;
        int yOneColumn = 100;
        int length = 500;

        for (int i = 0; i <= 23; i++) {
            int x = xOneColumn + (i * (0 + UNIT_SIZE));
            g.drawLine(x, yOneColumn, x, yOneColumn + length);
        }

        //Variables for the row
        int xOneRow = 0;
        int yOneRow = 125;
        int width = 600;

        for (int i = 0; i <= 24; i++) {
            int y = yOneRow + (i * (0 + UNIT_SIZE));
            g.drawLine(xOneRow, y, xOneRow + width, y);
        }


    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println(playerX);
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    playerDx -= 0.5;
                    break;

                case KeyEvent.VK_RIGHT:
                    playerDx += 0.5;
                    break;

                case KeyEvent.VK_UP:
                    playerDy += 0.5;
                    break;

                case KeyEvent.VK_DOWN:
                    playerDy -= 0.5;
                    break;
            }
        }
    }

    private void updateGame() {
        playerDx *= 0.9;
        playerDy *= 0.9;

        playerX += playerDx;
        playerY += playerDy;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        updateGame();
        System.out.println("hi");
    }
}
