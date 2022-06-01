package frc.robot.commands.IntakeBalls;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LimitSwitch;

public class StartIntake extends CommandBase {

	private static Intake intake;
	private static LimitSwitch lSwitch;

	public StartIntake(Intake sucker , LimitSwitch limitsSwitch ) {
		intake = sucker;
		lSwitch = limitsSwitch;
		addRequirements(intake,lSwitch);
	}

	@Override
	public void initialize() {
		intake.setVoltage(8);
		System.out.println("shooter started");
	}


	public void end(boolean interupted){

		intake.stop();
	}

	public boolean isFinished(){
		return lSwitch.get();
	}


}
