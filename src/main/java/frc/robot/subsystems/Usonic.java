package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Usonic extends SubsystemBase{
    Ultrasonic uSonic = new Ultrasonic(Constants.ultrasonicOutputChannel, Constants.ultrasonicInputChannel);

    public Usonic(){
        uSonic.setEnabled(true);
		Ultrasonic.setAutomaticMode(true);
    }

    public double get(){
        return uSonic.getRangeInches();
    }
}
