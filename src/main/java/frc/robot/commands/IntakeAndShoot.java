package frc.robot.commands;


import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveBase;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.Yeeter;

public class IntakeAndShoot extends SequentialCommandGroup {

    //subsystems
    private DriveBase drivebase;
    private Intake intake;
    private Yeeter shooter;
    private Limelight limelight;

    //commands
    private StartIntake startIntake;
    private AlignBase alignBase = new AlignBase(drivebase, limelight);

    public IntakeAndShoot(){


        addCommands(startIntake.andThen(alignBase));
    }
    
    

    
}
