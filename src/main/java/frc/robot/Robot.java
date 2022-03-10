// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

//imports for managing limelight
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

//imports for robot
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

//build off of a demo mecanum drive program
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

  private Spark m_intakeL;
  private Spark m_intakeR;

  private Spark m_launcherBottom;
  private Spark m_launcherTop;

	public NetworkTable table;
	public NetworkTableEntry tx;
	public NetworkTableEntry ty;
	public NetworkTableEntry ta;

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



    // Invert the right side motors.
    frontRight.setInverted(true);
    rearRight.setInverted(true);
    m_intakeL.setInverted(true);

    m_robotDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);

		//declare stick and gyro
    stick = new Joystick(stickChannel);
    gyro = new ADXRS450_Gyro();

		//add limelight and declare methods to get limelight data
		table = NetworkTableInstance.getDefault().getTable("limelight");
		tx = table.getEntry("tx");
		ty = table.getEntry("ty");
		ta = table.getEntry("ta");

    gyro.calibrate();
	  
	  // Controller Joysticks. X axis is horizontal value of joystick, y axis is vertical.
// Fully left on the X axis gives a value of -1, fully right gives a value of 1.
// Fully down on the Y axis gives a value of -1, fully up gives a value of 1.
private double leftYAxisValue, leftXAxisValue, rightYAxisValue, rightXAxisValue;
// Controller Triggers. 0 is completely released, 1 is completely pressed, 0.5 is half pressed etc.
private double leftTriggerValue, rightTriggerValue;
// Controller Buttons and Bumpers
private boolean rightBumperPressed, leftBumperPressed;
private boolean aButtonPressed, bButtonPressed, yButtonPressed, xButtonPressed;
// Controller DPad Angle (It's dumb and gives you the "angle" of the DPad. For example, 90 degrees is right, 180 down, 270 left, 0 up)
private int dpadAngle;

public XboxController() {
    // Joystick Axis
    leftYAxisValue = controller.getLeftY();
    leftXAxisValue = controller.getLeftX();
    rightYAxisValue = controller.getRightY();
    rightXAxisValue = controller.getRightX();
    
    // Trigger Axis
    leftTriggerValue = controller.getLeftTriggerAxis();
    rightTriggerValue = controller.getRightTriggerAxis();

    // Bumper Buttons
    rightBumperPressed = controller.getRightBumper();
    leftBumperPressed = controller.getLeftBumper();

    // Some other buttons
    aButtonPressed = controller.getAButton();
    bButtonPressed = controller.getBButton();
    xButtonPressed = controller.getXButton();
    yButtonPressed = controller.getYButton();

    // Dpad
    dpadAngle = controller.getPOV();
}
  }

  @Override
  public void teleopPeriodic() {

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
    }else{
      runlauncher();
    }


		// display limelight x and y values
		displayLimelight();
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

	public void displayLimelight(){
		//post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX",tx.getDouble(0.0));
    SmartDashboard.putNumber("LimelightY",ty.getDouble(0.0));
    SmartDashboard.putNumber("LimelightArea",ta.getDouble(0.0));
    SmartDashboard.putNumber("GyroAngle",gyro.getAngle());
	}

  public void runlauncher(){
    if(homingStage == 0){
      //spin until facing hub

    }else if(homingStage == 1){
      //if needed, move to correct distance from robot

      

    }else if(homingStage == 2){
      //set motors to speed
    }
  }
}
