import java.awt.Color;
import java.awt.Graphics;

public class Projectile {
  private float x;
  private float y;
  private final float vx;
  private final float vy;
  private final int damage;
  private final boolean fromEnemy;
  private final int size;
  private boolean spent;

  public Projectile(float x, float y, float vx, float vy, int damage, boolean fromEnemy) {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.damage = damage;
    this.fromEnemy = fromEnemy;
    this.size = Constants.EnemyConstants.PROJECTILE_SIZE;
  }

  public void update() {
    x += vx;
    y += vy;
  }

  public void draw(Graphics g) {
    g.setColor(fromEnemy ? new Color(255, 90, 40) : new Color(80, 180, 255));
    g.fillOval((int) x, (int) y, size, size);
  }

  public boolean isOffScreen() {
    return x < -20
        || y < -20
        || x > Constants.MapConstants.SCREEN_WIDTH + 20
        || y > Constants.MapConstants.SCREEN_HEIGHT + 20;
  }

  public boolean hitsBase() {
    if (!fromEnemy) return false;
    float left = Constants.MapConstants.DOME_X;
    float right = left + Constants.MapConstants.DOME_WIDTH;
    float top = Constants.MapConstants.DOME_Y;
    float bottom = top + Constants.MapConstants.DOME_HEIGHT;
    return x + size > left && x < right && y + size > top && y < bottom;
  }

  public boolean hitsEnemy(Enemy enemy) {
    if (fromEnemy || enemy.isDead()) return false;
    return x + size > enemy.getX()
        && x < enemy.getX() + enemy.getSize()
        && y + size > enemy.getY()
        && y < enemy.getY() + enemy.getSize();
  }

  public int getDamage() {
    return damage;
  }

  public boolean isFromEnemy() {
    return fromEnemy;
  }

  public boolean isSpent() {
    return spent;
  }

  public void spend() {
    spent = true;
  }
}
