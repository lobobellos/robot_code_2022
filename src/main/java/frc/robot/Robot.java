// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

//import cameraServer
import edu.wpi.first.cameraserver.CameraServer;


//imports for managing limelight
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

//Imports for robot
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.Timer;

// Build off of a demo mecanum drive program
public class Robot extends TimedRobot {

  private static final int kFrontLeftChannel = 2;
  private static final int kRearLeftChannel = 3;
  private static final int kFrontRightChannel = 1;
  private static final int kRearRightChannel = 0;

  private static final int intakeLeftChannel = 4;
  private static final int intakeRightChannel = 5;
  private static final int l_bottomChannel = 6;
  private static final int l_topChannel = 7;

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

  //var used for toggling shooter
  private static boolean shooterRunning = false;
  private static boolean shooterToggle = true;
  private int homingStage = 1;

  private static final double deadZoneX = 0;
	private static final double deadZoneY = 0;
	private static final double deadZoneZ = 0;

  private MecanumDrive m_robotDrive;
  private Joystick stick;
  private ADXRS450_Gyro gyro;
  private Ultrasonic uSonic;

  private Spark m_intakeL;
  private Spark m_intakeR;

  private Spark m_launcherBottom;
  private Spark m_launcherTop;

	public NetworkTable table;
	public NetworkTableEntry tx;
	public NetworkTableEntry ty;
	public NetworkTableEntry ta;

  //Climbing mechanism
  private Spark m_climbL;
  private Spark m_climbR;
  private boolean extendArms = false;
  private boolean retractArms = true;
  private int climbButton = 4;
  private static final int processTime = 6; 
  private int climbLeftChannel = 8;
  private int climbRightChannel = 9;

  @Override
  public void robotInit() {
    
    //declare motor controllers
    Spark frontLeft = new Spark(kFrontLeftChannel);
    Spark rearLeft = new Spark(kRearLeftChannel);
    Spark frontRight = new Spark(kFrontRightChannel);
    Spark rearRight = new Spark(kRearRightChannel);

    m_intakeL = new Spark(intakeLeftChannel);
    m_intakeR = new Spark(intakeRightChannel);
    m_launcherBottom = new Spark(l_bottomChannel);
    m_launcherTop = new Spark(l_topChannel);
    m_climbL = new Spark(climbLeftChannel);
    m_climbR = new Spark(climbRightChannel);
    
    // Invert the right side motors.
    frontRight.setInverted(true);
    rearRight.setInverted(true);
    m_intakeL.setInverted(true);

    m_robotDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);

		//declare stick, gyro, and ultrasonic
    stick = new Joystick(stickChannel);
    gyro = new ADXRS450_Gyro();
		uSonic = new Ultrasonic(1, 2);

		// Add limelight and declare methods to get limelight data
		table = NetworkTableInstance.getDefault().getTable("limelight");
		tx = table.getEntry("tx");
		ty = table.getEntry("ty");
		ta = table.getEntry("ta");

		//initialize cameraServer
		CameraServer.startAutomaticCapture();

		//calibrate gyro
    gyro.calibrate();
		//calibrate ultrasonic sensor
		Ultrasonic.setAutomaticMode(true);
  }

  @Override
  public void teleopPeriodic() {

			// display limelight x and y values
		updateDashboard();
		
    if(!shooterRunning){
      //applies safe mode if nessecary
      applySafeMode();

      //get inputs from joystick and use them
      applyDeadzone();

      //toggle intake
      toggleIntake();

      //toggle launcher
      toggleShooter();

      // Use the joystick X axis for lateral movement, Y axis for forward
      // movement, and Z axis for rotation.
      m_robotDrive.driveCartesian(stickY, stickX, stickZ, gyroAngle);
    } else {
      runlauncher();
    }
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
    } else {
      //parse throttle (min:0.26 , max:1)
      throttle = ((-stick.getThrottle())+1.7)/2.7;
    }
  }

  public void applyDeadzone(){

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
		if( Math.abs(stick.getZ()) < deadZoneZ){
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
        m_intakeR.set(0);
        m_intakeL.set(0);
        System.out.println("intake off");
      }else{
        intakeRunning = true;
        m_intakeR.set(0.5);
        m_intakeL.set(0.5);
        System.out.println("intake on");
      }
    }else if(stick.getRawButton(8) == false){
      intakeToggle = true;
    }
  }

  public void toggleShooter(){
    //Toggles shooter motors
    if(shooterToggle && stick.getRawButton(1)){
      shooterToggle = false;
      if(!shooterRunning){
        shooterRunning = true;
        homingStage = 0;
      }
      SmartDashboard.putBoolean("shooter running", shooterRunning);
    }else if(stick.getRawButton(1) == false){
      shooterToggle = true;
    }
  }

	public void updateDashboard(){
		//post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX",tx.getDouble(0.0));
    SmartDashboard.putNumber("LimelightY",ty.getDouble(0.0));
    SmartDashboard.putNumber("LimelightArea",ta.getDouble(0.0));
    SmartDashboard.putNumber("GyroAngle",gyro.getAngle());
    SmartDashboard.putBoolean("shooter running", shooterRunning);
    SmartDashboard.putNumber("ultrasonic", uSonic.getRangeInches());
	}

	
  public void runlauncher(){
    if(homingStage == 0){
      //spin until facing hub
			if(tx.getDouble(0.0) >0.5){
				m_robotDrive.driveCartesian(0.0, 0.0, 0.35, 0.0);
			}else if(tx.getDouble(0.0) <= -0.5){
				m_robotDrive.driveCartesian(0.0, 0.0, -0.35, 0.0);
			}else{
				//homingStage = 1;
        shooterRunning = false;
			}
    }else if(homingStage == 1){
      //if needed, move to correct distance from robot
      if(uSonic.getRangeInches() >= 100){
        m_robotDrive.driveCartesian(-0.5, 0.0, 0.0, 0.0);
      }else if(uSonic.getRangeInches() <= 110){
        m_robotDrive.driveCartesian(0.5, 0.0, 0.0, 0.0);
      }else{
        homingStage = 2;
      }
    }else if(homingStage == 2){
      //set motors to speed
			m_launcherBottom.set(0.5);
			m_launcherTop.set(0.5);
    }
  }

/**
  *Toggles the climbing mechanism
  *When the button is pressed, and either extend or retract is true, it will do the opposite. 
  @precondition arms must be retracted before doing pressing button up.
  */
  public void toggleClimb() {
    if (stick.getRawButton(climbButton)) {
        //Extends or retracts arms
        if (extendArms) {
          m_climbL.setInverted(true);
          m_climbR.setInverted(true);
          m_climbL.set(0.9);
          m_climbR.set(0.9);
          //Wating for climb to fully retract
          Timer.delay(proccessTime);
          retractArms = true;
          extendArms = false;
          System.out.println("Retracts Arms: " + retractArms);
          System.out.println("Extend Arns: " + extendArms);
          m_climbR.setInverted(true);
          m_climbL.setInverted(true);
          m_climbL.stopMotor();
          m_climbR.stopMotor();
        } else if (retractArms) {
          m_climbL.set(3);
          m_climbR.set(3);
          Timer.delay(proccessTime);
          extendArms = true;
          retractArms = false;
          System.out.println("Retracts Arms: " + retractArms);
          System.out.println("Extend Arns: " + extendArms);
          m_climbL.stopMotor();
          m_climbR.stopMotor();
      }
    }
  }
}