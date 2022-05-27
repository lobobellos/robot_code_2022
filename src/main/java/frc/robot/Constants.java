// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

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

  public static double stickX = 0.0;
	public static double stickY = 0;
	public static double stickZ = 0;
  public static double gyroAngle = 0;
  public static double throttle;
  public static final double spinTime = 5;


  public static final int stickChannel = 0;
  public static final int eStopChannel = 1;
  public static final boolean useGyro = true;

  //vars used for toggling safemode
  public static boolean safeMode = false;
  public static boolean safeModeToggle = true;

  //var used for toggling intake
  public static boolean intakeRunning = false;
  public double shootSpeed = 6;

  //var used for toggling targeting
  public static boolean targetingRunning = false;

  //vars used for targeting shooter
  public static boolean shooterRunning = false;
  public boolean limeLightAlign= false;

  
  public static final double deadZoneX = 0.1;
	public static final double deadZoneY = 0.1;
	public static final double deadZoneZ = 0.5;


  public Ultrasonic uSonic;
  public DigitalInput limitSwitch;

  public Spark m_intakeL;
  public Spark m_shooterM;
  public Spark m_shooterT;

  //estopped
  public boolean eStopped;

  //timers used for launching 
  public Timer shooterClock;
  public Timer switchIntakeTimer;

  //bools used for targeting and launching
  public boolean hasBall = false;
  public boolean secondaryMovement = false;
  public boolean runTargeting = false;
  public boolean runIntake = false;
  public boolean sonicAlign = false;


  //Climbing mechanism
  public Spark m_climb;
  public boolean climbRunning = false;

  //Allignment mechanism
  public boolean toggleAllign = false;
  public boolean distanceStage = false;
  public boolean allignStage = false;

}
