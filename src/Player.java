import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class Player {

  private float x;
  private float y;

  private float Dx;
  private float Dy;

  private int direction = 90; // clockwise (90 is to the right)

  HashMap<Integer, Boolean> keyMap;

  public Player() {
    keyMap = new HashMap<>();
    addKeys();
  }

  public float getX(){return x;}
    public float getY(){return y;}
    public float getDx(){return Dx;}
    public float getDy(){return Dy;}

  public void update() {
    move();

    if (keyMap.get(KeyEvent.VK_DOWN)) {
      direction = 180;
      Dy += Constants.PlayerConstants.PLAYER_SPEED_INCREASE;
    }
    if (keyMap.get(KeyEvent.VK_UP)) {
      direction = 0;
      Dy -= Constants.PlayerConstants.PLAYER_SPEED_INCREASE;
    }
    if (keyMap.get(KeyEvent.VK_RIGHT)) {
      direction = 90;
      Dx += Constants.PlayerConstants.PLAYER_SPEED_INCREASE;
    }
    if (keyMap.get(KeyEvent.VK_LEFT)) {
      direction = 270;
      Dx -= Constants.PlayerConstants.PLAYER_SPEED_INCREASE;
    }
  }

  public void draw(Graphics g) {
    g.setColor(Color.GREEN);
    g.fillRect(
        (int) x,
        (int) y,
        Constants.PlayerConstants.PLAYER_WIDTH,
        Constants.PlayerConstants.PLAYER_HEIGHT);
  }

  private void move() {
    Dx *= Constants.PlayerConstants.IDLE_PLAYER_SPEED_MULT;
    Dy *= Constants.PlayerConstants.IDLE_PLAYER_SPEED_MULT;

    if (Math.abs(Dx) < 0.01f) Dx = 0;
    if (Math.abs(Dy) < 0.01f) Dy = 0;

    // cap the total magnitude of the velocity vector
    float currentSpeed = (float) Math.sqrt((Dx * Dx) + (Dy * Dy));
    float maxSpeed = Constants.PlayerConstants.MAX_PLAYER_SPEED;

    if (currentSpeed > maxSpeed) {
      float ratio = maxSpeed / currentSpeed;
      Dx *= ratio;
      Dy *= ratio;
    }

    x += Dx;
    y += Dy;
  }

  public void interact() {
    Dy *= -1.5f;
    Dx *= -1.5f;

  }

  public int getDirection() {
    return direction;
  }

  public double[] getPosition() {
    return new double[] {x, y};
  }

  private void addKeys() {
    keyMap.put(KeyEvent.VK_LEFT, false);
    keyMap.put(KeyEvent.VK_RIGHT, false);
    keyMap.put(KeyEvent.VK_UP, false);
    keyMap.put(KeyEvent.VK_DOWN, false);
  }

  public void addListener(GamePanel panel) {
    panel.addKeyListener(new MyKeyAdapter());
  }

  public class MyKeyAdapter extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_DOWN -> keyMap.put(KeyEvent.VK_DOWN, true);
        case KeyEvent.VK_UP -> keyMap.put(KeyEvent.VK_UP, true);
        case KeyEvent.VK_LEFT -> keyMap.put(KeyEvent.VK_LEFT, true);
        case KeyEvent.VK_RIGHT -> keyMap.put(KeyEvent.VK_RIGHT, true);
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_DOWN -> keyMap.put(KeyEvent.VK_DOWN, false);
        case KeyEvent.VK_UP -> keyMap.put(KeyEvent.VK_UP, false);
        case KeyEvent.VK_LEFT -> keyMap.put(KeyEvent.VK_LEFT, false);
        case KeyEvent.VK_RIGHT -> keyMap.put(KeyEvent.VK_RIGHT, false);
      }
    }
  }
}
