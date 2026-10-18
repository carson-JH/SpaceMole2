import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Enemy {
  private float x;
  private float y;
  private final EnemyType type;
  private final float speed;
  private int health;
  private final int maxHealth;
  private long lastAttackTime;
  private boolean comingFromLeft;

  public Enemy(EnemyType type, float x, float y, boolean comingFromLeft, int waveNumber) {
    this.type = type;
    this.x = x;
    this.y = y;
    this.comingFromLeft = comingFromLeft;
    float late = Math.max(0, waveNumber - 8);
    this.maxHealth = (int) (type.maxHealth * (1f + 0.1f * late));
    this.health = this.maxHealth;
    this.speed = type.speed * Math.min(2.4f, 1f + 0.04f * Math.max(0, waveNumber - 10));
  }

  public void update(Base base, List<Projectile> outProjectiles) {
    if (isDead() || base.isDestroyed()) return;

    float targetX = comingFromLeft
        ? Constants.MapConstants.DOME_X - type.size
        : Constants.MapConstants.DOME_X + Constants.MapConstants.DOME_WIDTH;
    float distToBase = horizontalDistanceToBase();

    if (type.ranged) {
      if (distToBase > type.preferredRange) {
        moveToward(targetX);
      } else if (distToBase < type.preferredRange - 20) {
        moveToward(comingFromLeft ? x - 10 : x + 10);
      }
      tryRangedAttack(base, outProjectiles);
    } else {
      if (distToBase > Constants.EnemyConstants.ATTACK_RANGE_PADDING) {
        moveToward(targetX);
      } else {
        tryMeleeAttack(base);
      }
    }

    y = Constants.MapConstants.SURFACE_Y - type.size;
  }

  private void moveToward(float targetX) {
    if (x < targetX) {
      x += speed;
      if (x > targetX) x = targetX;
    } else if (x > targetX) {
      x -= speed;
      if (x < targetX) x = targetX;
    }
  }

  private void tryMeleeAttack(Base base) {
    long now = System.currentTimeMillis();
    if (now - lastAttackTime >= type.attackCooldownMs) {
      base.takeDamage(type.damage);
      lastAttackTime = now;
    }
  }

  private void tryRangedAttack(Base base, List<Projectile> outProjectiles) {
    long now = System.currentTimeMillis();
    if (now - lastAttackTime < type.attackCooldownMs) return;

    float cx = x + type.size / 2f;
    float cy = y + type.size / 2f;
    float dx = base.getCenterX() - cx;
    float dy = base.getCenterY() - cy;
    float dist = (float) Math.sqrt(dx * dx + dy * dy);
    if (dist < 1) return;

    lastAttackTime = now;
    float speed = Constants.EnemyConstants.PROJECTILE_SPEED;
    outProjectiles.add(
        new Projectile(cx - 4, cy - 4, (dx / dist) * speed, (dy / dist) * speed, type.damage, true));
  }

  public void takeDamage(int amount) {
    health -= amount;
    if (health < 0) health = 0;
  }

  public boolean collidesWithPlayer(Player player) {
    return player.getX() < x + type.size
        && player.getX() + Constants.PlayerConstants.PLAYER_WIDTH > x
        && player.getY() < y + type.size
        && player.getY() + Constants.PlayerConstants.PLAYER_HEIGHT > y;
  }

  public void draw(Graphics g) {
    g.setColor(type.color);
    g.fillRect((int) x, (int) y, type.size, type.size);
    g.setColor(type.color.darker());
    g.drawRect((int) x, (int) y, type.size, type.size);

    int barW = type.size;
    int barH = 4;
    g.setColor(Color.DARK_GRAY);
    g.fillRect((int) x, (int) y - 7, barW, barH);
    g.setColor(Color.GREEN);
    g.fillRect((int) x, (int) y - 7, (int) (barW * (health / (float) maxHealth)), barH);

    if (type == EnemyType.BOSS) {
      g.setColor(Color.WHITE);
      g.drawString("BOSS", (int) x, (int) y - 10);
    }
  }

  private float horizontalDistanceToBase() {
    float left = Constants.MapConstants.DOME_X;
    float right = left + Constants.MapConstants.DOME_WIDTH;
    if (x + type.size < left) return left - (x + type.size);
    if (x > right) return x - right;
    return 0;
  }

  public boolean isDead() {
    return health <= 0;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public int getSize() {
    return type.size;
  }

  public EnemyType getType() {
    return type;
  }

  public static List<Enemy> createWave(int waveNumber) {
    List<Enemy> spawned = new ArrayList<>();
    int basicCount = 1 + (waveNumber - 1) / 2;
    int rangedCount = waveNumber < 4 ? 0 : 1 + (waveNumber - 4) / 3;
    int tankCount = waveNumber < 7 ? 0 : 1 + (waveNumber - 7) / 4;
    int bossCount = waveNumber < 8 ? 0 : waveNumber / 8;

    if (waveNumber >= 12) {
      basicCount += waveNumber - 11;
    }
    if (waveNumber >= 16) {
      rangedCount += (waveNumber - 15);
      tankCount += (waveNumber - 15) / 2;
    }
    if (waveNumber >= 20) {
      basicCount += (waveNumber - 19) * 2;
      bossCount += (waveNumber - 19) / 3;
    }

    for (int i = 0; i < basicCount; i++) {
      spawned.add(spawnAtEdge(EnemyType.BASIC, i % 2 == 0, i * 22, waveNumber));
    }
    for (int i = 0; i < rangedCount; i++) {
      spawned.add(spawnAtEdge(EnemyType.RANGED, i % 2 == 1, 50 + i * 26, waveNumber));
    }
    for (int i = 0; i < tankCount; i++) {
      spawned.add(spawnAtEdge(EnemyType.TANK, i % 2 == 0, 80 + i * 32, waveNumber));
    }
    for (int i = 0; i < bossCount; i++) {
      spawned.add(spawnAtEdge(EnemyType.BOSS, (waveNumber + i) % 2 == 0, i * 60, waveNumber));
    }
    return spawned;
  }

  private static Enemy spawnAtEdge(EnemyType type, boolean fromLeft, int stagger, int waveNumber) {
    float y = Constants.MapConstants.SURFACE_Y - type.size;
    float x = fromLeft ? -type.size - stagger : Constants.MapConstants.SCREEN_WIDTH + stagger;
    return new Enemy(type, x, y, fromLeft, waveNumber);
  }
}
