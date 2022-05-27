// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj2.command.RunCommand;

/** An example command that uses an example subsystem. */
public class DefaultDrive extends RunCommand {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})


  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public DefaultDrive(DriveBase subsystem) {
    super(()->System.out.println(),subsystem);

  }

  // Called when the command is initially scheduled.

}
