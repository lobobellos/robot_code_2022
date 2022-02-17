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


  private static double stickX = 0.0;
	private static double stickY = 0;
	private static double stickZ = 0;
<<<<<<< HEAD
  public static double throttle = 0;
=======
  private static double gyroAngle = 0;
  private static double throttle;
>>>>>>> 95b7f50b1977ae6f279302c88217e5ea444824a2

  private static final int stickChannel = 0;
  private static final boolean useGyro = true;

  private static final double deadZoneX = 0;
	private static final double deadZoneY = 0;
	private static final double deadZoneZ = 0;

  private MecanumDrive m_robotDrive;
  private Joystick stick;
  private ADXRS450_Gyro gyro;

  @Override
  public void robotInit() {
    Spark frontLeft = new Spark(kFrontLeftChannel);
    Spark rearLeft = new Spark(kRearLeftChannel);
    Spark frontRight = new Spark(kFrontRightChannel);
    Spark rearRight = new Spark(kRearRightChannel);

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

    //get inputs from joystick and use them
    applyDeadzone();

    // Use the joystick X axis for lateral movement, Y axis for forward
    // movement, and Z axis for rotation.
    m_robotDrive.driveCartesian(stickY, stickX, stickZ, gyroAngle);
  }

  public void applyDeadzone(){
<<<<<<< HEAD

    throttle = ((-m_stick.getThrottle()) +1)/2;
=======
    //parse throttle
    throttle = ((-stick.getThrottle())+1)/2;
>>>>>>> 95b7f50b1977ae6f279302c88217e5ea444824a2

		//apply a deadzone
		if( Math.abs(stick.getX()) < deadZoneX){
			stickX = 0.0;
		}else{
<<<<<<< HEAD
			stickX = m_stick.getX()*throttle;
=======
			stickX = stick.getX()*throttle;
>>>>>>> 95b7f50b1977ae6f279302c88217e5ea444824a2
		}
		if( Math.abs(stick.getY()) < deadZoneY){
			stickY = 0.0;
		}else{
<<<<<<< HEAD
			stickY = -m_stick.getY()*throttle;
=======
			stickY = stick.getY()*throttle;
>>>>>>> 95b7f50b1977ae6f279302c88217e5ea444824a2
		}
		if( Math.abs(stick.getZ()) < deadZoneZ || !stick.getRawButton(0) ){
			stickZ = 0.0;
		}else{
<<<<<<< HEAD
			stickZ = m_stick.getZ()*throttle;
=======
			stickZ = stick.getZ()*throttle;
>>>>>>> 95b7f50b1977ae6f279302c88217e5ea444824a2
		}
    if(useGyro){
      gyroAngle = gyro.getAngle();
    }else{
      gyroAngle = 0.0;
    }
	}
}
