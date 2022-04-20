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


  private static final int stickChannel = 0;
  private static final int eStopChannel = 1;
  private static final boolean useGyro = true;

  //vars used for toggling safemode
  private static boolean safeMode = false;
  private static boolean safeModeToggle = true;

  //var used for toggling intake
  private static boolean intakeRunning = false;
  private double shootSpeed = 6;

  //var used for toggling targeting
  private static boolean targetingRunning = false;

  //vars used for targeting shooter
  private static boolean shooterRunning = false;
  private boolean limeLightAlign= false;

  
  private static final double deadZoneX = 0.1;
	private static final double deadZoneY = 0.1;
	private static final double deadZoneZ = 0.5;

  private MecanumDrive m_robotDrive;
  private Joystick stick;
  private Joystick eStop;
  private ADXRS450_Gyro gyro;
  public Ultrasonic uSonic;
  public DigitalInput limitSwitch;

  private Spark m_intakeL;
  private Spark m_shooterM;
  private Spark m_shooterT;

  //estopped
  private boolean eStopped;

  //timers used for launching 
  private Timer shooterClock;
  private Timer switchIntakeTimer;

  //bools used for targeting and launching
  private boolean hasBall = false;
  private boolean secondaryMovement = false;
  private boolean runTargeting = false;
  private boolean runIntake = false;
  private boolean sonicAlign = false;

  //limelight outputs
	public NetworkTable table;
	public NetworkTableEntry tx;
	public NetworkTableEntry ty;
	public NetworkTableEntry ta;

  //Climbing mechanism
  private Spark m_climb;
  private boolean climbRunning = false;

  //Allignment mechanism
  private boolean toggleAllign = false;
  private boolean distanceStage = false;
  private boolean allignStage = false;

  @Override
  public void robotInit() {

    //set Estopped to false on startup
    eStopped = false;

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
    eStop = new Joystick(eStopChannel);
    gyro = new ADXRS450_Gyro();
		uSonic = new Ultrasonic(ultrasonicOutputChannel, ultrasonicInputChannel);
    limitSwitch =  new DigitalInput(2);
    switchIntakeTimer = new Timer();


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

    //clocks
    shooterClock = new Timer();
    switchIntakeTimer = new Timer();
  }
  
  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void autonomousInit() {
    m_robotDrive.driveCartesian(-1, 0, 0);
    Timer.delay(1.0);
    //drive back
    m_robotDrive.driveCartesian(1, 0, 0);
    Timer.delay(1.0);
    m_robotDrive.driveCartesian(0, 0, 0);
    m_shooterM.setVoltage(shootSpeed+1);
    m_shooterT.setVoltage(shootSpeed+1);
    Timer.delay(4);
    m_intakeL.setVoltage(12);
    Timer.delay(2);
    m_shooterM.setVoltage(0);
    m_shooterT.setVoltage(0);
    m_intakeL.setVoltage(0);

    //start taxi
    m_robotDrive.driveCartesian(-1, 0, 0);
    Timer.delay(0.5);
    m_robotDrive.driveCartesian(-1, 0, 0);
    Timer.delay(0.5);
    m_robotDrive.driveCartesian(0, 0, 0);
  }

  public void disabledInit(){
    //turns off shooter when disabled
    targetingRunning =false;
    safeMode = false;
    m_intakeL.setVoltage(0);
    m_shooterM.setVoltage(0);
    m_shooterT.setVoltage(0);
    sonicAlign = false;
  }

  public void disabledPeriodic(){
    updateDashboard();
  }

  @Override
  public void teleopPeriodic() {

    //check the E-stop
    if(eStop.getRawButton(0)){
      eStopped = true;
    }

		// display limelight x and y values
		updateDashboard();

    //toggle shooter
    toggleShooter();

    //applies safe mode if nessecary
    applySafeMode();

    //get inputs from joystick and use them
    applyDeadzone();

    //turns on climb if button pressed
    toggleClimb();

    resetShooter();

    if(!shooterRunning){
      // Use the joystick X axis for lateral movement, Y axis for forward
      // movement, and Z axis for rotation.
      m_robotDrive.driveCartesian(stickY, stickX, stickZ, gyroAngle);
    } else{
      runShooter();
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
	
  public void toggleShooter(){
    if(stick.getRawButtonPressed(1) && !shooterRunning){
      //set shooterRunning to true
      shooterRunning = true;
      
      //start spinning the intake
      m_intakeL.setVoltage(8);
    }
  }
  
//Runs shooter and intake
public void runShooter() {

  if(!hasBall){
    m_robotDrive.driveCartesian(stickY,stickX,stickZ);
  }

  if (limitSwitch.get() && !hasBall) {
    hasBall = true;
    m_robotDrive.driveCartesian(0, 0, 0);
    //start timer
    switchIntakeTimer.reset();
    switchIntakeTimer.start();
  } 
  // if has ball, loop runs continuously in periodic.
  if (hasBall) {
      if(switchIntakeTimer.get() <= 0.5){
        //stop the motor 0.5 secs after running intake
        m_intakeL.set(0.0);
      }else if(switchIntakeTimer.get() > 0.5 && switchIntakeTimer.get() <= 0.75){
        //shoot ball out for half a sec
        m_intakeL.set(-0.5);
        m_shooterM.setVoltage(-6);
      }else if(switchIntakeTimer.get() > 0.75 && switchIntakeTimer.get() <= 2.0){
        //allow driving while motors are getting up to speed
        m_robotDrive.driveCartesian(stickY, stickX, stickZ);
        //stop intake motors
        m_intakeL.setVoltage(-0.0);
        //Startup motors for shooter
        m_shooterM.setVoltage(shootSpeed);
        m_shooterT.setVoltage(shootSpeed);
        secondaryMovement = true;
      }else if(secondaryMovement){
        //allow movement
        m_robotDrive.driveCartesian(stickY,stickX,stickZ);
        if(stick.getRawButtonPressed(2)){
          secondaryMovement = false;
          runIntake = true;
          runTargeting = true;
          limeLightAlign = true;
        }
      }else if(runTargeting && runIntake) {
        //allow skiping of alignment with button 3
        if(stick.getRawButtonPressed(3)){
          limeLightAlign = false;
        }
        if(limeLightAlign){
          //align using limelight
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
            limeLightAlign = false;
          }
        }else{
          //pull the intake
          shooterClock.reset();
          shooterClock.start();
          //runTargeting(); 
          m_intakeL.set(1);
          runIntake = false;
        }
        
      }else if(runTargeting && !runIntake && shooterClock.get() >= 2){
        //stop everything
        switchIntakeTimer.stop();
        hasBall = false;

        shooterRunning = false;
        secondaryMovement = false;
        runTargeting = false;
        runIntake = false;
        sonicAlign = false;
        limeLightAlign= false;

        m_shooterM.set(0);
        m_shooterT.set(0);
        m_intakeL.set(0.0);
      }
    }
  }

  public void resetShooter() {
    if(stick.getRawButtonPressed(11)) {
      switchIntakeTimer.stop();
      hasBall = false;

      shooterRunning = false;
      secondaryMovement = false;
      runTargeting = false;
      runIntake = false;
      sonicAlign = false;

      m_shooterM.set(0);
      m_shooterT.set(0);
      m_intakeL.set(0.0);
    }
  }


  public void toggleTargeting(){
    //Toggles shooter motors
    if(stick.getRawButtonPressed(1)){
      if(!targetingRunning){
        targetingRunning = true;
      }
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
    SmartDashboard.putBoolean("climb running",climbRunning);
    SmartDashboard.putBoolean("switch pressed",limitSwitch.get());
    SmartDashboard.putBoolean("switch onicAlign",sonicAlign);
	}

	
  public void alignRobot() {
    //If button 12 hasn't been pressed, allow for user control
    if (!toggleAllign) {
      m_robotDrive.driveCartesian(stickX, stickY, stickZ);
    }

    //If button is pressed, toggle is set to true, allowing for aiming at hoop
    if (stick.getRawButtonPressed(12)) {
      toggleAllign = true;
    }

    //Aim at hoop
    if (!allignStage && toggleAllign) {
      if (tx.getDouble(0.0) > 1) {
        if (tx.getDouble(0.0) < 10) {
          m_robotDrive.driveCartesian(0.0, 0.0, 0.25, 0.0);
        } else {
          m_robotDrive.driveCartesian(0.0, 0.0, 0.5, 0.0);
        }
      } else if(tx.getDouble(0.0) <= -1){
				if(tx.getDouble(0.0) > -10){
          m_robotDrive.driveCartesian(0.0, 0.0, -0.25, 0.0);
        }else{
          m_robotDrive.driveCartesian(0.0, 0.0, -0.5, 0.0);  
        }
			} else {
				allignStage = true;
    }
  }

  //Gauge  and fix distance from hoop
  if (allignStage && !distanceStage && toggleAllign) {
    if (uSonic.getRangeInches() >= 5 ) {
      if (uSonic.getRangeInches() >= 60) {
        m_robotDrive.driveCartesian(0.75, 0.0, 0.0, 0.0);
      }else if (uSonic.getRangeInches() <= 50) {
        m_robotDrive.driveCartesian(-0.75, 0.0, 0.0, 0.0);
      }else{
        shooterClock.reset();
        shooterClock.start();
      }
    } else {
      distanceStage = true;
    }
  }

  //Set everything to false once everything is alligned.
  if (allignStage && distanceStage) {
    toggleAllign = false;
    allignStage = false;
    distanceStage = false;
  }
}


	//use button 4 in pits to load spring
	//use button 5 to deploy during match
	public void toggleClimb() {
		//If climbButton and stick is pressed
		if(stick.getRawButton(4)){
			m_climb.set(0.5);
		}else if(stick.getRawButton(5)){
			m_climb.set(-0.5);
		}else{
			m_climb.set(0);
		}
	}

  
  //Makes the robot spin for a specified amount of time
  public void spin() {
    Timer time = new Timer();
    time.start(); 
    
    while (time.get() <= spinTime / 2) {
      m_robotDrive.driveCartesian(0.0, 0.0, 1.0, 0.0);
    }

     while (time.get() <= spinTime / 2) {
      m_robotDrive.driveCartesian(0.0, 0.0, -1.0, 0.0);
    }
  
    time.stop();
    m_robotDrive.driveCartesian(0.0, 0.0, 0.0, 0.0);
    return;
  }
}