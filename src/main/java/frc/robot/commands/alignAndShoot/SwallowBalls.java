package frc.robot.commands.alignAndShoot;

import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.Intake;

public class SwallowBalls extends WaitCommand {

    private Intake intake;

    public SwallowBalls(Intake ntake){
        super(2);

        intake = ntake;
        addRequirements(intake);
    }

    public void initialize(){
        intake.setVoltage(8);
    }
    
    public void end(){
        intake.stop();
    }
}
