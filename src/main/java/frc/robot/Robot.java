// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

/** This is a demo program showing how to use Mecanum control with the MecanumDrive class. */
public class Robot extends TimedRobot {
  private static final int kFrontLeftChannel = 2;
  private static final int kRearLeftChannel = 3;
  private static final int kFrontRightChannel = 1;
  private static final int kRearRightChannel = 0;

  private static final int intakeChannel = 4;

  private static double stickX = 0.0;
	private static double stickY = 0;
	private static double stickZ = 0;
  private static double gyroAngle = 0;
  private static double throttle;

  private static final int stickChannel = 0;

  private static final boolean useGyro = true;

  //vars used for toggling safemode
  private static boolean safeMode = false;
  private static boolean safeModeToggle = true;

  //var used for toggling intake
  private static boolean intakeRunning = false;
  private static boolean intakeToggle = true;

  private static final double deadZoneX = 0;
	private static final double deadZoneY = 0;
	private static final double deadZoneZ = 0;

  private MecanumDrive m_robotDrive;
  private Joystick stick;
  private ADXRS450_Gyro gyro;
  private Spark m_intake;

  @Override
  public void robotInit() {
    Spark frontLeft = new Spark(kFrontLeftChannel);
    Spark rearLeft = new Spark(kRearLeftChannel);
    Spark frontRight = new Spark(kFrontRightChannel);
    Spark rearRight = new Spark(kRearRightChannel);
    m_intake = new Spark(intakeChannel);

    //m_intake.close();

    // Invert the right side motors.
    // You may need to change or remove this to match your robot.
    frontRight.setInverted(true);
    rearRight.setInverted(true);

    m_robotDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);

    stick = new Joystick(stickChannel);
    gyro = new ADXRS450_Gyro();

    gyro.calibrate();
  }

  @Override
  public void teleopPeriodic() {

    //applies safe mode if nessecary
    applySafeMode();

    //get inputs from joystick and use them
    applyDeadzone();

    //toggle intake
    toggleIntake();

    // Use the joystick X axis for lateral movement, Y axis for forward
    // movement, and Z axis for rotation.
    m_robotDrive.driveCartesian(stickY, stickX, stickZ, gyroAngle);
  }

  public void applySafeMode(){

    //Toggles safe mode
    if(safeModeToggle && stick.getRawButton(7)){
      safeModeToggle = false;
      if(safeMode){
        safeMode = false;
        System.out.println("safeMode off");
      }else{
        safeMode = true;
        System.out.println("safeMode on");
      }
    }else if(stick.getRawButton(7) == false){
      safeModeToggle = true;
    }

    //applies safe mode if nessecary
    if(safeMode){
      throttle = 0.3;
    }else{
      //parse throttle (min:0.26 , max:1)
      throttle = ((-stick.getThrottle())+1.7)/2.7;
    }
  }

  public void applyDeadzone(){
    //parse throttle (min:0.26 , max:1)
    throttle = ((-stick.getThrottle())+1.7)/2.7;

		//apply a deadzone
		if( Math.abs(stick.getX()) < deadZoneX){
			stickX = 0.0;
		}else{
			stickX = stick.getX()*throttle;
		}
		if( Math.abs(stick.getY()) < deadZoneY){
			stickY = 0.0;
		}else{
			stickY = -stick.getY()*throttle;
		}
		if( Math.abs(stick.getZ()) < deadZoneZ || !stick.getRawButton(2) ){
			stickZ = 0.0;
		}else{
			stickZ = stick.getZ()*throttle;
		}

    //applies gyro 
    if(useGyro){
      gyroAngle = gyro.getAngle();
    }else{
      gyroAngle = 0.0;
    }
    
	}

  public void toggleIntake(){
    //Toggles intake motor
    if(intakeToggle && stick.getRawButton(8)){
      intakeToggle = false;
      if(intakeRunning){
        intakeRunning = false;
        m_intake.set(0);
        System.out.println("intake off");
      }else{
        intakeRunning = true;
        m_intake.set(1);
        System.out.println("intake on");
      }
    }else if(stick.getRawButton(8) == false){
      intakeToggle = true;
    }
  }
}
