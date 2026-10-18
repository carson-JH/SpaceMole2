import java.awt.Color;

public enum EnemyType {
  BASIC(22, 0.75f, 4, 800, 22, 36, false, new Color(200, 55, 55)),
  RANGED(16, 0.65f, 3, 1100, 20, 110, true, new Color(170, 70, 210)),
  TANK(55, 0.4f, 7, 1000, 30, 32, false, new Color(70, 80, 95)),
  BOSS(140, 0.45f, 12, 850, 44, 48, false, new Color(120, 20, 20));

  public final int maxHealth;
  public final float speed;
  public final int damage;
  public final int attackCooldownMs;
  public final int size;
  public final int preferredRange;
  public final boolean ranged;
  public final Color color;

  EnemyType(
      int maxHealth,
      float speed,
      int damage,
      int attackCooldownMs,
      int size,
      int preferredRange,
      boolean ranged,
      Color color) {
    this.maxHealth = maxHealth;
    this.speed = speed;
    this.damage = damage;
    this.attackCooldownMs = attackCooldownMs;
    this.size = size;
    this.preferredRange = preferredRange;
    this.ranged = ranged;
    this.color = color;
  }
}
