// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.IntakeBalls.IntakeBalls;
import frc.robot.commands.alignAndShoot.AlignAndShoot;
import frc.robot.commands.alignAndShoot.SwallowBalls;
import frc.robot.commands.hook.*;

import frc.robot.subsystems.*;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final DriveBase driveBase = new DriveBase();
  private final Climber climber = new Climber();
  private final Limelight limeLight = new Limelight();
  private final Intake intake = new Intake();
  private final LimitSwitch lSwitch = new LimitSwitch();
  private final Yeeter shooter = new Yeeter();

  private final RaiseHook raiseHook = new RaiseHook(climber);
  private final LowerHook lowerHook = new LowerHook(climber);
  private final StopHook stopHook = new StopHook(climber);

  private final IntakeBalls intakeBalls =new IntakeBalls(intake, lSwitch, shooter);
  private final AlignAndShoot alignAndShoot = new AlignAndShoot(driveBase,intake,limeLight,shooter);

  private final SwallowBalls swallowBalls = new SwallowBalls(intake, shooter);


  private CommandXboxController xboxController;

  
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {


    xboxController = new CommandXboxController(1);

    driveBase.setDefaultCommand(new RunCommand(()->
    driveBase.driveCartesian(
      -xboxController.getRightY(),
      xboxController.getRightX(),
      -xboxController.getLeftTriggerAxis() + xboxController.getRightTriggerAxis(),
      1
    )
    ,driveBase));
    

    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {

    xboxController.button(6)
    .whileTrue(raiseHook)
    .onFalse(stopHook);
    
    xboxController.button(7)
    .whileTrue(lowerHook)
    .onFalse(stopHook);

    xboxController.a()
    .onTrue(intakeBalls);
    
    xboxController.b()
    .onTrue(alignAndShoot);
    
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return new SequentialCommandGroup(
      //drive back for a second and then drive back in
      new InstantCommand(()->{driveBase.driveCartesian(-1, 0, 0, 1);},driveBase),
      new WaitCommand(1),
      new InstantCommand(()->{driveBase.driveCartesian(1, 0, 0, 1);},driveBase),
      new WaitCommand(1),
      new InstantCommand(()->{driveBase.driveCartesian(0, 0, 0, 0);},driveBase),
      //get shooters up to speed
      new InstantCommand(()->{shooter.setVoltageM(7);},shooter),
      new InstantCommand(()->{shooter.setVoltageT(7);},shooter),
      new WaitCommand(2),
      //shoot the ball
      swallowBalls
    );
  }
}
