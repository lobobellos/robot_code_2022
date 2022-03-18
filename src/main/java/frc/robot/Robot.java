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

  //PWM channels
  private static final int kFrontLeftChannel = 2;
  private static final int kRearLeftChannel = 3;
  private static final int kFrontRightChannel = 1;
  private static final int kRearRightChannel = 0;
  private static final int intakeLeftChannel = 4;
  private static final int intakeRightChannel = 5;

  //DIO channels
  private static final int ultrasonicOutputChannel = 0;
  private static final int ultrasonicInputChannel = 1;

  private static double stickX = 0.0;
	private static double stickY = 0;
	private static double stickZ = 0;
  private static double gyroAngle = 0;
  private static double throttle;
  private static final double spinTime = 5;

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
	private static final double deadZoneZ = 0.5;

  private MecanumDrive m_robotDrive;
  private Joystick stick;
  private ADXRS450_Gyro gyro;
  public Ultrasonic uSonic;

  private Spark m_intakeL;
  private Spark m_shooterR;


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
  private final int processTime = 6; 
  private int climbLeftChannel = 8;
  private int climbRightChannel = 9;

  //timer
  public Timer autoTimer;

  @Override
  public void robotInit() {
    
    //declare motor controllers
    Spark frontLeft = new Spark(kFrontLeftChannel);
    Spark rearLeft = new Spark(kRearLeftChannel);
    Spark frontRight = new Spark(kFrontRightChannel);
    Spark rearRight = new Spark(kRearRightChannel);

    m_intakeL = new Spark(intakeLeftChannel);
    m_shooterR = new Spark(intakeRightChannel);

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
		uSonic = new Ultrasonic(ultrasonicOutputChannel, ultrasonicInputChannel);

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
    uSonic.setEnabled(true);
		Ultrasonic.setAutomaticMode(true);

    //enable the timer
    Timer autoTimer = new Timer();

  }
  
  @Override
  public void autonomousInit(){
    autoTimer.start();
  }

  @Override
  public void autonomousPeriodic(){
    if(autoTimer.get() <=  2){
      m_robotDrive.driveCartesian(0.0, 0.0, 1, 0.0);
    }else if(autoTimer.get() <=  4){
      m_robotDrive.driveCartesian(0.0, 0.0, -1, 0.0);
    }else if(autoTimer.get() <= 8){
      runlauncher();
    }
  }

  @Override
  public void teleopInit() {
    //Spins bot at initialization phase;
    spin();
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
        m_shooterR.set(0);
        m_intakeL.set(0);
        System.out.println("intake off");
      }else{
        intakeRunning = true;
        m_shooterR.set(-0.5);
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
    SmartDashboard.putBoolean("safeMode",safeMode);
    SmartDashboard.putBoolean("intake running", intakeRunning);
    SmartDashboard.putNumber("ultrasonic", uSonic.getRangeInches());
    SmartDashboard.putBoolean("Retracts Arms: ", retractArms);
    SmartDashboard.putBoolean("Extend Arms: ", extendArms);
	}

	
  public void runlauncher(){
    if(homingStage == 0){
      //spin until facing hub
			if(tx.getDouble(0.0) > 0.25){
        if(tx.getDouble(0.0) < 10){
          m_robotDrive.driveCartesian(0.0, 0.0, 0.25, 0.0);
        }else{
          m_robotDrive.driveCartesian(0.0, 0.0, 0.5, 0.0);
        }
			}else if(tx.getDouble(0.0) <= -0.25){
				if(tx.getDouble(0.0) > -10){
          m_robotDrive.driveCartesian(0.0, 0.0, -0.25, 0.0);
        }else{
          m_robotDrive.driveCartesian(0.0, 0.0, -0.5, 0.0);
        }
			}else{
				homingStage = 1;
			}
    }else if(homingStage == 1){
      //if needed, move to correct distance from robot
      if(uSonic.getRangeInches() >= 100){
        m_robotDrive.driveCartesian(-0.5, 0.0, 0.0, 0.0);
      }else if(uSonic.getRangeInches() <= 110){
        m_robotDrive.driveCartesian(0.5, 0.0, 0.0, 0.0);
      }else{
        //homingStage = 2;
        shooterRunning = false;
      }
    }else if(homingStage == 2){
      //set motors to speed
			m_shooterR.set(0.5);

    }
  }

/**
  *Toggles the climbing mechanism
  *When the button is pressed, and either extend or retract is true, it will do the opposite. 
  @precondition arms must be retracted before doing pressing button up.
  */
  public void toggleClimb() {
    //If button is pressed
    if (stick.getRawButton(climbButton)) {
      Timer time = new Timer();
        //Extends or retracts arms
        if (extendArms) {
          m_climbL.setInverted(true);
          m_climbR.setInverted(true);
          m_climbL.set(0.9);
          m_climbR.set(0.9);
          
          while (!(time.get() <= processTime)) {
            m_climbL.stopMotor();
            m_climbR.stopMotor();
          }
          
          retractArms = true;
          extendArms = false;
          m_climbR.setInverted(false);
          m_climbL.setInverted(false);
          time.stop();
          return;
        } else if (retractArms) {
            m_climbL.set(0.9);
            m_climbR.set(0.9);
          
            while (!(time.get() <= processTime)) {
              m_climbL.stopMotor();
              m_climbR.stopMotor();
            }
          
          extendArms = true;
          retractArms = false;
          time.stop();
          return;
      }
    }
  }
  
  //Makes the robot spin for a specified amount of time

  // please stop using linear programming
  //its gross
  public void spin() {
    Timer time = new Timer();
    time.start(); 
    
    while (time.get() <= spinTime / 2) {
      m_robotDrive.driveCartesian(0.0, 0.0, 1.0, 0.0);
    }
    
    time.stop();
    m_robotDrive.driveCartesian(0.0, 0.0, 0.0, 0.0);
    time.start();
    
     while (time.get() <= spinTime / 2) {
      m_robotDrive.driveCartesian(0.0, 0.0, -1.0, 0.0);
    }
  
    time.stop();
    m_robotDrive.driveCartesian(0.0, 0.0, 0.0, 0.0);
    return;
  }
}