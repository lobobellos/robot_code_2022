package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class LimitSwitch extends SubsystemBase {
    DigitalInput limitSwitch = new DigitalInput(2);
    Boolean lastState;

    

    Boolean get(){
        return limitSwitch.get();
    }
    Boolean getPressed(){
        return (!lastState && limitSwitch.get());
    }



    @Override
    public void periodic() {
        lastState = limitSwitch.get();
    }


}
