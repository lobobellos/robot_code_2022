package frc.robot.commands.alignAndShoot;

import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Yeeter;

public class SwallowBalls extends WaitCommand {

    private Intake intake;
    private Yeeter shooter;

    public SwallowBalls(Intake ntake, Yeeter shootr){
        super(2);

        intake = ntake;
        shooter = shootr;
        addRequirements(intake, shooter);
    }

    public void initialize(){
        super.initialize();
        shooter.setVoltageM(8);
        shooter.setVoltageT(8);
        intake.setVoltage(8);
        
    }

    public void execute(){}
    
    public void end(boolean interupted){
        intake.stop();
        shooter.stopAll();
    }
}
