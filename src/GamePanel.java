import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
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

  Block[][] undergroundMap = new Block[UNDERGROUND_WIDTH][UNDERGROUND_HEIGHT];

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

  public void initializeUndergroundGrid(Block[][] map) {
    Random rand = new Random();

    for (int x = 0; x < UNDERGROUND_WIDTH; x++) {
      for (int y = 0; y < UNDERGROUND_HEIGHT; y++) {
        map[x][y] = new Block(BlockState.STONE);
      }
    }
    int numOfOres = 0;
    while (numOfOres < NUMBER_OF_ORES) {
      int x = rand.nextInt(0, UNDERGROUND_WIDTH);
      int y = rand.nextInt(0, UNDERGROUND_HEIGHT);
      if (map[x][y].getState() == BlockState.STONE) {
        map[x][y] = new Block(BlockState.ORE);
        numOfOres += 1;
      }
    }
    for (Block[] blocks : map) {
      for (Block b : blocks) {
        System.out.print(b.getState() + ", ");
      }
      System.out.println();
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  public void draw(Graphics g) {
    drawUnderground(g);
    drawGrid(g);
    player.draw(g);
  }

  public void drawUnderground(Graphics g){
      // Draw undergroundMap
      for (int x = 0; x < UNDERGROUND_WIDTH; x++) {
          for (int y = 0; y < UNDERGROUND_HEIGHT; y++) {
              int xPos = x * UNIT_SIZE;
              int yPos = SCREEN_WIDTH / 2 + y * UNIT_SIZE;
              Image img = stoneImage;
              if (undergroundMap[x][y].getState() == BlockState.ORE) img = oreImage;
              else if (undergroundMap[x][y].getState() == BlockState.AIR) continue; // Ignore if air (background)

              g.drawImage(img, xPos, yPos, UNIT_SIZE, UNIT_SIZE, null);
          }
      }
  }

  public void drawGrid(Graphics g){
      g.setColor(Color.BLACK);

      // Variables for the column
      int xOneColumn = 0;
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
  }

    private void checkMiningCollisions() {
        // 1. Predict player bounding box for next frame
        float nextX = player.getX() + player.getDx();
        float nextY = player.getY() + player.getDy();

        // 2. Use Math.floor to correctly handle negative coordinates above/left of grid
        int minGridX = (int) Math.floor(nextX / Constants.MapConstants.UNIT_SIZE);
        int maxGridX = (int) Math.floor((nextX + Constants.PlayerConstants.PLAYER_WIDTH - 1) / Constants.MapConstants.UNIT_SIZE);

        int minGridY = (int) Math.floor((nextY - (Constants.MapConstants.SCREEN_HEIGHT / 2)) / Constants.MapConstants.UNIT_SIZE);
        int maxGridY = (int) Math.floor((nextY + Constants.PlayerConstants.PLAYER_HEIGHT - 1 - (Constants.MapConstants.SCREEN_HEIGHT / 2)) / Constants.MapConstants.UNIT_SIZE);

        boolean hitBlock = false;

        // 3. Check all tiles overlapping the player box
        for (int gx = minGridX; gx <= maxGridX; gx++) {
            for (int gy = minGridY; gy <= maxGridY; gy++) {

                // Only interact if tile indices are strictly inside grid boundaries
                if (gx >= 0 && gx < UNDERGROUND_WIDTH && gy >= 0 && gy < UNDERGROUND_HEIGHT) {
                    Block block = undergroundMap[gx][gy];

                    if (block.getState() != BlockState.AIR) {
                        block.takeDamage(1);
                        hitBlock = true;
                    }
                }
            }
        }

        // 4. Trigger bounce if any block was hit
        if (hitBlock) {
            player.interact();
        }
    }

  private void updateGame() {
    checkMiningCollisions();
    player.update();


  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
    updateGame();
  }
}
