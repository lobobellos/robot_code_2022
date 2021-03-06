package frc.robot.subsystems;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Yeeter extends SubsystemBase {
    private static Spark shooterM = new Spark(Constants.mainShooterChannel);
    private static Spark shooterT = new Spark(Constants.topShooterChannel);

    public void setVoltageM(double voltage){
        shooterM.setVoltage(voltage);
    }

    public void setVoltageT(double voltage){
        shooterT.setVoltage(voltage);
    }

    public void stopM(){
        shooterM.stopMotor();
    }

    public void stopT(){
        shooterT.stopMotor();
    }

    public void stopAll(){
        shooterT.stopMotor();
        shooterM.stopMotor();
    }
}
