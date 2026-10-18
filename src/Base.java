import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Base {
  private int health = Constants.BaseConstants.MAX_HEALTH;
  private int defenseLevel = 0;
  private long lastTurretShot;
  private int damageFlashMs;

  public void update() {
    if (damageFlashMs > 0) {
      damageFlashMs -= 16;
    }
  }

  public void takeDamage(int rawDamage) {
    if (isDestroyed()) return;
    int reduced = Math.max(1, rawDamage - defenseLevel);
    health -= reduced;
    if (health < 0) health = 0;
    damageFlashMs = 180;
  }

  public boolean tryUpgrade(Player player) {
    if (defenseLevel >= Constants.BaseConstants.MAX_DEFENSE_LEVEL) return false;
    int cost = getUpgradeCost();
    if (!player.spendResources(cost)) return false;
    defenseLevel++;
    health = Math.min(Constants.BaseConstants.MAX_HEALTH, health + Constants.BaseConstants.UPGRADE_HEAL);
    return true;
  }

  public int getUpgradeCost() {
    return Constants.BaseConstants.UPGRADE_BASE_COST * (defenseLevel + 1);
  }

  public boolean isDestroyed() {
    return health <= 0;
  }

  public boolean isMaxDefense() {
    return defenseLevel >= Constants.BaseConstants.MAX_DEFENSE_LEVEL;
  }

  public int getHealth() {
    return health;
  }

  public int getDefenseLevel() {
    return defenseLevel;
  }

  public float getCenterX() {
    return Constants.MapConstants.DOME_X + Constants.MapConstants.DOME_WIDTH / 2f;
  }

  public float getCenterY() {
    return Constants.MapConstants.DOME_Y + Constants.MapConstants.DOME_HEIGHT / 2f;
  }

  public Projectile tryShoot(Enemy target, long now) {
    if (target == null || isDestroyed()) return null;
    int cooldown =
        Math.max(280, Constants.BaseConstants.TURRET_COOLDOWN_MS - defenseLevel * 80);
    if (now - lastTurretShot < cooldown) return null;

    float tx = target.getX() + target.getSize() / 2f;
    float ty = target.getY() + target.getSize() / 2f;
    float dx = tx - getCenterX();
    float dy = ty - getCenterY();
    float dist = (float) Math.sqrt(dx * dx + dy * dy);
    if (dist > Constants.BaseConstants.TURRET_RANGE + defenseLevel * 18 || dist < 1) {
      return null;
    }

    lastTurretShot = now;
    float speed = Constants.BaseConstants.PROJECTILE_SPEED + defenseLevel * 0.4f;
    int damage = Constants.BaseConstants.TURRET_DAMAGE + defenseLevel * 2;
    return new Projectile(
        getCenterX() - 4, getCenterY() - 4, (dx / dist) * speed, (dy / dist) * speed, damage, false);
  }

  public void draw(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    int x = Constants.MapConstants.DOME_X;
    int y = Constants.MapConstants.DOME_Y;
    int w = Constants.MapConstants.DOME_WIDTH;
    int h = Constants.MapConstants.DOME_HEIGHT;

    Color wallColor = damageFlashMs > 0 ? new Color(180, 40, 40) : new Color(100, 110, 130);
    if (defenseLevel > 0) {
      wallColor =
          damageFlashMs > 0
              ? new Color(180, 40, 40)
              : new Color(70 + defenseLevel * 8, 120, 90 + defenseLevel * 12);
    }

    g2d.setStroke(new BasicStroke(3 + defenseLevel));
    g2d.setColor(wallColor);
    g2d.drawLine(x, y, x, y + h);
    g2d.drawLine(x + w, y, x + w, y + h);
    g2d.drawLine(x, y, x + w, y);

    // Turret on the roof
    int turretX = x + w / 2 - 6;
    int turretY = y - 10;
    g2d.setColor(new Color(60, 70, 90));
    g2d.fillRect(turretX, turretY, 12, 12);
    g2d.setColor(new Color(80, 180, 255));
    g2d.fillOval(turretX + 2, turretY + 2, 8, 8);

    g2d.setStroke(new BasicStroke(1));

    drawHealthBar(g, x, y, w);
  }

  private void drawHealthBar(Graphics g, int x, int y, int w) {
    int barY = y - 22;
    int barH = 8;
    g.setColor(new Color(40, 40, 40));
    g.fillRect(x, barY, w, barH);
    float ratio = health / (float) Constants.BaseConstants.MAX_HEALTH;
    g.setColor(ratio > 0.4f ? new Color(60, 180, 80) : new Color(200, 50, 50));
    g.fillRect(x, barY, (int) (w * ratio), barH);
    g.setColor(Color.DARK_GRAY);
    g.drawRect(x, barY, w, barH);
  }

  public void drawHud(Graphics g, int resources) {
    g.setFont(new Font("Arial", Font.BOLD, Constants.PlayerConstants.RESOURCE_FONT_SIZE));
    g.setColor(Color.BLACK);
    g.drawString(
        "Collected Resources: " + resources,
        Constants.PlayerConstants.RESOURCE_COUNTER_X,
        Constants.PlayerConstants.RESOURCE_COUNTER_Y);
    g.drawString(
        "Base HP: " + health + "/" + Constants.BaseConstants.MAX_HEALTH,
        Constants.PlayerConstants.RESOURCE_COUNTER_X,
        Constants.PlayerConstants.RESOURCE_COUNTER_Y + 22);
    g.drawString(
        "Defense Lv " + defenseLevel + "/" + Constants.BaseConstants.MAX_DEFENSE_LEVEL,
        Constants.PlayerConstants.RESOURCE_COUNTER_X,
        Constants.PlayerConstants.RESOURCE_COUNTER_Y + 44);

    String upgradeText =
        isMaxDefense()
            ? "Defense MAX"
            : "Near base: press B to upgrade (" + getUpgradeCost() + " resources)";
    g.setFont(new Font("Arial", Font.PLAIN, 14));
    g.drawString(
        upgradeText,
        Constants.PlayerConstants.RESOURCE_COUNTER_X,
        Constants.PlayerConstants.RESOURCE_COUNTER_Y + 64);
    g.drawString(
        "Use the base hatch to enter/exit. You cannot tunnel up through dirt.",
        Constants.PlayerConstants.RESOURCE_COUNTER_X,
        Constants.PlayerConstants.RESOURCE_COUNTER_Y + 82);
  }
}
