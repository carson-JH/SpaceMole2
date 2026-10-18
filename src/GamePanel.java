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
  private ResourceManager resourceManager;
  // Class variables
  BufferedImage stoneImage;
  BufferedImage oreImage;

  static final int SCREEN_WIDTH = 600;
  static final int SCREEN_HEIGHT = 600;
  static final int UNIT_SIZE = 25;
  static final int NUMBER_OF_ORES = 80;
  static final int UNDERGROUND_WIDTH = SCREEN_WIDTH / UNIT_SIZE;
  static final int UNDERGROUND_HEIGHT = SCREEN_HEIGHT / 2 / UNIT_SIZE;

  public static Block[][] undergroundMap = new Block[UNDERGROUND_WIDTH][UNDERGROUND_HEIGHT];

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
    resourceManager = new ResourceManager();

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
    drawBase(g);
    drawGrid(g);
    resourceManager.draw(g);
    player.draw(g);
    drawResourceCounter(g);
  }

  public void drawUnderground(Graphics g) {
    // Draw undergroundMap
    for (int x = 0; x < UNDERGROUND_WIDTH; x++) {
      for (int y = 0; y < UNDERGROUND_HEIGHT; y++) {
        int xPos = x * UNIT_SIZE;
        int yPos = SCREEN_WIDTH / 2 + y * UNIT_SIZE;
        Image img = stoneImage;
        if (undergroundMap[x][y].getState() == BlockState.ORE) img = oreImage;
        else if (undergroundMap[x][y].getState() == BlockState.AIR)
          continue; // Ignore if air (background)

        g.drawImage(img, xPos, yPos, UNIT_SIZE, UNIT_SIZE, null);
      }
    }
  }

  public void drawGrid(Graphics g) {
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

  public void drawResourceCounter(Graphics g) {
    g.setFont(new Font("Arial", Font.BOLD, Constants.PlayerConstants.RESOURCE_FONT_SIZE));
    g.setColor(Color.BLACK);
    g.drawString(
        "Collected Resources: " + player.getCollectedResources(),
        Constants.PlayerConstants.RESOURCE_COUNTER_X,
        Constants.PlayerConstants.RESOURCE_COUNTER_Y);
  }

  public void drawBase(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    g2d.setStroke(new BasicStroke(3));
    g2d.setColor(new Color(100, 110, 130)); // Metallic blue-grey

    int x = Constants.MapConstants.DOME_X;
    int y = Constants.MapConstants.DOME_Y;
    int w = Constants.MapConstants.DOME_WIDTH;
    int h = Constants.MapConstants.DOME_HEIGHT;

    // Draw Left Wall
    g2d.drawLine(x, y, x, y + h);

    // Draw Right Wall
    g2d.drawLine(x + w, y, x + w, y + h);

    // Draw Top Ceiling
    g2d.drawLine(x, y, x + w, y);

    // Reset stroke thickness back to default 1px
    g2d.setStroke(new BasicStroke(1));
  }

  private void checkMiningCollisions() {
    // 1. Predict player bounding box for next frame
    float nextX = player.getX() + player.getDx();
    float nextY = player.getY() + player.getDy();

    int minGridX = (int) Math.floor(nextX / Constants.MapConstants.UNIT_SIZE);
    int maxGridX =
        (int)
            Math.floor(
                (nextX + Constants.PlayerConstants.PLAYER_WIDTH - 1)
                    / Constants.MapConstants.UNIT_SIZE);

    int minGridY =
        (int)
            Math.floor(
                (nextY - (Constants.MapConstants.SCREEN_HEIGHT / 2))
                    / Constants.MapConstants.UNIT_SIZE);
    int maxGridY =
        (int)
            Math.floor(
                (nextY
                        + Constants.PlayerConstants.PLAYER_HEIGHT
                        - 1
                        - (Constants.MapConstants.SCREEN_HEIGHT / 2))
                    / Constants.MapConstants.UNIT_SIZE);

    boolean hitBlock = false;

    for (int gx = minGridX; gx <= maxGridX; gx++) {
      for (int gy = minGridY; gy <= maxGridY; gy++) {
        if (gx >= 0 && gx < UNDERGROUND_WIDTH && gy >= 0 && gy < UNDERGROUND_HEIGHT) {
          Block block = undergroundMap[gx][gy];

          if (block.getState() == BlockState.AIR) continue;

          // compute block position in screen coords (same as draw)
          float blockX = gx * Constants.MapConstants.UNIT_SIZE;
          float blockY =
              (Constants.MapConstants.SCREEN_HEIGHT / 2) + gy * Constants.MapConstants.UNIT_SIZE;
          float playerCenterX = nextX + Constants.PlayerConstants.PLAYER_WIDTH / 2f;
          float playerCenterY = nextY + Constants.PlayerConstants.PLAYER_HEIGHT / 2f;
          float blockCenterX = blockX + Constants.MapConstants.UNIT_SIZE / 2f;
          float blockCenterY = blockY + Constants.MapConstants.UNIT_SIZE / 2f;

          float dx = blockCenterX - playerCenterX;
          float dy = blockCenterY - playerCenterY;

          int requiredKey;
          if (Math.abs(dx) > Math.abs(dy)) {
            requiredKey = dx > 0 ? KeyEvent.VK_RIGHT : KeyEvent.VK_LEFT;
          } else {
            requiredKey = dy > 0 ? KeyEvent.VK_DOWN : KeyEvent.VK_UP;
          }

          if (!player.isKeyPressed(requiredKey)) {
            continue;
          }

          BlockState before = block.getState();
          block.takeDamage(1);
          if (before == BlockState.ORE && block.getState() == BlockState.AIR) {
            resourceManager.spawnFromBrokenOre(gx, gy);
          }
          hitBlock = true;
        }
      }
    }

    if (hitBlock) {
      player.interact();
    }
  }

  private void checkBaseCollisions() {
    float bLeft = Constants.MapConstants.DOME_X;
    float bRight = Constants.MapConstants.DOME_X + Constants.MapConstants.DOME_WIDTH;
    float bTop = Constants.MapConstants.DOME_Y;
    float bBottom = Constants.MapConstants.DOME_Y + Constants.MapConstants.DOME_HEIGHT;
    float pWidth = Constants.PlayerConstants.PLAYER_WIDTH;
    float pHeight = Constants.PlayerConstants.PLAYER_HEIGHT;

    // Horizontal resolution
    float nextX = player.getX() + player.getDx();
    float pY = player.getY();

    // Only process wall collisions if the player is vertically alongside the base
    if (pY + pHeight > bTop && pY < bBottom) {

      // LEFT WALL - Hitting from outside (moving right)
      if (player.getX() + pWidth <= bLeft && nextX + pWidth > bLeft) {
        player.setX(bLeft - pWidth);
        player.setDx(0);
      }
      // LEFT WALL - Hitting from inside (moving left)
      else if (player.getX() >= bLeft && nextX < bLeft) {
        player.setX(bLeft);
        player.setDx(0);
      }
      // RIGHT WALL - Hitting from outside (moving left)
      else if (player.getX() >= bRight && nextX < bRight) {
        player.setX(bRight);
        player.setDx(0);
      }
      // RIGHT WALL - Hitting from inside (moving right)
      else if (player.getX() + pWidth <= bRight && nextX + pWidth > bRight) {
        player.setX(bRight - pWidth);
        player.setDx(0);
      }
    }

    // Vertical resolution
    float pX = player.getX(); // Use updated X after horizontal checks
    float nextY = player.getY() + player.getDy();

    if (pX + pWidth > bLeft && pX < bRight) {

      if (player.getY() + pHeight <= bTop && nextY + pHeight > bTop) {
        player.setY(bTop - pHeight);
        player.setDy(0);
      } else if (player.getY() >= bTop && nextY < bTop) {
        player.setY(bTop);
        player.setDy(0);
      }
    }
  }

  public void checkResourcePickup() {
    if (player.isSpacePressed()) {
      int collected = resourceManager.collectNearbyOrbs(player.getX(), player.getY());
      if (collected > 0) player.addResources(collected);
    }
  }

  private void updateGame() {
    player.updateVelocity();
    checkMiningCollisions();
    checkBaseCollisions();
    player.applyMovement();
    checkResourcePickup();
    resourceManager.update(undergroundMap);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
    updateGame();
  }
}
