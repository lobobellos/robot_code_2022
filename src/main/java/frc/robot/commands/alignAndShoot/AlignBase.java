package frc.robot.commands.alignAndShoot;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveBase;
import frc.robot.subsystems.Limelight;

public class AlignBase extends CommandBase {
    
    private static DriveBase driveBase;
    private static Limelight limelight;

    public AlignBase(DriveBase base, Limelight light){
        driveBase = base;
        limelight = light;
        addRequirements(driveBase,limelight);
        
    }
    
    @Override
    public void initialize() {
        
    }

    public void execute() {
        if(limelight.tx > 1){
            if(limelight.tx < 10){
              driveBase.driveCartesian(0.0, 0.0, 0.25, 0.0);
            }else{
              driveBase.driveCartesian(0.0, 0.0, 0.5, 0.0);
            }
        }else if(limelight.tx <= -1){
            if(limelight.tx > -10){
              driveBase.driveCartesian(0.0, 0.0, -0.25, 0.0);
            }else{
              driveBase.driveCartesian(0.0, 0.0, -0.5, 0.0);
            }
        }else{
            cancel();
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
