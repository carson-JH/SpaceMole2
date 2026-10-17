public class Vector2 {
  public float x, y;

  public Vector2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public void add(Vector2 other) {
    x += other.x;
    y += other.y;
  }

  public void scale(float s) {
    x *= s;
    y *= s;
  }

  public float length() {
    return (float) Math.sqrt(x * x + y * y);
  }

  public void normalize() {
    float len = length();
    if (len > 0) {
      x /= len;
      y /= len;
    }
  }
}
