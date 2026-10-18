import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyManager {
  private final List<Enemy> enemies = new ArrayList<>();
  private final List<Projectile> projectiles = new ArrayList<>();
  private int waveNumber = 0;
  private long nextWaveAt;
  private long lastPlayerHit;

  public EnemyManager() {
    nextWaveAt = System.currentTimeMillis() + Constants.EnemyConstants.FIRST_WAVE_DELAY_MS;
  }

  public void update(Base base, Player player) {
    long now = System.currentTimeMillis();
    if (!base.isDestroyed() && now >= nextWaveAt) {
      waveNumber++;
      enemies.addAll(Enemy.createWave(waveNumber));
      nextWaveAt = now + waveIntervalFor(waveNumber);
    }

    List<Projectile> newShots = new ArrayList<>();
    for (Enemy enemy : enemies) {
      enemy.update(base, newShots);
      handlePlayerCollision(enemy, player, base, now);
    }
    projectiles.addAll(newShots);

    Enemy turretTarget = nearestEnemy(base);
    Projectile turretShot = base.tryShoot(turretTarget, now);
    if (turretShot != null) {
      projectiles.add(turretShot);
    }

    updateProjectiles(base);
    enemies.removeIf(Enemy::isDead);
  }

  private void handlePlayerCollision(Enemy enemy, Player player, Base base, long now) {
    if (enemy.isDead() || !enemy.collidesWithPlayer(player)) return;

    enemy.takeDamage(Constants.PlayerConstants.PLAYER_CONTACT_DAMAGE);
    if (now - lastPlayerHit >= Constants.PlayerConstants.PLAYER_CONTACT_COOLDOWN_MS) {
      base.takeDamage(2);
      lastPlayerHit = now;
    }
    player.interact();
  }

  private void updateProjectiles(Base base) {
    Iterator<Projectile> iterator = projectiles.iterator();
    while (iterator.hasNext()) {
      Projectile shot = iterator.next();
      shot.update();

      if (shot.isOffScreen() || shot.isSpent()) {
        iterator.remove();
        continue;
      }

      if (shot.hitsBase()) {
        base.takeDamage(shot.getDamage());
        iterator.remove();
        continue;
      }

      if (!shot.isFromEnemy()) {
        boolean hit = false;
        for (Enemy enemy : enemies) {
          if (shot.hitsEnemy(enemy)) {
            enemy.takeDamage(shot.getDamage());
            hit = true;
            break;
          }
        }
        if (hit) {
          iterator.remove();
        }
      }
    }
  }

  private Enemy nearestEnemy(Base base) {
    Enemy best = null;
    float bestDist = Float.MAX_VALUE;
    for (Enemy enemy : enemies) {
      if (enemy.isDead()) continue;
      float cx = enemy.getX() + enemy.getSize() / 2f;
      float cy = enemy.getY() + enemy.getSize() / 2f;
      float dx = cx - base.getCenterX();
      float dy = cy - base.getCenterY();
      float dist = dx * dx + dy * dy;
      if (dist < bestDist) {
        bestDist = dist;
        best = enemy;
      }
    }
    return best;
  }

  public void draw(Graphics g) {
    for (Enemy enemy : enemies) {
      enemy.draw(g);
    }
    for (Projectile shot : projectiles) {
      shot.draw(g);
    }
  }

  private int waveIntervalFor(int completedWave) {
    int interval =
        Constants.EnemyConstants.WAVE_INTERVAL_MS - (completedWave - 1) * 900;
    return Math.max(Constants.EnemyConstants.MIN_WAVE_INTERVAL_MS, interval);
  }

  public int getWaveNumber() {
    return waveNumber;
  }

  public int getSecondsUntilNextWave() {
    long remaining = nextWaveAt - System.currentTimeMillis();
    if (remaining < 0) remaining = 0;
    return (int) Math.ceil(remaining / 1000.0);
  }

  public void reset() {
    enemies.clear();
    projectiles.clear();
    waveNumber = 0;
    lastPlayerHit = 0;
    nextWaveAt = System.currentTimeMillis() + Constants.EnemyConstants.FIRST_WAVE_DELAY_MS;
  }
}
