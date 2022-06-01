package frc.robot.commands.IntakeBalls;

import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.Intake;

public class EjectBall extends WaitCommand {

    private Intake intake;

    public EjectBall(Intake ntake){
        super(0.25);

        intake = ntake;

        addRequirements(intake);
    }


    public void initialize(){
        super.initialize();

        intake.setVoltage(-8);
    }

    public void execute(){}

    public void end(boolean interupted){

		intake.stop();
	}
}
