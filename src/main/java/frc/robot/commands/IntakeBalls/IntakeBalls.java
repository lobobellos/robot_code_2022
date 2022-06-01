package frc.robot.commands.IntakeBalls;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LimitSwitch;
import frc.robot.subsystems.Yeeter;

public class IntakeBalls extends SequentialCommandGroup {

    private static Intake intake;
    private LimitSwitch lSwitch;
    private Yeeter shooter;

    public IntakeBalls(Intake ntake,LimitSwitch limitswitch,Yeeter shootr){

        intake = ntake;
        lSwitch = limitswitch;
        shooter = shootr;
        addRequirements(intake,lSwitch,shootr);

        addCommands(
            new StartIntake(intake, lSwitch),
            new EjectBall(intake),
            new StartShooter(shooter)
        );
    }
}
