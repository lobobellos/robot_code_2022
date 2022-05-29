package frc.robot.subsystems;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shooter extends SubsystemBase {
    public Spark shooterM = new Spark(Constants.mainShooterChannel);
    public Spark shooterT = new Spark(Constants.topShooterChannel);

    void setVoltageM(double voltage){
        shooterM.setVoltage(voltage);
    }

    void setVoltageT(double voltage){
        shooterT.setVoltage(voltage);
    }

    void stopM(){
        shooterM.stopMotor();
    }

    void stopT(){
        shooterT.stopMotor();
    }
}
