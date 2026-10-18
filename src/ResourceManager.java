import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ResourceManager {
  private final List<ResourceOrb> orbs = new ArrayList<>();
  private final Random random = new Random();

  public void spawnFromBrokenOre(int gridX, int gridY) {
    int unit = Constants.MapConstants.UNIT_SIZE;
    int size = Constants.ResourceConstants.ORB_SIZE;
    float worldX = gridX * unit + (unit - size) / 2f;
    float worldY = Constants.MapConstants.SCREEN_HEIGHT / 2f + gridY * unit + (unit - size) / 2f;
    float vx = (random.nextFloat() * 2f - 1f) * Constants.ResourceConstants.SPAWN_NUDGE;
    float vy = Constants.ResourceConstants.SPAWN_POP_SPEED;
    orbs.add(new ResourceOrb(worldX, worldY, vx, vy));
  }

  public int collectNearbyOrbs(float playerX, float playerY) {
    int collectedValue = 0;

    // Calculate player center point
    float pCenterX = playerX + (Constants.PlayerConstants.PLAYER_WIDTH / 2.0f);
    float pCenterY = playerY + (Constants.PlayerConstants.PLAYER_HEIGHT / 2.0f);

    Iterator<ResourceOrb> iterator = orbs.iterator();
    while (iterator.hasNext()) {
      ResourceOrb orb = iterator.next();

      // Calculate orb center point
      float orbCenterX = orb.getX() + (Constants.ResourceConstants.ORB_SIZE / 2.0f);
      float orbCenterY = orb.getY() + (Constants.ResourceConstants.ORB_SIZE / 2.0f);

      // Calculate distance between player and orb
      float dx = pCenterX - orbCenterX;
      float dy = pCenterY - orbCenterY;
      float distance = (float) Math.sqrt((dx * dx) + (dy * dy));

      if (distance <= Constants.PlayerConstants.PICKUP_RADIUS) {
        collectedValue += orb.getValue();
        iterator.remove(); // Safely remove the orb from the world
      }
    }

    return collectedValue;
  }

  public void update(Block[][] undergroundMap) {
    for (ResourceOrb orb : orbs) {
      orb.update(undergroundMap);
    }
  }

  public void draw(Graphics g) {
    for (ResourceOrb orb : orbs) {
      orb.draw(g);
    }
  }

  public List<ResourceOrb> getOrbs() {
    return orbs;
  }
}
