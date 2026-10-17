public class Enemy {
  private double x;
  private double y;

  private double Dx;
  private double Dy;

  public Enemy(EnemyType type, double x, double y) {}

  public void update() {
    move();
  }

  private void move() {
    Dx *= 0.9;
    Dy *= 0.9;

    x += Dx;
    y += Dy;
  }

  public boolean attack() {
    return false; // FIXME: implement attacking
  }

  public double[] getPosition() {
    return new double[] {x, y};
  }
}
