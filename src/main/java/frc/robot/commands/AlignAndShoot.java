package frc.robot.commands;


import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DriveBase;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.Yeeter;

public class AlignAndShoot extends SequentialCommandGroup {

    //subsystems
    private DriveBase drivebase;
    private Intake intake;
    private Yeeter shooter;
    private Limelight limelight;

    

    public AlignAndShoot(){


        addCommands(
            new AlignBase(drivebase, limelight)
        );
    }
    
    

    
}
