import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {

  private Player player;

  long time;

  Timer timer;

  GamePanel() {
    this.setPreferredSize(
        new Dimension(Constants.MapConstants.SCREEN_WIDTH, Constants.MapConstants.SCREEN_HEIGHT));
    this.setBackground(Color.white);
    this.setFocusable(true);

    player = new Player();
    player.addListener(this);

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
    player.draw(g);

    g.setColor(Color.black);

    // Variables for the column
    int xOneColumn = 25;
    int yOneColumn = 100;
    int length = 500;

    for (int i = 0; i <= 23; i++) {
      int x = xOneColumn + (i * (0 + Constants.MapConstants.UNIT_SIZE));
      g.drawLine(x, yOneColumn, x, yOneColumn + length);
    }

    // Variables for the row
    int xOneRow = 0;
    int yOneRow = 125;
    int width = 600;

    for (int i = 0; i <= 24; i++) {
      int y = yOneRow + (i * (0 + Constants.MapConstants.UNIT_SIZE));
      g.drawLine(xOneRow, y, xOneRow + width, y);
    }
  }

  private void updateGame() {
    player.update();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
    updateGame();
  }
}
