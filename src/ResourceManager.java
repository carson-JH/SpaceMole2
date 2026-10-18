import java.awt.Graphics;
import java.util.ArrayList;
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
