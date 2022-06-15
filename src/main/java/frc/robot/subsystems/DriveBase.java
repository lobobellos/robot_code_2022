// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class DriveBase extends SubsystemBase {
  
  private static Spark m_fl = new Spark(2);
  private static Spark m_fr = new Spark(1);
  private static Spark m_rl = new Spark(3);
  private static Spark m_rr = new Spark(0);

  private MecanumDrive m_robotDrive;
  /** Creates a new ExampleSubsystem. */
  public DriveBase() {
    m_fr.setInverted(true);
    m_rr.setInverted(true);

    m_robotDrive = new MecanumDrive(m_fl, m_rl, m_fr, m_rr);
  }

  public void driveCartesian(double y,double x,double z,double t){
    m_robotDrive.driveCartesian(y, x, z,0.0);
  }
}
