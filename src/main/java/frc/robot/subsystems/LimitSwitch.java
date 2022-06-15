package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class LimitSwitch extends SubsystemBase {
    private static DigitalInput limitSwitch = new DigitalInput(2);

    public Boolean get(){
        return limitSwitch.get();
    }

}
