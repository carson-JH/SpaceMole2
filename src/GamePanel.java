import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {

  private Player player;
  // Class variables
  BufferedImage stoneImage;
  BufferedImage oreImage;

  static final int SCREEN_WIDTH = 600;
  static final int SCREEN_HEIGHT = 600;
  static final int UNIT_SIZE = 25;
  static final int NUMBER_OF_ORES = 80;
  static final int UNDERGROUND_WIDTH = SCREEN_WIDTH / UNIT_SIZE;
  static final int UNDERGROUND_HEIGHT = SCREEN_HEIGHT / 2 / UNIT_SIZE;

  BlockState[][] undergroundMap = new BlockState[UNDERGROUND_WIDTH][UNDERGROUND_HEIGHT];

  double playerX = 0;
  double playerY = 0;
  double playerDx = 0;
  double playerDy = 0;

  long time;

  Timer timer;

  GamePanel() {
    this.setPreferredSize(
        new Dimension(Constants.MapConstants.SCREEN_WIDTH, Constants.MapConstants.SCREEN_HEIGHT));
    this.setBackground(Color.white);
    this.setFocusable(true);

    player = new Player();
    player.addListener(this);

    loadTextures();
    startGame();
  }

  public void loadTextures() {
    try {
      stoneImage = ImageIO.read(new File("assets/stoneTexture.png"));
      oreImage = ImageIO.read(new File("assets/oreTexture.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void startGame() {
    time = System.currentTimeMillis();
    timer = new Timer(16, this);
    timer.start();
    initializeUndergroundGrid(undergroundMap);
  }

  public void initializeUndergroundGrid(BlockState[][] map) {
    Random rand = new Random();

    for (int x = 0; x < UNDERGROUND_WIDTH; x++) {
      for (int y = 0; y < UNDERGROUND_HEIGHT; y++) {
        map[x][y] = BlockState.STONE;
      }
    }
    int numOfStones = 0;
    while (numOfStones < NUMBER_OF_ORES) {
      int x = rand.nextInt(0, UNDERGROUND_WIDTH);
      int y = rand.nextInt(0, UNDERGROUND_HEIGHT);
      if (map[x][y] == BlockState.STONE) {
        map[x][y] = BlockState.ORE;
        numOfStones += 1;
      }
    }
    for (BlockState[] states : map) {
      for (BlockState b : states) {
        System.out.print(b + ", ");
      }
      System.out.println();
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  public void draw(Graphics g) {
    // Draw undergroundMap
    for (int x = 0; x < UNDERGROUND_WIDTH; x++) {
      for (int y = 0; y < UNDERGROUND_HEIGHT; y++) {
        int xPos = x * UNIT_SIZE;
        int yPos = SCREEN_WIDTH / 2 + y * UNIT_SIZE;
        Image img = stoneImage;
        if (undergroundMap[x][y] == BlockState.ORE) img = oreImage;
        // FIXME: charck air texture

        g.drawImage(img, xPos, yPos, UNIT_SIZE, UNIT_SIZE, null);
      }
    }

    g.setColor(Color.BLACK);

    // Variables for the column
    int xOneColumn = 25;
    int yOneColumn = Constants.MapConstants.SCREEN_HEIGHT / 2;
    int length = Constants.MapConstants.SCREEN_HEIGHT / 2;

    for (int i = 0; i <= 23; i++) {
      int x = xOneColumn + (i * (Constants.MapConstants.UNIT_SIZE));
      g.drawLine(x, yOneColumn, x, yOneColumn + length);
    }

    // Variables for the row
    int xOneRow = 0;
    int yOneRow = Constants.MapConstants.SCREEN_HEIGHT / 2;
    int width = Constants.MapConstants.SCREEN_WIDTH;

    for (int i = 0; i <= 24; i++) {
      int y = yOneRow + (i * (Constants.MapConstants.UNIT_SIZE));
      g.drawLine(xOneRow, y, xOneRow + width, y);
    }
    player.draw(g);
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
