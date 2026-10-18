import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class Player {

  private float x;
  private float y;

  private float Dx;
  private float Dy;

  private int collectedResources = 0;

  private int direction = 90; // clockwise (90 is to the right)

  HashMap<Integer, Boolean> keyMap;

  private boolean upgradePressed;
  private boolean restartPressed;

  public Player() {
    keyMap = new HashMap<>();
    addKeys();
    reset();
  }

  public void reset() {
    x = Constants.MapConstants.DOME_X + 12;
    y = Constants.MapConstants.SURFACE_Y - Constants.PlayerConstants.PLAYER_HEIGHT;
    Dx = 0;
    Dy = 0;
    collectedResources = 0;
    upgradePressed = false;
    restartPressed = false;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float getDx() {
    return Dx;
  }

  public float getDy() {
    return Dy;
  }

  public void setX(float x) {
    this.x = x;
  }

  public void setY(float y) {
    this.y = y;
  }

  public void setDx(float Dx) {
    this.Dx = Dx;
  }

  public void setDy(float Dy) {
    this.Dy = Dy;
  }

  public int getCollectedResources() {
    return collectedResources;
  }

  public void updateVelocity() {
    float resourceSpeedMult = (float) (1.0 - (0.10 * Math.log(collectedResources + 1)));
    resourceSpeedMult = Math.max(0.2f, resourceSpeedMult);
    float speedIncrease = Constants.PlayerConstants.PLAYER_SPEED_INCREASE * resourceSpeedMult;

    if (keyMap.get(KeyEvent.VK_DOWN)) {
      direction = 180;
      if (!checkDownCollision()) {
        Dy += speedIncrease;
      }
    }
    if (keyMap.get(KeyEvent.VK_UP)) {
      direction = 0;
      Dy -= speedIncrease;
    }
    if (keyMap.get(KeyEvent.VK_RIGHT)) {
      direction = 90;
      Dx += speedIncrease;
    }
    if (keyMap.get(KeyEvent.VK_LEFT)) {
      direction = 270;
      Dx -= speedIncrease;
    }
  }

  public void addResources(int numOfResources) {
    collectedResources += numOfResources;
  }

  public boolean spendResources(int amount) {
    if (collectedResources < amount) return false;
    collectedResources -= amount;
    return true;
  }

  public boolean consumeUpgradePressed() {
    if (!upgradePressed) return false;
    upgradePressed = false;
    return true;
  }

  public boolean consumeRestartPressed() {
    if (!restartPressed) return false;
    restartPressed = false;
    return true;
  }

  public boolean isSpacePressed() {
    return keyMap.getOrDefault(KeyEvent.VK_SPACE, false);
  }

  public void draw(Graphics g) {
    g.setColor(Color.GREEN);
    g.fillRect(
        (int) x,
        (int) y,
        Constants.PlayerConstants.PLAYER_WIDTH,
        Constants.PlayerConstants.PLAYER_HEIGHT);
  }

  public void applyMovement() {
    Dx *= Constants.PlayerConstants.IDLE_PLAYER_SPEED_MULT;
    Dy *= Constants.PlayerConstants.IDLE_PLAYER_SPEED_MULT;

    boolean holdingUp = keyMap.getOrDefault(KeyEvent.VK_UP, false);
    if ((!checkDownCollision() && !holdingUp) || keyMap.get(KeyEvent.VK_DOWN)) {
      Dy += Constants.PlayerConstants.PLAYER_GRAVITY;
      if (Dy > Constants.PlayerConstants.MAX_PLAYER_FALL_SPEED)
        Dy = Constants.PlayerConstants.MAX_PLAYER_FALL_SPEED;
    }

    if (Math.abs(Dx) < 0.01f) Dx = 0;
    if (Math.abs(Dy) < 0.01f) Dy = 0;

    // cap the total magnitude of the velocity vector
    float currentSpeed = (float) Math.sqrt((Dx * Dx) + (Dy * Dy));
    float maxSpeed = Constants.PlayerConstants.MAX_PLAYER_SPEED;

    if (currentSpeed > maxSpeed) {
      float ratio = maxSpeed / currentSpeed;
      Dx *= ratio;
      Dy *= ratio;
    }

    x += Dx;
    y += Dy;
    clampToWorld();
  }

  private void clampToWorld() {
    if (x < 0) {
      x = 0;
      Dx = 0;
    }
    int maxX = Constants.MapConstants.SCREEN_WIDTH - Constants.PlayerConstants.PLAYER_WIDTH;
    if (x > maxX) {
      x = maxX;
      Dx = 0;
    }
    if (y < 0) {
      y = 0;
      Dy = 0;
    }
    int maxY = Constants.MapConstants.SCREEN_HEIGHT - Constants.PlayerConstants.PLAYER_HEIGHT;
    if (y > maxY) {
      y = maxY;
      Dy = 0;
    }
  }

  public void interact() {
    float knock = Constants.PlayerConstants.PLAYER_INTERACT_KNOCK;
    switch (direction) {
      case 0 -> Dy = knock;
      case 180 -> Dy = -knock;
      case 90 -> Dx = -knock;
      case 270 -> Dx = knock;
      default -> {
        Dy *= -1.1f;
        Dx *= -1.1f;
      }
    }
  }

  /**
   * Returns the Block directly beneath the player's feet (one tile under the tile that currently
   * contains the player's feet). Returns null if out of bounds or map is null.
   */
  private Block getBlockDirectlyBelow(Block[][] map) {
    if (map == null) return null;

    float footY = y + Constants.PlayerConstants.PLAYER_HEIGHT;
    int gx =
        (int)
            Math.floor(
                (x + Constants.PlayerConstants.PLAYER_WIDTH / 2f)
                    / Constants.MapConstants.UNIT_SIZE);
    int gy =
        (int)
            Math.floor(
                (footY + 1f - (Constants.MapConstants.SCREEN_HEIGHT / 2f))
                    / Constants.MapConstants.UNIT_SIZE);

    if (gx >= 0 && gx < map.length && gy >= 0 && gy < map[0].length) {
      return map[gx][gy];
    }
    return null;
  }

  //  public int[] getTileBelowIndices() {
  //    float footY = y + Constants.PlayerConstants.PLAYER_HEIGHT;
  //    int gx = (int) Math.floor((x + Constants.PlayerConstants.PLAYER_WIDTH / 2f) /
  // Constants.MapConstants.UNIT_SIZE);
  //    int gy = (int) Math.floor((footY - (Constants.MapConstants.SCREEN_HEIGHT / 2f)) /
  // Constants.MapConstants.UNIT_SIZE);
  //    return new int[] {gx, gy + 1};
  //  }

  private boolean checkDownCollision() {
    float footY = y + Constants.PlayerConstants.PLAYER_HEIGHT;
    boolean onSurface =
        y < Constants.MapConstants.SURFACE_Y
            && footY <= Constants.MapConstants.SURFACE_Y + 1.5f
            && !keyMap.get(KeyEvent.VK_DOWN);
    if (onSurface) return true;

    if (getBlockDirectlyBelow(GamePanel.undergroundMap) == null) return false;
    return (getBlockDirectlyBelow(GamePanel.undergroundMap).getState() == BlockState.ORE
            || getBlockDirectlyBelow(GamePanel.undergroundMap).getState() == BlockState.STONE)
        && !keyMap.get(KeyEvent.VK_UP);
  }

  public int getDirection() {
    return direction;
  }

  public boolean isKeyPressed(int keyCode) {
    return keyMap.getOrDefault(keyCode, false);
  }

  public double[] getPosition() {
    return new double[] {x, y};
  }

  private void addKeys() {
    keyMap.put(KeyEvent.VK_LEFT, false);
    keyMap.put(KeyEvent.VK_RIGHT, false);
    keyMap.put(KeyEvent.VK_UP, false);
    keyMap.put(KeyEvent.VK_DOWN, false);
    keyMap.put(KeyEvent.VK_SPACE, false);
    keyMap.put(KeyEvent.VK_B, false);
  }

  public void addListener(GamePanel panel) {
    panel.addKeyListener(new MyKeyAdapter());
  }

  public class MyKeyAdapter extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_DOWN -> keyMap.put(KeyEvent.VK_DOWN, true);
        case KeyEvent.VK_UP -> keyMap.put(KeyEvent.VK_UP, true);
        case KeyEvent.VK_LEFT -> keyMap.put(KeyEvent.VK_LEFT, true);
        case KeyEvent.VK_RIGHT -> keyMap.put(KeyEvent.VK_RIGHT, true);
        case KeyEvent.VK_SPACE -> keyMap.put(KeyEvent.VK_SPACE, true);
        case KeyEvent.VK_B -> {
          keyMap.put(KeyEvent.VK_B, true);
          upgradePressed = true;
        }
        case KeyEvent.VK_R -> restartPressed = true;
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_DOWN -> keyMap.put(KeyEvent.VK_DOWN, false);
        case KeyEvent.VK_UP -> keyMap.put(KeyEvent.VK_UP, false);
        case KeyEvent.VK_LEFT -> keyMap.put(KeyEvent.VK_LEFT, false);
        case KeyEvent.VK_RIGHT -> keyMap.put(KeyEvent.VK_RIGHT, false);
        case KeyEvent.VK_SPACE -> keyMap.put(KeyEvent.VK_SPACE, false);
        case KeyEvent.VK_B -> keyMap.put(KeyEvent.VK_B, false);
      }
    }
  }
}
