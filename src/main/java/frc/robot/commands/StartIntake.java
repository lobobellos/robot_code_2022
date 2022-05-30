package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LimitSwitch;

public class StartIntake extends CommandBase {

    private Intake intake;
    private  LimitSwitch lSwitch;

    public StartIntake(Intake sucker , LimitSwitch limitsSwitch ) {
        intake = sucker;
        lSwitch = limitsSwitch;
        addRequirements(intake,lSwitch);
    }
}
