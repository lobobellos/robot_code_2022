package frc.robot.commands.IntakeBalls;

import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.Yeeter;

public class StartShooter extends WaitCommand{

    private Yeeter yeeter;


    public StartShooter(Yeeter shooter) {
        super(2);

        yeeter = shooter;

        addRequirements(yeeter);
    }

    



    public void initialize(){

        yeeter.setVoltageM(8);
        yeeter.setVoltageT(8);
    }



    public void end(){

    }
    
}
