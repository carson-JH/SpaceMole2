import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {

  static final int SCREEN_WIDTH = 600;
  static final int SCREEN_HEIGHT = 600;
  static final int UNIT_SIZE = 25;


  GamePanel() {
    this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    this.setBackground(Color.white);
    this.setFocusable(true);
    this.addKeyListener(new MyKeyAdapter());

    startGame();
  }

  public void startGame() {

  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  public void draw(Graphics g) {


  }


  public class MyKeyAdapter extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:

          break;

        case KeyEvent.VK_RIGHT:

          break;

        case KeyEvent.VK_UP:

          break;

        case KeyEvent.VK_DOWN:

          break;
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }
}
