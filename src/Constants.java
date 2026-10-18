import java.awt.*;

public class Constants {
  public static class PlayerConstants {
    public static final int PLAYER_WIDTH = 25;
    public static final int PLAYER_HEIGHT = 25;
    public static final float PLAYER_SPEED_INCREASE = 0.65f;
    public static final float MAX_PLAYER_SPEED = 5.0f;
    public static final float IDLE_PLAYER_SPEED_MULT = 0.85f;
    public static final float PLAYER_GRAVITY = ResourceConstants.GRAVITY;
    public static final float MAX_PLAYER_FALL_SPEED = ResourceConstants.MAX_FALL_SPEED;
    public static final float PICKUP_RADIUS = 40.0f;
    public static final int RESOURCE_FONT_SIZE = 20;
    public static final int RESOURCE_COUNTER_X = 20;
    public static final int RESOURCE_COUNTER_Y = 30;
  }

  public static class MapConstants {
    public static final int SCREEN_HEIGHT = 600;
    public static final int SCREEN_WIDTH = 600;
    public static final int UNIT_SIZE = 25;
    public static final int ORE_HEALTH = 8;
    public static final int STONE_HEALTH = 5;

    // Dome/Base dimensions (40% of screen width)
    public static final int DOME_WIDTH = (int) (SCREEN_WIDTH * 0.40); // 240 pixels
    public static final int DOME_HEIGHT = 80; // Height of the dome
    public static final int DOME_X =
        (SCREEN_WIDTH - DOME_WIDTH) / 2; // Centered horizontally (180 pixels)
    public static final int DOME_Y =
        (SCREEN_HEIGHT / 2) - DOME_HEIGHT; // Directly on top of surface (220 pixels)
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
