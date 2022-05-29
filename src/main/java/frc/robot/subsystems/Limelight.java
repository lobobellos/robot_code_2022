package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {

    public NetworkTable table =NetworkTableInstance.getDefault().getTable("limelight");
	public NetworkTableEntry txN = table.getEntry("tx");
	public NetworkTableEntry tyN = table.getEntry("ty");
	public NetworkTableEntry taN = table.getEntry("ta");
    
    public Limelight(){

    }

    public double tx = txN.getDouble(0);
    public double ty = tyN.getDouble(0);
    public double ta = taN.getDouble(0);
}
