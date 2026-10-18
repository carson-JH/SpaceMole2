import java.awt.*;

public class Constants {
  public static class PlayerConstants {
    public static final int PLAYER_WIDTH = 25;
    public static final int PLAYER_HEIGHT = 25;
    public static final float PLAYER_SPEED_INCREASE = 0.65f;
    public static final float MAX_PLAYER_SPEED = 5.0f;
    public static final float IDLE_PLAYER_SPEED_MULT = 0.85f;
  }

  public static class MapConstants {
    public static final int SCREEN_HEIGHT = 600;
    public static final int SCREEN_WIDTH = 600;
    public static final int UNIT_SIZE = 25;
    public static final int ORE_HEALTH = 5;
    public static final int STONE_HEALTH = 3;
  }

  public static class ResourceConstants {
    public static final int ORB_SIZE = 10;
    public static final int ORB_VALUE = 1;
    public static final float GRAVITY = 0.12f;
    public static final float MAX_FALL_SPEED = 3.2f;
    public static final float BOUNCE = 0.35f;
    public static final float SPAWN_POP_SPEED = -1.6f;
    public static final float SPAWN_NUDGE = 0.8f;
  }
}
