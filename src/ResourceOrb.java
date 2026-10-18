import java.awt.Color;
import java.awt.Graphics;

public class ResourceOrb {
  private float x;
  private float y;
  private float vx;
  private float vy;
  private final int value;
  private final int size;

  public ResourceOrb(float x, float y, float vx, float vy) {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.value = Constants.ResourceConstants.ORB_VALUE;
    this.size = Constants.ResourceConstants.ORB_SIZE;
  }

  public void update(Block[][] undergroundMap) {
    applyGravity();
    moveAndCollide(undergroundMap);
  }

  public void draw(Graphics g) {
    g.setColor(new Color(255, 196, 64));
    g.fillOval((int) x, (int) y, size, size);
    g.setColor(new Color(255, 236, 170));
    g.fillOval((int) x + 2, (int) y + 2, size / 2, size / 2);
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public int getValue() {
    return value;
  }

  private void applyGravity() {
    vy += Constants.ResourceConstants.GRAVITY;
    if (vy > Constants.ResourceConstants.MAX_FALL_SPEED) {
      vy = Constants.ResourceConstants.MAX_FALL_SPEED;
    }
  }

  private void moveAndCollide(Block[][] undergroundMap) {
    x += vx;
    clampHorizontalToScreen();
    resolveHorizontal(undergroundMap);

    y += vy;
    clampVerticalToScreen();
    resolveVertical(undergroundMap);
  }

  private void clampHorizontalToScreen() {
    int screenWidth = Constants.MapConstants.SCREEN_WIDTH;
    if (x < 0) {
      x = 0;
      bounceHorizontal();
    } else if (x + size > screenWidth) {
      x = screenWidth - size;
      bounceHorizontal();
    }
  }

  private void clampVerticalToScreen() {
    int screenHeight = Constants.MapConstants.SCREEN_HEIGHT;
    if (y + size > screenHeight) {
      y = screenHeight - size;
      bounceVertical();
    } else if (y < 0) {
      y = 0;
      bounceVertical();
    }
  }

  private void resolveHorizontal(Block[][] undergroundMap) {
    boolean hit = false;
    int[] bounds = overlappingTiles();
    for (int gx = bounds[0]; gx <= bounds[1]; gx++) {
      for (int gy = bounds[2]; gy <= bounds[3]; gy++) {
        if (!isSolid(undergroundMap, gx, gy)) {
          continue;
        }
        hit = true;
        float[] block = blockBounds(gx, gy);
        if (vx > 0) {
          x = Math.min(x, block[0] - size);
        } else if (vx < 0) {
          x = Math.max(x, block[2]);
        } else {
          float overlapLeft = (x + size) - block[0];
          float overlapRight = block[2] - x;
          if (overlapLeft < overlapRight) {
            x = block[0] - size;
          } else {
            x = block[2];
          }
        }
      }
    }
    if (hit) {
      bounceHorizontal();
    }
  }

  private void resolveVertical(Block[][] undergroundMap) {
    boolean hit = false;
    int[] bounds = overlappingTiles();
    for (int gx = bounds[0]; gx <= bounds[1]; gx++) {
      for (int gy = bounds[2]; gy <= bounds[3]; gy++) {
        if (!isSolid(undergroundMap, gx, gy)) {
          continue;
        }
        hit = true;
        float[] block = blockBounds(gx, gy);
        if (vy > 0) {
          y = Math.min(y, block[1] - size);
        } else if (vy < 0) {
          y = Math.max(y, block[3]);
        } else {
          float overlapTop = (y + size) - block[1];
          float overlapBottom = block[3] - y;
          if (overlapTop < overlapBottom) {
            y = block[1] - size;
          } else {
            y = block[3];
          }
        }
      }
    }
    if (hit) {
      bounceVertical();
    }
  }

  private int[] overlappingTiles() {
    int unit = Constants.MapConstants.UNIT_SIZE;
    int surfaceY = Constants.MapConstants.SCREEN_HEIGHT / 2;
    int minGX = (int) Math.floor(x / unit);
    int maxGX = (int) Math.floor((x + size - 1) / unit);
    int minGY = (int) Math.floor((y - surfaceY) / unit);
    int maxGY = (int) Math.floor((y + size - 1 - surfaceY) / unit);
    return new int[] {minGX, maxGX, minGY, maxGY};
  }

  private float[] blockBounds(int gridX, int gridY) {
    int unit = Constants.MapConstants.UNIT_SIZE;
    float left = gridX * unit;
    float top = Constants.MapConstants.SCREEN_HEIGHT / 2f + gridY * unit;
    return new float[] {left, top, left + unit, top + unit};
  }

  private boolean isSolid(Block[][] undergroundMap, int gridX, int gridY) {
    if (gridX < 0 || gridY < 0 || gridX >= undergroundMap.length || gridY >= undergroundMap[0].length) {
      return false;
    }
    return undergroundMap[gridX][gridY].getState() != BlockState.AIR;
  }

  private void bounceHorizontal() {
    vx *= -Constants.ResourceConstants.BOUNCE;
    if (Math.abs(vx) < 0.35f) {
      vx = 0;
    }
  }

  private void bounceVertical() {
    vy *= -Constants.ResourceConstants.BOUNCE;
    if (Math.abs(vy) < 0.35f) {
      vy = 0;
    }
  }
}
