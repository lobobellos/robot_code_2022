package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

  //PWM channels
  public static final int kFrontLeftChannel = 2;
  public static final int kRearLeftChannel = 3;
  public static final int kFrontRightChannel = 1;
  public static final int kRearRightChannel = 0;
  public static final int intakeLeftChannel = 5;
  public static final int mainShooterChannel = 4;
  public static final int topShooterChannel = 6;
  public static final int climberChannel = 7;

  //DIO channels
  public static final int ultrasonicOutputChannel = 0;
  public static final int ultrasonicInputChannel = 1;

  //joystick channels
  public static final int stickChannel = 0;
  public static final int eStopChannel = 1;

  //estopped
  public boolean eStopped;

}
