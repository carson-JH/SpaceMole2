import java.awt.*;

public class Constants {
  public static class PlayerConstants {
    public static final int PLAYER_WIDTH = 25;
    public static final int PLAYER_HEIGHT = 25;
    public static final float PLAYER_SPEED_INCREASE = 0.85f;
    public static final float MAX_PLAYER_SPEED = 6.0f;
    public static final float IDLE_PLAYER_SPEED_MULT = 0.85f;
    public static final float PLAYER_GRAVITY = 0; // set to aero for now
    public static final float MAX_PLAYER_FALL_SPEED = ResourceConstants.MAX_FALL_SPEED;
    public static final float PICKUP_RADIUS = 40.0f;
    public static final int RESOURCE_FONT_SIZE = 18;
    public static final int RESOURCE_COUNTER_X = 20;
    public static final int RESOURCE_COUNTER_Y = 26;
    public static final int PLAYER_CONTACT_DAMAGE = 6;
    public static final int PLAYER_CONTACT_COOLDOWN_MS = 400;
    public static final float PLAYER_INTERACT_KNOCK = 2.8f;
  }

  public static class MapConstants {
    public static final int SCREEN_HEIGHT = 600;
    public static final int SCREEN_WIDTH = 600;
    public static final int UNIT_SIZE = 25;
    public static final int ORE_HEALTH = 22;
    public static final int STONE_HEALTH = 16;
    public static final int MINING_COOLDOWN_MS = 220;

    // Dome/Base dimensions (40% of screen width)
    public static final int DOME_WIDTH = (int) (SCREEN_WIDTH * 0.40); // 240 pixels
    public static final int DOME_HEIGHT = 80; // Height of the dome
    public static final int DOME_X =
        (SCREEN_WIDTH - DOME_WIDTH) / 2; // Centered horizontally (180 pixels)
    public static final int DOME_Y =
        (SCREEN_HEIGHT / 2) - DOME_HEIGHT; // Directly on top of surface (220 pixels)
    public static final int SURFACE_Y = SCREEN_HEIGHT / 2;
    public static final int FLOOR_THICKNESS = 6;
  }

  public static class BaseConstants {
    public static final int MAX_HEALTH = 100;
    public static final int MAX_DEFENSE_LEVEL = 5;
    public static final int UPGRADE_BASE_COST = 5;
    public static final int UPGRADE_HEAL = 12;
    public static final int TURRET_RANGE = 220;
    public static final int TURRET_COOLDOWN_MS = 700;
    public static final int TURRET_DAMAGE = 4;
    public static final float PROJECTILE_SPEED = 4.5f;
  }

  public static class EnemyConstants {
    public static final int FIRST_WAVE_DELAY_MS = 28000;
    public static final int WAVE_INTERVAL_MS = 20000;
    public static final int MIN_WAVE_INTERVAL_MS = 5000;
    public static final int ATTACK_RANGE_PADDING = 6;
    public static final float PROJECTILE_SPEED = 3.2f;
    public static final int PROJECTILE_SIZE = 8;
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
