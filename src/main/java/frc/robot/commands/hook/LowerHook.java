package frc.robot.commands.hook;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.Climber;

public class LowerHook extends InstantCommand{

    public LowerHook(Climber climb){
        super(()->
      climb.lower()
      ,climb);
    }
}