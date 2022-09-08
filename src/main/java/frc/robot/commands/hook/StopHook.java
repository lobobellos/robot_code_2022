package frc.robot.commands.hook;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.Climber;

public class StopHook extends InstantCommand{

    public StopHook(Climber climb){
        super(()->
      climb.stop()
      ,climb);
    }
}