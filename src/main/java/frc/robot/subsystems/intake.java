package frc.robot.subsystems;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Intake extends SubsystemBase{
    Spark intake = new Spark(Constants.intakeLeftChannel);

    public Intake(){
        intake.setInverted(true);
    }

    public void setVoltage(double volts){
        intake.setVoltage(volts);
    }

    public void stop(){
        intake.stopMotor();
        intake.setVoltage(0);
    }
}   