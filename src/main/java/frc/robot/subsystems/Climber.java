package frc.robot.subsystems;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Climber extends SubsystemBase {
    private Spark m_climb=new Spark(Constants.climberChannel);

    public Climber(){

    }

    public void raise(){
        m_climb.set(1);
    }

    public void lower(){
        m_climb.set(-1);
    }

    public void stop(){
        m_climb.stopMotor();
    }
}
