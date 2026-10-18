import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {

  private Player player;
  private ResourceManager resourceManager;
  private Base base;
  private EnemyManager enemyManager;
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
  long lastMineTime;

  Timer timer;

  GamePanel() {
    this.setPreferredSize(
        new Dimension(Constants.MapConstants.SCREEN_WIDTH, Constants.MapConstants.SCREEN_HEIGHT));
    this.setBackground(Color.white);
    this.setFocusable(true);

    player = new Player();
    player.addListener(this);
    resourceManager = new ResourceManager();
    base = new Base();
    enemyManager = new EnemyManager();

    loadTextures();
    startGame();
  }

  public void loadTextures() {
    stoneImage = loadImage("stoneTexture.png");
    oreImage = loadImage("oreTexture.png");
    if (stoneImage == null) {
      stoneImage = makeFallbackTexture(new Color(118, 128, 142), new Color(72, 82, 96));
    }
    if (oreImage == null) {
      oreImage = makeFallbackTexture(new Color(28, 22, 18), new Color(220, 110, 30));
    }
  }

  private BufferedImage loadImage(String fileName) {
    String[] classpathPaths = {
      "/" + fileName, "/assets/" + fileName, fileName, "assets/" + fileName
    };
    for (String path : classpathPaths) {
      try (InputStream in = GamePanel.class.getResourceAsStream(path)) {
        if (in != null) {
          return ImageIO.read(in);
        }
      } catch (IOException ignored) {
      }
    }

    File dir = new File(System.getProperty("user.dir"));
    for (int i = 0; i < 6 && dir != null; i++) {
      File file = new File(dir, "assets/" + fileName);
      if (file.isFile()) {
        try {
          return ImageIO.read(file);
        } catch (IOException ignored) {
        }
      }
      dir = dir.getParentFile();
    }
    System.err.println("Could not load texture: " + fileName);
    return null;
  }

  private BufferedImage makeFallbackTexture(Color base, Color accent) {
    BufferedImage img = new BufferedImage(UNIT_SIZE, UNIT_SIZE, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    g.setColor(base);
    g.fillRect(0, 0, UNIT_SIZE, UNIT_SIZE);
    g.setColor(accent);
    g.fillRect(3, 3, 8, 8);
    g.fillRect(14, 12, 7, 7);
    g.fillRect(6, 16, 6, 6);
    g.dispose();
    return img;
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
    // The entire underside of the base is open so the player can enter/exit
    // without colliding with the top row of dirt (which was snapping/teleporting them).
    int baseMinGx =
        (int) Math.floor(Constants.MapConstants.DOME_X / (float) Constants.MapConstants.UNIT_SIZE);
    int baseMaxGx =
        (int)
            Math.floor(
                (Constants.MapConstants.DOME_X + Constants.MapConstants.DOME_WIDTH - 1)
                    / (float) Constants.MapConstants.UNIT_SIZE);
    for (int x = baseMinGx; x <= baseMaxGx; x++) {
      if (x >= 0 && x < UNDERGROUND_WIDTH) {
        map[x][0] = new Block(BlockState.AIR);
      }
    }
    int hatchGx = UNDERGROUND_WIDTH / 2;
    for (int x = hatchGx - 1; x <= hatchGx + 1; x++) {
      for (int y = 0; y <= 1; y++) {
        if (x >= 0 && x < UNDERGROUND_WIDTH) {
          map[x][y] = new Block(BlockState.AIR);
        }
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
    drawFloor(g);
    base.draw(g);
    drawGrid(g);
    resourceManager.draw(g);
    enemyManager.draw(g);
    player.draw(g);
    base.drawHud(g, player.getCollectedResources());
    drawWave(g);
    if (base.isDestroyed()) {
      drawGameOver(g);
    }
  }

  public void drawUnderground(Graphics g) {
    // Draw undergroundMap
    for (int x = 0; x < UNDERGROUND_WIDTH; x++) {
      for (int y = 0; y < UNDERGROUND_HEIGHT; y++) {
        int xPos = x * UNIT_SIZE;
        int yPos = SCREEN_WIDTH / 2 + y * UNIT_SIZE;
        BlockState state = undergroundMap[x][y].getState();
        if (state == BlockState.AIR) {
          continue;
        }
        Image img = state == BlockState.ORE ? oreImage : stoneImage;
        if (img != null) {
          g.drawImage(img, xPos, yPos, UNIT_SIZE, UNIT_SIZE, null);
        } else {
          g.setColor(state == BlockState.ORE ? new Color(180, 80, 20) : new Color(110, 120, 130));
          g.fillRect(xPos, yPos, UNIT_SIZE, UNIT_SIZE);
        }
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

  public void drawFloor(Graphics g) {
    int surfaceY = Constants.MapConstants.SURFACE_Y;
    int hatchLeft = Constants.MapConstants.DOME_X;
    int hatchRight = Constants.MapConstants.DOME_X + Constants.MapConstants.DOME_WIDTH;
    int thickness = Constants.MapConstants.FLOOR_THICKNESS;

    g.setColor(new Color(70, 78, 88));
    g.fillRect(0, surfaceY - thickness / 2, hatchLeft, thickness);
    g.fillRect(hatchRight, surfaceY - thickness / 2, SCREEN_WIDTH - hatchRight, thickness);

    g.setColor(new Color(40, 32, 24));
    g.fillRect(hatchLeft, surfaceY - 2, hatchRight - hatchLeft, 4);
  }

  public void drawWave(Graphics g) {
    g.setFont(new Font("Arial", Font.BOLD, 16));
    g.setColor(Color.BLACK);
    String wave = "Wave: " + enemyManager.getWaveNumber();
    g.drawString(wave, SCREEN_WIDTH - 110, 26);
  }

  public void drawGameOver(Graphics g) {
    g.setColor(new Color(0, 0, 0, 140));
    g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 36));
    g.drawString("BASE DESTROYED", 130, 260);
    g.setFont(new Font("Arial", Font.PLAIN, 18));
    g.drawString("The base is your health. Press R to restart.", 110, 300);
  }

  //  private void checkMiningCollisions() {
  //    // 1. Predict player bounding box for next frame
  //    float nextX = player.getX() + player.getDx();
  //    float nextY = player.getY() + player.getDy();
  //
  //    int minGridX = (int) Math.floor(nextX / Constants.MapConstants.UNIT_SIZE);
  //    int maxGridX =
  //        (int)
  //            Math.floor(
  //                (nextX + Constants.PlayerConstants.PLAYER_WIDTH - 1)
  //                    / Constants.MapConstants.UNIT_SIZE);
  //
  //    int minGridY =
  //        (int)
  //            Math.floor(
  //                (nextY - (Constants.MapConstants.SCREEN_HEIGHT / 2))
  //                    / Constants.MapConstants.UNIT_SIZE);
  //    int maxGridY =
  //        (int)
  //            Math.floor(
  //                (nextY
  //                        + Constants.PlayerConstants.PLAYER_HEIGHT
  //                        - 1
  //                        - (Constants.MapConstants.SCREEN_HEIGHT / 2))
  //                    / Constants.MapConstants.UNIT_SIZE);
  //
  //    boolean hitBlock = false;
  //
  //    for (int gx = minGridX; gx <= maxGridX; gx++) {
  //      for (int gy = minGridY; gy <= maxGridY; gy++) {
  //        if (gx >= 0 && gx < UNDERGROUND_WIDTH && gy >= 0 && gy < UNDERGROUND_HEIGHT) {
  //          Block block = undergroundMap[gx][gy];
  //
  //          if (block.getState() == BlockState.AIR) continue;
  //
  //          // compute block position in screen coords (same as draw)
  //          float blockX = gx * Constants.MapConstants.UNIT_SIZE;
  //          float blockY =
  //              (Constants.MapConstants.SCREEN_HEIGHT / 2) + gy *
  // Constants.MapConstants.UNIT_SIZE;
  //          float playerCenterX = nextX + Constants.PlayerConstants.PLAYER_WIDTH / 2f;
  //          float playerCenterY = nextY + Constants.PlayerConstants.PLAYER_HEIGHT / 2f;
  //          float blockCenterX = blockX + Constants.MapConstants.UNIT_SIZE / 2f;
  //          float blockCenterY = blockY + Constants.MapConstants.UNIT_SIZE / 2f;
  //
  //          float dx = blockCenterX - playerCenterX;
  //          float dy = blockCenterY - playerCenterY;
  //
  //          int requiredKey;
  //          if (Math.abs(dx) > Math.abs(dy)) {
  //            requiredKey = dx > 0 ? KeyEvent.VK_RIGHT : KeyEvent.VK_LEFT;
  //          } else {
  //            requiredKey = dy > 0 ? KeyEvent.VK_DOWN : KeyEvent.VK_UP;
  //          }
  //
  //          if (!player.isKeyPressed(requiredKey)) {
  //            continue;
  //          }
  //
  //          BlockState before = block.getState();
  //          block.takeDamage(1);
  //          if (before == BlockState.ORE && block.getState() == BlockState.AIR) {
  //            resourceManager.spawnFromBrokenOre(gx, gy);
  //          }
  //          hitBlock = true;
  //        }
  //      }
  //    }
  //
  //    if (hitBlock) {
  //      player.interact();
  //    }
  //  }

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
                (nextY - (Constants.MapConstants.SCREEN_HEIGHT / 2f))
                    / Constants.MapConstants.UNIT_SIZE);
    int maxGridY =
        (int)
            Math.floor(
                (nextY
                        + Constants.PlayerConstants.PLAYER_HEIGHT
                        - 1
                        - (Constants.MapConstants.SCREEN_HEIGHT / 2f))
                    / Constants.MapConstants.UNIT_SIZE);

    boolean hitBlock = false;

    for (int gx = minGridX; gx <= maxGridX; gx++) {
      for (int gy = minGridY; gy <= maxGridY; gy++) {
        if (gx >= 0 && gx < UNDERGROUND_WIDTH && gy >= 0 && gy < UNDERGROUND_HEIGHT) {
          Block block = undergroundMap[gx][gy];

          if (block.getState() == BlockState.AIR) continue;
          // Surface-row tiles under the base are a shaft, not a wall.
          if (isSurfaceBlockUnderBase(gx, gy)) continue;

          float blockX = gx * Constants.MapConstants.UNIT_SIZE;
          float blockY =
              (Constants.MapConstants.SCREEN_HEIGHT / 2f) + gy * Constants.MapConstants.UNIT_SIZE;
          float playerCenterX = nextX + Constants.PlayerConstants.PLAYER_WIDTH / 2f;
          float playerCenterY = nextY + Constants.PlayerConstants.PLAYER_HEIGHT / 2f;
          float blockCenterX = blockX + Constants.MapConstants.UNIT_SIZE / 2f;
          float blockCenterY = blockY + Constants.MapConstants.UNIT_SIZE / 2f;

          float dx = blockCenterX - playerCenterX;
          float dy = blockCenterY - playerCenterY;

          int requiredKey;

          // SNAP TO BOUNDARY: Treat the block as a solid wall before attempting to mine it
          if (Math.abs(dx) > Math.abs(dy)) {
            requiredKey = dx > 0 ? KeyEvent.VK_RIGHT : KeyEvent.VK_LEFT;

            // Snap X position to the edge of the block
            if (dx > 0) {
              player.setX(blockX - Constants.PlayerConstants.PLAYER_WIDTH);
            } else {
              player.setX(blockX + Constants.MapConstants.UNIT_SIZE);
            }
            player.setDx(0);
            nextX = player.getX(); // Update prediction so we don't double-snap inside the loop

          } else {
            requiredKey = dy > 0 ? KeyEvent.VK_DOWN : KeyEvent.VK_UP;

            // Snap Y position to the edge of the block
            if (dy > 0) {
              player.setY(blockY - Constants.PlayerConstants.PLAYER_HEIGHT);
            } else {
              player.setY(blockY + Constants.MapConstants.UNIT_SIZE);
            }
            player.setDy(0);
            nextY = player.getY(); // Update prediction so we don't double-snap inside the loop
          }

          // If they aren't actively pushing into the block, stop here (acts as a solid wall)
          if (!player.isKeyPressed(requiredKey)) {
            continue;
          }

          // Proceed with mining logic
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

  private boolean playerIsNearBase() {
    float pCenterX = player.getX() + Constants.PlayerConstants.PLAYER_WIDTH / 2f;
    float pCenterY = player.getY() + Constants.PlayerConstants.PLAYER_HEIGHT / 2f;
    float dx = pCenterX - base.getCenterX();
    float dy = pCenterY - base.getCenterY();
    return Math.sqrt(dx * dx + dy * dy) <= 140;
  }

  /** True when the player is fully between the base walls (open shaft through the floor). */
  private boolean isInBaseInterior() {
    float pLeft = player.getX();
    float pRight = player.getX() + Constants.PlayerConstants.PLAYER_WIDTH;
    float bLeft = Constants.MapConstants.DOME_X;
    float bRight = bLeft + Constants.MapConstants.DOME_WIDTH;
    return pLeft >= bLeft && pRight <= bRight;
  }

  private boolean isSurfaceBlockUnderBase(int gx, int gy) {
    if (gy != 0) return false;
    float blockLeft = gx * Constants.MapConstants.UNIT_SIZE;
    float blockRight = blockLeft + Constants.MapConstants.UNIT_SIZE;
    float bLeft = Constants.MapConstants.DOME_X;
    float bRight = bLeft + Constants.MapConstants.DOME_WIDTH;
    return blockRight > bLeft && blockLeft < bRight;
  }

  private void checkSurfaceFloor() {
    float surfaceY = Constants.MapConstants.SURFACE_Y;
    float pHeight = Constants.PlayerConstants.PLAYER_HEIGHT;

    // No floor inside the base: only the walls and the roof should block movement.
    if (isInBaseInterior()) {
      return;
    }

    float y = player.getY();
    float feet = y + pHeight;

    // Overlapping the surface line: snap to the side the player is mostly on.
    // Fully underground players are left alone so they can still fly/move up in tunnels.
    if (y < surfaceY && feet > surfaceY) {
      float overlapAbove = surfaceY - y;
      float overlapBelow = feet - surfaceY;
      if (overlapAbove >= overlapBelow) {
        player.setY(surfaceY - pHeight);
        if (player.getDy() > 0) {
          player.setDy(0);
        }
      } else {
        player.setY(surfaceY);
        if (player.getDy() < 0) {
          player.setDy(0);
        }
      }
    }
  }

  private void restartGame() {
    player.reset();
    resourceManager = new ResourceManager();
    base = new Base();
    enemyManager.reset();
    initializeUndergroundGrid(undergroundMap);
  }

  private void updateGame() {
    if (player.consumeRestartPressed()) {
      restartGame();
    }

    if (base.isDestroyed()) {
      return;
    }

    if (player.consumeUpgradePressed() && playerIsNearBase()) {
      base.tryUpgrade(player);
    }

    player.updateVelocity();
    checkMiningCollisions();
    checkBaseCollisions();
    checkSurfaceFloor();
    player.applyMovement();
    checkSurfaceFloor();
    checkResourcePickup();
    resourceManager.update(undergroundMap);
    base.update();
    enemyManager.update(base, player);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
    updateGame();
  }
}
