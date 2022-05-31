package frc.robot.commands.alignAndShoot;


import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveBase;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Limelight;


public class AlignAndShoot extends SequentialCommandGroup {

    //subsystems
    private DriveBase drivebase;
    private Intake intake;
    private Limelight limelight;

    

    public AlignAndShoot(DriveBase base,Intake ntake,Limelight light){

        drivebase = base;
        intake = ntake;
        limelight = light;

        addRequirements(drivebase,intake,limelight);

        addCommands(
            new AlignBase(drivebase, limelight),
            new SwallowBalls(intake)
        );
    }
    
    

    
}
