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
import edu.wpi.first.wpilibj.DigitalInput;
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
  private static final int intakeLeftChannel = 5;
  private static final int mainShooterChannel = 4;
  private static final int topShooterChannel = 6;
  private static final int climberChannel = 7;



  //DIO channels
  private static final int ultrasonicOutputChannel = 0;
  private static final int ultrasonicInputChannel = 1;

  private static double stickX = 0.0;
	private static double stickY = 0;
	private static double stickZ = 0;
  private static double gyroAngle = 0;
  private static double throttle;
  private static final double spinTime = 5;
  private static boolean spinCompleted = false;

  private static final int stickChannel = 0;
  private static final boolean useGyro = true;

  //vars used for toggling safemode
  private static boolean safeMode = false;
  private static boolean safeModeToggle = true;

  //var used for toggling intake
  private static boolean intakeRunning = false;
  private static boolean intakeToggle = true;

  //var used for toggling targeting
  private static boolean targetingRunning = false;
  private static boolean targetingToggle = true;
  private int homingStage = 1;

  //vars used for targeting shooter
  private static boolean shooterRunning = false;
  private static boolean shooterToggle = true;

  
  private static final double deadZoneX = 0.1;
	private static final double deadZoneY = 0.1;
	private static final double deadZoneZ = 0.5;

  private MecanumDrive m_robotDrive;
  private Joystick stick;
  private ADXRS450_Gyro gyro;
  public Ultrasonic uSonic;
  public DigitalInput limitSwitch;

  private Spark m_intakeL;
  private Spark m_shooterM;
  private Spark m_shooterT;


  private Timer shooterClock;
  private Timer climbClock;


	public NetworkTable table;
	public NetworkTableEntry tx;
	public NetworkTableEntry ty;
	public NetworkTableEntry ta;

  //Climbing mechanism
  private Spark m_climb;
  private boolean extendArms = false;
  private boolean retractArms = true;
  private int climbButton = 4;
  private final int processTime = 6; 
  private boolean climbRunning = false;
  private boolean climbToggle = false;

  @Override
  public void robotInit() {
    
    //declare motor controllers
    Spark frontLeft = new Spark(kFrontLeftChannel);
    Spark rearLeft = new Spark(kRearLeftChannel);
    Spark frontRight = new Spark(kFrontRightChannel);
    Spark rearRight = new Spark(kRearRightChannel);

    m_intakeL = new Spark(intakeLeftChannel);
    m_shooterM = new Spark(mainShooterChannel);
    m_shooterT = new Spark(topShooterChannel);

    m_climb = new Spark(climberChannel);
    // Invert the right side motors.
    frontRight.setInverted(true);
    rearRight.setInverted(true);
    m_intakeL.setInverted(true);

    m_robotDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);

		//declare stick, gyro, and ultrasonic
    stick = new Joystick(stickChannel);
    gyro = new ADXRS450_Gyro();
		uSonic = new Ultrasonic(ultrasonicOutputChannel, ultrasonicInputChannel);
    limitSwitch =  new DigitalInput(2);

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

    //define camera
    shooterClock = new Timer();
    climbClock = new Timer();

  }
  
  @Override
  public void autonomousPeriodic() {
      if (spinCompleted) {
        //runTargeting();
    }
  }

  @Override
  public void autonomousInit() {
    //Spins bot at initialization phase;
    spin();
    spinCompleted = true;
  }

  public void disabledInit(){
    //turns off shooter when disabled
    targetingRunning =false;
    safeMode = false;
    m_intakeL.set(0);
    m_shooterM.set(0);
    m_shooterT.set(0);
  }

  public void disabledPeriodic(){
    updateDashboard();
  }

  @Override
  public void teleopPeriodic() {

		// display limelight x and y values
		updateDashboard();

    //toggle intake
    toggleIntake();
		
    if((!targetingRunning) && (!shooterRunning)){
      //applies safe mode if nessecary
      applySafeMode();

      //get inputs from joystick and use them
      applyDeadzone();

      //turns on climb if button pressed
      toggleClimb();

      //toggle targeting
      toggleTargeting();

      //toggle shooter
      toggleShooter();

      // Use the joystick X axis for lateral movement, Y axis for forward
      // movement, and Z axis for rotation.
      m_robotDrive.driveCartesian(stickY, stickX, stickZ, gyroAngle);
    } else if(targetingRunning && !shooterRunning){
      runTargeting();
    }else if(shooterRunning && !targetingRunning){
      toggleShooter();
      m_robotDrive.driveCartesian(0, 0, 0);
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
        m_intakeL.set(0);
        //m_shooterM.set(0);
        //m_shooterT.set(0);
      }else{
        intakeRunning = true;
        m_intakeL.set(0.75);
        //m_shooterM.set(-0.5);
        //m_shooterT.set(0.25);
      }
    }else if(stick.getRawButton(8) == false){
      intakeToggle = true;
    }
  }

  public void toggleShooter(){
    //Toggles intake motor
    if(shooterToggle && stick.getRawButton(2)){
      shooterToggle = false;
      if(shooterRunning){
        shooterRunning = false;
        m_shooterM.set(0);
        m_shooterT.set(0);
      }else{
        shooterRunning = true;
        m_shooterM.set(-0.5);
        m_shooterT.set(0.25);
      }
    }else if(!stick.getRawButton(2)){
      shooterToggle = true;
    }
  }


  public void toggleTargeting(){
    //Toggles shooter motors
    if(targetingToggle && stick.getRawButton(1)){
      targetingToggle = false;
      if(!targetingRunning){
        targetingRunning = true;
        homingStage = 0;
      }
    }else if(stick.getRawButton(1) == false){
      targetingToggle = true;
    }
  }

	public void updateDashboard(){
		//post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX",tx.getDouble(0.0));
    SmartDashboard.putNumber("LimelightY",ty.getDouble(0.0));
    SmartDashboard.putNumber("LimelightArea",ta.getDouble(0.0));
    SmartDashboard.putNumber("GyroAngle",gyro.getAngle());
    SmartDashboard.putBoolean("targeting running", targetingRunning);
    SmartDashboard.putBoolean("shooter running", shooterRunning);
    SmartDashboard.putBoolean("safeMode",safeMode);
    SmartDashboard.putBoolean("intake running", intakeRunning);
    SmartDashboard.putNumber("ultrasonic", uSonic.getRangeInches());
    SmartDashboard.putBoolean("Retracts Arms: ", retractArms);
    SmartDashboard.putBoolean("Extend Arms: ", extendArms);
    SmartDashboard.putNumber("shooter timer",shooterClock.get());
    SmartDashboard.putNumber("shooter phase",homingStage);
    SmartDashboard.putBoolean("climb running",climbRunning);
	}

	
  public void runTargeting(){
    if(homingStage == 0){
      //spin until facing hub
			if(tx.getDouble(0.0) > 1){
        if(tx.getDouble(0.0) < 10){
          m_robotDrive.driveCartesian(0.0, 0.0, 0.25, 0.0);
        }else{
          m_robotDrive.driveCartesian(0.0, 0.0, 0.5, 0.0);
        }
			}else if(tx.getDouble(0.0) <= -1){
				if(tx.getDouble(0.0) > -10){
          m_robotDrive.driveCartesian(0.0, 0.0, -0.25, 0.0);
        }else{
          m_robotDrive.driveCartesian(0.0, 0.0, -0.5, 0.0);
        }
			}else{
				homingStage = 1;
			}
    }else if(homingStage == 1){
      //only run if the intake is down
      if(uSonic.getRangeInches() >= 5 ){
        //if needed, move to correct distance from robot
        if(uSonic.getRangeInches() >= 60){
          m_robotDrive.driveCartesian(0.75, 0.0, 0.0, 0.0);
        }else if(uSonic.getRangeInches() <= 50){
          m_robotDrive.driveCartesian(-0.75, 0.0, 0.0, 0.0);
        }else{
          shooterClock.start();
          homingStage = 2;
        }

      }else{
        //if unsafe, turn off homing sequence
        targetingRunning = false;
      }

    }else if(homingStage == 2){
      if(shooterClock.get() < 2){
        m_shooterM.set(1);
        m_shooterT.set(1);
        m_robotDrive.driveCartesian(0.0, 0.0, 0.0, gyroAngle);
      }else{
        targetingRunning = false;
        m_shooterM.set(0.0);
        m_shooterT.set(0.0);
        shooterClock.stop();
        shooterClock.reset();
      }
    }
  }

/**
  *Toggles the climbing mechanism
  *When the button is pressed, and either extend or retract is true, it will do the opposite. 
  @precondition arms must be retracted before doing pressing button up.
  */
  public void toggleClimb() {
    //If climbButton and stick is pressed
    if(climbToggle && stick.getRawButton(climbButton)){
      climbToggle = false;
      climbRunning = true;
      climbClock.reset();
      climbClock.start();

    } else if(!stick.getRawButton(climbButton)) {
      climbToggle = true;
    }

    //If climb is running. 
    if (climbRunning) {
      
      //Extends or retracts arms
      if(climbClock.get() <=processTime){
        m_climb.set(0.5);
      } else {
        m_climb.set(0.0);
        climbRunning = false;
      } 
    }
  }

  
  //Makes the robot spin for a specified amount of time
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