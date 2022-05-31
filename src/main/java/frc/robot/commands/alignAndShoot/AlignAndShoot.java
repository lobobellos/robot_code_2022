package frc.robot.commands.alignAndShoot;


import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveBase;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.Yeeter;


public class AlignAndShoot extends SequentialCommandGroup {

    //subsystems
    private DriveBase drivebase;
    private Intake intake;
    private Limelight limelight;
    private Yeeter shooter;

    

    public AlignAndShoot(DriveBase base,Intake ntake,Limelight light,Yeeter shootr){

        drivebase = base;
        intake = ntake;
        limelight = light;
        shooter = shootr;

        addRequirements(drivebase,intake,limelight,shooter);

        addCommands(
            new AlignBase(drivebase, limelight),
            new SwallowBalls(intake),
            new InstantCommand(
                ()->shooter.stopAll()
            ,shooter)
        );
    }
    
    

    
}
